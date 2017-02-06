package net.sz.game.engine.nio.nettys.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import java.util.List;
import net.sz.game.engine.nio.nettys.NettyPool;
import org.apache.log4j.Logger;

/**
 * 基于 netty 4.0.21 的 netty 服务
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyTcpServer {

    private static final Logger log = Logger.getLogger(NettyTcpServer.class);
    private int port = 9527;
    private String hostname = "0.0.0.0";
    ChannelFuture sync = null;
    //ServerBootstrap是设置服务器的辅助类
    INettyHandler nettyHandler;

    SimpleChannelInboundHandler simpleChannelInboundHandler = new MyChannelInboundHandler2();

    class MyChannelInboundHandler2 extends SimpleChannelInboundHandler<ByteBuf> {

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
            log.debug("内部错误", cause);
//                                            if ("java.lang.IllegalArgumentException".equals(cause.getClass().getName()))
            {
                NettyPool.getInstance().closeSession(ctx, "服务器异常剔除下线：" + cause.getClass().getName());
//                Long sessionAttr = NettyPool.getInstance().getSessionAttr(ctx, NettyPool.SessionKey, Long.class);
//                NettyPool.getInstance().getSessions().remove(sessionAttr);
//                NettyTcpServer.this.nettyHandler.closeSession(ctx);
            }
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
            log.debug("连接断开：" + asLongText);
            NettyPool.getInstance().getSessions().remove(asLongText);
            NettyTcpServer.this.nettyHandler.closeSession(asLongText, ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            String asLongText = ctx.channel().id().asLongText();
            log.debug("连接闲置：" + ctx.channel().id().asLongText());
            NettyTcpServer.this.nettyHandler.channelInactive(asLongText, ctx);
        }

        /**
         * 创建链接后，链接被激活
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            if (sync == null) {
                NettyPool.getInstance().closeSession(ctx, "服务器尚未启动");
            } else {
                String asLongText = ctx.channel().id().asLongText();
                log.debug("新建：" + asLongText);
                NettyCoder.setSessionAttr(ctx, NettyCoder.SessionCreateTime, System.currentTimeMillis());
                NettyCoder.setSessionAttr(ctx, NettyCoder.SessionLastTime, System.currentTimeMillis());
                //NettyPool.getInstance().setSessionAttr(ctx, NettyPool.SessionLoginTime, System.currentTimeMillis());
                NettyPool.getInstance().getSessions().put(asLongText, ctx);
                NettyTcpServer.this.nettyHandler.channelActive(asLongText, ctx);
            }
        }
    };

    public NettyTcpServer(INettyHandler nettyHandler) {
        this.nettyHandler = nettyHandler;
    }

    public NettyTcpServer(int port, INettyHandler nettyHandler) {
        this.port = port;
        this.nettyHandler = nettyHandler;
    }

    public NettyTcpServer(String hostname, int port, INettyHandler nettyHandler) {
        this.hostname = hostname;
        this.port = port;
        this.nettyHandler = nettyHandler;
    }

    /**
     *
     * @param workthread 工作线程 不得小于4,不得大于10
     */
    public void start(int workthread) {
        if (workthread < 4 || workthread > 10) {
            workthread = 4;
        }
        try {
            //NioEventLoopGroup是一个多线程的I/O操作事件循环池(参数是线程数量)
            EventLoopGroup bossGroup = new NioEventLoopGroup(workthread);
            //当有新的连接进来时将会被注册到workerGroup(不提供参数，会使用默认的线程数)
            EventLoopGroup workerGroup = new NioEventLoopGroup(workthread);
            ServerBootstrap bs = new ServerBootstrap();
            //group方法是将上面创建的两个EventLoopGroup实例指定到ServerBootstrap实例中去
            bs.group(bossGroup, workerGroup)
                    //channel方法用来创建通道实例(NioServerSocketChannel类来实例化一个进来的连接)
                    .channel(NioServerSocketChannel.class)
                    //为新连接到服务器的handler分配一个新的channel。ChannelInitializer用来配置新生成的channel。(如需其他的处理，继续ch.pipeline().addLast(新匿名handler对象)即可)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //处理逻辑放到 NettyClientHandler 类中去
                            ch.pipeline()
                                    /* 定义常量，减少内存开销，gc开销 */
                                    .addLast("aggegator", new HttpObjectAggregator(1024 * 32))//定义缓冲数据量
                                    //                                    .addLast("Decoder", new NettyDecoder())//NettyCoder.instance.NETTY_DECODER)
                                    //                                    .addLast("Encoder", new NettyEncoder())//NettyCoder.instance.NETTY_ENCODER)
                                    //.addLast("ping", new IdleStateHandler(10, 10, 10, TimeUnit.SECONDS))
                                    //                                    .addLast("handler", new MyChannelInboundHandler())
                                    .addLast("handler", new MyChannelInboundHandler2());//simpleChannelInboundHandler);
                        }
                    })
                    /* 设置链接检测 */
                    .option(ChannelOption.TCP_NODELAY, true)
                    //使用内存池
                    //.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())
                    //option()方法用于设置监听套接字
                    .option(ChannelOption.SO_BACKLOG, 500)
                    //childOption()方法用于设置和客户端连接的套接字
                    .childOption(ChannelOption.SO_KEEPALIVE, true) /**/;
            // Bind and start to accept incoming connections
            sync = bs.bind(hostname, this.port).awaitUninterruptibly();
            log.info("开启Tcp服务端口 " + hostname + ":" + this.port + " 监听");
        } catch (Exception ex) {
            log.error("开启Tcp服务端口 " + hostname + ":" + this.port + " 监听 失败", ex);
            System.exit(0);
        }
    }

    /**
     *
     * @deprecated 请使用 NettyPool.getInstance().stopServer();
     */
    @Deprecated
    public void stop() {
        if (sync != null) {
            sync.channel().disconnect();
            sync.channel().close();
            sync = null;
        }
    }

}
