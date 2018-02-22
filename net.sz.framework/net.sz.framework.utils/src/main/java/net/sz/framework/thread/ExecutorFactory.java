package net.sz.framework.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.thread.timer.TimerTask;
import net.sz.framework.utils.MemoryUtil;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ExecutorFactory {

    private static final SzLogger log = SzLogger.getLogger();

    private ExecutorFactory() {

    }

    public static final ThreadGroup GlobalThreadGroup = new ThreadGroup("Global-ThreadGroup");
    public static final ThreadGroup UnknownThreadGroup = new ThreadGroup("Unknown-ThreadGroup");

    private static NoticeThreadException noticeThreadExceptionAll = null;
    private static boolean initEnd = false;

    /**
     * 非区块任务队列
     */
    public static final ServerExecutor2Timer DEFAULT_SERVICE;

    private static final ConcurrentHashMap<Long, ServerExecutor> EXC_ID_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ExecutorKey, ServerExecutor> EXC_KEY_MAP = new ConcurrentHashMap<>();

    private static final Thread TIMER_THREAD;
    private static final Thread TIMER_CHECK_THREAD;

    /**
     * 检查定时器任务间隔时间
     */
    public static final int TIMER_CHECK_WAIT = 2;

    /**
     * 检查线程运行状态检查时间
     */
    public static final int TIMER_CHECK_STACKTRACE_WAIT = 1000;

    static {

        DEFAULT_SERVICE = ExecutorFactory.newServerExecutor2Timer(ExecutorType.Sys, ExecutorKey.valueOf("GLOBAL"), GlobalThreadGroup, 1, 200000);

        DEFAULT_SERVICE.addTimerTask(new TimerTask(10 * 60 * 1000) {
            @Override
            public void run() {
                log.error(MemoryUtil.getMemory());
            }
        });

        TIMER_THREAD = new Thread(GlobalThreadGroup, () -> {
            Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                synchronized (currentThread) {
                    try {
                        currentThread.wait(TIMER_CHECK_WAIT);
                    } catch (InterruptedException ex) {
                    }
                }
                long begin = TimeUtil.currentTimeMillis();
                HashMap<Long, ServerExecutor> hashMap = new HashMap<>(EXC_ID_MAP);
                for (Map.Entry<Long, ServerExecutor> entrySet : hashMap.entrySet()) {
                    ServerExecutor value = entrySet.getValue();
                    if (ExecutorType.Sys.equals(value.getThreadType()) || initEnd) {
                        value.checkTimerRun();
                    } else {
                        if (log.isInfoEnabled()) {
                            log.info("服务器尚未初始化完成不会执行定时器功能 " + value.toString());
                        }
                    }
                }
                long tmp = (TimeUtil.currentTimeMillis() - begin);
                if (tmp > 2000) {
                    log.error("CheckThreadTimerEventScript cost:" + (TimeUtil.currentTimeMillis() - begin));
                }
            }
        }, "CHECK_TASK_TIMER");
        TIMER_THREAD.start();

        TIMER_CHECK_THREAD = new Thread(GlobalThreadGroup, () -> {
            Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted()) {
                synchronized (currentThread) {
                    try {
                        currentThread.wait(TIMER_CHECK_STACKTRACE_WAIT);
                    } catch (InterruptedException ex) {
                    }
                }
                long begin = TimeUtil.currentTimeMillis();
                HashMap<Long, ServerExecutor> hashMap = new HashMap<>(EXC_ID_MAP);
                for (Map.Entry<Long, ServerExecutor> entrySet : hashMap.entrySet()) {
                    ServerExecutor value = entrySet.getValue();
                    value.checkThreadStackTrace();
                    if (value instanceof ServerExecutorQueue2Timer) {
                        ((ServerExecutorQueue2Timer) value).checkExecutorKey();
                    }
                }
                long tmp = (TimeUtil.currentTimeMillis() - begin);
                if (tmp > 2000) {
                    log.error("CheckThreadTimerEventScript cost:" + (TimeUtil.currentTimeMillis() - begin));
                }
            }
        }, "CHECK_THREAD_STACK_TRACE");
        TIMER_CHECK_THREAD.start();
    }

    public static void close() {
        HashMap<Long, ServerExecutor> hashMap = new HashMap<>(EXC_ID_MAP);
        for (Map.Entry<Long, ServerExecutor> entrySet : hashMap.entrySet()) {
            ServerExecutor value = entrySet.getValue();
            value.close();
        }
    }

    public static boolean isInitEnd() {
        return initEnd;
    }

    public static void setInitEnd(boolean initEnd) {
        ExecutorFactory.initEnd = initEnd;
    }

    /**
     * 发送线程异常通知
     *
     * @param throwable
     */
    public static void noticeThreadException(Throwable throwable) {
        if (noticeThreadExceptionAll != null) {
            noticeThreadExceptionAll.noticeThreadException(throwable);
        }
    }

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
     * @param noticeThreadException
     */
    public static void setNoticeThreadExceptionAll(NoticeThreadException noticeThreadException) {
        noticeThreadExceptionAll = noticeThreadException;
    }

    public static ServerExecutor newServerExecutor(ExecutorKey executorKey) {
        return newServerExecutor(executorKey, 1);
    }

    public static ServerExecutor newServerExecutor(ExecutorKey executorKey, int threadCount) {
        return newServerExecutor(executorKey, threadCount, Integer.MAX_VALUE);
    }

    public static ServerExecutor newServerExecutor(ExecutorKey executorKey, int threadCount, int capacity) {
        return newServerExecutor(executorKey, GlobalThreadGroup, threadCount, capacity);
    }

    public static ServerExecutor newServerExecutor(ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        return newServerExecutor(ExecutorType.User, executorKey, threadGroup, threadCount, capacity);
    }

    public static ServerExecutor newServerExecutor(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        ServerExecutor executorService = new ServerExecutor(threadType, executorKey, threadGroup, threadCount, capacity);
        addExecutorService(executorService);
        return executorService;
    }

    public static ServerExecutor2Timer newServerExecutor2Timer(ExecutorKey executorKey) {
        return newServerExecutor2Timer(executorKey, 1);
    }

    public static ServerExecutor2Timer newServerExecutor2Timer(ExecutorKey executorKey, int threadCount) {
        return newServerExecutor2Timer(executorKey, threadCount, Integer.MAX_VALUE);
    }

    public static ServerExecutor2Timer newServerExecutor2Timer(ExecutorKey executorKey, int threadCount, int capacity) {
        return newServerExecutor2Timer(executorKey, GlobalThreadGroup, threadCount, capacity);
    }

    public static ServerExecutor2Timer newServerExecutor2Timer(ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        return newServerExecutor2Timer(ExecutorType.User, executorKey, threadGroup, threadCount, capacity);
    }

    public static ServerExecutor2Timer newServerExecutor2Timer(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        ServerExecutor2Timer executorService = new ServerExecutor2Timer(threadType, executorKey, threadGroup, threadCount, capacity);
        addExecutorService(executorService);
        return executorService;
    }

    public static ServerExecutorQueue newServerExecutorQueue(ExecutorKey executorKey) {
        return newServerExecutorQueue(executorKey, 1);
    }

    public static ServerExecutorQueue newServerExecutorQueue(ExecutorKey executorKey, int threadCount) {
        return newServerExecutorQueue(executorKey, threadCount, Integer.MAX_VALUE);
    }

    public static ServerExecutorQueue newServerExecutorQueue(ExecutorKey executorKey, int threadCount, int capacity) {
        return newServerExecutorQueue(executorKey, GlobalThreadGroup, threadCount, capacity);
    }

    public static ServerExecutorQueue newServerExecutorQueue(ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        return newServerExecutorQueue(ExecutorType.User, executorKey, threadGroup, threadCount, Integer.MAX_VALUE);
    }

    public static ServerExecutorQueue newServerExecutorQueue(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        ServerExecutorQueue executorService = new ServerExecutorQueue(threadType, executorKey, threadGroup, threadCount, capacity);
        addExecutorService(executorService);
        return executorService;
    }

    public static ServerExecutorQueue2Timer newServerExecutorQueue2Timer(ExecutorKey executorKey) {
        return newServerExecutorQueue2Timer(executorKey, 1);
    }

    public static ServerExecutorQueue2Timer newServerExecutorQueue2Timer(ExecutorKey executorKey, int threadCount) {
        return newServerExecutorQueue2Timer(executorKey, threadCount, Integer.MAX_VALUE);
    }

    public static ServerExecutorQueue2Timer newServerExecutorQueue2Timer(ExecutorKey executorKey, int threadCount, int capacity) {
        return newServerExecutorQueue2Timer(executorKey, GlobalThreadGroup, threadCount, capacity);
    }

    public static ServerExecutorQueue2Timer newServerExecutorQueue2Timer(ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        return newServerExecutorQueue2Timer(ExecutorType.User, executorKey, threadGroup, threadCount, capacity);
    }

    public static ServerExecutorQueue2Timer newServerExecutorQueue2Timer(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        ServerExecutorQueue2Timer executorService = new ServerExecutorQueue2Timer(threadType, executorKey, threadGroup, threadCount, capacity);
        addExecutorService(executorService);
        return executorService;
    }

    /**
     * 增加队列线程执行器到控制器
     *
     * @param executorService
     */
    public static void addExecutorService(ServerExecutor executorService) {
        if (EXC_KEY_MAP.containsKey(executorService.getExecutorKey())) {
            throw new UnsupportedOperationException("executorService.getExecutorKey() 重复");
        }
        EXC_ID_MAP.put(executorService.getThreadId(), executorService);
        EXC_KEY_MAP.put(executorService.getExecutorKey(), executorService);
    }

    /**
     *
     * @param executorKey
     * @return
     */
    public static ServerExecutor getExecutor(ExecutorKey executorKey) {
        return EXC_KEY_MAP.get(executorKey);
    }

    public static <T extends ServerExecutor> T getExecutor(ExecutorKey executorKey, Class<T> clazz) {
        return (T) EXC_KEY_MAP.get(executorKey);
    }

    /**
     *
     * @param tid
     * @return
     */
    public static ServerExecutor getExecutor(long tid) {
        ServerExecutor get = EXC_ID_MAP.get(tid);
        return get;
    }

    /**
     *
     * @param <T>
     * @param tid
     * @param clazz
     * @return
     */
    public static <T extends ServerExecutor> T getExecutor(long tid, Class<T> clazz) {
        return (T) EXC_ID_MAP.get(tid);
    }

    /**
     *
     * @return
     */
    public static ServerExecutor currentExecutor() {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof BaseThread) {
            return getExecutor(currentThread.getId());
        }
        return null;
    }

    /**
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T extends ServerExecutor> T currentExecutor(Class<T> clazz) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof BaseThread) {
            return (T) getExecutor(currentThread.getId());
        }
        return null;
    }

}
