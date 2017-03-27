package net.sz.test;

import net.sz.game.engine.nio.nettys.NettyPool;
import net.sz.game.engine.nio.nettys.http.NettyHttpServer;
import net.sz.game.engine.nio.nettys.http.NioHttpRequest;
import net.sz.game.engine.nio.nettys.http.handler.IHttpHandler;
import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws CloneNotSupportedException {
        NettyHttpServer addBindHttpServer = NettyPool.getInstance().addBindHttpServer("127.0.1", 9527);
        addBindHttpServer.addHttpBind(new IHttpHandler() {
            @Override
            public void run(String url, NioHttpRequest request) {
                request.addContent("<html><body>login holle ！</body></html>");
            }
        }, "*");
        addBindHttpServer.start(4);
    }
}
