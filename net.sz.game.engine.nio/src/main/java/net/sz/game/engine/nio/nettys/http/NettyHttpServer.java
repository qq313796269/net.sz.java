package net.sz.game.engine.nio.nettys.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.nio.nettys.NettyPool;
import net.sz.game.engine.nio.nettys.http.handler.IHttpHandler;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.util.ConcurrentHashSet;
import net.sz.game.engine.utils.StringUtil;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyHttpServer {

    private static SzLogger log = SzLogger.getLogger();
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(ThreadPool.GlobalThreadGroup.getParent(), "Http-Thread-Group");

    // <editor-fold defaultstate="collapsed" desc="解码器 class HttpServerHandler extends SimpleChannelInboundHandler<Object>">
    class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

        public HttpServerHandler() {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error(cause);
            ctx.close();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//            if (log.isInfoEnabled()) {
//                log.info("一次请求：：" + msg.getClass().getName());
//            }
//            if (msg instanceof HttpObjectAggregator) {
//                log.info("一次请求：：" + msg.getClass().getName());
//                ((HttpObjectAggregator)msg);
//            }
            String ip = NettyPool.getInstance().getIP(ctx);
            if (StringUtil.isNullOrEmpty(ip)) {
                close(ctx, "ip地址欺诈：：：" + ip);
                return;
            }
            if (NettyHttpServer.this.whiteIPSet.size() > 0) {
                String[] ips = NettyHttpServer.this.whiteIPSet.toArray(new String[0]);
                boolean isreturn = true;
                for (int i = 0; i < ips.length; i++) {
                    if (ip.startsWith(ips[i])) {
                        isreturn = false;
                        break;
                    }
                }

                if (isreturn) {
                    close(ctx, "不在ip地址白名单列表中：：：" + ip);
                    return;
                }

            } else if (NettyHttpServer.this.blackIPSet.size() > 0) {
                String[] ips = NettyHttpServer.this.blackIPSet.toArray(new String[0]);
                for (int i = 0; i < ips.length; i++) {
                    if (ip.startsWith(ips[i])) {
                        close(ctx, "ip地址黑名单列表中：：：" + ip);
                        return;
                    }
                }
            }
            URI uri = null;
            String urlPath = null;
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest request = null;
                HashMap<String, String> paramsMap = new HashMap<>();
                String httpbody = null;
                request = (FullHttpRequest) msg;
                uri = new URI(request.getUri());

                urlPath = uri.getPath();
                if (urlPath.length() > 1) {
                    if (urlPath.endsWith("/")) {
                        urlPath = urlPath.substring(0, urlPath.length() - 1);
                    }
                    if (urlPath.startsWith("/")) {
                        if (urlPath.length() > 1) {
                            urlPath = urlPath.substring(1);
                        }
                    }
                }

                /*这里是不处理的*/
                if (!(urlPath.equalsIgnoreCase("favicon.ico"))) {

                    HttpActionBean httpActionBean = NettyHttpServer.this.handlerMap.get(urlPath);
                    if (httpActionBean == null) {
                        /*判断如果没有处理选项，看看是否有默认处理选项*/
                        httpActionBean = NettyHttpServer.this.handlerMap.get("*");
                    }
                    if (httpActionBean != null) {
                        if (request.getMethod().equals(HttpMethod.GET)) {
                            httpbody = uri.getQuery();
                        } else if (request.getMethod().equals(HttpMethod.POST)) {
                            ByteBuf content = request.content();
                            content.retain();
                            byte[] contentbuf = new byte[content.readableBytes()];
                            content.readBytes(contentbuf);
                            content.release();
                            httpbody = new String(contentbuf, "utf-8");
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("URI：" + urlPath + "；httpbody:" + httpbody);
                        }
                        if (!StringUtil.isNullOrEmpty(httpbody)) {
//                        httpbody = java.net.URLDecoder.decode(httpbody, "utf-8");
                            String[] split = httpbody.split("&");
                            for (String string : split) {
                                int indexOf = string.indexOf("=");
                                if (indexOf >= 0) {
                                    String substring = string.substring(0, indexOf);
                                    String substring1 = string.substring(indexOf + 1);
                                    String get = paramsMap.get(substring);
                                    if (!StringUtil.isNullOrEmpty(get)) {
                                        get = get + "=" + substring1;
                                        paramsMap.put(substring, get);
                                    } else {
                                        paramsMap.put(substring, substring1);
                                    }
                                }
                            }
                        }
                        NioHttpRequest requestMessage = new NioHttpRequest();
                        requestMessage.setUrl(urlPath);
                        requestMessage.setIp(ip);
                        requestMessage.setHttpBody(httpbody);
                        requestMessage.setSession(ctx);
                        requestMessage.setRequest(request);
                        requestMessage.setParams(paramsMap);
                        ThreadPool.addTask(httpActionBean.getThreadId(), new HttpTask(httpActionBean.getHttpHandler(), requestMessage));
                        return;
                    }
                } else {
                    ctx.close();
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("无效请求：：" + msg.getClass().getName() + " " + msg.toString());
                }
            }
            close(ctx, urlPath);
        }
    }
    // </editor-fold>

    private int PORT;
    private String HostName;
    private ChannelFuture sync = null;
    /**
     * ip黑名单，坚决不允许访问，
     */
    private ConcurrentHashSet<String> blackIPSet = new ConcurrentHashSet<>();
    /**
     * ip白名单，如果存在此值，那么访问ip必须是列表里面的值
     */
    private ConcurrentHashSet<String> whiteIPSet = new ConcurrentHashSet<>();

    private ConcurrentHashMap<String, HttpActionBean> handlerMap = new ConcurrentHashMap<>();

    /**
     *
     * @param hostname
     * @param port
     */
    public NettyHttpServer(String hostname, int port) {
        this.PORT = port;
        this.HostName = hostname;
    }

    /**
     * 一旦调用白名单设置，除了白名单一切ip都不允许访问
     *
     * @param ips
     */
    public void addWhiteIP(String... ips) {
        for (String ip : ips) {
            whiteIPSet.add(ip);
        }
    }

    /**
     * 设置黑名单，不允许访问列表
     *
     * @param ips
     */
    public void addBlackIP(String... ips) {
        for (String ip : ips) {
            blackIPSet.add(ip);
        }
    }

    /**
     * 移除白名单，如果白名单为空，表示无限制
     *
     * @param ips
     */
    public void removeWhiteIP(String... ips) {
        for (String ip : ips) {
            whiteIPSet.remove(ip);
        }
    }

    /**
     * 移除黑名单
     *
     * @param ips
     */
    public void removeBlackIP(String... ips) {
        for (String ip : ips) {
            blackIPSet.remove(ip);
        }
    }

    /**
     * * 表示处理所有结果
     *
     * @param httpHandler
     * @param urls
     */
    public void addHttpBind(IHttpHandler httpHandler, String... urls) {
        addHttpBind(httpHandler, 1, urls);
    }

    /**
     * * 表示处理所有结果
     *
     * @param httpHandler
     * @param threadcount 对每一个url单独创建处理线程数量
     * @param urls
     */
    public void addHttpBind(IHttpHandler httpHandler, int threadcount, String... urls) {
        for (String url : urls) {
            long addThread = ThreadPool.addThread(THREAD_GROUP, HostName + ":" + PORT + "/" + url, threadcount);
            HttpActionBean httpActionBean = new HttpActionBean(httpHandler, addThread);
            handlerMap.put(url.toLowerCase(), httpActionBean);
        }
    }

    /**
     * 移除一个监听
     *
     * @param url
     */
    public void removeHttpBind(String url) {
        handlerMap.remove(url.toLowerCase());
    }

    /**
     * 默认是http
     *
     * @param workthread
     */
    public void start(int workthread) {
        start(workthread, false);
    }

    /**
     *
     * @param workthread 工作线程 不得小于4,不得大于10
     * @param isSSL
     */
    public void start(int workthread, boolean isSSL) {
        if (workthread < 4 || workthread > 10) {
            workthread = 4;
        }
        try {
            SslContext sslCtx;
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            //具体场景要通过文件
            sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());

            NioEventLoopGroup workerGroup = new NioEventLoopGroup(workthread);
            ServerBootstrap b = new ServerBootstrap();
            b.group(workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (isSSL) {
                        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                    }
                    pipeline.addLast(new HttpRequestDecoder());
                    pipeline.addLast(new HttpServerCodec());
                    /**
                     * usually we receive http message infragment,if we want
                     * full http message, we should bundle HttpObjectAggregator
                     * and we can get FullHttpRequest。
                     * 我们通常接收到的是一个http片段，如果要想完整接受一次请求的所有数据，我们需要绑定HttpObjectAggregator，然后我们
                     * 就可以收到一个FullHttpRequest-是一个完整的请求信息。
                     *
                     */
                    pipeline.addLast(new HttpObjectAggregator(65535));//定义缓冲数据量
                    /*顺序必须保证*/
                    pipeline.addLast(new HttpServerHandler());
                    pipeline.addLast(new HttpResponseEncoder());
                }
            });
            sync = b.bind(HostName, PORT).sync();
            if (isSSL) {
                log.error("https 服务器已启动 -> " + HostName + ":" + PORT);
            } else {
                log.error("http 服务器已启动 -> " + HostName + ":" + PORT);
            }
        } catch (Throwable ex) {
            if (isSSL) {
                log.error("https 服务器启动异常 -> " + HostName + ":" + PORT, ex);
            } else {
                log.error("http 服务器启动异常 -> " + HostName + ":" + PORT, ex);
            }
            System.exit(1);
        }
    }

    /**
     *
     * @deprecated 请使用 NettyPool.getInstance().stopServer();
     */
    @Deprecated
    public void stop() {
        if (sync != null) {
            sync.channel().close();
            sync = null;
        }
    }

    public static void close(ChannelHandlerContext session, String msg) {
        try {
            ByteBuf wrappedBuffer;
            if (msg != null) {
                wrappedBuffer = Unpooled.wrappedBuffer(("404 NOT FOUND url：" + msg).getBytes("utf-8"));
            } else {
                wrappedBuffer = Unpooled.wrappedBuffer("404 NOT FOUND".getBytes("utf-8"));
            }
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, wrappedBuffer);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, NioHttpRequest.HttpContentType.Html);
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            session.writeAndFlush(response);
            session.close();
        } catch (Throwable ex) {
            log.error("HttpRequestMessage.close 失败", ex);
        }
    }

    public static void main(String[] args) throws Exception {

        NettyHttpServer server = new NettyHttpServer("0.0.0.0", 7082);

        server.addHttpBind(
                (url, requestMessage) -> {
                    requestMessage.addContentLine("<html>");
                    requestMessage.addContentLine("    <head>");
                    requestMessage.addContentLine("    </head>");
                    requestMessage.addContentLine("    <body>");
                    requestMessage.addContentLine("     测试中文乱码问题   " + url);
                    requestMessage.addContentLine("    </body>");
                    requestMessage.addContentLine("</html>");
                    requestMessage.respons();
                },
                "*");

        server.start(4, false);
    }
}
