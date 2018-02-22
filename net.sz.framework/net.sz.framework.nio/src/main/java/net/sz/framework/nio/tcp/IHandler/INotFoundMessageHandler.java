package net.sz.framework.nio.tcp.IHandler;

import io.netty.channel.ChannelHandlerContext;
import net.sz.framework.scripts.IInitBaseScript;

/**
 * 在未找到当前消息处理器是调用
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface INotFoundMessageHandler extends IInitBaseScript {

    /**
     * 未注册的消息调用
     *
     * @param ctx
     * @param msgId
     * @param bytebuf
     */
    void notFoundHandler(ChannelHandlerContext ctx, int msgId, byte[] bytebuf);

}
