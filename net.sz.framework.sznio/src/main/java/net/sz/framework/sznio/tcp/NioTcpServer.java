package net.sz.framework.sznio.tcp;

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
import java.util.concurrent.ConcurrentSkipListSet;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.sznio.NioSession;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.szthread.TaskModel;
import net.sz.framework.struct.thread.ThreadType;
import net.sz.framework.utils.TimeUtil;

/**
 * 异步socket tcp 服务
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NioTcpServer {

    private static final SzLogger log = SzLogger.getLogger();

    // <editor-fold desc="测试 public static void main(String[] args)">
    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        NioTcpServer nioTcpServer = new NioTcpServer("127.0.0.1", 1000, 4, new NioTcpServerProbufferHandler() {
            @Override
            public void handleAccept(NioSession session) {
            }

            @Override
            public void handleClose(NioSession session, Exception ex) {
            }

            @Override
            public void handleRead(NioSession session, int msgid, byte[] array) {
                try {
                    log.debug(session + new String(array, "utf-8"));
                    session.send(array);
                } catch (Exception e) {
                    log.debug("ss", e);
                }
            }
        });
        nioTcpServer.start();
    }
    // </editor-fold>

    //绑定地址
    private InetSocketAddress inetSocketAddress;
    //处理handler接口
    private NioTcpServerHandler handler = null;
    //处理新建链接
    private Selector selectorAccept = null;
    private ServerSocketChannel serverSocketChannel = null;
    private int WorkerThread = 4;
    SzQueueThread threadSelectorAccept = null;
    SzQueueThread threadRead = null;
    ConcurrentSkipListSet<Long> readkey = new ConcurrentSkipListSet<>();

    /**
     * 一秒钟接收消息数量 ， 默认50条消息
     */
    public static int secondMsgCount = 50;
    /**
     * 每一秒收取消息的总量 ,默认 50k
     */
    public static int secondBytesCount = 50 * 1024;

    /**
     * 通信线程分组
     */
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("net-sz-nio-tcp-group");
    /**
     * 存储所有session对象
     */
    public static final ConcurrentHashMap<Long, NioSession> sessionMap = new ConcurrentHashMap<>();

    /**
     *
     * @param hostname
     * @param port
     * @param workerthread
     * @param handler
     */
    public NioTcpServer(String hostname, int port, int workerthread, NioTcpServerHandler handler) {
        inetSocketAddress = new InetSocketAddress(hostname, port);
        this.WorkerThread = workerthread;
        this.handler = handler;
    }

    // <editor-fold desc="打开监听 public void start()">
    /**
     * 打开监听
     */
    public void start() {
        try {
            // 打开选择器
            selectorAccept = Selector.open();
            // 打开通道
            serverSocketChannel = ServerSocketChannel.open();
            // 非阻塞
            serverSocketChannel.configureBlocking(false);
            //绑定地址
            serverSocketChannel.socket().bind(inetSocketAddress);
            // 向通道注册选择器和对应事件标识
            serverSocketChannel.register(selectorAccept, SelectionKey.OP_ACCEPT);

            if (threadSelectorAccept == null) {
                threadSelectorAccept = new PollingAccepRun(ThreadType.Sys, THREAD_GROUP, "net-sz-nio-PollingAccep", 1);
                if (WorkerThread < 1) {
                    WorkerThread = 1;
                }
                threadRead = new SzQueueThread(ThreadType.Sys, THREAD_GROUP, "net-sz-nio-read", WorkerThread);
            }
            log.error("Server: socket server started. " + inetSocketAddress);
        } catch (Exception e) {
            log.error("启动服务器：" + inetSocketAddress.toString() + " 失败!!!", e);
        }
    }
    // </editor-fold>

    // <editor-fold desc="轮询新建链接 class PollingAccepRun implements Runnable">
    /**
     * 轮询新建链接
     */
    class PollingAccepRun extends SzQueueThread {

        public PollingAccepRun(ThreadType threadType, ThreadGroup group, String name, int threadCount) {
            super(threadType, group, name, threadCount);
        }

        @Override
        public void run() {
            while (true) { // 轮询
                try {
//                    log.debug("PollingAccepRun 1");
                    //获取选择器
                    int nKeys = NioTcpServer.this.selectorAccept.select();
//                    log.debug("PollingAccepRun 2 " + nKeys);
                    if (nKeys > 0) {
                        //拿到所有选择器类型
                        Set<SelectionKey> selectedKeys = NioTcpServer.this.selectorAccept.selectedKeys();
                        Iterator<SelectionKey> it = selectedKeys.iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            try {
                                if (key.isAcceptable()) {
//                                    log.debug("Acceptable 1");
                                    //取出新建的连接对象
                                    SocketChannel socketChannel = NioTcpServer.this.serverSocketChannel.accept();
                                    //设置为异步对象
                                    socketChannel.configureBlocking(false);
                                    socketChannel.socket().setReceiveBufferSize(4000);
                                    socketChannel.socket().setSendBufferSize(4000);
                                    //创建niosession
                                    NioSession nioSession = new NioSession(socketChannel);
                                    nioSession.setIp(((InetSocketAddress) socketChannel.socket().getRemoteSocketAddress()).getAddress().getHostAddress());
                                    nioSession.setPort(socketChannel.socket().getLocalPort());
                                    NioTcpServer.sessionMap.put(nioSession.getId(), nioSession);
                                    //通知
                                    NioTcpServer.this.handler.handleAccept(nioSession);
//                                    log.debug("Acceptable 2 " + nioSession);
                                    //注册收取消息管道
                                    SelectionKey register = socketChannel.register(selectorAccept, SelectionKey.OP_READ, nioSession);
//                                    log.debug("Acceptable 3 " + nioSession);
//                                AcceptChannelRun acceptChannelRun = new AcceptChannelRun(key);
//                                acceptChannelRun.run();
                                } else if (key.isReadable()) {
//                                    log.debug("Readable");
                                    Object attachment = key.attachment();
                                    if (attachment instanceof NioSession) {
                                        NioSession nioSession = (NioSession) attachment;
                                        if (nioSession.isAvailable()) {
                                            if (readkey.add(nioSession.getId())) {
                                                ReadChannelRun readChannelRun = new ReadChannelRun(nioSession);
                                                threadRead.addTask(readChannelRun);
                                            }
                                        }
                                    }
                                }
                            } catch (CancelledKeyException e) {
                                //有可能该链接失效了，所有是取消状态
                                key.cancel();
                            }
                            it.remove();
                        }
                    }
                } catch (Exception e) {
                    log.error("监听", e);
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

        NioSession nioSession;

        public ReadChannelRun(NioSession nioSession) {
            this.nioSession = nioSession;
        }

        @Override
        public void run() {
            Exception ex = null;
            try {
                if (!nioSession.isAvailable()) {
                    return;
                }
                ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
                for (;;) {
                    //当开始处理一次收取消息的时候很有可能当前数据大于你要读取的缓冲区
                    //所有需要循环读取数据直到为0
                    long currentTimeMillis = TimeUtil.currentTimeMillis();
                    int dayOfSecond = TimeUtil.getDayOfSecond(currentTimeMillis);
                    if (dayOfSecond != nioSession.getSecond()) {
                        nioSession.setSecondBytesCount(0);
                        nioSession.setSecondMsgCount(0);
                        nioSession.setSecond(dayOfSecond);
                    }

                    nioSession.setLastTime(currentTimeMillis);

                    byteBuffer.clear();
                    int readBytes = nioSession.getChannel().read(byteBuffer);
                    if (readBytes > 0) {
                        nioSession.setSecondMsgCount(nioSession.getSecondMsgCount() + 1);
                        nioSession.setSecondBytesCount(nioSession.getSecondBytesCount() + readBytes);
                        if (nioSession.getSecondMsgCount() > secondMsgCount || nioSession.getSecondBytesCount() > secondBytesCount) {
                            log.error("收取消息超过每秒钟限制 " + "收取消息条数：" + nioSession.getSecondMsgCount() + "，收取消息字节数：" + nioSession.getSecondBytesCount());
                            break;
                        }
                        actionReadByte(nioSession, byteBuffer, readBytes);
                    } else if (readBytes == 0) {
                        readkey.remove(nioSession.getId());
                        return;
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                ex = e;
                log.debug("", e);
            }
            //关闭资源释放资源
            nioSession.close();

            /**
             * 通知有链接断开
             */
            NioTcpServer.this.handler.handleClose(nioSession, ex);
            /**
             *
             */
            NioTcpServer.sessionMap.remove(nioSession.getId());
            //移除轮询的键值
            if (nioSession != null) {
                readkey.remove(nioSession.getId());
            }
        }
    }
    // </editor-fold>

    /**
     * 
     * @param nioSession
     * @param byteBuffer
     * @param readBytes
     */
    void actionReadByte(NioSession nioSession, ByteBuffer byteBuffer, int readBytes) {
        byte[] ret = null;
        if (nioSession.getReadByteList() != null) {
            ret = new byte[nioSession.getReadByteList().length + readBytes];
            System.arraycopy(nioSession.getReadByteList(), 0, ret, 0, nioSession.getReadByteList().length);
            System.arraycopy(byteBuffer.array(), 0, ret, nioSession.getReadByteList().length, readBytes);
            nioSession.setReadByteList(null);
        } else {
            ret = new byte[readBytes];
            System.arraycopy(byteBuffer.array(), 0, ret, 0, readBytes);
        }

        ByteBuffer wrap = ByteBuffer.wrap(ret);
        int offset = 0;
        for (;;) {
            if (ret.length - offset > 8) {
                //够一个的组合
                int msglen = wrap.getInt();//消息长度
                offset += 4;
                if (ret.length - 4 >= msglen) {
                    int msgid = wrap.getInt();//读取消息id
                    //设置目前读取的长度
                    offset += 4;
                    //拷贝出来待用
                    byte[] array = new byte[msglen - 4];
                    System.arraycopy(ret, offset, array, 0, array.length);
                    handler.handleRead(nioSession, msgid, array);
                    //设置目前读取的长度
                    offset += array.length;
                    //设置目前读取的长度
                    wrap.position(offset);
                } else {
                    offset -= 4;
                    byte[] array = new byte[ret.length - offset];
                    System.arraycopy(ret, offset, array, 0, array.length);
                    nioSession.setReadByteList(array);
                    return;
                }
            } else {
                if (ret.length - offset > 0) {
                    byte[] array = new byte[ret.length - offset];
                    System.arraycopy(ret, offset, array, 0, array.length);
                    nioSession.setReadByteList(array);
                }
                return;
            }
        }
    }
}
