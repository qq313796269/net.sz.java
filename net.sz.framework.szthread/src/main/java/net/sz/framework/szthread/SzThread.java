package net.sz.framework.szthread;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.framework.struct.thread.BaseThreadModel;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.struct.thread.ThreadType;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 * 队列线程
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SzThread extends BaseThreadRunnable implements Serializable, Cloneable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 6734940117523552130L;

    /*嵌套引用*/
    private final ConcurrentLinkedQueue<TaskModel> taskQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Long, TimerTaskModel> timerTaskQueue = new ConcurrentHashMap<>();

    public ConcurrentLinkedQueue<TaskModel> getTaskQueue() {
        return taskQueue;
    }

    public ConcurrentHashMap<Long, TimerTaskModel> getTimerTaskQueue() {
        return timerTaskQueue;
    }

    /**
     * 停止线程,马上终止线程
     */
    @Override
    public void close() {
        this.setRuning(false);
        clear();
        synchronized (taskQueue) {
            /* 唤醒队列, 开始执行 */
            taskQueue.notifyAll();
        }
        super.close();
    }

    /**
     *
     * @param group
     */
    public SzThread(ThreadGroup group) {
        this(ThreadType.User, group, "无名", 1);
    }

    /**
     *
     * @param name
     */
    public SzThread(String name) {
        this(ThreadType.User, BaseThreadRunnable.UnknownThreadGroup, name, 1);
    }

    /**
     *
     * @param threadType
     * @param group
     * @param name
     * @param threadCount
     */
    public SzThread(ThreadType threadType, ThreadGroup group, String name, int threadCount) {
        super(threadType, name);
        this.createMyThread(group, threadCount);
    }

    /**
     * 创建线程
     *
     * @param myId
     * @param tid
     * @param group
     * @param name
     */
    @Override
    protected void createMyThread(int myId, long tid, ThreadGroup group, Runnable runnable, String name) {
        SzThreadModel thread = new SzThreadModel(myId, tid, group, runnable, name + "-" + myId + "-" + tid);
        thread.setUncaughtExceptionHandler(this);
        this.getThreads().put(myId, thread);
        thread.start();
    }

    /**
     * 清理所有任务
     */
    public void clear() {
        this.taskQueue.clear();
        this.timerTaskQueue.clear();
    }

    /**
     * 默认线程队列
     *
     * @param taskId
     * @return
     */
    public final boolean romveTimerTask(long taskId) {
        return this.timerTaskQueue.remove(taskId) != null;
    }

    /**
     * 默认线程队列
     *
     * @param runnable
     * @return
     */
    public final boolean romveTimerTask(Runnable runnable) {
        if (runnable instanceof TimerTaskModel) {
            TimerTaskModel timertask = (TimerTaskModel) runnable;
            return this.romveTimerTask(timertask.getTaskId()); //To change body of generated methods, choose Tools | Templates.
        }
        return false;
    }

    /**
     *
     *
     * @param runnable
     * @return
     */
    @Override
    public final Runnable addTask(Runnable runnable) {
        if (runnable instanceof TaskModel) {
            TaskModel task = (TaskModel) runnable;
            if (isRuning()) {
                if (!isWaiting()) {
                    task.setCreateTime(TimeUtil.currentTimeMillis());
                    if (this.taskQueue.size() < getMaxTaskCount()) {
                        task.setRunTid(this.getTid());
                        taskQueue.add(task);
                        synchronized (taskQueue) {
                            /* 唤醒队列, 开始执行 只唤醒一个线程 */
                            taskQueue.notify();
                        }
                    } else {
                        Exception exception = new Exception("线程任务数量超过：" + getMaxTaskCount());
                        log.error(this.toString(), exception);
                        /* 如果有注册，那么通知 */
                        if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                            BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(exception);
                        }
                    }
                } else {
                    Exception exception = new Exception("线程模型已经挂起操作，请先恢复线程模型");
                    log.error(this.toString(), exception);
                    /* 如果有注册，那么通知 */
                    if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                        BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(exception);
                    }
                }
            } else {
                Exception exception = new Exception("线程模型已经停止执行，请先恢复线程模型");
                log.error(this.toString(), exception);
                /* 如果有注册，那么通知 */
                if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                    BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(exception);
                }
            }
            return task;
        }
        return null;
    }

    /**
     * 默认线程队列
     *
     * @param runnable
     * @return
     */
    @Override
    public final Runnable addTimerTask(Runnable runnable) {
        if (runnable instanceof TimerTaskModel) {
            TimerTaskModel timertask = (TimerTaskModel) runnable;
            if (isRuning()) {
                if (!isWaiting()) {
                    timertask.setCreateTime(TimeUtil.currentTimeMillis());
                    //一开始执行一次
                    if (timertask.startAction) {
                        addTask(timertask);
                    }
                    if (timerTaskQueue.size() < getMaxTaskCount()) {
                        timerTaskQueue.put(timertask.getTaskId(), timertask);
                    } else {
                        Exception exception = new Exception("线程定时器数量超过：" + getMaxTaskCount());
                        log.error(this.toString(), exception);
                        /* 如果有注册，那么通知 */
                        if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                            BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(exception);
                        }
                    }
                } else {
                    Exception exception = new Exception("线程模型已经挂起操作，请先恢复线程模型");
                    log.error(this.toString(), exception);
                    /* 如果有注册，那么通知 */
                    if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                        BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(exception);
                    }
                }
            } else {
                Exception exception = new Exception("线程模型已经停止执行，请先恢复线程模型");
                log.error(this.toString(), exception);
                /* 如果有注册，那么通知 */
                if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                    BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(exception);
                }
            }
            return timertask;
        }
        return null;
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
            if (t instanceof SzThreadModel) {
                SzThreadModel threadModel = (SzThreadModel) t;
                if (threadModel.getLastCommand() != null) {
                    if (threadModel.getLastCommand() instanceof TimerTaskModel) {
                        /*防止线程意外终止，导致任务无法执行，所以先设置本次执行结束不影响功能实现*/
                        ((TimerTaskModel) threadModel.getLastCommand()).beforeEnd = true;
                        ((TimerTaskModel) threadModel.getLastCommand()).lastTime = TimeUtil.currentTimeMillis();
                    }
                }
            }
            super.uncaughtException(t, e);
        } catch (Throwable throwable) {
            log.fatal("线程状态监控", throwable);
        }
    }

    /**
     * 线程执行方案
     */
    @Override
    public void run() {
        SzThreadModel currentThread = (SzThreadModel) Thread.currentThread();
        String threadName = Thread.currentThread().getName();
        while (this.isRuning()) {
            /*如果是空或者已经挂起的线程*/
            while (this.isRuning() && !this.isWaiting() && taskQueue.isEmpty()) {
                synchronized (taskQueue) {
                    try {
                        taskQueue.wait();
                    } catch (Exception e) {
                        log.error("获取锁错误", e);
                    }
                }
            }

            /*有可能是终止线程发出的唤醒*/
            if (this.isRuning() && !this.isWaiting()) {
                TaskModel taskModel = taskQueue.poll();

                if (taskModel != null) {

                    /* 设置任务 */
                    currentThread.setLastCommand(taskModel);
                    currentThread.setLastCommandExecuteTime(TimeUtil.currentTimeMillis());

                    String lastCommandName = taskModel.getClass().getName();

                    try {
                        run(taskModel);
                        /*执行完成后回调*/
                        taskModel.onSuccessCallBack();
                    } catch (Throwable e) {
                        try {
                            taskModel.onErrorCallBack();
                        } catch (Exception e1) {
                            /* 如果有注册，那么通知 */
                            if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                                BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(e1);
                            } else {
                                /* 避免重复执行，错误并没有再次执行 */
                                log.error("工人<“" + threadName + "”> 执行任务<" + lastCommandName + "> 错误回调函数遇到错误: ", e1);
                            }
                        }
                        /* 如果有注册，那么通知 */
                        if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                            BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(e);
                        } else {
                            /* 避免重复执行，错误并没有再次执行 */
                            log.error("工人<“" + threadName + "”> 执行任务<" + lastCommandName + "> 遇到错误: ", e);
                        }
                        /* 避免重复执行，错误并没有再次执行 */
                        log.error("工人<“" + threadName + "”> 执行任务<" + lastCommandName + "> 遇到错误: ", e);
                    }

                    taskModel.setRunEnd(true);

                    /*执行结束*/
                    taskModel.anotifyEndAll();

                    if (currentThread.getLastCommand() instanceof TimerTaskModel) {
                        ((TimerTaskModel) taskModel).beforeEnd = true;
                        ((TimerTaskModel) taskModel).lastTime = TimeUtil.currentTimeMillis();
                    }

                    long timeL1 = TimeUtil.currentTimeMillis() - currentThread.getLastCommandExecuteTime();

                    if (timeL1 > 10) {
                        String name = "工人<“" + threadName + "”> 完成了任务：" + lastCommandName + " 执行耗时：" + timeL1;
                        if (lastCommandName.contains("CUDThread")) {
                            /*如果是数据库任务不超过30秒不提示*/
                            if (timeL1 > 30000) {
                                if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                                    BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(new UnsupportedOperationException(name));
                                }
                            }
                        } else if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                            BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(new UnsupportedOperationException(name));
                        } else {
                            log.error(name);
                        }
                    }

                    /* 清理任务最后一次执行任务 */
                    currentThread.setLastCommand(null);
                    currentThread.setLastCommandExecuteTime(0);
                    currentThread.setLastTaskModelQueue(null);
                }
            }
        }
        log.error("线程结束, 工人<“" + threadName + "”>退出");
    }
    // </editor-fold>

    // <editor-fold desc="线程执行方案 public void run()">
    /**
     * 线程执行方案
     *
     * @param taskModel
     */
    public void run(TaskModel taskModel) {
        if (taskModel == null) {
            return;
        }
        if (taskModel.isCancel()) {
            /* 如果任务已经取消 */
            return;
        }
        taskModel.run();
    }
    // </editor-fold>

    // <editor-fold desc="定时器线程执行器 public void timerRun()">
    /**
     * 定时器线程执行器
     */
    @Override
    public void timerRun() {
        if (isRuning() && !this.isWaiting() && !timerTaskQueue.isEmpty()) {
            HashMap<Long, TimerTaskModel> hashMap = new HashMap<>(timerTaskQueue);
            for (Map.Entry<Long, TimerTaskModel> entry : hashMap.entrySet()) {
                TimerTaskModel timerEvent = entry.getValue();

                if (checkAddTimerRun(timerEvent)) {
                    this.addTask(timerEvent);
                }
                if (checkRemoveTimerRun(timerEvent)) {
                    timerTaskQueue.remove(timerEvent.getTaskId());
                }
            }
        }
    }

    /**
     * 定时器线程执行器
     *
     * @param timerEvent
     * @return true 表示需要从源集合删除
     */
    public boolean checkAddTimerRun(TimerTaskModel timerEvent) {
        if (timerEvent == null) {
            return false;
        }
        int execCount = timerEvent.getExecCount();
        long lastTime = timerEvent.getLastTime();
        long nowTime = TimeUtil.currentTimeMillis();
        if (lastTime == 0) {
            timerEvent.setLastTime(nowTime);
        } else {
            if (!timerEvent.isCancel()) {
                if (!timerEvent.beforeEnd) {
                    /*如果上一次尚未执行*/
                    return false;
                }
                if (nowTime >= timerEvent.getStartTime() /*是否满足开始时间*/
                        && (timerEvent.getEndTime() <= 0 || nowTime <= timerEvent.getEndTime()) /*判断结束时间*/
                        && (nowTime - lastTime >= timerEvent.getIntervalTime())) /*判断上次执行到目前是否满足间隔时间*/ {
                    /*设置本次执行尚未结束*/
                    timerEvent.beforeEnd = false;
                    timerEvent.setRunEnd(false);
                    if (execCount > Integer.MAX_VALUE - 2) {
                        execCount = 0;
                    }
                    execCount++;
                    /*记录*/
                    timerEvent.setExecCount(execCount);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 定时器线程执行器
     *
     * @param timerEvent
     * @return true 表示需要从源集合删除
     */
    public boolean checkRemoveTimerRun(TimerTaskModel timerEvent) {
        if (timerEvent == null) {
            return true;
        }
        int execCount = timerEvent.getExecCount();
        long nowTime = TimeUtil.currentTimeMillis();
        /*判断删除条件*/
        if (timerEvent.isCancel()
                || (timerEvent.getEndTime() > 0 && nowTime < timerEvent.getEndTime())
                || (timerEvent.getMaxExecCount() > 0 && timerEvent.getMaxExecCount() <= execCount)) {
            if (log.isDebugEnabled()) {
                log.debug("清理定时器任务：" + timerEvent.getClass().getName());
            }
            return true;
        }
        return false;
    }
    // </editor-fold>

    /**
     *
     * @return @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.

    }

}
