package net.sz.framework.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.nio.http.NettyHttpServer;
import net.sz.framework.nio.tcp.CheckNettySocketTimerTask;
import net.sz.framework.nio.tcp.INettyHandler;
import net.sz.framework.nio.tcp.MessageHandler;
import net.sz.framework.nio.tcp.NettyCoder;
import net.sz.framework.nio.tcp.NettyTcpHandler;
import net.sz.framework.nio.tcp.NettyTcpServer;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.util.concurrent.ConcurrentHashSet;
import net.sz.framework.utils.GlobalUtil;

/**
 * netty 管理器
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public final class NettyPool {

    private static final SzLogger log = SzLogger.getLogger();

    private static final NettyPool instance = new NettyPool();

    public static NettyPool getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<String, NettyHttpServer> httpServerMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, NettyTcpServer> tcpServerMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ChannelHandlerContext> sessions = new ConcurrentHashMap<>();

    /**
     * ip黑名单，坚决不允许访问，
     */
    protected ConcurrentHashSet<String> blackIPSet = new ConcurrentHashSet<>();
    /**
     * ip白名单，如果存在此值，那么访问ip必须是列表里面的值
     */
    protected ConcurrentHashSet<String> whiteIPSet = new ConcurrentHashSet<>();

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

    public ConcurrentHashSet<String> getBlackIPSet() {
        return blackIPSet;
    }

    public ConcurrentHashSet<String> getWhiteIPSet() {
        return whiteIPSet;
    }

    private NettyPool() {
        ThreadPool.GlobalThread.addTimerTask(new CheckNettySocketTimerTask());
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

    public NettyTcpServer addBindTcpServer(String hostname, int port, NettyCoder nettyCoder, INettyHandler nettyHandler) {
        NettyTcpServer nettyTcpServer = new NettyTcpServer(hostname, port, nettyCoder, nettyHandler);
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
            value.close();
        }

        HashMap<String, NettyHttpServer> httpserverMap = new HashMap<>(this.httpServerMap);

        for (Map.Entry<String, NettyHttpServer> entry : httpserverMap.entrySet()) {
            String key = entry.getKey();
            NettyHttpServer value = entry.getValue();
            value.close();
        }

        HashMap<String, ChannelHandlerContext> hashMap = new HashMap<>(NettyPool.getInstance().getSessions());
        for (Map.Entry<String, ChannelHandlerContext> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            ChannelHandlerContext value = entry.getValue();
            closeSession(value, "关闭服务器");
        }

    }
}
