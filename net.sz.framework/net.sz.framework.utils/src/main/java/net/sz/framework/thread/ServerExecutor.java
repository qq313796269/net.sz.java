package net.sz.framework.thread;

import java.io.Closeable;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 * 任务队列执行器
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ServerExecutor extends BaseExecutor implements Serializable, Cloneable, Closeable, Runnable, Thread.UncaughtExceptionHandler {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 8037355950251100140L;

    /*当期任务队列*/
    private final BlockingQueue<BaseTask> tasksQueue = new LinkedBlockingQueue<>();

    /**
     *
     * @param threadType
     * @param threadGroup
     * @param executorKey
     */
    public ServerExecutor(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup) {
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
    public ServerExecutor(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        super(threadType, executorKey, threadGroup, threadCount, capacity);
    }

    /**
     * 把任务添加到队列结尾处,并发执行
     *
     * @param task
     * @return
     */
    public boolean addTask(BaseTask task) {
        try {
            return this.tasksQueue.add(task);
        } catch (Throwable e) {
            log.error(this.toString() + " 线程任务数量超过： error", e);
            /* 如果有注册，那么通知 */
            if (ExecutorFactory.getNoticeThreadExceptionAll() != null) {
                ExecutorFactory.getNoticeThreadExceptionAll().noticeThreadException(e);
            }
        }
        return false;
    }

    /**
     *
     */
    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof BaseThread) {
            BaseThread baseThread = (BaseThread) currentThread;
            while (!currentThread.isInterrupted()) {
                try {
                    try {
                        if (this.tasksQueue == null) {
                            synchronized (this) {
                                wait(2000);
                            }
                        }
                        BaseTask task = this.tasksQueue.take();
                        runTask(baseThread, task);
                    } catch (Throwable throwable) {
                        log.error(this.toString(), throwable);
                    }
                    //情况任务记录
                    baseThread.setLastCommand(null);
                    baseThread.setLastCommandExecuteTime(0);
                } catch (Throwable e) {
                    /* 避免重复执行，错误并没有再次执行 */
                    log.error("工人<“" + baseThread.getName() + "”> 执行任务<> 错误回调函数遇到错误: ", e);
                }
            }
        }
        log.error("线程退出，销毁", new Exception(currentThread.toString()));
    }

    protected void runTask(BaseThread baseThread, BaseTask task) {
        runTask(baseThread, null, task);
    }

    protected void runTask(BaseThread baseThread, ExecutorKey executorKey, BaseTask task) {
        if (task != null && !task.isCancel()) {
            String lastCommandName = task.getClass().getName();
            baseThread.setLastCommand(task);
            baseThread.setLastCommandExecuteTime(TimeUtil.currentTimeMillis());
            try {
                task.run();
                task.onSuccessCallBack();
            } catch (Throwable throwable) {
                try {
                    task.onErrorCallBack(throwable);
                } catch (Throwable e1) {
                    ExecutorFactory.noticeThreadException(e1);
                    /* 避免重复执行，错误并没有再次执行 */
                    log.error("工人<“" + baseThread.getName() + "”> 执行任务<" + lastCommandName + "> 错误回调函数遇到错误: ", e1);
                }
                ExecutorFactory.noticeThreadException(throwable);
                /* 避免重复执行，错误并没有再次执行 */
                log.error("工人<“" + baseThread.getName() + "”> 执行任务<" + lastCommandName + "> 遇到错误: ", throwable);
            }
            /**/
            task.setRunEnd(true);
            /*执行结束*/
            task.anotifyEndAll();
            /**/
            runTaskEnd(task);
            long endTimer = System.currentTimeMillis() - baseThread.getLastCommandExecuteTime();
            if (endTimer > 5) {
                log.error(baseThread.toString() + " 执行任务 (" + task.getClass().getName() + ") 耗时：" + endTimer + " 剩余任务了量：" + this.taskSize());
            }
        }
    }

    /**
     * 单次执行任务完成后调用
     *
     * @param task
     */
    protected void runTaskEnd(BaseTask task) {
    }

    protected void checkTimerRun() {
    }

    /**
     * 任务数量
     *
     * @return
     */
    public int taskSize() {
        return this.tasksQueue.size();
    }

    protected BlockingQueue<BaseTask> getTasksQueue() {
        return tasksQueue;
    }

}
