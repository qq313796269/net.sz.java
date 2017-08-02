package net.sz.framework.struct.thread;

import java.io.Serializable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.MailUtil;
import net.sz.framework.utils.StringUtil;
import net.sz.framework.utils.TimeUtil;

/**
 * 线程基础模型
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class BaseThreadModel extends Thread implements Serializable, Cloneable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 2086301211046020566L;

    private static NoticeThreadException noticeThreadExceptionAll = null;

    /**
     * 全局线程异常回调函数
     *
     * @return
     */
    public static NoticeThreadException getNoticeThreadExceptionAll() {
        return noticeThreadExceptionAll;
    }

    /**
     * 全局线程异常回调函数
     *
     * @param noticeThreadExceptionAll
     */
    public static void setNoticeThreadExceptionAll(NoticeThreadException noticeThreadExceptionAll) {
        BaseThreadModel.noticeThreadExceptionAll = noticeThreadExceptionAll;
    }

    /* 自定义线程id ，全局唯一id*/
    private long tid;
    /* 线程的自定义id */
    private int myId;
    /* 自定义线程名称 */
    private String tName;
    /* 最后一次发送线程异常邮件时间 */
    private long lastErrorSendMailTime = 0;
    /* false 表示删除线程 */
    private volatile boolean runing = true;
    /* 表示线程当前是否挂起 */
    private volatile boolean waiting = false;
    /* 线程处理任务的最大数量 */
    private int maxTaskCount = 20000;
    /* 最后一次执行的任务 */
    private Runnable lastCommand;
    /* 开始执行任务的时间 */
    private volatile long lastCommandExecuteTime = 0;

    public BaseThreadModel() {
    }

    public BaseThreadModel(Runnable target) {
        super(target);
    }

    public BaseThreadModel(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public BaseThreadModel(String name) {
        super(name);
    }

    public BaseThreadModel(ThreadGroup group, String name) {
        super(group, name);
    }

    public BaseThreadModel(Runnable target, String name) {
        super(target, name);
    }

    public BaseThreadModel(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public BaseThreadModel(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    /**
     * 关闭线程
     */
    public void close() {
        try {
            /*终止线程并且等待*/
            this.interrupt();
        } catch (Throwable e) {
            log.error("终止线程", e);
        }
        try {
            /*终止线程并且等待*/
            this.join();
        } catch (Throwable e) {
            log.error("终止线程", e);
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
        if (isRuning() && !this.isWaiting()) {
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
        return getTid();
    }

    /**
     * 自定义线程id ，全局唯一id
     *
     * @return
     */
    public long getTid() {
        return tid;
    }

    /**
     * 自定义线程id ，全局唯一id
     *
     * @param tid
     */
    @Deprecated
    public final void setTid(long tid) {
        this.tid = tid;
    }

    /**
     * 自定义小id
     *
     * @return
     */
    public int getMyId() {
        return myId;
    }

    public final void setMyId(int myId) {
        this.myId = myId;
    }

    /**
     * 自定义名称
     *
     * @return
     */
    public String gettName() {
        return tName;
    }

    public final void settName(String tName) {
        this.tName = tName;
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
     * 是否在暂停状态
     *
     * @return
     */
    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    /**
     * 执行任务数量
     *
     * @return
     */
    public int getMaxTaskCount() {
        return maxTaskCount;
    }

    public void setMaxTaskCount(int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
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
        return "BaseThread{" + "tid=" + tid + ", myId=" + myId + ", tName=" + tName + ", runing=" + runing + ", waiting=" + waiting + ", maxTaskCount=" + maxTaskCount + '}';
    }

}
