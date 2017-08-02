package net.sz.framework.nio.tcp;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
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
    default void channelActive(String channelId, ChannelHandlerContext session) {
    }

    /**
     * 断开连接
     *
     * @param channelId id
     * @param session
     */
    default void closeSession(String channelId, ChannelHandlerContext session) {
    }
}
