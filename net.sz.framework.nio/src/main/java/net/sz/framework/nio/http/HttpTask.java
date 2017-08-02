package net.sz.framework.nio.http;

import net.sz.framework.nio.http.handler.IHttpHandler;
import net.sz.framework.struct.thread.BaseThreadModel;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.TaskModel;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpTask extends TaskModel {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 4058803644259899899L;

    private IHttpHandler httpHandler;
    private NioHttpRequest requestMessage;

    public HttpTask(IHttpHandler httpHandler, NioHttpRequest requestMessage) {
        this.httpHandler = httpHandler;
        this.requestMessage = requestMessage;
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
                if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                    BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(new UnsupportedOperationException(name));
                }
            }
        }
    }
}
