package net.sz.framework.thread;

import java.io.Closeable;
import java.io.Serializable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.LongId0;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.MailUtil;

/**
 * 任务队列执行器
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class BaseExecutor implements Serializable, Cloneable, Closeable, Runnable, Thread.UncaughtExceptionHandler {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 8037355950251100140L;

    private static final LongId0 THREAD_ID_UTIL = new LongId0();

    private volatile boolean closed = false;
    /**
     * 定义线程id
     */
    private final long threadId;
    /*定义线程类型*/
    private final ExecutorType threadType;
    /**/
    private final ExecutorKey executorKey;
    /**
     * 检测线程卡死时间
     */
    private long checkThreadBlocking = 15000l;

    /*执行线程模型*/
    private BaseThread[] threads = null;

    /**
     *
     * @param threadType
     * @param threadGroup
     * @param executorKey
     */
    public BaseExecutor(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup) {
        this(threadType, executorKey, threadGroup, 1, Integer.MAX_VALUE);
    }

    /**
     *
     * @param threadType
     * @param executorKey
     * @param threadGroup
     * @param threadCount 1 ~ 50
     * @param capacity 允许最大的堆积量
     */
    public BaseExecutor(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        this.threadId = THREAD_ID_UTIL.getId();
        this.threadType = threadType;
        this.executorKey = executorKey;

        if (threadCount < 1) {
            throw new UnsupportedOperationException("threadCount < 1");
        }

        if (threadCount > 20) {
            throw new UnsupportedOperationException("threadCount > 50");
        }

        this.threads = new BaseThread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            createMyThread(i, threadId, threadGroup, this, executorKey.getKey());
        }
    }

    /**
     *
     * @param myId
     * @param tid
     * @param group
     * @param runnable
     * @param tname
     */
    private void createMyThread(int myId, long tid, ThreadGroup group, Runnable runnable, Serializable tname) {
        BaseThread baseThread = new BaseThread(group, runnable, tname.toString());
        baseThread.setMyId(myId);
        baseThread.setThreadId(tid);
        baseThread.setUncaughtExceptionHandler(this);
        baseThread.start();
        this.threads[myId] = baseThread;
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
            if (ExecutorFactory.getNoticeThreadExceptionAll() != null) {
                ExecutorFactory.getNoticeThreadExceptionAll().noticeThreadException(e);
            }
            if (t instanceof BaseThread) {
                BaseThread thread = (BaseThread) t;
                /*重建线程*/
                createMyThread(thread.getMyId(), thread.getId(), thread.getThreadGroup(), thread, thread.getName());
                log.error("重建线程：" + thread.toString());
            }
            t.stop();
        } catch (Throwable throwable) {
            log.fatal("线程状态监控", throwable);
        }
    }

    /**
     *
     * 查看线程堆栈
     */
    public void checkThreadStackTrace() {
        for (int k = 0; k < threads.length; k++) {
            BaseThread currentThread = threads[k];
            long procc = currentThread.checkThreadStackTrace();
            if (procc > 15000l) {
                /* 15秒 直接终止当前线程，重建线程 */
                uncaughtException(currentThread, new Exception("线程卡死"));
            }
        }
    }

    /**
     *
     */
    @Override
    public void close() {
        closed = true;
        for (int k = 0; k < threads.length; k++) {
            threads[k].close();
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public ExecutorType getThreadType() {
        return threadType;
    }

    public ExecutorKey getExecutorKey() {
        return executorKey;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * 此时间线程卡死重启
     *
     * @return
     */
    public long getCheckThreadBlocking() {
        return checkThreadBlocking;
    }

    /**
     * 设置线程卡死重启时间
     *
     * @param checkThreadBlocking
     */
    public void setCheckThreadBlocking(long checkThreadBlocking) {
        this.checkThreadBlocking = checkThreadBlocking;
    }

    protected BaseThread[] getThreads() {
        return threads;
    }

    @Override
    public String toString() {
        return "{" + "threadId=" + threadId + ", threadType=" + threadType + ", executorKey=" + executorKey + '}';
    }

}
