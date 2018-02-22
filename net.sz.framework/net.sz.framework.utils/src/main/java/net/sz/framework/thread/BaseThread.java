package net.sz.framework.thread;

import java.io.Closeable;
import java.io.Serializable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.MailUtil;
import net.sz.framework.utils.StringUtil;
import net.sz.framework.utils.TimeUtil;

/**
 * 线程模型，这个线程是可以重启，继承覆盖
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class BaseThread extends Thread implements Serializable, Cloneable, Closeable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -7973669890204165191L;

    /* 自定义线程id ，全局唯一id*/
    private long threadId;
    /* 线程的自定义id */
    private int myId;
    /* 最后一次发送线程异常邮件时间 */
    private long lastErrorSendMailTime = 0;
    /* false 表示删除线程 */
    private volatile boolean runing = true;
    /* 最后一次执行的任务 */
    private Runnable lastCommand;
    /* 开始执行任务的时间 */
    private volatile long lastCommandExecuteTime = 0;

    public BaseThread() {
    }

    public BaseThread(Runnable target) {
        super(target);
    }

    public BaseThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public BaseThread(String name) {
        super(name);
    }

    public BaseThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public BaseThread(Runnable target, String name) {
        super(target, name);
    }

    public BaseThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    /**
     * 关闭线程
     */
    @Override
    public void close() {
        try {
            /*终止线程并且等待*/
            this.stop();
            log.error("thread close ");
        } catch (Throwable e) {
            log.error("thread close error", e);
        }
    }

    /**
     * 查看线程堆栈
     *
     * @return
     */
    public long checkThreadStackTrace() {
        StringBuilder buf = new StringBuilder();
        long checkThreadStackTrace = this.checkThreadStackTrace(buf);
        String toString = buf.toString();
        if (!StringUtil.isNullOrEmpty(toString)) {
            log.error(toString);
            if (TimeUtil.currentTimeMillis() - this.lastErrorSendMailTime > 5 * 60 * 1000) {
                this.lastErrorSendMailTime = TimeUtil.currentTimeMillis();
                MailUtil.sendMail("线程执行已卡死 -> 游戏id-" + GlobalUtil.getGameId() + "  平台-" + GlobalUtil.getPlatformId() + "  服务器id-" + GlobalUtil.getServerId(), toString);
            }
        }
        return checkThreadStackTrace;
    }

    /**
     * 查看线程堆栈
     *
     * @param buf
     * @return
     */
    protected long checkThreadStackTrace(StringBuilder buf) {
        long procc = 0;
        if (!isInterrupted()) {
            /*如果线程卡住，锁住，暂停，*/
            if (this.getState() == Thread.State.BLOCKED
                    || this.getState() == Thread.State.TIMED_WAITING
                    || this.getState() == Thread.State.WAITING) {
                procc = TimeUtil.currentTimeMillis() - this.getLastCommandExecuteTime();
                if (procc > 5000 && procc < 864000000L) {
                    /*小于10天//因为多线程操作时间可能不准确*/
                    buf.append("线程[")
                            .append(this.getName())
                            .append("]状态 -> ")
                            .append(this.getState())
                            .append("可能已卡死 -> ")
                            .append(procc / 1000f)
                            .append(" s\n    ")
                            .append("执行任务：")
                            .append(this.getLastCommand().getClass().getName());
                    try {
                        StackTraceElement[] elements = this.getStackTrace();
                        for (int i = 0; i < elements.length; i++) {
                            buf.append("\n    ")
                                    .append(elements[i].getClassName())
                                    .append(".")
                                    .append(elements[i].getMethodName())
                                    .append("(").append(elements[i].getFileName())
                                    .append(";")
                                    .append(elements[i].getLineNumber()).append(")");
                        }
                    } catch (Throwable e) {
                        buf.append(e);
                    }
                    buf.append("\n++++++++++++++++++++++++++++++++++");
                } else {
                    procc = 0;
                }
            }
        }
        return procc;
    }

    /**
     * 自定义线程id ，全局唯一id
     *
     * @return
     */
    @Override
    public long getId() {
        return getThreadId();
    }

    /**
     * 自定义线程id ，全局唯一id
     *
     * @return
     */
    public long getThreadId() {
        return threadId;
    }

    /**
     * 自定义线程id ，全局唯一id
     *
     * @param threadId
     */
    @Deprecated
    public final void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    /**
     * 自定义小id
     *
     * @return
     */
    public int getMyId() {
        return myId;
    }

    /**
     *
     * @param myId
     * @deprecated
     */
    @Deprecated
    public final void setMyId(int myId) {
        this.myId = myId;
    }

    /**
     * 最后一次发送线程异常时间
     *
     * @return
     */
    public long getLastErrorSendMailTime() {
        return lastErrorSendMailTime;
    }

    public void setLastErrorSendMailTime(long lastErrorSendMailTime) {
        this.lastErrorSendMailTime = lastErrorSendMailTime;
    }

    /**
     * 是否在运行状态，false 表示需要删除
     *
     * @return
     */
    public boolean isRuning() {
        return runing;
    }

    public void setRuning(boolean runing) {
        this.runing = runing;
    }

    /**
     * 最后执行的任务
     *
     * @return
     */
    public Runnable getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(Runnable lastCommand) {
        this.lastCommand = lastCommand;
    }

    /**
     * 最后执行任务的时间
     *
     * @return
     */
    public long getLastCommandExecuteTime() {
        return lastCommandExecuteTime;
    }

    public void setLastCommandExecuteTime(long lastCommandExecuteTime) {
        this.lastCommandExecuteTime = lastCommandExecuteTime;
    }

    @Override
    public String toString() {
        return "{" + "tid=" + threadId + ", myId=" + myId + ", tName=" + getName() + ", runing=" + runing + '}';
    }
}
