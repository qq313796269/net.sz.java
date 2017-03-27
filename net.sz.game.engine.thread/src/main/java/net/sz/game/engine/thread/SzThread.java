package net.sz.game.engine.thread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.utils.GlobalUtil;
import net.sz.game.engine.utils.LongId0Util;
import net.sz.game.engine.utils.MailUtil;
import net.sz.game.engine.utils.StringUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SzThread implements Serializable, Cloneable, Runnable, Thread.UncaughtExceptionHandler {

    private static SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 6734940117523552130L;
    private static final LongId0Util THREAD_ID_UTIL = new LongId0Util();

    // <editor-fold defaultstate="collapsed" desc="字段信息">
    protected final ConcurrentHashMap<Integer, ThreadModel> threads = new ConcurrentHashMap<>();
    /**
     * 任务列表 线程安全的任务列表
     */
    //protected final List<TaskModel> taskQueue = new ArrayList<>();
    protected final ConcurrentLinkedQueue<TaskModel> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * 定时任务列表
     */
    protected final ConcurrentHashMap<Long, TimerTaskModel> timerQueue = new ConcurrentHashMap<>();

    protected long tid;
    protected String tName;
    protected long lastSendMail = 0;
    // false标识删除线程
    protected volatile boolean runing = true;
    // 表示线程当前是否挂起
    protected volatile boolean waiting = false;
    protected ThreadType threadType;

    /**
     * 停止线程,马上终止线程
     */
    public void stop() {
        if (this.runing) {
            this.runing = false;
            ThreadPool.remove(tid);
        }
        clear();
        synchronized (taskQueue) {
            try {
                ThreadModel[] valuesThreads = threads.values().toArray(new ThreadModel[0]);
                for (int k = 0; k < valuesThreads.length; k++) {
                    ThreadModel currentThread = valuesThreads[k];
                    try {
                        /*终止线程并且等待*/
                        currentThread.interrupt();
                        currentThread.join();
                    } catch (Exception e) {
                    }
                }
                threads.clear();
            } catch (Exception e) {
            }
            /* 唤醒队列, 开始执行 */
            taskQueue.notifyAll();
        }
    }

    /**
     * false 表示已经停止了
     *
     * @return
     */
    public boolean isRuning() {
        return runing;
    }

    /**
     * false 表示已经暂停了
     *
     * @return
     */
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

    public ThreadType getThreadType() {
        return threadType;
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

    public void setTid(long tid) {
        this.tid = tid;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public void setThreadType(ThreadType threadType) {
        this.threadType = threadType;
    }

    public void reset(String tname) {
        this.tid = THREAD_ID_UTIL.getId();
        this.tName = tname + "-1-" + this.tid;
        for (Map.Entry<Integer, ThreadModel> entry : threads.entrySet()) {
            ThreadModel value = entry.getValue();
            value.setId(this.tid);
            value.settName(this.tName);
        }
    }

    @Override
    public String toString() {
        return "tid=" + tid + ",Name=" + this.gettName();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="初始化 init">
    public SzThread(ThreadGroup group) {
        this(ThreadType.User, group, "无名", 1);
    }

    public SzThread(String name) {
        this(ThreadType.User, ThreadPool.UnknownThreadGroup, name, 1);
    }

    public SzThread(ThreadType threadType, ThreadGroup group, String name, int threadCount) {
        tid = THREAD_ID_UTIL.getId();
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
    final void createMyThread(int myId, long tid, ThreadGroup group, Runnable runnable, String name) {
        ThreadModel thread;
        thread = new ThreadModel(myId, tid, group, runnable, name + "-" + myId + "-" + tid);
        thread.setUncaughtExceptionHandler(this);
        thread.start();
        threads.put(myId, thread);
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
    public void addTask(TaskModel runnable) {
        if (runing) {
            if (!isWaiting()) {
                runnable.setCreateTime(System.currentTimeMillis());
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
    public void addTimerTask(TimerTaskModel runnable) {
        if (runing) {
            if (!isWaiting()) {
                runnable.setCreateTime(System.currentTimeMillis());
                //一开始执行一次
                if (runnable.startAction) {
                    addTask(runnable);
                }

                if (timerQueue.size() < 5000) {
                    timerQueue.put(runnable.getTaskId(), runnable);
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
    public boolean romveTimerTask(TimerTaskModel runnable) {
        return romveTimerTask(runnable.getTaskId());
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
        try {
            String toString = e.toString();
            log.fatal("未捕获的异常", e);
            MailUtil.sendMail("执行线程异常退出 -> 游戏id-" + GlobalUtil.GameID + "  平台-" + GlobalUtil.PlatformId + "  服务器id-" + GlobalUtil.getServerID(), toString);
            /* 如果有注册，那么通知 */
            if (ThreadPool.NoticeThreadException_All != null) {
                ThreadPool.NoticeThreadException_All.noticeThreadException(e);
            }
            if (t instanceof ThreadModel) {
                ThreadModel threadModel = (ThreadModel) t;
                if (threadModel.getLastCommand() != null) {
                    if (threadModel.getLastCommand() instanceof TimerTaskModel) {
                        /*防止线程意外终止，导致任务无法执行，所以先设置本次执行结束不影响功能实现*/
                        ((TimerTaskModel) threadModel.getLastCommand()).beforeEnd = true;
                        ((TimerTaskModel) threadModel.getLastCommand()).lastTime = System.currentTimeMillis();
                    }
                }
                /*重建线程*/
                createMyThread(((ThreadModel) t).getMyId(), tid, t.getThreadGroup(), ((ThreadModel) t).getRun(), tName);
            }
            t.interrupt();
        } catch (Throwable throwable) {
            SzLogger.writeConsole("线程状态监控", throwable);
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
            try {
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
                if (runing && !this.isWaiting()) {
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
                            currentThread.getLastCommand().run();
                        } catch (Throwable e) {
                            /* 如果有注册，那么通知 */
                            if (ThreadPool.NoticeThreadException_All != null) {
                                ThreadPool.NoticeThreadException_All.noticeThreadException(e);
                            } else {
                                /* 避免重复执行，错误并没有再次执行 */
                                log.error("工人<“" + threadName + "”> 执行任务<" + lastCommandName + "> 遇到错误: ", e);
                            }
                        }
                        if (currentThread.getLastCommand() instanceof TimerTaskModel) {
                            /*防止线程意外终止，导致任务无法执行，所以先设置本次执行结束不影响功能实现*/
                            ((TimerTaskModel) currentThread.getLastCommand()).beforeEnd = true;
                            ((TimerTaskModel) currentThread.getLastCommand()).lastTime = System.currentTimeMillis();
                        }
                        long timeL1 = System.currentTimeMillis() - currentThread.getLastExecuteTime();
                        if (timeL1 > 10) {
                            String name = "工人<“" + threadName + "”> 完成了任务：" + lastCommandName + " 执行耗时：" + timeL1;
                            if (lastCommandName.contains("CUDThread")) {
                                /*如果是数据库任务不超过30秒不提示*/
                                if (timeL1 > 30000) {
                                    if (ThreadPool.NoticeThreadException_All != null) {
                                        ThreadPool.NoticeThreadException_All.noticeThreadException(new UnsupportedOperationException(name));
                                    }
                                }
                            } else if (ThreadPool.NoticeThreadException_All != null) {
                                ThreadPool.NoticeThreadException_All.noticeThreadException(new UnsupportedOperationException(name));
                            } else {
                                log.error(name);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                /* 避免重复执行，错误并没有再次执行 */
                log.error("工人<“" + threadName + "”> 执行任务<" + lastCommandName + "> 遇到错误: ", e);
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
            timerRun(timerQueue);
        }
    }

    /**
     * 定时器线程执行器
     *
     * @param timerTaskModels
     */
    public void timerRun(Map<Long, TimerTaskModel> timerTaskModels) {
        /**
         * timerrun函数运行的临时变量，由于创建频繁，为例减少垃圾回收，独立出来
         */
        ArrayList<TimerTaskModel> tmpTimerTaskModels = new ArrayList<>();
        /*队列不为空的情况下 取出队列定时器任务*/
        tmpTimerTaskModels.clear();
        tmpTimerTaskModels.addAll(timerTaskModels.values());
        for (int i = 0; i < tmpTimerTaskModels.size(); i++) {
            if (!runing || this.isWaiting()) {
                /*如果已经挂起或者终止线程了， 那么不在继续执行*/
                return;
            }
            TimerTaskModel timerEvent = tmpTimerTaskModels.get(i);
            boolean timerRun = timerRun(timerEvent);
            if (timerRun) {
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
    public boolean timerRun(TimerTaskModel timerEvent) {
        if (timerEvent == null) {
            return false;
        }
        int execCount = timerEvent.execCount;
        long lastTime = timerEvent.lastTime;
        long nowTime = System.currentTimeMillis();
        if (lastTime == 0) {
            timerEvent.lastTime = nowTime;
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
                    if (execCount > Integer.MAX_VALUE - 2) {
                        execCount = 0;
                    }
                    execCount++;
                    /*记录*/
                    timerEvent.execCount = execCount;
                    /*提交执行定时器最先执行*/
                    this.addTask(timerEvent);
                }
            }
            /*判断删除条件*/
            if (timerEvent.isCancel()
                    || (timerEvent.getEndTime() > 0 && nowTime < timerEvent.getEndTime())
                    || (timerEvent.getMaxExecCount() > 0 && timerEvent.getMaxExecCount() <= execCount)) {
                if (log.isDebugEnabled()) {
                    log.debug("清理定时器任务：" + timerEvent.getClass().getName());
                }
                return true;
            }
        }
        return false;
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
            StringBuilder buf = new StringBuilder();
            ThreadModel[] valuesThreads = values.toArray(new ThreadModel[0]);
            for (int k = 0; k < valuesThreads.length; k++) {
                ThreadModel currentThread = valuesThreads[k];
                /*如果线程卡住，锁住，暂停，*/
                if (currentThread.getState() == Thread.State.BLOCKED
                        || currentThread.getState() == Thread.State.TIMED_WAITING
                        || currentThread.getState() == Thread.State.WAITING) {
                    long procc = System.currentTimeMillis() - currentThread.getLastExecuteTime();
                    if (procc > 5000 && procc < 864000000L) {
                        /*小于10天//因为多线程操作时间可能不准确*/
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
                        } catch (Throwable e) {
                            buf.append(e);
                        }
                        buf.append("\n++++++++++++++++++++++++++++++++++");

                        if (procc > 120000l) {
                            /* 2 *60 * 1000  2 分钟直接终止当前线程，重建线程 */
                            uncaughtException(currentThread, new Exception("线程卡死"));
                        }
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

    /**
     *
     * @return @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 自定义线程
     * <br>
     * author 失足程序员<br>
     * mail 492794628@qq.com<br>
     * phone 13882122019<br>
     */
    public final class ThreadModel extends Thread {

        private Runnable run;
        //线程的自定义id
        private long _id;
        //线程的自定义id
        private int myId;

        private String tName;
        //正在执行的任务
        private volatile TaskModel lastCommand;
        //开始执行任务的时间
        private volatile long lastExecuteTime = 0;

        /**
         *
         * @param myId
         * @param tid 自定义线程id
         * @param group 分组
         * @param run 执行方法
         * @param name 线程名称
         */
        public ThreadModel(int myId, long tid, ThreadGroup group, Runnable run, String name) {
            super(group, run);
            this.setName(name);
            this._id = tid;
            this.tName = name;
            this.run = run;
        }

        public TaskModel getLastCommand() {
            return lastCommand;
        }

        public void setLastCommand(TaskModel lastCommand) {
            this.lastCommand = lastCommand;
        }

        public long getLastExecuteTime() {
            return lastExecuteTime;
        }

        public void setLastExecuteTime(long lastExecuteTime) {
            this.lastExecuteTime = lastExecuteTime;
        }

        /**
         * 返回线程自定义id
         *
         * @return
         */
        @Override
        public long getId() {
            return _id;
        }

        public void setId(long _id) {
            this._id = _id;
        }

        public String gettName() {
            return tName;
        }

        public void settName(String tName) {
            this.tName = tName;
            this.setName(tName);
        }

        public Runnable getRun() {
            return run;
        }

        public int getMyId() {
            return myId;
        }

        @Override
        public String toString() {
            return "_id=" + _id + ", myId=" + myId + ", tName=" + tName + ", lastExecuteTime=" + lastExecuteTime;
        }

    }

}
