package net.sz.framework.sznio.http;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.TaskModel;


/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class HttpHandlerRun extends TaskModel {

   private static final SzLogger log = SzLogger.getLogger();

    private NioHttpRequest nioHttpRequest;
    private _HttpBindBean httpBindBean;

    public HttpHandlerRun(NioHttpRequest nioSession, _HttpBindBean httpBindBean) {
        this.nioHttpRequest = nioSession;
        this.httpBindBean = httpBindBean;
    }

    @Override
    public void run() {
        httpBindBean.getHandler().run(nioHttpRequest);
    }

}
