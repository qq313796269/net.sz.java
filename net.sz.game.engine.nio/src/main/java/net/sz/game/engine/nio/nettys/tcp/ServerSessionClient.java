package net.sz.game.engine.nio.nettys.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.TimerTaskEvent;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ServerSessionClient {

    private static final Logger log = Logger.getLogger(ServerSessionClient.class);

    public static void main(String[] args) {
        ServerSessionClient serverSessionClient = new ServerSessionClient("127.0.0.1", 6553, 2, 1, new INettyHandler() {

            @Override
            public void channelActive(String channelId, ChannelHandlerContext session) {

            }

            @Override
            public void channelInactive(String channelId, ChannelHandlerContext session) {

            }

            @Override
            public void closeSession(String channelId, ChannelHandlerContext session) {

            }
        });
    }

    ServerSessionThread serverSessionThread;

    NettyTcpClient nettyTcpClient;

    String host;
    int port;
    INettyHandler iNettyHandler;

    /**
     * 断线会自动从新连接
     *
     * @param host
     * @param port
     * @param channelCount
     * @param iNettyHandler
     */
    public ServerSessionClient(String host, int port, int channelCount, int threadCount, INettyHandler iNettyHandler) {
        this.host = host;
        this.port = port;
        this.iNettyHandler = iNettyHandler;

        serverSessionThread = new ServerSessionThread(threadCount);

        this.host = host;
        this.port = port;

        nettyTcpClient = new NettyTcpClient(new INettyHandler() {

            @Override
            public void channelActive(String channelId, ChannelHandlerContext session) {
                serverSessionThread.addSession(session.channel());
                if (ServerSessionClient.this.iNettyHandler != null) {
                    ServerSessionClient.this.iNettyHandler.channelActive(channelId, session);
                }
            }

            @Override
            public void channelInactive(String channelId, ChannelHandlerContext session) {
                if (ServerSessionClient.this.iNettyHandler != null) {
                    ServerSessionClient.this.iNettyHandler.channelInactive(channelId, session);
                }
            }

            @Override
            public void closeSession(String channelId, ChannelHandlerContext session) {
                serverSessionThread.removeSession(session.channel());
                resetServer();
                if (ServerSessionClient.this.iNettyHandler != null) {
                    ServerSessionClient.this.iNettyHandler.closeSession(channelId, session);
                }
            }

        });

        for (int i = 0; i < channelCount; i++) {
            resetServer();
        }

    }

    void resetServer() {
        log.error("服务器连接失败，3000 秒后。。。。尝试下一次注册");
        ThreadPool.addTimerTask(ThreadPool.GlobalThread, new TimerTaskEvent(1, 3000) {

            @Override
            public void run() {
                Channel connect = nettyTcpClient.connect(host, port);
                if (connect == null) {
                    resetServer();
                }
            }
        });
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
        ByteBuf byteBufFormBytes = NettyCoder.getByteBufFormMessage(message);
        return addMessage(byteBufFormBytes);
    }

    /**
     *
     * @param msgid
     * @param buf
     * @return
     */
    public boolean addMessage(int msgid, byte[] buf) {
        ByteBuf byteBufFormBytes = NettyCoder.getByteBufFormBytes(msgid, buf);
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

}
