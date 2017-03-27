package net.sz.test.csharp;

import io.netty.channel.ChannelHandlerContext;
import net.sz.game.engine.nio.nettys.NettyPool;
import net.sz.game.engine.nio.nettys.tcp.INettyHandler;
import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CsharpTest {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        NettyPool.getInstance().addBindTcpServer("127.0.0.1", 9527, new INettyHandler() {
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
}
