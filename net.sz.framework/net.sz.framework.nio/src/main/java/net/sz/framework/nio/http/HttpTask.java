package net.sz.framework.nio.http;

import net.sz.framework.nio.http.handler.IHttpHandler;
import net.sz.framework.thread.BaseTask;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.thread.ExecutorFactory;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpTask extends BaseTask {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 4058803644259899899L;

    private IHttpHandler httpHandler;
    private NioHttpRequest requestMessage;

    public static final HttpTask DEFAULT_TASK = new HttpTask(null, null);

    private HttpTask() {
    }

    private HttpTask(IHttpHandler httpHandler, NioHttpRequest requestMessage) {
        this.httpHandler = httpHandler;
        this.requestMessage = requestMessage;
    }

    public void setHttpTask(IHttpHandler httpHandler, NioHttpRequest requestMessage) {
        this.httpHandler = httpHandler;
        this.requestMessage = requestMessage;
    }

    @Override
    public HttpTask clone() {
        HttpTask clone = null;
        try {
            clone = (HttpTask) super.clone();
            clone.httpHandler = null;
            clone.requestMessage = null;
        } catch (Exception e) {
        }
        return clone;
    }

    @Override
    public void run() {
        long bigen = TimeUtil.currentTimeMillis();
        try {
            httpHandler.run(requestMessage.getUrl(), requestMessage);
        } catch (Throwable e) {
            log.error("处理http协议未捕获异常：" + httpHandler.getClass().getName(), e);
            requestMessage.close("505 内部错误");
        }
        if (!requestMessage.responsOver) {
            if (log.isInfoEnabled()) {
                log.info("未执行 respons() 函数-> " + httpHandler.getClass().getName());
            }
            if (requestMessage.builder == null || requestMessage.builder.length() < 1) {
                requestMessage.close("505 内部错误");
            } else {
                requestMessage.respons();
            }
        }
        long timeL1 = TimeUtil.currentTimeMillis() - bigen;
        if (timeL1 > 20) {
            String name = "任务：" + httpHandler.getClass().getName() + " 执行耗时：" + timeL1;
            /*如果是数据库任务不超过30秒不提示*/
            if (timeL1 > 30000) {
                ExecutorFactory.noticeThreadException(new UnsupportedOperationException(name));
            }
        }
    }
}
