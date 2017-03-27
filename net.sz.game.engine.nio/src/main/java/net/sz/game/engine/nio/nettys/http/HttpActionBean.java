package net.sz.game.engine.nio.nettys.http;

import net.sz.game.engine.nio.nettys.http.handler.IHttpHandler;

/**
 * http监听
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpActionBean {

    private IHttpHandler httpHandler;
    private long threadId;

    public HttpActionBean(IHttpHandler httpHandler, long threadId) {
        this.httpHandler = httpHandler;
        this.threadId = threadId;
    }

    public IHttpHandler getHttpHandler() {
        return httpHandler;
    }

    public void setHttpHandler(IHttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

}
