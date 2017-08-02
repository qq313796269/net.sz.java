package net.sz.framework.sznio.http;

import net.sz.framework.szlog.SzLogger;


/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class _HttpBindBean {

    private static final SzLogger log = SzLogger.getLogger();
    private INioHttpHandler handler;
    private long threadId;

    public _HttpBindBean() {
    }

    public _HttpBindBean(INioHttpHandler handler, long threadId) {
        this.handler = handler;
        this.threadId = threadId;
    }

    public INioHttpHandler getHandler() {
        return handler;
    }

    public void setHandler(INioHttpHandler handler) {
        this.handler = handler;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

}
