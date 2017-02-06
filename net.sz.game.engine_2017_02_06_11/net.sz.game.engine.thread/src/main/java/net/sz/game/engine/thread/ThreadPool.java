package net.sz.game.engine.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.scripts.manager.ScriptManager;
import net.sz.game.engine.utils.LongIdUtil;
import net.sz.game.engine.thread.timer.GlobScriptTimerThread;
import net.sz.game.engine.thread.timer.PrintlnServerMemoryTimerEvent;
import org.apache.log4j.Logger;

/**
 * 线程管理器
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ThreadPool {

    static private final Logger log = Logger.getLogger(ThreadPool.class);
    /**
     * 全局线程
     */
    static public final long GlobalThread;
    static public final LongIdUtil THREAD_ID_UTIL = new LongIdUtil(1);
    static public final ThreadGroup GlobalThreadGroup = new ThreadGroup("Global ThreadGroup");
    static public final ThreadGroup UnknownThreadGroup = new ThreadGroup("Unknown ThreadGroup");

    /**
     * 线程模型内部错误通知
     */
    static public NoticeThreadException NoticeThreadException_All = null;

    /**
     * 全局脚本定时器执行线程
     */
    static final GlobScriptTimerThread GlobalScriptTimerThread;
    /**
     * 检查线程卡死情况
     */
    static final CheckThreadTimerThread CheckThreadTimerThreadModel;
    /**
     * 唤醒线程内部定时触发任务线程
     */
    static final TimerThread GlobalTimerThread;
    /**
     * 这里的线程，内部定时器只会在 StarEnd = true 时执行
     */
    static final ConcurrentHashMap<Long, ThreadRunnable> threadMap = new ConcurrentHashMap<>();
    /**
     * 线程池，备用池
     */
    static final ConcurrentHashMap<Long, ThreadRunnable> threadPoolMap = new ConcurrentHashMap<>();

    //表示服务器是否启动完成
    static private boolean StarEnd = false;

    public static boolean isStarEnd() {
        return StarEnd;
    }

    public static void setStarEnd(boolean StarEnd) {
        ThreadPool.StarEnd = StarEnd;
    }

    public static void main(String[] args) {

        ThreadPool.addTimerTask(GlobalThread, new TimerTaskEvent(1000) {

            @Override
            public void run() {

                log.error("ssssss");
            }
        });
    }

    static {

        ThreadRunnable threadModel = new ThreadRunnable(ThreadType.Sys, GlobalThreadGroup, "GloblThread", 1);

        //查询服务器消耗定时模型
        threadModel.addTimerTask(new PrintlnServerMemoryTimerEvent());

        //创建全局线程
        GlobalThread = threadModel.getId();

        threadMap.put(GlobalThread, threadModel);

        /*创建全局脚本执行定时器线程*/
        GlobalScriptTimerThread = new GlobScriptTimerThread(ScriptManager.getInstance().getBaseScriptEntry());
        GlobalScriptTimerThread.start();

        /* 检查线程卡死情况 */
        CheckThreadTimerThreadModel = new CheckThreadTimerThread();
        CheckThreadTimerThreadModel.start();

        /*创建定时器线程*/
        GlobalTimerThread = new TimerThread();
        GlobalTimerThread.start();
    }

    /**
     * 获取线程池中所有线程
     *
     * @return
     */
    static public ConcurrentHashMap<Long, ThreadRunnable> getThreadMap() {
        return threadMap;
    }

    /**
     * 在停止服务器时调用
     *
     * @deprecated
     */
    @Deprecated
    static public void removeAll() {
        HashMap<Long, ThreadRunnable> hashMap = new HashMap<>(threadMap);
        for (Map.Entry<Long, ThreadRunnable> entry : hashMap.entrySet()) {
            ThreadRunnable value = entry.getValue();
            value.stop();
        }
    }

    /**
     * 把线程置为删除状态，返回线程池，
     *
     * @param tid
     * @return
     */
    static public ThreadRunnable remove(long tid) {
        ThreadRunnable remove = threadMap.remove(tid);
        if (remove != null) {
            remove.stop();
            threadPoolMap.put(tid, remove);
        }
        return remove;
    }

    /**
     * 获取线程池的一个线程
     *
     * @param name
     * @param threadcount
     * @return
     */
    static public ThreadRunnable getThread(String name, int threadcount) {
        long threadId = 0;
        if (threadPoolMap.size() > 0) {
            if (threadPoolMap.keys().hasMoreElements()) {
                threadId = threadPoolMap.keys().nextElement();
            }
        }
        return threadMap.get(threadId);
    }

    /**
     * 获取线程池的一个线程
     *
     * @param threadId
     * @return
     */
    static public ThreadRunnable getThread(long threadId) {
        ThreadRunnable get = threadMap.get(threadId);
        if (get == null) {
            log.error("无法找到线程模型：" + threadId, new Exception("无法找到线程模型：" + threadId));
        }
        return get;
    }

    // <editor-fold defaultstate="collapsed" desc="向线程池注册一个线程 static public long addThread(String name)">
    /**
     * 向线程池注册一个线程<br>
     * 默认分组 UnknownThreadGroup<br>
     * 默认类型 ThreadType.User
     *
     * @param name 线程名称
     * @return
     */
    static public long addThread(String name) {
        return addThread(ThreadType.User, UnknownThreadGroup, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="向线程池注册一个线程 static public long addThread(ThreadType threadType, String name)">
    /**
     * 向线程池注册一个线程<br>
     * 默认分组 UnknownThreadGroup
     *
     * @param threadType
     * @param name 线程名称
     * @return
     */
    static public long addThread(ThreadType threadType, String name) {
        return addThread(threadType, UnknownThreadGroup, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="向线程池注册一个线程 static public long addThread(ThreadType threadType, String name, int threadcount)">
    /**
     * 向线程池注册一个线程<br>
     * 默认分组 UnknownThreadGroup<br>
     * 默认类型 ThreadType.User
     *
     * @param name 线程名称
     * @param threadcount 线程量
     * @return
     */
    static public long addThread(String name, int threadcount) {
        return addThread(ThreadType.User, UnknownThreadGroup, name, threadcount);
    }
    // </editor-fold>

    /**
     * 向线程池注册一个线程<br>
     * 默认分组 UnknownThreadGroup
     *
     * @param threadType
     * @param name 线程名称
     * @param threadcount 线程量
     * @return
     */
    static public long addThread(ThreadType threadType, String name, int threadcount) {
        return addThread(threadType, UnknownThreadGroup, name, threadcount);
    }

    /**
     * 向线程池注册一个线程<br>
     * 默认类型 ThreadType.User
     *
     * @param group 线程分组信息
     * @param name 线程名称
     * @return
     */
    static public long addThread(ThreadGroup group, String name) {
        return addThread(ThreadType.User, group, name, 1);
    }

    /**
     * 向线程池注册一个线程
     *
     * @param threadType
     * @param group 线程分组信息
     * @param name 线程名称
     * @return
     */
    static public long addThread(ThreadType threadType, ThreadGroup group, String name) {
        return addThread(threadType, group, name, 1);
    }

    /**
     * 向线程池注册一个线程<br>
     * 默认类型 ThreadType.User
     *
     * @param group 线程分组信息
     * @param name 线程名称
     * @param threadcount 线程量
     * @return
     */
    static public long addThread(ThreadGroup group, String name, int threadcount) {
        return addThread(ThreadType.User, group, name, threadcount);
    }

    /**
     * 向线程池注册一个线程
     *
     * @param threadType
     * @param group 线程分组信息
     * @param name 线程名称
     * @param threadcount 线程量
     * @return
     */
    static public long addThread(ThreadType threadType, ThreadGroup group, String name, int threadcount) {
        ThreadRunnable threadModel = new ThreadRunnable(threadType, group, name, threadcount);
        return addThread(threadModel);
    }

    /**
     * 向线程池注册一个线程
     *
     * @param threadModel
     * @return
     */
    static public long addThread(ThreadRunnable threadModel) {
        threadMap.put(threadModel.getId(), threadModel);
        return threadModel.getId();
    }

    /**
     * 添加任务
     *
     * @param threadId
     * @param task
     * @return
     */
    static public boolean addTask(long threadId, TaskEvent task) {
        ThreadRunnable threadModel = getThread(threadId);
        if (threadModel != null) {
            threadModel.addTask(task);
            return true;
        }
        return false;
    }

    /**
     * 添加定时器任务
     *
     * @param threadId
     * @param task
     * @return
     */
    static public boolean addTimerTask(long threadId, TimerTaskEvent task) {
        ThreadRunnable threadModel = getThread(threadId);
        if (threadModel != null) {
            threadModel.addTimerTask(task);
            return true;
        }
        return false;
    }

    static public long getCurrentThreadID() {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof ThreadModel) {
            long threadId = currentThread.getId();
            ThreadRunnable threadModel = getThread(threadId);
            if (threadModel != null) {
                return threadId;
            }
        }
        return 0;
    }

    /**
     * 添加任务,添加任务到当前线程
     *
     * @param task
     * @return
     */
    static public boolean addCurrentThreadTask(TaskEvent task) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof ThreadModel) {
            long threadId = currentThread.getId();
            ThreadRunnable threadModel = getThread(threadId);
            if (threadModel != null) {
                threadModel.addTask(task);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加定时器任务，添加任务到当前线程
     *
     * @param task
     * @return
     */
    static public boolean addCurrentThreadTimerTask(TimerTaskEvent task) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof ThreadModel) {
            long threadId = currentThread.getId();
            ThreadRunnable threadModel = getThread(threadId);
            if (threadModel != null) {
                threadModel.addTimerTask(task);
                return true;
            }
        }
        return false;
    }

}
