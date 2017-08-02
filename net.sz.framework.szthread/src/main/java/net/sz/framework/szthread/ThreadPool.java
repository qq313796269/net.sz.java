package net.sz.framework.szthread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.struct.thread.ThreadType;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.timer.PrintlnServerMemoryTimerEvent;

/**
 * 线程管理器
 * <br>
 * 需要设置 GlobalUtil.SERVERSTARTEND = true; 标识服务器启动完成
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ThreadPool {

    private static final SzLogger log = SzLogger.getLogger();
    /**
     * 全局线程
     */
    static public final SzThread GlobalThread;

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
    static final ConcurrentHashMap<Long, BaseThreadRunnable> threadMap = new ConcurrentHashMap<>();
    /**
     * 线程池，备用池
     */
    static final ConcurrentHashMap<Long, BaseThreadRunnable> threadPoolMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ThreadGroup parent = BaseThreadRunnable.GlobalThreadGroup.getParent();
        log.error(parent);
        GlobalThread.addTimerTask(new TimerTaskModel(1000) {

            @Override
            public void run() {

                log.error("ssssss");
            }
        });
    }

    static {

        GlobalThread = new SzThread(ThreadType.Sys, BaseThreadRunnable.GlobalThreadGroup, "Global-Thread", 1);

        //查询服务器消耗定时模型
        GlobalThread.addTimerTask(new PrintlnServerMemoryTimerEvent());

        threadMap.put(GlobalThread.getTid(), GlobalThread);

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
    static public ConcurrentHashMap<Long, BaseThreadRunnable> getThreadMap() {
        return threadMap;
    }

    /**
     * 在停止服务器时调用
     *
     * @deprecated
     */
    @Deprecated
    static public void removeAll() {
        HashMap<Long, BaseThreadRunnable> hashMap = new HashMap<>(threadMap);
        for (Map.Entry<Long, BaseThreadRunnable> entry : hashMap.entrySet()) {
            BaseThreadRunnable value = entry.getValue();
            if (value.getThreadType() == ThreadType.User) {
                value.close();
            }
        }
        threadMap.clear();
    }

    /**
     * 把线程置为删除状态，返回线程池，
     *
     * @param tid
     * @return
     */
    static public BaseThreadRunnable remove(long tid) {
        BaseThreadRunnable remove = threadMap.remove(tid);
        if (remove != null) {
            remove.close();
        }
        return remove;
    }

    /**
     * 把线程置为暂停状态，放回线程池，
     *
     * @param tid
     * @return
     */
    @Deprecated
    static public BaseThreadRunnable waiting(long tid) {
        BaseThreadRunnable remove = threadMap.remove(tid);
        if (remove != null) {
            remove.setWaiting(true);
            synchronized (threadPoolMap) {
                threadPoolMap.put(tid, remove);
            }
        }
        return remove;
    }

    /**
     * 获取线程池的一个线程
     *
     * @return
     */
    @Deprecated
    static public BaseThreadRunnable getNextThreadPool() {
        long threadId = 0;
        BaseThreadRunnable threadRunnable = null;
        synchronized (threadPoolMap) {
            if (threadPoolMap.size() > 0) {
                if (threadPoolMap.keys().hasMoreElements()) {
                    threadId = threadPoolMap.keys().nextElement();
                    threadRunnable = threadPoolMap.remove(threadId);
                }
            }
        }
        return threadRunnable;
    }

    /**
     * 获取线程
     *
     * @param threadId
     * @return
     */
    static public BaseThreadRunnable getThread(long threadId) {
        BaseThreadRunnable get = threadMap.get(threadId);
        if (get == null) {
            log.error("无法找到线程模型：" + threadId, new Exception("无法找到线程模型：" + threadId));
        }
        return get;
    }

    // <editor-fold desc="向线程池注册一个线程 static public long addThread(String name)">
    /**
     * 向线程池注册一个线程<br>
     * 默认分组 UnknownThreadGroup<br>
     * 默认类型 ThreadType.User
     *
     * @param name 线程名称
     * @return
     */
    static public BaseThreadRunnable addThread(String name) {
        return addThread(ThreadType.User, BaseThreadRunnable.UnknownThreadGroup, name);
    }
    // </editor-fold>

    // <editor-fold desc="向线程池注册一个线程 static public long addThread(ThreadType threadType, String name)">
    /**
     * 向线程池注册一个线程<br>
     * 默认分组 UnknownThreadGroup
     *
     * @param threadType
     * @param name 线程名称
     * @return
     */
    static public BaseThreadRunnable addThread(ThreadType threadType, String name) {
        return addThread(threadType, BaseThreadRunnable.UnknownThreadGroup, name);
    }
    // </editor-fold>

    // <editor-fold desc="向线程池注册一个线程 static public long addThread(ThreadType threadType, String name, int threadcount)">
    /**
     * 向线程池注册一个线程<br>
     * 默认分组 UnknownThreadGroup<br>
     * 默认类型 ThreadType.User
     *
     * @param name 线程名称
     * @param threadcount 线程量
     * @return
     */
    static public BaseThreadRunnable addThread(String name, int threadcount) {
        return addThread(ThreadType.User, BaseThreadRunnable.UnknownThreadGroup, name, threadcount);
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
    static public BaseThreadRunnable addThread(ThreadType threadType, String name, int threadcount) {
        return addThread(threadType, BaseThreadRunnable.UnknownThreadGroup, name, threadcount);
    }

    /**
     * 向线程池注册一个线程<br>
     * 默认类型 ThreadType.User
     *
     * @param group 线程分组信息
     * @param name 线程名称
     * @return
     */
    static public BaseThreadRunnable addThread(ThreadGroup group, String name) {
        return addThread(group, name, 1);
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
    static public BaseThreadRunnable addThread(ThreadGroup group, String name, int threadcount) {
        return addThread(ThreadType.User, group, name, threadcount);
    }

    /**
     * 向线程池注册一个线程
     *
     * @param threadType
     * @param group 线程分组信息
     * @param name 线程名称
     * @return
     */
    static public BaseThreadRunnable addThread(ThreadType threadType, ThreadGroup group, String name) {
        return addThread(threadType, group, name, 1);
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
    static public BaseThreadRunnable addThread(ThreadType threadType, ThreadGroup group, String name, int threadcount) {
        SzThread threadModel = new SzThread(threadType, group, name, threadcount);
        return addThread(threadModel);
    }

    /**
     * 向线程池注册一个线程
     *
     * @param threadModel
     * @return
     */
    static public BaseThreadRunnable addThread(BaseThreadRunnable threadModel) {
        threadMap.put(threadModel.getTid(), threadModel);
        return threadModel;
    }

    /**
     * 添加任务
     *
     * @param threadId
     * @param task
     * @return
     */
    static public boolean addTask(long threadId, TaskModel task) {
        BaseThreadRunnable threadModel = getThread(threadId);
        if (threadModel != null) {
            threadModel.addTask(task);
            return true;
        }
        return false;
    }

    /**
     * 添加任务
     *
     * @param threadId
     * @param key
     * @param task
     * @return
     */
    static public boolean addTask(long threadId, String key, TaskModel task) {
        BaseThreadRunnable threadModel = getThread(threadId);
        if (threadModel != null) {
            threadModel.addTask(key, task);
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
    static public boolean addTimerTask(long threadId, TimerTaskModel task) {
        BaseThreadRunnable threadModel = getThread(threadId);
        if (threadModel != null) {
            threadModel.addTimerTask(task);
            return true;
        }
        return false;
    }

    /**
     * 添加定时器任务
     *
     * @param threadId
     * @param key
     * @param task
     * @return
     */
    static public boolean addTimerTask(long threadId, String key, TimerTaskModel task) {
        BaseThreadRunnable threadModel = getThread(threadId);
        if (threadModel != null) {
            threadModel.addTimerTask(key, task);
            return true;
        }
        return false;
    }

    /**
     * 获取当前线程
     *
     * @return
     */
    static public long getCurrentThreadID() {
        return BaseThreadRunnable.getCurrentThreadID();
    }

    /**
     * 添加任务,添加任务到当前线程
     *
     * @param task
     * @return
     */
    static public boolean addCurrentThreadTask(TaskModel task) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof SzThreadModel) {
            long threadId = currentThread.getId();
            BaseThreadRunnable threadModel = getThread(threadId);
            if (threadModel != null) {
                threadModel.addTask(task);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加任务,添加任务到当前线程
     *
     * @param key
     * @param task
     * @return
     */
    static public boolean addCurrentThreadTask(String key, TaskModel task) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof SzThreadModel) {
            long threadId = currentThread.getId();
            BaseThreadRunnable threadModel = getThread(threadId);
            if (threadModel != null) {
                threadModel.addTask(key, task);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加定时器任务，添加任务到当前线程
     * <br>
     * 需要确认不是场景地图线程
     *
     * @param task
     * @return
     */
    @Deprecated
    static public boolean addCurrentThreadTimerTask(TimerTaskModel task) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof SzThreadModel) {
            long threadId = currentThread.getId();
            BaseThreadRunnable threadModel = getThread(threadId);
            if (threadModel != null) {
                threadModel.addTimerTask(task);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加定时器任务，添加任务到当前线程
     * <br>
     * 需要确认不是场景地图线程
     *
     * @param key key值
     * @param task 任务
     * @return
     */
    @Deprecated
    static public boolean addCurrentThreadTimerTask(String key, TimerTaskModel task) {
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof SzThreadModel) {
            long threadId = currentThread.getId();
            BaseThreadRunnable threadModel = getThread(threadId);
            if (threadModel != null) {
                threadModel.addTimerTask(key, task);
                return true;
            }
        }
        return false;
    }

}
