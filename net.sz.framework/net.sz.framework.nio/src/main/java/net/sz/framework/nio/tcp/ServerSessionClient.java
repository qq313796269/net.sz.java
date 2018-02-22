package net.sz.framework.nio.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.sz.framework.nio.NettyPool;
import net.sz.framework.thread.ExecutorFactory;
import net.sz.framework.thread.timer.TimerTask;
import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ServerSessionClient {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        ServerSessionClient serverSessionClient = new ServerSessionClient("127.0.0.1", 6553, 2, 1, NettyCoder.getDefaultCoder(), new INettyHandler() {

            @Override
            public void channelActive(String channelId, ChannelHandlerContext session) {

            }

            @Override
            public void closeSession(String channelId, ChannelHandlerContext session) {

            }
        });
    }

    NettyCoder nettyCoder;

    ServerSessionThread serverSessionThread;
    NettyTcpClient nettyTcpClient;
    int port = 9527;
    String hostname = "127.0.0.1";
    INettyHandler iNettyHandler;

    boolean reConnect = true;

    /**
     * 断线会自动从新连接
     *
     * @param hostname
     * @param port
     * @param channelCount
     * @param threadCount
     * @param nettyCoder
     * @param iNettyHandler
     */
    public ServerSessionClient(String hostname, int port, int channelCount, int threadCount, NettyCoder nettyCoder, INettyHandler iNettyHandler) {
        this.iNettyHandler = iNettyHandler;
        this.nettyCoder = nettyCoder;
        serverSessionThread = new ServerSessionThread(nettyCoder, threadCount);

        this.hostname = hostname;
        this.port = port;

        nettyTcpClient = new NettyTcpClient(this.hostname, this.port, nettyCoder, new INettyHandler() {

            @Override
            public void channelActive(String channelId, ChannelHandlerContext session) {
                serverSessionThread.addSession(session.channel());
                if (ServerSessionClient.this.iNettyHandler != null) {
                    ServerSessionClient.this.iNettyHandler.channelActive(channelId, session);
                }
            }

            @Override
            public void closeSession(String channelId, ChannelHandlerContext session) {
                serverSessionThread.removeSession(session.channel());
                resetServer(true);
                if (ServerSessionClient.this.iNettyHandler != null) {
                    ServerSessionClient.this.iNettyHandler.closeSession(channelId, session);
                }
            }

        });

        for (int i = 0; i < channelCount; i++) {
            resetServer(false);
        }

    }

    void resetServer(boolean isReset) {
        if (reConnect) {
            TimerTask timerTaskEvent = new TimerTask(1, 3000) {

                @Override
                public void run() {
                    Channel connect = nettyTcpClient.connect();
                    if (connect == null) {
                        resetServer(true);
                    }
                }
            };
            if (isReset) {
                log.error("服务器 " + nettyTcpClient.toString() + " 连接失败，3 秒后。。。。尝试下一次注册");
                ExecutorFactory.DEFAULT_SERVICE.addTimerTask(timerTaskEvent);
            } else {
                ExecutorFactory.DEFAULT_SERVICE.addTask(timerTaskEvent);
            }
        } else if (log.isDebugEnabled()) {
            log.debug("当前服务器链接关闭状态，无须重新链接");
        }
    }

    /**
     *
     * @param message
     * @return
     */
    public boolean addMessage(com.google.protobuf.Message.Builder message) {
        return addMessage(message.build());
    }

    /**
     *
     * @param message
     * @return
     */
    public boolean addMessage(com.google.protobuf.Message message) {
        ByteBuf byteBufFormBytes = nettyCoder.getByteBufFormMessage(message);
        return addMessage(byteBufFormBytes);
    }

    /**
     *
     * @param msgid
     * @param buf
     * @return
     */
    public boolean addMessage(int msgid, byte[] buf) {
        ByteBuf byteBufFormBytes = nettyCoder.getByteBufFormBytes(msgid, buf);
        return addMessage(byteBufFormBytes);
    }

    /**
     *
     * @param byteBuf
     * @return
     */
    public boolean addMessage(ByteBuf byteBuf) {
        return this.serverSessionThread.addMessage(byteBuf);
    }

    public void close() {
        this.reConnect = false;

        Channel[] toArray = this.serverSessionThread.channels.toArray(new Channel[0]);

        for (Channel channel : toArray) {
            NettyPool.getInstance().closeSession(channel, "关闭服务器链接");
        }

    }

}
