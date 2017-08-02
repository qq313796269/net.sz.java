package net.sz.framework.nio.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import net.sz.framework.nio.NettyPool;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 * 客户端连接管理器
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyTcpClient {

    private static final SzLogger log = SzLogger.getLogger();

    private Bootstrap bootstrap;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workGroup = null;
    private int port = 9527;
    private String hostname = "127.0.0.1";
    private INettyHandler nettyHandler;
    private NettyCoder nettyCoder;
    private ChannelInitializer<Channel> channelInitializer = null;
    private AtomicLong syncId = new AtomicLong();

    SimpleChannelInboundHandler simpleChannelInboundHandler = new MyChannelInboundHandler();

    class MyChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {

        /**
         * 收到消息
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
            NettyTcpClient.this.nettyCoder.decode0(ctx, byteBuf);
        }

        /**
         * 发现异常
         *
         * @param ctx
         * @param cause
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error(NettyTcpClient.this.toString() + "通信内部异常", cause);
        }

        /**
         * 断开连接后
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) {
            String asLongText = ctx.channel().id().asLongText();
            if (log.isDebugEnabled()) {
                log.debug(NettyTcpClient.this.toString() + "连接断开：" + asLongText);
            }
            NettyPool.getInstance().getSessions().remove(asLongText);
            NettyTcpClient.this.nettyHandler.closeSession(asLongText, ctx);
        }

        /**
         * 创建链接后，链接被激活
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            String asLongText = ctx.channel().id().asLongText();
            NettyCoder.setSessionAttr(ctx, NettyCoder.SessionCreateTime, TimeUtil.currentTimeMillis());
            NettyCoder.setSessionAttr(ctx, NettyCoder.SessionLastTime, TimeUtil.currentTimeMillis());
            NettyPool.getInstance().getSessions().put(asLongText, ctx);
            if (log.isDebugEnabled()) {
                log.debug(NettyTcpClient.this.toString() + "接入成功：" + asLongText);
            }
            NettyTcpClient.this.nettyHandler.channelActive(asLongText, ctx);
        }
    };

    /**
     *
     * @param host
     * @param port
     * @param nettyCoder
     * @param nettyHandler
     */
    public NettyTcpClient(String host, int port, NettyCoder nettyCoder, INettyHandler nettyHandler) {
        this.nettyHandler = nettyHandler;
        this.nettyCoder = nettyCoder;
        this.hostname = host;
        this.port = port;

        channelInitializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline
                        //.addLast("Decoder", new NettyDecoder())//NettyCoder.instance.NETTY_DECODER)
                        //.addLast("Encoder", new NettyEncoder())//NettyCoder.instance.NETTY_ENCODER)
                        .addLast("handler", new MyChannelInboundHandler());//simpleChannelInboundHandler);
            }
        };

        bossGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2 * 1000)
                .option(ChannelOption.SO_TIMEOUT, 2 * 1000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(channelInitializer)
                .channel(NioSocketChannel.class);
    }

    /**
     * 返回一个连接，请自行释放对象,如果无法连接，返回 null
     *
     * @return
     */
    public Channel connect() {
        try {
            if (log.isInfoEnabled()) {
                log.info("向" + NettyTcpClient.this.toString() + "服务器请求 Socket 连接");
            }
            ChannelFuture channelFuture = bootstrap.connect(hostname, port);
            return channelFuture.awaitUninterruptibly().channel();
        } catch (Throwable ex) {
            if (log.isDebugEnabled()) {
                log.debug(NettyTcpClient.this.toString() + "连接", ex);
            }
        }
        return null;
    }

    public void close() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        log.info("关闭" + NettyTcpClient.this.toString() + "Socket 连接");
    }

    @Override
    public String toString() {
        return " {NettyTcpClient -> " + hostname + ":" + this.port + "} ";
    }

}
