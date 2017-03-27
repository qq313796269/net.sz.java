package net.sz.game.engine.nio.nettys.tcp;

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
import net.sz.game.engine.nio.nettys.NettyPool;
import net.sz.game.engine.szlog.SzLogger;

/**
 * 客户端连接管理器
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyTcpClient {

    private static SzLogger log = SzLogger.getLogger();

    private Bootstrap bootstrap;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workGroup = null;
    private INettyHandler nettyHandler;
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
            List<Object> decode = NettyCoder.decode(ctx, byteBuf);

            NettyCoder.setSessionAttr(ctx, NettyCoder.SessionLastTime, System.currentTimeMillis());

            for (int i = 0; i < decode.size(); i++) {
                NettyMessageBean msg = (NettyMessageBean) decode.get(i);
                NettyCoder.actionMessage(ctx, msg.getMsgid(), msg.getMsgbuffer());
            }
        }

        /**
         * 发现异常
         *
         * @param ctx
         * @param cause
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("通信内部异常", cause);
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
                log.debug("连接断开：" + asLongText);
            }
            NettyPool.getInstance().getSessions().remove(asLongText);
            NettyTcpClient.this.nettyHandler.closeSession(asLongText, ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            String asLongText = ctx.channel().id().asLongText();
            if (log.isDebugEnabled()) {
                log.debug("连接闲置：" + asLongText);
            }
            NettyTcpClient.this.nettyHandler.channelInactive(asLongText, ctx);
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
            NettyCoder.setSessionAttr(ctx, NettyCoder.SessionCreateTime, System.currentTimeMillis());
            NettyCoder.setSessionAttr(ctx, NettyCoder.SessionLastTime, System.currentTimeMillis());
            NettyPool.getInstance().getSessions().put(asLongText, ctx);
            if (log.isDebugEnabled()) {
                log.debug("新建：" + asLongText);
            }
            NettyTcpClient.this.nettyHandler.channelActive(asLongText, ctx);
        }
    };

    public NettyTcpClient(INettyHandler nettyHandler) {
        this.nettyHandler = nettyHandler;
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
     * @param host
     * @param port
     * @return
     */
    public Channel connect(String host, int port) {
        try {
            if (log.isInfoEnabled()) {
                log.info("向 Host=" + host + ", Port=" + port + " 服务器请求 Socket 连接");
            }
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            return channelFuture.awaitUninterruptibly().channel();
        } catch (Throwable ex) {
            if (log.isDebugEnabled()) {
                log.debug("连接", ex);
            }
        }
        return null;
    }

    public void close() {
        bossGroup.shutdownGracefully();
    }

}
