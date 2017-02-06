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
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.utils.StringUtil;
import org.apache.log4j.Logger;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyHttpServer {

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
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest request = null;
                HashMap<String, String> paramsMap = new HashMap<>();
                String httpbody = null;
                request = (FullHttpRequest) msg;
                URI uri = new URI(request.getUri());
                String urlPath = "";
                urlPath = uri.getPath();
                if (urlPath.startsWith("/")) {
                    urlPath = urlPath.substring(1);
                }
                if (urlPath.endsWith("/")) {
                    urlPath = urlPath.substring(0, urlPath.length() - 1);
                }
                urlPath = urlPath.toLowerCase();
                if (urlPath.equals("favicon.ico") || urlPath.equals("")) {
                    log.debug(urlPath);
                    close(ctx);
                    return;
                }
                HttpActionBean httpActionBean = NettyHttpServer.this.handlerMap.get(urlPath);
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
                    log.debug("URI：" + urlPath + "；httpbody:" + httpbody);
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
                    requestMessage.setIp(NettyPool.getInstance().getIP(ctx));
                    requestMessage.setHttpBody(httpbody);
                    requestMessage.setSession(ctx);
                    requestMessage.setRequest(request);
                    requestMessage.setParams(paramsMap);
                    ThreadPool.addTask(httpActionBean.getThreadId(), new HttpTask(httpActionBean.getHttpHandler(), requestMessage));
                    return;
                }
            }
            log.info("一次无效请求：：" + msg.getClass().getName());
            close(ctx);
        }

        public void close(ChannelHandlerContext session) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.wrappedBuffer("404 NOT FOUND".getBytes()));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html");
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            session.writeAndFlush(response);
            session.close();
        }
    }

    private static Logger log = Logger.getLogger(NettyHttpServer.class);

    private int PORT;
    private String HostName;
    private ChannelFuture sync = null;

    private ConcurrentHashMap<String, HttpActionBean> handlerMap = new ConcurrentHashMap<>();

    public NettyHttpServer(String hostname, int port) {
        this.PORT = port;
        this.HostName = hostname;
    }

    public void addHttpBind(String url, IHttpHandler httpHandler) {
        addHttpBind(url, 1, httpHandler);
    }

    public void addHttpBind(String url, int threadcount, IHttpHandler httpHandler) {
        handlerMap.put(url.toLowerCase(), new HttpActionBean(HostName + ":" + PORT, url, httpHandler, threadcount));
    }

    public void removeHttpBind(String url) {
        handlerMap.remove(url.toLowerCase());
    }

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
                    pipeline.addLast("decoder", new HttpRequestDecoder());
                    /**
                     * usually we receive http message infragment,if we want
                     * full http message, we should bundle HttpObjectAggregator
                     * and we can get FullHttpRequest。
                     * 我们通常接收到的是一个http片段，如果要想完整接受一次请求的所有数据，我们需要绑定HttpObjectAggregator，然后我们
                     * 就可以收到一个FullHttpRequest-是一个完整的请求信息。
                     *
                     */
                    pipeline.addLast("servercodec", new HttpServerCodec());
                    pipeline.addLast("aggegator", new HttpObjectAggregator(1024 * 1024 * 64));//定义缓冲数据量
                    //顺序必须保证
                    pipeline.addLast(new HttpServerHandler());
                    pipeline.addLast("responseencoder", new HttpResponseEncoder());
                }
            });
            sync = b.bind(HostName, PORT).sync();
            if (isSSL) {
                log.error("https 服务器已启动 -> " + HostName + ":" + PORT);
            } else {
                log.error("http 服务器已启动 -> " + HostName + ":" + PORT);
            }
        } catch (Exception ex) {
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

    public static void main(String[] args) throws Exception {

        NettyHttpServer server = new NettyHttpServer("0.0.0.0", 8080);
        server.addHttpBind("login", (NioHttpRequest requestMessage) -> {
            log.error(requestMessage.getParam("user"));
            requestMessage.addContentLine("<html>");
            requestMessage.addContentLine("    <head>");
            requestMessage.addContentLine("    </head>");
            requestMessage.addContentLine("    <body>");
            requestMessage.addContentLine("        sssssssssssssssssss");
            requestMessage.addContentLine("    </body>");
            requestMessage.addContentLine("</html>");
            requestMessage.respons(NioHttpRequest.HttpContentType.Html);
        });
        server.start(4, true);
    }
}
