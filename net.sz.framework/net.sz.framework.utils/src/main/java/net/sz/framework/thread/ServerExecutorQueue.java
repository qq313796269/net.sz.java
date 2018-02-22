package net.sz.framework.thread;

import java.io.Closeable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.szlog.SzLogger;

/**
 * 区块划分任务队列执行器
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ServerExecutorQueue extends ServerExecutor implements Serializable, Cloneable, Closeable, Runnable, Thread.UncaughtExceptionHandler {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 3934829859104387467L;

    /*                                key  任务 */
    private final ConcurrentHashMap<ExecutorKey, QueueTask> keyTaskMap = new ConcurrentHashMap<>();

    public ServerExecutorQueue(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup) {
        super(threadType, executorKey, threadGroup);
    }

    public ServerExecutorQueue(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        super(threadType, executorKey, threadGroup, threadCount, capacity);
    }

    /**
     * 检测无用的key
     */
    public void checkExecutorKey() {
        HashMap<ExecutorKey, QueueTask> hashMap = new HashMap<>(this.keyTaskMap);
        for (Map.Entry<ExecutorKey, QueueTask> entry : hashMap.entrySet()) {
            ExecutorKey key = entry.getKey();
            if (System.currentTimeMillis() - key.getUpdateTime() > 5 * 60000) {
                removeExecutorKey(key);
            }
        }
    }

    /**
     *
     * @param key
     */
    protected void addExecutorKey(ExecutorKey key) {
        if (!this.keyTaskMap.containsKey(key)) {
            this.keyTaskMap.put(key, new QueueTask(key));
        }
    }

    /**
     *
     * @param key
     */
    protected void removeExecutorKey(ExecutorKey key) {
        this.keyTaskMap.remove(key);
    }

    /**
     *
     * @param key 如果key ExecutorKey.DEFAULTKEY 并发执行，其余队列执行
     * @param task
     * @return
     */
    public boolean addTask(String key, BaseTask task) {
        return addTask(ExecutorKey.valueOf(key), task);
    }

    /**
     *
     * @param key 如果key ExecutorKey.DEFAULTKEY 并发执行，其余队列执行
     * @param task
     * @return
     */
    public boolean addTask(ExecutorKey key, BaseTask task) {
        if (key == null || ExecutorKey.DEFAULT_KEY.equals(key)) {
            return this.addTask(task);
        }

        QueueTask queueTask;

        synchronized (key) {
            queueTask = this.keyTaskMap.get(key);
            if (queueTask == null) {
                addExecutorKey(key);
                queueTask = this.keyTaskMap.get(key);
            }
        }

        boolean add;

        synchronized (this) {
            add = queueTask.add(task);
            if (!queueTask.isPutEnd()) {
                queueTask.setPutEnd(true);
                this.addTask(queueTask);
            }
        }
        //runQueueTaskAction(queueTask);
        return add;
    }

    @Override
    protected void runTask(BaseThread baseThread, BaseTask task) {
        if (task instanceof QueueTask) {
            QueueTask queueTask = (QueueTask) task;
            BaseTask poll = queueTask.poll();
            if (poll != null) {
                runTask(baseThread, queueTask.getKey(), poll);
            }
//            runQueueTaskAction(queueTask);
            synchronized (this) {
                if (queueTask.isEmpty()) {
                    queueTask.setPutEnd(false);
                } else {
                    queueTask.setPutEnd(true);
                    this.addTask(queueTask);
                }
            }
            return;
        }
        super.runTask(baseThread, task);
    }

    @Override
    protected void runTask(BaseThread baseThread, ExecutorKey executorKey, BaseTask task) {
        /*这里可以对key值做任意重复*/
        super.runTask(baseThread, executorKey, task);
    }

    protected ConcurrentHashMap<ExecutorKey, QueueTask> getKeyTaskMap() {
        return keyTaskMap;
    }

}
