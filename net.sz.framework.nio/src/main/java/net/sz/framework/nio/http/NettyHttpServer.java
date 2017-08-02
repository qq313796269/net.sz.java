package net.sz.framework.nio.http;

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
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.nio.NettyPool;
import net.sz.framework.nio.http.handler.IHttpHandler;
import net.sz.framework.nio.tcp.NettyCoder;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.util.ObjectAttribute;
import net.sz.framework.utils.StringUtil;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public final class NettyHttpServer {

    private static final SzLogger log = SzLogger.getLogger();
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(BaseThreadRunnable.GlobalThreadGroup.getParent(), "Http-Thread-Group");

    // <editor-fold desc="解码器 class HttpServerHandler extends SimpleChannelInboundHandler<Object>">
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
            String ip = NettyCoder.getIP(ctx);
            if (StringUtil.isNullOrEmpty(ip)) {
                NettyHttpServer.close(ctx, "ip地址欺诈：：：" + ip);
                return;
            }
            if (NettyPool.getInstance().getWhiteIPSet().size() > 0) {
                String[] ips = NettyPool.getInstance().getWhiteIPSet().toArray(new String[0]);
                boolean isreturn = true;
                for (int i = 0; i < ips.length; i++) {
                    if (ip.startsWith(ips[i])) {
                        isreturn = false;
                        break;
                    }
                }

                if (isreturn) {
                    NettyHttpServer.close(ctx, "不在ip地址白名单列表中：：：" + ip);
                    return;
                }

            } else if (NettyPool.getInstance().getBlackIPSet().size() > 0) {
                String[] ips = NettyPool.getInstance().getBlackIPSet().toArray(new String[0]);
                for (int i = 0; i < ips.length; i++) {
                    if (ip.startsWith(ips[i])) {
                        NettyHttpServer.close(ctx, "ip地址黑名单列表中：：：" + ip);
                        return;
                    }
                }
            }
            URI uri = null;
            String urlPath = null;
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest request = null;
                ObjectAttribute<String> paramsMap = new ObjectAttribute<>();
                String httpbody = null;
                request = (FullHttpRequest) msg;
                uri = new URI(request.uri());
                urlPath = uri.getPath();
                String host = request.headers().get(HttpHeaders.Names.HOST);
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
                        if (request.method().equals(HttpMethod.GET)) {
                            httpbody = uri.getQuery();
                        } else if (request.method().equals(HttpMethod.POST)) {
                            ByteBuf content = request.content();
                            content.retain();
                            byte[] contentbuf = new byte[content.readableBytes()];
                            content.readBytes(contentbuf);
                            content.release();
                            httpbody = new String(contentbuf, "utf-8");
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("URI：" + urlPath + "; " + request.getMethod() + "; httpbody:" + httpbody);
                        }
                        if (!StringUtil.isNullOrEmpty(httpbody)) {
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
                        requestMessage.setDomainName(host);
                        requestMessage.setHttpMethod(request.method());
                        requestMessage.setRequest(request);
                        requestMessage.setParams(paramsMap);
                        ThreadPool.addTask(httpActionBean.getThreadId(), new HttpTask(httpActionBean.getHttpHandler(), requestMessage));
                        return;
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("http get favicon.ico ");
                    }
                    ctx.close();
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("无效请求：：" + msg.getClass().getName() + " " + msg.toString());
                }
            }
            NettyHttpServer.close(ctx, urlPath);
        }
    }
    // </editor-fold>

    private int PORT;
    private String HostName;
    private ChannelFuture sync = null;

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
            long addThread = ThreadPool.addThread(THREAD_GROUP, HostName + ":" + PORT + "/" + url, threadcount).getTid();
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
     * @param workthread 工作线程 不得小于0,不得大于10
     * @param isSSL
     */
    public void start(int workthread, boolean isSSL) {
        if (workthread < 0 || workthread > 10) {
            workthread = 1;
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
                log.error("https " + toString() + " 服务器已启动");
            } else {
                log.error("http " + toString() + " 服务器已启动");
            }
        } catch (Throwable ex) {
            if (isSSL) {
                log.error("https " + toString() + " 服务器启动异常", ex);
            } else {
                log.error("http " + toString() + " 服务器启动异常", ex);
            }
            System.exit(1);
        }
    }

    @Override
    public String toString() {
        return "{NettyHttpServer -> " + HostName + ":" + PORT + '}';
    }

    /**
     *
     * @deprecated 请使用 NettyPool.getInstance().stopServer();
     */
    @Deprecated
    public void close() {
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

        NettyHttpServer server = new NettyHttpServer("0.0.0.0", 9527);

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
