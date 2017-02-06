package net.sz.game.engine.nio.nettys.http;

import net.sz.game.engine.nio.nettys.http.handler.IHttpHandler;
import net.sz.game.engine.thread.ThreadPool;

/**
 * http监听
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class HttpActionBean {

    private String bindurl;
    private IHttpHandler httpHandler;
    private long threadId;

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Http ThreadGroup");

    public HttpActionBean(String hostname, String bindurl, IHttpHandler httpHandler, int threadCount) {
        this.bindurl = bindurl;
        this.httpHandler = httpHandler;
        this.threadId = ThreadPool.addThread(THREAD_GROUP, hostname + "/" + bindurl, threadCount);
    }

    public String getBindurl() {
        return bindurl;
    }

    public void setBindurl(String bindurl) {
        this.bindurl = bindurl;
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
