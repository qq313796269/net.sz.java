package net.sz.game.engine.nio.nettys.http.handler;

import net.sz.game.engine.nio.nettys.http.NioHttpRequest;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IHttpHandler {

    /**
     *
     * @param requestMessage 内容
     */
    void run(NioHttpRequest requestMessage);

}
