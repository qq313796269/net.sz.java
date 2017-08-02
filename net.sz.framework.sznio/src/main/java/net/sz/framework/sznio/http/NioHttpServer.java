package net.sz.framework.sznio.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.sznio.NioSession;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.szthread.TaskModel;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.struct.thread.ThreadType;
import net.sz.framework.utils.StringUtil;
import net.sz.framework.utils.TimeUtil;

/**
 * 异步socket tcp 服务
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NioHttpServer {

    public static void main(String[] args) {
        // http://127.0.0.1:1000/login
        NioHttpServer nioTcpServer = new NioHttpServer("0.0.0.0", 9527, 4);
        nioTcpServer.addBind("", new INioHttpHandler() {
            @Override
            public void run(NioHttpRequest request) {
                request.addContent("ok");
                request.respons(NioHttpRequest.HttpContentType.Text);
            }
        });
        nioTcpServer.addBind("login", new INioHttpHandler() {
            @Override
            public void run(NioHttpRequest request) {
                request.addContent("login ok");
                request.respons(NioHttpRequest.HttpContentType.Text);
            }
        });
        nioTcpServer.start();
    }

    private static final SzLogger log = SzLogger.getLogger();
    private InetSocketAddress inetSocketAddress;
    private Selector selectorAccept = null;
    private ServerSocketChannel serverSocketChannel = null;
    private int workThread = 4;
    private SzQueueThread threadSelectorAccept = null;
    private SzQueueThread threadRead = null;
    private static final ConcurrentHashMap<Long, Long> readkey = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, _HttpBindBean> httpBindMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, NioSession> httpRequestMap = new ConcurrentHashMap<>();
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(BaseThreadRunnable.GlobalThreadGroup, "net.sz.nio http group");
    private static final ThreadGroup THREAD_GROUP1 = new ThreadGroup(THREAD_GROUP, "nip http bind url");

    /**
     *
     * @param hostname 地址
     * @param port 端口
     * @param workThread 执行线程的数量 ，用于轮询监听
     */
    public NioHttpServer(String hostname, int port, int workThread) {
        inetSocketAddress = new InetSocketAddress(hostname, port);
        this.workThread = workThread;
    }

    /**
     * 添加监听url目录 ,默认的请求方式以一个线程处理监听
     *
     * @param url URl 路径
     * @param handler 处理程序
     */
    public void addBind(String url, INioHttpHandler handler) {
        addBind(url, 1, handler);
    }

    /**
     * 添加监听url目录
     *
     * @param url URl 路径
     * @param handler 处理程序
     * @param threadcount 执行该监听的线程数
     */
    public void addBind(String url, int threadcount, INioHttpHandler handler) {
        if (threadcount < 1) {
            threadcount = 1;
        }
        long addThreadModel = ThreadPool.addThread(THREAD_GROUP1, url).getTid();

        _HttpBindBean httpBindBean = new _HttpBindBean(handler, addThreadModel);
        httpBindMap.put(url, httpBindBean);
    }

    // <editor-fold desc="打开监听 public void start()">
    /**
     * 打开监听
     */
    public void start() {
        try {
            selectorAccept = Selector.open(); // 打开选择器
            serverSocketChannel = ServerSocketChannel.open(); // 打开通道
            serverSocketChannel.configureBlocking(false); // 非阻塞
            serverSocketChannel.socket().bind(inetSocketAddress);
            serverSocketChannel.register(selectorAccept, SelectionKey.OP_ACCEPT); // 向通道注册选择器和对应事件标识
            if (threadSelectorAccept == null) {
                threadSelectorAccept = new PollingHttpAccepRun(ThreadType.Sys, THREAD_GROUP, "net.sz.nio PollingAccep", 1);
                if (workThread < 1) {
                    workThread = 1;
                }
                threadRead = new SzQueueThread(ThreadType.Sys, THREAD_GROUP, "net.sz.nio read", workThread);
            }
            log.error("Server: socket server started. " + inetSocketAddress);
        } catch (Exception e) {
            log.error("启动服务器：" + inetSocketAddress.toString() + " 失败!!!", e);
        }
    }
    // </editor-fold>

    // <editor-fold desc="轮询新建链接 class PollingHttpAccepRun implements Runnable">
    /**
     * 轮询新建链接
     */
    class PollingHttpAccepRun extends SzQueueThread {

        public PollingHttpAccepRun(ThreadType threadType, ThreadGroup group, String name, int threadCount) {
            super(threadType, group, name, threadCount);
        }

        @Override
        public void run() {
            while (true) { // 轮询
                try {
//                    log.debug("PollingHttpAccepRun 1");
                    int nKeys = NioHttpServer.this.selectorAccept.select();
//                    log.debug("PollingHttpAccepRun 2 " + nKeys);
                    if (nKeys > 0) {
                        Set<SelectionKey> selectedKeys = NioHttpServer.this.selectorAccept.selectedKeys();
                        Iterator<SelectionKey> it = selectedKeys.iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            try {

                                if (key.isAcceptable()) {
//                                    log.debug("Acceptable");
                                    //取出新建的连接对象
                                    SocketChannel socketChannel = NioHttpServer.this.serverSocketChannel.accept();
                                    //设置为异步对象
                                    socketChannel.configureBlocking(false);
                                    //创建niosession
                                    NioHttpRequest nioSession = new NioHttpRequest(socketChannel);
                                    nioSession.setIp(((InetSocketAddress) socketChannel.socket().getRemoteSocketAddress()).getAddress().getHostAddress());
                                    nioSession.setPort(socketChannel.socket().getLocalPort());
                                    httpRequestMap.put(nioSession.getId(), nioSession);
                                    //注册收取消息管道
                                    socketChannel.register(selectorAccept, SelectionKey.OP_READ, nioSession);
                                } else if (key.isReadable()) {
                                    Object attachment = key.attachment();
                                    if (attachment instanceof NioHttpRequest) {
                                        NioHttpRequest nioSession = (NioHttpRequest) attachment;
                                        Long get = readkey.get(nioSession.getId());
                                        if (get == null || TimeUtil.currentTimeMillis() - get > 3000) {
                                            readkey.put(nioSession.getId(), TimeUtil.currentTimeMillis());
//                                            log.debug("Readable " + nioSession.getId());
                                            //为了快速，执行，放到其他线程处理
                                            ReadChannelRun readChannelRun = new ReadChannelRun(nioSession);
                                            threadRead.addTask(readChannelRun);
                                        }
                                    }
                                }
                            } catch (CancelledKeyException e) {
                                key.cancel();
                            }
                            it.remove();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="读取缓冲区数据 class ReadChannelRun extends TaskModel">
    /**
     * 读取缓冲区数据
     */
    class ReadChannelRun extends TaskModel {

        NioHttpRequest nioHttpRequest;

        public ReadChannelRun(NioHttpRequest nioSession) {
            this.nioHttpRequest = nioSession;
        }

        @Override
        public void run() {
            lab_exit:
            {
                try {
                    NioHttpServer.httpRequestMap.remove(nioHttpRequest.getId());

//                    String StreamReadLine = StreamReadLine(nioHttpRequest.getChannel());
                    //                    InputStream inputStream = nioHttpRequest.getChannel().socket().getInputStream();
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
//                    BufferedReader rdr = new BufferedReader(null);
                    BufferedReader rdr = new BufferedReader(new StringReader(receiveString(nioHttpRequest.getChannel())));
                    {
                        String line = rdr.readLine();
                        if (StringUtil.isNullOrEmpty(line)) {
                            break lab_exit;
                        }

                        if (line.toLowerCase().contains("/favicon.ico")) {
                            nioHttpRequest.respons(NioHttpRequest.HttpContentType.Text);
                            break lab_exit;
                        }

                        String[] url = line.split(" ");
                        if (url.length < 3) {
                            break lab_exit;
                        }
//                        log.debug(nioHttpRequest.getId() + "  " + url[1]);

//                        log.debug(receive);
                        nioHttpRequest.setMethod(url[0].toUpperCase());
                        if (nioHttpRequest.getMethod().equals("POST")) {
                            //截取 监听url
                            nioHttpRequest.setUrl(url[1].substring(1));
                        } else if (nioHttpRequest.getMethod().equals("GET")) {
                            int idx = url[1].indexOf('?');
                            if (idx != -1) {
                                //截取 监听url
                                nioHttpRequest.setUrl(url[1].substring(1, idx));
                            } else {
                                //截取 监听url
                                nioHttpRequest.setUrl(url[1].substring(1));
                            }
                        }
                        if (StringUtil.isNullOrEmpty(nioHttpRequest.getUrl())) {
                            nioHttpRequest.setUrl("");
                        }
                        _HttpBindBean httpBindBean = httpBindMap.get(nioHttpRequest.getUrl());
                        if (httpBindBean == null) {
                            nioHttpRequest.responsFailure();
                            break lab_exit;
                        } else {
                            // Read header
                            while (!StringUtil.isNullOrEmpty((line = rdr.readLine()))) {
                                String[] tokens = line.split(": ");
                                if (tokens != null && tokens.length > 1) {
                                    nioHttpRequest.getHeads().put(tokens[0], tokens[1]);
                                }
                            }

                            if (nioHttpRequest.getMethod().equals("POST")) {
                                //获取post参数
                                int len = Integer.parseInt(nioHttpRequest.getHeads().get("Content-Length"));
                                if (len > 0) {
                                    char[] buf = new char[len];
                                    int readCount = rdr.read(buf);
                                    if (readCount > 0) {
                                        nioHttpRequest.setHttpcontent(String.copyValueOf(buf, 0, readCount));
                                    }
                                }
                            } else if (nioHttpRequest.getMethod().equals("GET")) {
                                int idx = url[1].indexOf('?');
                                if (idx != -1) {
                                    //截取 监听get参数
                                    nioHttpRequest.setHttpcontent(url[1].substring(idx + 1));
                                }
                            } else {
                                return;
                            }
                            //解析参数
                            if (nioHttpRequest.getHttpcontent() != null) {
                                String[] match = nioHttpRequest.getHttpcontent().split("\\&");
                                for (String element : match) {
                                    int indexOf = element.indexOf("=");
                                    if (indexOf < 0) {
                                        if (!nioHttpRequest.getParams().containsKey(element)) {
                                            nioHttpRequest.getParams().put(element, "");
                                        }
                                    } else {
                                        String key = element.substring(0, indexOf);
                                        String value = element.substring(indexOf + 1);
                                        if (nioHttpRequest.getParams().containsKey(key)) {
                                            nioHttpRequest.getParams().put(key, nioHttpRequest.getParams().get(key) + "=" + value);
                                        } else {
                                            nioHttpRequest.getParams().put(key, value);
                                        }
                                    }
                                }
                            }
                            //移交给线程处理
                            ThreadPool.addTask(httpBindBean.getThreadId(), new HttpHandlerRun(nioHttpRequest, httpBindBean));
                        }
                        //移除轮询的键值
                        readkey.remove(nioHttpRequest.getId());
                        return;
                    }
                } catch (Exception e) {
                    log.debug("读取 http server 消息：" + (nioHttpRequest.getId()), e);
                }
            }
            //关闭资源释放资源
            nioHttpRequest.close();
            //移除轮询的键值
            readkey.remove(nioHttpRequest.getId());
        }
    }
    // </editor-fold>

    /**
     * 接受数据
     *
     * @param socketChannel
     * @return
     * @throws Exception
     */
    private static String receiveString(SocketChannel socketChannel) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        byte[] bytes = null;
        int size = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 3; i++) {
            while (!socketChannel.socket().isInputShutdown()
                    && !socketChannel.socket().isClosed()
                    && (size = socketChannel.read(buffer)) > 0) {
                buffer.flip();
                bytes = new byte[size];
                buffer.get(bytes);
                baos.write(bytes);
                buffer.clear();
            }
            Thread.sleep(1);
        }
        bytes = baos.toByteArray();
        return new String(bytes, "utf-8");
    }

    private String StreamReadLine(SocketChannel socketChannel) throws Exception {
//        socketChannel.read(dst);
        int next_char;
        String data = "";
        while (true) {
            next_char = socketChannel.socket().getInputStream().read();
            if (next_char == '\n') {
                break;
            }
            if (next_char == '\r') {
                continue;
            }
            if (next_char == -1) {
                Thread.sleep(1);
                continue;
            };
            data += (char) next_char;
        }
        return data;
    }
}
