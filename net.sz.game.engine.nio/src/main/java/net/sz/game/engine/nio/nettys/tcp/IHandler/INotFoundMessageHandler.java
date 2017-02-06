package net.sz.game.engine.nio.nettys.tcp.IHandler;

import io.netty.channel.ChannelHandlerContext;
import net.sz.game.engine.scripts.IBaseScript;

/**
 * 在未找到当前消息处理器是调用
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface INotFoundMessageHandler extends IBaseScript {

    /**
     * 未注册的消息调用
     *
     * @param ctx
     * @param msgId
     * @param bytebuf
     * @return
     */
    boolean notFoundHandler(ChannelHandlerContext ctx, int msgId, byte[] bytebuf);

}
