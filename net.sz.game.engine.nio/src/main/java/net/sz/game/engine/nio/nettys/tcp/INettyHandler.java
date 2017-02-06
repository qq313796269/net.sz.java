package net.sz.game.engine.nio.nettys.tcp;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface INettyHandler {

    /**
     * 创建链接后，链接被激活
     *
     * @param channelId id
     * @param session
     */
    void channelActive(String channelId, ChannelHandlerContext session);

    /**
     * 连接闲置状态
     *
     * @param channelId id
     * @param session
     */
    void channelInactive(String channelId, ChannelHandlerContext session);

    /**
     * 断开连接
     *
     * @param channelId id
     * @param session
     */
    void closeSession(String channelId, ChannelHandlerContext session);
}
