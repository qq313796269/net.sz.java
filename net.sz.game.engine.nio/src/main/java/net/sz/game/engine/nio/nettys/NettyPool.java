package net.sz.game.engine.nio.nettys;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.nio.nettys.http.NettyHttpServer;
import net.sz.game.engine.nio.nettys.tcp.CheckNettySocketTimerTask;
import net.sz.game.engine.nio.nettys.tcp.INettyHandler;
import net.sz.game.engine.nio.nettys.tcp.MessageHandler;
import net.sz.game.engine.nio.nettys.tcp.NettyTcpHandler;
import net.sz.game.engine.nio.nettys.tcp.NettyTcpServer;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.thread.ThreadPool;

/**
 * netty 管理器
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyPool {

    private static SzLogger log = SzLogger.getLogger();

    private static final NettyPool instance = new NettyPool();

    public static NettyPool getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<String, NettyHttpServer> httpServerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, NettyTcpServer> tcpServerMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ChannelHandlerContext> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, MessageHandler> handlerMap = new ConcurrentHashMap<>(0);

    public NettyPool() {
        ThreadPool.addTimerTask(ThreadPool.GlobalThread, new CheckNettySocketTimerTask());
    }

    public ConcurrentHashMap<String, ChannelHandlerContext> getSessions() {
        return sessions;
    }

    public NettyHttpServer addBindHttpServer(String hostname, int port) {
        NettyHttpServer httpServer = new NettyHttpServer(hostname, port);
        httpServerMap.put(hostname + "-" + port, httpServer);
        return httpServer;
    }

    public NettyHttpServer getHttpServer(String hostname, int port) {
        return httpServerMap.get(hostname + "-" + port);
    }

    public NettyTcpServer addBindTcpServer(String hostname, int port, INettyHandler nettyHandler) {
        NettyTcpServer nettyTcpServer = new NettyTcpServer(hostname, port, nettyHandler);
        tcpServerMap.put(hostname + "-" + port, nettyTcpServer);
        return nettyTcpServer;
    }

    public void closeSession(ChannelHandlerContext session, String... msgs) {
        if (session != null) {
            if (session.channel() != null) {
                if (log.isInfoEnabled()) {
                    log.info("关闭连接：" + session.channel().id().asLongText() + " -> " + String.join(",", msgs));
                }
            } else if (log.isInfoEnabled()) {
                log.info("关闭连接：-> " + String.join(",", msgs));
            }
            session.close();
        }
    }

    public void closeSession(Channel session, String... msgs) {
        if (session != null) {
            if (log.isInfoEnabled()) {
                log.info("关闭连接：" + session.id().asLongText() + " -> " + String.join(",", msgs));
            }
            session.close();
        } else if (log.isInfoEnabled()) {
            log.info("关闭连接：-> " + String.join(",", msgs));
        }
    }

    /**
     * 停止所有服务，并且关闭所有现有连接
     */
    public void stopServer() {

        HashMap<String, NettyTcpServer> tcpserverMap = new HashMap<>(this.tcpServerMap);
        for (Map.Entry<String, NettyTcpServer> entry : tcpserverMap.entrySet()) {
            String key = entry.getKey();
            NettyTcpServer value = entry.getValue();
            value.stop();
        }

        HashMap<String, NettyHttpServer> httpserverMap = new HashMap<>(this.httpServerMap);

        for (Map.Entry<String, NettyHttpServer> entry : httpserverMap.entrySet()) {
            String key = entry.getKey();
            NettyHttpServer value = entry.getValue();
            value.stop();
        }

        HashMap<String, ChannelHandlerContext> hashMap = new HashMap<>(NettyPool.getInstance().getSessions());
        for (Map.Entry<String, ChannelHandlerContext> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            ChannelHandlerContext value = entry.getValue();
            NettyPool.getInstance().closeSession(value, "关闭服务器");
        }

    }

    /**
     * 获取链接的ip地址
     *
     * @param session
     * @return
     */
    public String getIP(ChannelHandlerContext session) {
        try {
            InetSocketAddress insocket = (InetSocketAddress) session.channel().remoteAddress();
            return insocket.getAddress().getHostAddress().toLowerCase();
        } catch (Throwable e) {
            log.error("获取IP地址失败：：：", e);
        }
        return "";
    }

    /**
     * 获取链接的ip地址
     *
     * @param session
     * @return
     */
    public String getIP(Channel session) {
        try {
            InetSocketAddress insocket = (InetSocketAddress) session.remoteAddress();
            return insocket.getAddress().getHostAddress().toLowerCase();
        } catch (Throwable e) {
        }
        return "";
    }

    public ConcurrentHashMap<Integer, MessageHandler> getHandlerMap() {
        return handlerMap;
    }

    /**
     * 注册消息
     *
     * @param threadId
     * @param messageId
     * @param handler
     * @param builder
     * @param msgQueue 用于分组
     */
    public void register(int messageId, long threadId, NettyTcpHandler handler, com.google.protobuf.Message.Builder builder, int msgQueue) {
        MessageHandler msgold = handlerMap.get(messageId);
        MessageHandler messageHandler = new MessageHandler(messageId, threadId, handler, builder, msgQueue);
        /* TODO 验证线程模型 */

        if (builder == null) {
            log.error("MessagePool.register 异常! messageClass 不能为null!" + messageHandler, new Exception());
            if (ThreadPool.isStarEnd()) {
                return;
            } else {
                System.exit(1);
            }
        }

        if (handler == null) {
            log.error("MessagePool.register 异常! handler 不能为null!" + messageHandler, new Exception());
            if (ThreadPool.isStarEnd()) {
                return;
            } else {
                System.exit(1);
            }
        }

        // TODO 验证线程模型
        if (threadId != 0 && ThreadPool.getThread(threadId) == null) {
            log.error("无法找到线程模型:" + threadId + "对应的处理器.请确保服务器启动时先初始化线程模型对象!" + messageHandler, new Exception());
            if (ThreadPool.isStarEnd()) {
                return;
            } else {
                System.exit(1);
            }
        }
        if (msgold != null) {
            if (!msgold.getHandler().getClass().getName().equals(handler.getClass().getName())) {
                log.error("已注册消息：" + msgold + " 新注册的重复消息：" + messageHandler, new Exception());
                if (ThreadPool.isStarEnd()) {
                    return;
                } else {
                    System.exit(1);
                }
            }
        }
        handlerMap.put(messageId, messageHandler);
        log.error("注册消息：" + messageHandler);
    }
}
