package net.sz.game.engine.nio.nettys.tcp.IHandler;

import io.netty.channel.ChannelHandlerContext;
import net.sz.game.engine.scripts.IBaseScript;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IBeforeHandler extends IBaseScript {

    /**
     * 在处理消息之前，调用
     *
     * @param ctx 当前消息链接对象
     * @param msgId 当前消息id
     * @param bytebuf 当前包的流
     * @return 如果返回 true 就不再继续处理
     */
    boolean beforeHandler(ChannelHandlerContext ctx, int msgId, byte[] bytebuf);

}
