package net.sz.framework.szthread;

import net.sz.framework.struct.thread.ThreadType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.framework.struct.thread.BaseThreadModel;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.AtomInteger;
import net.sz.framework.utils.StringUtil;
import net.sz.framework.utils.TimeUtil;

/**
 * 队列线程
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SzQueueThread extends BaseThreadRunnable implements Serializable, Cloneable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 6734940117523552130L;

    private static final String KEY_0 = "key_0__";

    /*当前任务数量*/
    private final AtomInteger taskCount = new AtomInteger(0);
    /*                                地图id  任务 */
    private final ConcurrentHashMap<String, QueueTaskModel> keyTaskMap = new ConcurrentHashMap<>();
    /*嵌套引用*/
    private final ConcurrentLinkedQueue<QueueTaskModel> keyTaskQueue = new ConcurrentLinkedQueue<>();
    /*                                地图id                    任务id  定时器     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, TimerTaskModel>> keyTimerTaskQueue = new ConcurrentHashMap<>();

    /**
     * 线程模型的当前任务数量
     *
     * @return
     */
    public AtomInteger getTaskCount() {
        return taskCount;
    }

    /**
     * 多路单链模型
     *
     * @return
     */
    public ConcurrentHashMap<String, QueueTaskModel> getKeyTaskMap() {
        return keyTaskMap;
    }

    /**
     * 任务队列
     *
     * @return
     */
    public ConcurrentLinkedQueue<QueueTaskModel> getKeyTaskQueue() {
        return keyTaskQueue;
    }

    /**
     * 多路单链定时器任务
     *
     * @return
     */
    public ConcurrentHashMap<String, ConcurrentHashMap<Long, TimerTaskModel>> getKeyTimerTaskQueue() {
        return keyTimerTaskQueue;
    }

    /**
     * 停止线程,马上终止线程
     */
    @Override
    public void close() {
        this.setRuning(false);
        clear();
        synchronized (taskCount) {
            /* 唤醒队列, 开始执行 */
            taskCount.notifyAll();
        }
        super.close();
    }

    /**
     *
     * @param group
     */
    public SzQueueThread(ThreadGroup group) {
        this(ThreadType.User, group, "无名", 1);
    }

    /**
     *
     * @param name
     */
    public SzQueueThread(String name) {
        this(ThreadType.User, BaseThreadRunnable.UnknownThreadGroup, name, 1);
    }

    /**
     *
     * @param threadType
     * @param group
     * @param name
     * @param threadCount
     */
    public SzQueueThread(ThreadType threadType, ThreadGroup group, String name, int threadCount) {
        super(threadType, name);
        this.addKey(KEY_0);
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
        clearTimerTask();
        clearTask();
    }

    /**
     * 清理任务集合
     */
    public void clearTask() {
        this.keyTaskMap.clear();
        this.keyTaskQueue.clear();
    }

    /**
     * 清理定时器任务
     */
    public void clearTimerTask() {
        this.keyTimerTaskQueue.clear();
    }

    /**
     * false 表示已经存在键
     *
     * @param mapKey
     * @return false 表示已经存在键
     */
    public final boolean addKey(String mapKey) {
        QueueTaskModel taskModelQueue = this.keyTaskMap.get(mapKey);
        if (taskModelQueue == null) {
            taskModelQueue = new QueueTaskModel(mapKey);
            this.keyTaskMap.put(mapKey, taskModelQueue);
            this.keyTimerTaskQueue.put(mapKey, new ConcurrentHashMap<>());
            return true;
        }
        return false;
    }

    public final void removeKey(String mapKey) {
        QueueTaskModel taskModelQueue = this.keyTaskMap.remove(mapKey);
        if (taskModelQueue != null) {
            taskModelQueue.setCancel(true);
        }
        this.keyTimerTaskQueue.remove(mapKey);
    }

    /**
     * 默认线程队列
     *
     * @param taskId
     * @return
     */
    public final boolean romveTimerTask(long taskId) {
        return this.romveTimerTask(KEY_0, taskId);
    }

    /**
     * 默认线程队列
     *
     * @param runnable
     * @return
     */
    public final boolean romveTimerTask(Runnable runnable) {
        return this.romveTimerTask(KEY_0, runnable); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 默认线程队列
     *
     * @param runnable
     * @return
     */
    @Override
    public final Runnable addTask(Runnable runnable) {
        return this.addTask(KEY_0, runnable); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 增加新的任务 每增加一个新任务，都要唤醒任务队列
     *
     * @param key
     * @param runnable
     * @return
     */
    @Override
    public final Runnable addTask(String key, Runnable runnable) {
        if (runnable instanceof TaskModel) {
            TaskModel task = (TaskModel) runnable;
            if (StringUtil.isNullOrEmpty(key)) {
                return addTask(task);
            }
            if (isRuning()) {
                if (!isWaiting()) {
                    task.setCreateTime(TimeUtil.currentTimeMillis());
                    QueueTaskModel queueTaskModel = keyTaskMap.get(key);
                    if (queueTaskModel == null) {
                        Exception exception = new Exception("任务队列key不存在，可能非法或者已经退出：" + key);
                        log.error(this.toString(), exception);
                        /* 如果有注册，那么通知 */
                        if (BaseThreadModel.getNoticeThreadExceptionAll() != null) {
                            BaseThreadModel.getNoticeThreadExceptionAll().noticeThreadException(exception);
                        }
                    } else if (queueTaskModel.size() < getMaxTaskCount()) {
                        synchronized (queueTaskModel) {
                            task.setRunTid(this.getTid());
                            queueTaskModel.add(task);
                            taskCount.changeZero(1);
                            if (!queueTaskModel.isPutEnd()) {
                                this.keyTaskQueue.add(queueTaskModel);
                                queueTaskModel.setPutEnd(true);
                            }
                        }
                        synchronized (taskCount) {
                            /* 唤醒队列, 开始执行 只唤醒一个线程 */
                            taskCount.notify();
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
        return this.addTimerTask(KEY_0, runnable); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 向线程添加定时器任务
     *
     * @param key
     * @param runnable
     * @return
     */
    @Override
    public final Runnable addTimerTask(String key, Runnable runnable) {
        if (runnable instanceof TimerTaskModel) {
            TimerTaskModel timertask = (TimerTaskModel) runnable;
            if (StringUtil.isNullOrEmpty(key)) {
                return addTimerTask(timertask);
            }
            if (isRuning()) {
                if (!isWaiting()) {
                    timertask.setCreateTime(TimeUtil.currentTimeMillis());
                    //一开始执行一次
                    if (timertask.startAction) {
                        addTask(key, timertask);
                    }

                    ConcurrentHashMap<Long, TimerTaskModel> taskTimerMap = keyTimerTaskQueue.get(key);
                    if (taskTimerMap.size() < getMaxTaskCount()) {
                        taskTimerMap.put(timertask.getTaskId(), timertask);
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
     * 删除定时器任务
     *
     * @param key
     * @param runnable
     * @return
     */
    public final boolean romveTimerTask(String key, Runnable runnable) {
        if (runnable instanceof TimerTaskModel) {
            return romveTimerTask(key, ((TimerTaskModel) runnable).getTaskId());
        }
        return false;
    }

    /**
     * 删除定时器任务
     *
     * @param key
     * @param taskId
     * @return
     */
    public final boolean romveTimerTask(String key, long taskId) {
        return this.keyTimerTaskQueue.get(key).remove(taskId) != null;
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
                if (threadModel.getLastTaskModelQueue() != null) {
                    /**
                     * 把队列放回
                     */
                    synchronized (threadModel.getLastTaskModelQueue()) {
                        if (!threadModel.getLastTaskModelQueue().isCancel()) {
                            if (threadModel.getLastTaskModelQueue().isEmpty()) {
                                threadModel.getLastTaskModelQueue().setPutEnd(false);
                            } else {
                                this.keyTaskQueue.add(threadModel.getLastTaskModelQueue());
                                threadModel.getLastTaskModelQueue().setPutEnd(true);
                            }
                        }
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
        String threadName = currentThread.gettName();
        while (this.isRuning()) {
            /*如果是空或者已经挂起的线程*/
            while (this.isRuning() && taskCount.getValue() < 1) {
                synchronized (taskCount) {
                    try {
                        taskCount.wait();
                    } catch (Exception e) {
                        log.error("获取锁错误", e);
                    }
                }
            }

            /*有可能是终止线程发出的唤醒*/
            if (this.isRuning() && !this.isWaiting()) {
                QueueTaskModel taskModelQueue = keyTaskQueue.poll();
                if (taskModelQueue != null) {

                    if (taskModelQueue.isCancel()) {
                        continue;
                    }

                    TaskModel taskModel = taskModelQueue.poll();

                    if (taskModel != null) {
                        taskCount.changeZero(-1);

                        /* 设置任务 */
                        currentThread.setLastCommand(taskModel);
                        currentThread.setLastTaskModelQueue(taskModelQueue);
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

                    synchronized (taskModelQueue) {
                        if (taskModelQueue.isCancel()) {
                            continue;
                        }
                        if (taskModelQueue.isEmpty()) {
                            taskModelQueue.setPutEnd(false);
                        } else {
                            this.keyTaskQueue.add(taskModelQueue);
                            taskModelQueue.setPutEnd(true);
                        }
                    }

                } else {
                    synchronized (taskCount) {
                        try {
                            taskCount.wait(1);
                        } catch (Exception e) {
                            log.error("获取锁错误", e);
                        }
                    }
                }
            }

        }
        log.error("线程结束, 工人<“" + threadName + "”>退出");
    }

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
    public void timerRun() {
        if (isRuning() && !this.isWaiting() && !keyTimerTaskQueue.isEmpty()) {
            HashMap<String, ConcurrentHashMap<Long, TimerTaskModel>> hashMap = new HashMap<>(keyTimerTaskQueue);
            for (Map.Entry<String, ConcurrentHashMap<Long, TimerTaskModel>> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                ConcurrentHashMap<Long, TimerTaskModel> value1 = entry.getValue();
                timerRun(key, value1);
            }
        }
    }

    /**
     * 定时器线程执行器
     *
     * @param key
     * @param timerTaskModels
     */
    public void timerRun(String key, Map<Long, TimerTaskModel> timerTaskModels) {
        if (timerTaskModels.isEmpty()) {
            return;
        }
        /**
         * timerrun函数运行的临时变量，由于创建频繁，为例减少垃圾回收，独立出来
         */
        ArrayList<TimerTaskModel> tmpTimerTaskModels = new ArrayList<>(timerTaskModels.values());

        for (int i = 0; i < tmpTimerTaskModels.size(); i++) {
            if (!isRuning() || this.isWaiting()) {
                /*如果已经挂起或者终止线程了， 那么不在继续执行*/
                return;
            }

            TimerTaskModel timerEvent = tmpTimerTaskModels.get(i);

            if (checkAddTimerRun(timerEvent)) {
                this.addTask(key, timerEvent);
            }

            if (checkRemoveTimerRun(timerEvent)) {
                timerTaskModels.remove(timerEvent.getTaskId());
            }

        }
        tmpTimerTaskModels.clear();
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
