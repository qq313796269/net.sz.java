package net.sz.framework.szlog;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LogMsg {

    /*日志信息*/
    private String msg;
    /*错误信息*/
    private Throwable throwable;

    public LogMsg(String msg, Throwable throwable) {
        this.msg = msg;
        this.throwable = throwable;
    }

    public String getMsg() {
        return msg;
    }

    public Throwable getThrowable() {
        return throwable;
    }

}
