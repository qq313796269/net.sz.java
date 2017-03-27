package net.sz.game.engine.nio.nettys.http;

import net.sz.game.engine.nio.nettys.http.handler.IHttpHandler;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.thread.TaskModel;
import net.sz.game.engine.thread.ThreadPool;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpTask extends TaskModel {

    private static SzLogger log = SzLogger.getLogger();
    private IHttpHandler httpHandler;
    private NioHttpRequest requestMessage;

    public HttpTask(IHttpHandler httpHandler, NioHttpRequest requestMessage) {
        this.httpHandler = httpHandler;
        this.requestMessage = requestMessage;
    }

    public void run() {
        long bigen = System.currentTimeMillis();
        httpHandler.run(requestMessage.getUrl(), requestMessage);
        if (!requestMessage.isRespons) {
            log.error("未执行 respons() 函数->" + httpHandler.getClass().getName());
            if (requestMessage.builder == null || requestMessage.builder.length() < 1) {
                requestMessage.close("未执行 respons() 函数");
            } else {
                requestMessage.respons();
            }
        }
        long timeL1 = System.currentTimeMillis() - bigen;
        if (timeL1 > 20) {
            String name = "任务：" + httpHandler.getClass().getName() + " 执行耗时：" + timeL1;
            /*如果是数据库任务不超过30秒不提示*/
            if (timeL1 > 30000) {
                if (ThreadPool.NoticeThreadException_All != null) {
                    ThreadPool.NoticeThreadException_All.noticeThreadException(new UnsupportedOperationException(name));
                }
            } else {
                log.error(name);
            }
        }
    }
}
