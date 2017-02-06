package net.sz.game.engine.thread;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.game.engine.utils.GlobalUtil;
import net.sz.game.engine.utils.MailUtil;
import net.sz.game.engine.utils.StringUtil;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ThreadRunnable implements Runnable, Thread.UncaughtExceptionHandler {

    private static final Logger log = Logger.getLogger(ThreadRunnable.class);

    // <editor-fold defaultstate="collapsed" desc="字段信息">
    protected final ConcurrentHashMap<Integer, ThreadModel> threads = new ConcurrentHashMap<>();
    /**
     * 任务列表 线程安全的任务列表
     */
    //protected final List<TaskModel> taskQueue = new ArrayList<>();
    protected final ConcurrentLinkedQueue<TaskEvent> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 定时任务列表
     */
    protected final ConcurrentHashMap<Long, TimerTaskEvent> timerQueue = new ConcurrentHashMap<>();

    protected long tid;
    protected String tName;
    protected long lastSendMail = 0;
    // false标识删除线程
    protected volatile boolean runing = true;
    // 表示线程当前是否挂起
    protected volatile boolean waiting = false;
    protected ThreadType threadType;

    /**
     * 停止线程，设置线程的停止状态，并不会马上终止线程
     */
    public void stop() {
        if (this.runing) {
            this.runing = false;
            ThreadPool.remove(tid);
        }
        clear();
        synchronized (taskQueue) {
            /* 唤醒队列, 开始执行 */
            taskQueue.notifyAll();
        }
    }

    public boolean isRuning() {
        return runing;
    }

    public ThreadType getThreadType() {
        return threadType;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    /**
     * 线程名字
     *
     * @return
     */
    public String gettName() {
        return tName;
    }

    /**
     * 获取线程的自定义id
     *
     * @return
     */
    public long getId() {
        return this.tid;
    }

    public boolean isWaiting() {
        return waiting;
    }

    /**
     * 如果挂起线程，那么会清空所以任务
     *
     * @param waiting
     */
    @Deprecated
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
        if (waiting) {
            /*表示挂起了*/
            clear();
        }
    }

    @Override
    public String toString() {
        return "Thread{" + "tid=" + tid + ",Name=" + this.gettName() + '}';
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="清理所有任务 public void clear()">
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
        this.taskQueue.clear();
    }

    /**
     * 清理定时器任务
     */
    public void clearTimerTask() {
        this.timerQueue.clear();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加新的任务 每增加一个新任务，都要唤醒任务队列 public void addTask(TaskEvent runnable)">
    /**
     * 增加新的任务 每增加一个新任务，都要唤醒任务队列
     *
     * @param runnable
     */
    public void addTask(TaskEvent runnable) {
        if (runing) {
            if (!isWaiting()) {
                if (taskQueue.size() < 5000) {
                    taskQueue.add(runnable);
                } else {
                    Exception exception = new Exception("线程定时器数量超过5000");
                    log.error("线程id：" + tid + " 线程模型：" + tName, exception);
                    /* 如果有注册，那么通知 */
                    if (ThreadPool.NoticeThreadException_All != null) {
                        ThreadPool.NoticeThreadException_All.noticeThreadException(exception);
                    }
                }
                synchronized (taskQueue) {
                    /* 唤醒队列, 开始执行 只唤醒一个线程 */
                    taskQueue.notify();
                }
            } else {
                Exception exception = new Exception("线程模型已经挂起操作，请先恢复线程模型");
                log.error("线程id：" + tid + " 线程模型：" + tName, exception);
                /* 如果有注册，那么通知 */
                if (ThreadPool.NoticeThreadException_All != null) {
                    ThreadPool.NoticeThreadException_All.noticeThreadException(exception);
                }
            }
        } else {
            Exception exception = new Exception("线程模型已经停止执行，请先恢复线程模型");
            log.error("线程id：" + tid + " 线程模型：" + tName, exception);
            /* 如果有注册，那么通知 */
            if (ThreadPool.NoticeThreadException_All != null) {
                ThreadPool.NoticeThreadException_All.noticeThreadException(exception);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="向线程添加定时器任务 public void addTimerTask(TimerTaskEvent runnable)">
    /**
     * 向线程添加定时器任务
     *
     * @param runnable
     */
    public void addTimerTask(TimerTaskEvent runnable) {
        if (runing) {
            if (!isWaiting()) {
                //一开始执行一次
                if (runnable.startAction) {
                    addTask(runnable);
                }

                if (timerQueue.size() < 5000) {
                    timerQueue.put(runnable.getTaskEventId(), runnable);
                } else {
                    Exception exception = new Exception("线程定时器数量超过5000");
                    log.error("线程id：" + tid + " 线程模型：" + tName, exception);
                    /* 如果有注册，那么通知 */
                    if (ThreadPool.NoticeThreadException_All != null) {
                        ThreadPool.NoticeThreadException_All.noticeThreadException(exception);
                    }
                }
            } else {
                Exception exception = new Exception("线程模型已经挂起操作，请先恢复线程模型");
                log.error("线程id：" + tid + " 线程模型：" + tName, exception);
                /* 如果有注册，那么通知 */
                if (ThreadPool.NoticeThreadException_All != null) {
                    ThreadPool.NoticeThreadException_All.noticeThreadException(exception);
                }
            }
        } else {
            Exception exception = new Exception("线程模型已经停止执行，请先恢复线程模型");
            log.error("线程id：" + tid + " 线程模型：" + tName, exception);
            /* 如果有注册，那么通知 */
            if (ThreadPool.NoticeThreadException_All != null) {
                ThreadPool.NoticeThreadException_All.noticeThreadException(exception);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="删除定时器任务 public void romveTimerTask(TimerTaskEvent runnable)">
    /**
     * 删除定时器任务
     *
     * @param runnable
     * @return
     */
    public boolean romveTimerTask(TimerTaskEvent runnable) {
        return romveTimerTask(runnable.getTaskEventId());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="删除定时器任务 public void romveTimerTask(long taskId)">
    /**
     * 删除定时器任务
     *
     * @param taskId
     * @return
     */
    public boolean romveTimerTask(long taskId) {
        return this.timerQueue.remove(taskId) != null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化 init">
    public ThreadRunnable(ThreadGroup group) {
        this(ThreadType.User, group, "无名", 1);
    }

    public ThreadRunnable(String name) {
        this(ThreadType.User, ThreadPool.UnknownThreadGroup, name, 1);
    }

    public ThreadRunnable(ThreadType threadType, ThreadGroup group, String name, int threadCount) {
        tid = ThreadPool.THREAD_ID_UTIL.getId();
        if (threadCount < 1) {
            threadCount = 1;
        }
        for (int i = 1; i <= threadCount; i++) {
            createMyThread(i, tid, group, this, name);
        }
        this.tName = name;
        this.threadType = threadType;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="创建线程 void createMyThread(int myId, long tid, ThreadGroup group, Runnable run, String name)">
    /**
     * 创建线程
     *
     * @param myId
     * @param tid
     * @param group
     * @param run
     * @param name
     */
    void createMyThread(int myId, long tid, ThreadGroup group, Runnable run, String name) {
        ThreadModel thread;
        thread = new ThreadModel(myId, tid, group, this, name + "-" + tid + "-" + myId);
        thread.setUncaughtExceptionHandler(this);
        thread.start();
        threads.put(myId, thread);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="线程状态监控 public void uncaughtException(Thread t, Throwable e)">
    /**
     * 线程状态监控<br>
     * 由于自定义线程执行run方法内部一定加了try cache<br>
     * 还是收到这个方法，一定jvm 抛错，线程异常退出，必须保证线程能得到重启
     *
     * @param t
     * @param e
     */
    @Override
    public final void uncaughtException(Thread t, Throwable e) {
        String toString = e.toString();
        log.error("未捕获的异常", e);
        MailUtil.sendMail("执行线程异常退出 -> 游戏id-" + GlobalUtil.GameID + "  平台-" + GlobalUtil.PlatformId + "  服务器id-" + GlobalUtil.getServerID(), toString);

        /* 如果有注册，那么通知 */
        if (ThreadPool.NoticeThreadException_All != null) {
            ThreadPool.NoticeThreadException_All.noticeThreadException(e);
        }
        if (t instanceof ThreadModel) {
            createMyThread(((ThreadModel) t).getMyId(), tid, t.getThreadGroup(), ((ThreadModel) t).getRun(), tName);
        }
        t = null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="线程执行方案 public void run()">
    /**
     * 线程执行方案
     */
    @Override
    public void run() {
        ThreadModel currentThread = (ThreadModel) Thread.currentThread();
        String threadName = Thread.currentThread().getName();
        String lastCommandName = null;
        while (runing) {
            currentThread.setLastCommand(null);
            currentThread.setLastExecuteTime(0);
            /*如果是空或者已经挂起的线程*/
            while (taskQueue.isEmpty() || this.isWaiting()) {
                try {
                    /* 任务队列为空，则等待有新任务加入从而被唤醒 */
                    synchronized (taskQueue) {
                        taskQueue.wait();
                    }
                } catch (InterruptedException ie) {
                }
            }
            /*有可能是终止线程发出的唤醒*/
            if (runing) {
                /* 取出任务执行 */
                currentThread.setLastCommand(taskQueue.poll());
                if (currentThread.getLastCommand() != null) {
                    lastCommandName = currentThread.getLastCommand().getClass().getName();
                    if (currentThread.getLastCommand().isCancel()) {
                        /* 如果任务已经取消 */
                        continue;
                    }
                    /* 执行任务 */
                    currentThread.setLastExecuteTime(System.currentTimeMillis());
                    try {
                        if (currentThread.getLastCommand() instanceof TimerTaskEvent) {
                            /*防止线程意外终止，导致任务无法执行，所以先设置本次执行结束不影响功能实现*/
                            ((TimerTaskEvent) currentThread.getLastCommand()).beforeEnd = true;
                            ((TimerTaskEvent) currentThread.getLastCommand()).lastTime = System.currentTimeMillis();
                        }
                        currentThread.getLastCommand().run();
                    } catch (Exception e) {
                        /* 如果有注册，那么通知 */
                        if (ThreadPool.NoticeThreadException_All != null) {
                            ThreadPool.NoticeThreadException_All.noticeThreadException(e);
                        } else {
                            /* 避免重复执行，错误并没有再次执行 */
                            log.error("工人<“" + threadName + "”> 执行任务<" + lastCommandName + "> 遇到错误: ", e);
                        }
                    }
                    long timeL1 = System.currentTimeMillis() - currentThread.getLastExecuteTime();
                    if (timeL1 > 20) {
                        String name = "工人<“" + threadName + "”> 完成了任务：" + lastCommandName + " 执行耗时：" + timeL1;
                        if (lastCommandName.contains("CUDThread")) {
                            /*如果是数据库任务不超过30秒不提示*/
                            if (timeL1 > 30000) {
                                if (ThreadPool.NoticeThreadException_All != null) {
                                    ThreadPool.NoticeThreadException_All.noticeThreadException(new UnsupportedOperationException(name));
                                }
                            }
                        } else {
                            if (ThreadPool.NoticeThreadException_All != null) {
                                ThreadPool.NoticeThreadException_All.noticeThreadException(new UnsupportedOperationException(name));
                            } else {
                                log.error(name);
                            }
                        }
                    }
                }
            }
        }
        log.error("线程结束, 工人<“" + threadName + "”>退出");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="定时器线程执行器 public void timerRun()">
    /**
     * 定时器线程执行器
     */
    public void timerRun() {
        if (runing && !this.isWaiting() && !timerQueue.isEmpty()) {
            /*队列不为空的情况下 取出队列定时器任务*/
            TimerTaskEvent[] taskModels;
            Collection<TimerTaskEvent> values = timerQueue.values();
            taskModels = values.toArray(new TimerTaskEvent[0]);
            for (int i = 0; i < taskModels.length; i++) {
                if (!runing || this.isWaiting()) {
                    /*如果已经挂起或者终止线程了， 那么不在继续执行*/
                    return;
                }
                TimerTaskEvent timerEvent = taskModels[i];
                int execCount = timerEvent.execCount;
                long lastTime = timerEvent.lastTime;
                long nowTime = System.currentTimeMillis();
                if (lastTime == 0) {
                    timerEvent.lastTime = nowTime;
                } else {
                    if (!timerEvent.isCancel()) {
                        if (!timerEvent.beforeEnd) {
                            /*如果上一次尚未执行*/
                            continue;
                        }
                        if (nowTime >= timerEvent.getStartTime() // 是否满足开始时间
                                && (nowTime - timerEvent.getSubmitTime() >= timerEvent.getIntervalTime())// 提交以后是否满足了间隔时间
                                && (timerEvent.getEndTime() <= 0 || nowTime <= timerEvent.getEndTime()) // 判断结束时间
                                && (nowTime - lastTime >= timerEvent.getIntervalTime())) // 判断上次执行到目前是否满足间隔时间
                        {
                            /*设置本次执行尚未结束*/
                            timerEvent.beforeEnd = false;
                            /*记录*/
                            execCount++;
                            timerEvent.execCount = execCount;
                            /*提交执行定时器最先执行*/
                            this.addTask(timerEvent);
                        }
                    }
                    /*判断删除条件*/
                    if (timerEvent.isCancel() || (timerEvent.getEndTime() > 0 && nowTime < timerEvent.getEndTime())
                            || (timerEvent.getActionCount() > 0 && timerEvent.getActionCount() <= execCount)) {
                        timerQueue.remove(timerEvent.getTaskEventId());
                        log.debug("清理定时器任务：" + timerEvent.getClass().getName());
                    }
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="查看线程堆栈 public void showStackTrace()">
    /**
     *
     * 查看线程堆栈
     */
    public void showStackTrace() {
        if (runing && !this.isWaiting()) {
            Collection<ThreadModel> values = threads.values();
            ThreadModel[] valuesThreads = values.toArray(new ThreadModel[0]);

            StringBuilder buf = new StringBuilder();
            for (int k = 0; k < valuesThreads.length; k++) {
                ThreadModel currentThread = valuesThreads[k];
                /*如果现场意外终止*/
                if (currentThread.getState() != Thread.State.TERMINATED) {
                    long procc = System.currentTimeMillis() - currentThread.getLastExecuteTime();
                    if (procc > 5 * 1000 && procc < 864000000L) {//小于10天//因为多线程操作时间可能不准确
                        buf.append("线程[")
                                .append(currentThread.getName())
                                .append("]状态 -> ")
                                .append(currentThread.getState())
                                .append("可能已卡死 -> ")
                                .append(procc / 1000f)
                                .append("s\n    ")
                                .append("执行任务：")
                                .append(currentThread.getLastCommand().getClass().getName());
                        try {
                            StackTraceElement[] elements = currentThread.getStackTrace();
                            for (int i = 0; i < elements.length; i++) {
                                buf.append("\n    ")
                                        .append(elements[i].getClassName())
                                        .append(".")
                                        .append(elements[i].getMethodName())
                                        .append("(").append(elements[i].getFileName())
                                        .append(";")
                                        .append(elements[i].getLineNumber()).append(")");
                            }
                        } catch (Exception e) {
                            buf.append(e);
                        }
                        buf.append("\n++++++++++++++++++++++++++++++++++");
                    }
                }
            }
            String toString = buf.toString();
            if (!StringUtil.isNullOrEmpty(toString)) {
                log.error(toString);
                if (System.currentTimeMillis() - lastSendMail > 5 * 60 * 1000) {
                    lastSendMail = System.currentTimeMillis();
                    MailUtil.sendMail("线程执行已卡死 -> 游戏id-" + GlobalUtil.GameID + "  平台-" + GlobalUtil.PlatformId + "  服务器id-" + GlobalUtil.getServerID(), toString);
                }
            }
        }
    }
    // </editor-fold>

}
