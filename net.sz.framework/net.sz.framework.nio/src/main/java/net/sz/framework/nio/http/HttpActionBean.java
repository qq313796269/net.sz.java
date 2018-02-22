package net.sz.framework.nio.http;

import net.sz.framework.nio.http.handler.IHttpHandler;
import net.sz.framework.thread.ServerExecutor;
import net.sz.framework.thread.ServerExecutorQueue;

/**
 * http监听
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpActionBean {

    private IHttpHandler httpHandler;
    private ServerExecutorQueue serverExecutorQueue;

    public HttpActionBean(IHttpHandler httpHandler, ServerExecutorQueue serverExecutorQueue) {
        this.httpHandler = httpHandler;
        this.serverExecutorQueue = serverExecutorQueue;
    }

    public IHttpHandler getHttpHandler() {
        return httpHandler;
    }

    public void setHttpHandler(IHttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public ServerExecutorQueue getServerExecutorQueue() {
        return serverExecutorQueue;
    }

    public void setServerExecutorQueue(ServerExecutorQueue serverExecutorQueue) {
        this.serverExecutorQueue = serverExecutorQueue;
    }

}
