package net.sz.framework.struct.thread;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.LongId0;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.MailUtil;
import net.sz.framework.utils.StringUtil;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class BaseThreadRunnable implements Serializable, Cloneable, Runnable, Thread.UncaughtExceptionHandler {

    static private final SzLogger log = SzLogger.getLogger();
    static private final long serialVersionUID = 2829254368845966408L;
    static private final LongId0 THREAD_ID_UTIL = new LongId0();

    static public final ThreadGroup GlobalThreadGroup = new ThreadGroup("Global-ThreadGroup");

    static public final ThreadGroup UnknownThreadGroup = new ThreadGroup("Unknown-ThreadGroup");

    /**
     * 获取当前线程
     *
     * @return
     */
    static public long getCurrentThreadID() {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof BaseThreadModel) {
            return currentThread.getId();
        }
        return 0;
    }

    private long tid;
    private String tName;
    // false标识删除线程
    private volatile boolean runing = true;
    // 表示线程当前是否挂起
    private volatile boolean waiting = false;
    private int maxTaskCount = 20000;
    /* 最后一次发送线程异常邮件时间 */
    private long lastErrorSendMailTime = 0;

    private ThreadType threadType;

    private final ConcurrentHashMap<Integer, BaseThreadModel> threads = new ConcurrentHashMap<>();

    /**
     *
     * @param threadType
     * @param name
     */
    public BaseThreadRunnable(ThreadType threadType, String name) {
        tid = THREAD_ID_UTIL.getId();
        this.threadType = threadType;
        this.tName = name;
    }

    protected void createMyThread(ThreadGroup group, int threadCount) {
        if (threadCount < 1) {
            threadCount = 1;
        }
        for (int i = 1; i <= threadCount; i++) {
            createMyThread(i, tid, group, this, tName);
        }
    }

    /**
     * 创建线程
     *
     * @param myId
     * @param tid
     * @param group
     * @param runnable
     * @param name
     */
    protected abstract void createMyThread(int myId, long tid, ThreadGroup group, Runnable runnable, String name);
//    {
//        BaseThreadModel thread = new BaseThreadModel(group, runnable, name) {
//        };
//        thread.setMyId(myId);
//        thread.setTid(tid);
//        thread.settName(name);
//        thread.setUncaughtExceptionHandler(this);
//        thread.start();
//        threads.put(myId, thread);
//    }

    /**
     * 停止线程,马上终止线程
     */
    public void close() {
        if (this.isRuning()) {
            this.runing = false;
        }
        try {
            BaseThreadModel[] valuesThreads = threads.values().toArray(new BaseThreadModel[0]);
            for (int k = 0; k < valuesThreads.length; k++) {
                BaseThreadModel currentThread = valuesThreads[k];
                currentThread.close();
            }
            threads.clear();
        } catch (Throwable e) {
        }
    }

    /**
     * 线程状态监控<br>
     * 由于自定义线程执行run方法内部一定加了try cache<br>
     * 还是收到这个方法，一定jvm 抛错，线程异常退出，必须保证线程能得到重启
     *
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            String toString = e.toString();
            log.fatal("未捕获的异常", e);
            MailUtil.sendMail("执行线程异常退出 -> 游戏id-" + GlobalUtil.getGameId() + "  平台-" + GlobalUtil.getPlatformId() + "  服务器id-" + GlobalUtil.getServerId(), toString);
            /* 如果有注册，那么通知 */
            if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(e);
            }
            if (t instanceof BaseThreadModel) {
                BaseThreadModel threadModel = (BaseThreadModel) t;
                /*重建线程*/
                createMyThread(threadModel.getMyId(), this.getTid(), t.getThreadGroup(), threadModel, gettName());
            }
            t.interrupt();
        } catch (Throwable throwable) {
            log.fatal("线程状态监控", throwable);
        }
    }

    /**
     *
     * 查看线程堆栈
     */
    public void checkThreadStackTrace() {
        if (isRuning() && !this.isWaiting()) {
            Collection<BaseThreadModel> values = this.getThreads().values();
            StringBuilder buf = new StringBuilder();
            BaseThreadModel[] valuesThreads = values.toArray(new BaseThreadModel[0]);
            for (int k = 0; k < valuesThreads.length; k++) {
                BaseThreadModel currentThread = valuesThreads[k];
                long procc = currentThread.checkThreadStackTrace(buf);
                if (procc > 15000l) {
                    /* 15秒 直接终止当前线程，重建线程 */
                    uncaughtException(currentThread, new Exception("线程卡死"));
                }
            }
            String toString = buf.toString();
            if (!StringUtil.isNullOrEmpty(toString)) {
                log.error(toString);
                if (TimeUtil.currentTimeMillis() - this.getLastErrorSendMailTime() > 5 * 60 * 1000) {
                    this.setLastErrorSendMailTime(TimeUtil.currentTimeMillis());
                    MailUtil.sendMail("线程执行已卡死 -> 游戏id-" + GlobalUtil.getGameId() + "  平台-" + GlobalUtil.getPlatformId() + "  服务器id-" + GlobalUtil.getServerId(), toString);
                }
            }
        }
    }

    /**
     * 重置线程
     *
     * @param tname
     */
    public void reset(String tname) {
        this.tid = THREAD_ID_UTIL.getId();
        this.tName = tname + "-1-" + this.tid;
        for (Map.Entry<Integer, BaseThreadModel> entry : threads.entrySet()) {
            BaseThreadModel value = entry.getValue();
            value.setTid(this.tid);
            value.settName(this.tName);
        }
    }

    /**
     * 执行定时器任务
     */
    public abstract void timerRun();

    /**
     * 增加任务
     *
     * @param task
     * @return
     */
    public abstract Runnable addTask(Runnable task);

    /**
     * 增加定时器任务
     *
     * @param timerTask
     * @return
     */
    public abstract Runnable addTimerTask(Runnable timerTask);

    /**
     * 增加任务,队列线程需要重写
     *
     * @param key
     * @param task
     * @return
     * @deprecated 队列线程需要重写
     */
    @Deprecated
    public Runnable addTask(String key, Runnable task) {
        if (log.isInfoEnabled()) {
            log.info("BaseThreadRunnable", new UnsupportedOperationException("非队列线程"));
        }
        return addTask(task);
    }

    /**
     * 增加定时器任务,队列线程需要重写
     *
     * @param key
     * @param timerTask
     * @return
     * @deprecated 队列线程需要重写
     */
    @Deprecated
    public Runnable addTimerTask(String key, Runnable timerTask) {
        if (log.isInfoEnabled()) {
            log.info("BaseThreadRunnable", new UnsupportedOperationException("非队列线程"));
        }
        return addTimerTask(timerTask);
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public boolean isRuning() {
        return runing;
    }

    public void setRuning(boolean runing) {
        this.runing = runing;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public int getMaxTaskCount() {
        return maxTaskCount;
    }

    public void setMaxTaskCount(int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
    }

    public long getLastErrorSendMailTime() {
        return lastErrorSendMailTime;
    }

    public void setLastErrorSendMailTime(long lastErrorSendMailTime) {
        this.lastErrorSendMailTime = lastErrorSendMailTime;
    }

    public ConcurrentHashMap<Integer, BaseThreadModel> getThreads() {
        return threads;
    }

    public ThreadType getThreadType() {
        return threadType;
    }

    public void setThreadType(ThreadType threadType) {
        this.threadType = threadType;
    }

    @Override
    public String toString() {
        return "线程id：" + tid + " 线程模型：" + tName;
    }
}
