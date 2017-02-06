package net.sz.game.engine.nio.nettys.http;

import net.sz.game.engine.nio.nettys.http.handler.IHttpHandler;
import net.sz.game.engine.thread.TaskEvent;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpTask extends TaskEvent {

    private static final Logger log = Logger.getLogger(HttpTask.class);
    private IHttpHandler httpHandler;
    private NioHttpRequest requestMessage;

    public HttpTask(IHttpHandler httpHandler, NioHttpRequest requestMessage) {
        this.httpHandler = httpHandler;
        this.requestMessage = requestMessage;
    }

    public void run() {

        httpHandler.run(requestMessage);
        if (!requestMessage.isRespons) {
            log.error("执行了函数却未执行respons()函数->" + httpHandler.getClass().getName(), new Exception("执行了函数却未执行respons()函数->" + httpHandler.getClass().getName()));
            requestMessage.close();
        }

    }

}
