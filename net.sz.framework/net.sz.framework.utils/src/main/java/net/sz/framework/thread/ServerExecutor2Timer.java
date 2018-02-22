package net.sz.framework.thread;

import java.io.Closeable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.thread.timer.TimerTask;
import net.sz.framework.utils.TimeUtil;

/**
 * 包含执行定时器任务的任务队列执行器
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ServerExecutor2Timer extends ServerExecutor implements Serializable, Cloneable, Closeable, Runnable {

    private static final SzLogger log = SzLogger.getLogger();

    private static final long serialVersionUID = 3071660372429507888L;

    /*                             任务id  定时器     */
    private final ConcurrentHashMap<Long, TimerTask> timerTaskQueue = new ConcurrentHashMap<>();

    public ServerExecutor2Timer(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup) {
        super(threadType, executorKey, threadGroup);
    }

    public ServerExecutor2Timer(ExecutorType threadType, ExecutorKey executorKey, ThreadGroup threadGroup, int threadCount, int capacity) {
        super(threadType, executorKey, threadGroup, threadCount, capacity);
    }

    /**
     * 增加定时器任务
     *
     * @param timertask
     * @return
     */
    public boolean addTimerTask(TimerTask timertask) {
        try {
            //一开始执行一次
            if (timertask.isStartAction()) {
                addTask(timertask);
            }
            this.timerTaskQueue.put(timertask.getTaskId(), timertask);
            return true;
        } catch (Throwable e) {
            log.error(this.toString() + " addTimerTask error", e);
        }
        return false;
    }

    /**
     * 单次执行任务完成后调用
     *
     * @param task
     */
    @Override
    protected void runTaskEnd(BaseTask task) {
        if (task instanceof TimerTask) {
            ((TimerTask) task).setBeforeEnd(true);
            ((TimerTask) task).setLastTime(TimeUtil.currentTimeMillis());
        }
    }

    /**
     * 定时器线程执行器
     */
    @Override
    protected void checkTimerRun() {
        if (!isClosed()) {
            try {
                HashMap<Long, TimerTask> hashMap = new HashMap<>(this.timerTaskQueue);
                for (Map.Entry<Long, TimerTask> entry : hashMap.entrySet()) {
                    TimerTask timerEvent = entry.getValue();
                    if (checkTimerRun(timerEvent)) {
                        this.addTask(timerEvent);
                    }
                    if (checkRemoveTimerRun(timerEvent)) {
                        timerTaskQueue.remove(timerEvent.getTaskId());
                    }
                }
            } catch (Throwable e) {
            }
        }
    }

    /**
     * 定时器线程执行器
     *
     * @param timerEvent
     * @return true 表示需要从源集合删除
     */
    protected boolean checkTimerRun(TimerTask timerEvent) {
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
                if (!timerEvent.isBeforeEnd()) {
                    /*如果上一次尚未执行*/
                    return false;
                }
                if (nowTime >= timerEvent.getStartTime() /*是否满足开始时间*/
                        && (timerEvent.getEndTime() <= 0 || nowTime <= timerEvent.getEndTime()) /*判断结束时间*/
                        && (nowTime - lastTime >= timerEvent.getIntervalTime())) /*判断上次执行到目前是否满足间隔时间*/ {
                    /*设置本次执行尚未结束*/
                    timerEvent.setBeforeEnd(false);
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
    protected boolean checkRemoveTimerRun(TimerTask timerEvent) {
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

}
