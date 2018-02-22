package net.sz.framework.szlog;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LogMsg implements Cloneable {

    private static final LogMsg _LogMsg = new LogMsg(null, null);

    public static LogMsg copyMsg(String msg, Throwable throwable) {
        LogMsg clone = _LogMsg.clone();
        clone.msg = msg;
        clone.throwable = throwable;
        return clone;
    }

    /*日志信息*/
    private String msg;
    /*错误信息*/
    private Throwable throwable;

    private LogMsg(String msg, Throwable throwable) {
        this.msg = msg;
        this.throwable = throwable;
    }

    public String getMsg() {
        return msg;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    protected LogMsg clone() {
        try {
            Object clone = super.clone();
            return (LogMsg) clone;
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

}
