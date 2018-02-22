package net.sz.framework.thread;

import java.io.Serializable;
import net.sz.framework.util.LongId0;
import net.sz.framework.util.ObjectAttribute;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class BaseTask implements Runnable, Cloneable, Serializable {

    private static final long serialVersionUID = 4196020659994845804L;
    private static final LongId0 LONG_ID_UTIL = new LongId0();

    //运行时数据
    private transient ObjectAttribute tmpOthers = null;
    //任务创建的时间
    private long createTime;
    //任务的唯一id
    private long _taskId;
    /**
     * 当前执行的次数
     */
    private int execCount;
    //取消的任务
    private boolean cancel = false;
    /*是否已经在执行了*/
    private volatile boolean runEnd = false;
    private volatile long runThreadId = 0;

    public void setRunThreadId(long runThreadId) {
        this.runThreadId = runThreadId;
    }

    public void setRunEnd(boolean runEnd) {
        synchronized (this) {
            this.runEnd = runEnd;
        }
    }

    /**
     * 等待任务执行完成,默认等待20毫秒
     *
     */
    public void awaitEnd() {
        this.awaitEnd(20);
    }

    /**
     * 等待任务执行
     *
     * @param timeout 等待毫秒数
     */
    public void awaitEnd(int timeout) {
        if (!runEnd) {
            synchronized (this) {
                if (!runEnd) {
                    if (Thread.currentThread().getId() != runThreadId) {
                        try {
                            this.wait(timeout);
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                        }
                    }
                }
            }
        }
    }

    /**
     * 通知
     */
    public void anotifyEnd() {
        synchronized (this) {
            this.notify();
        }
    }

    /**
     * 通知所有等待
     */
    public void anotifyEndAll() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    public BaseTask() {
        this.createTime = TimeUtil.currentTimeMillis();
        this.cancel = false;
        this._taskId = LONG_ID_UTIL.getId();
    }

    /**
     * 当前任务的id
     *
     * @return
     */
    public long getTaskId() {
        return _taskId;
    }

    @Deprecated
    public void setTaskId(long _taskId) {
        this._taskId = _taskId;
    }

    /**
     * 成功执行后回调
     */
    public void onSuccessCallBack() {
    }

    /**
     * 执行遇到异常回调
     *
     * @param throwable
     */
    public void onErrorCallBack(Throwable throwable) {
    }

    /**
     * 创建时间
     *
     * @return
     */
    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * 临时变量
     *
     * @return
     */
    public ObjectAttribute getTmpOthers() {
        if (tmpOthers == null) {
            tmpOthers = new ObjectAttribute();
        }
        return tmpOthers;
    }

    /**
     * 谨慎调用
     *
     * @param tmpOthers
     * @deprecated
     */
    @Deprecated
    public void setTmpOthers(ObjectAttribute tmpOthers) {
        this.tmpOthers = tmpOthers;
    }

    /**
     * 是否已经取消
     *
     * @return
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * 设置任务取消状态，不会通知
     *
     * @param cancel
     */
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * 当前执行次数
     *
     * @return
     */
    public int getExecCount() {
        return execCount;
    }

    /**
     * 当前执行次数
     *
     * @param execCount
     * @deprecated
     */
    @Deprecated
    public void setExecCount(int execCount) {
        this.execCount = execCount;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BaseTask tm = null;
        tm = (BaseTask) super.clone();
        tm._taskId = LONG_ID_UTIL.getId();
        if (this.tmpOthers == null) {
            tm.tmpOthers = null;
        } else {
            tm.tmpOthers = (ObjectAttribute) tmpOthers.clone();
        }
        return tm;
    }

    public void release() {
    }

    @Override
    public String toString() {
        return "createTime=" + createTime + ", _taskEventId=" + _taskId + ", cancel=" + cancel;
    }
}
