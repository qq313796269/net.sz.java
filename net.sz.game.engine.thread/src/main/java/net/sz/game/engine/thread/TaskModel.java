package net.sz.game.engine.thread;

import java.io.Serializable;
import net.sz.game.engine.struct.ObjectAttribute;
import net.sz.game.engine.utils.LongId0Util;

/**
 * 任务模型
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class TaskModel implements Serializable, Cloneable {

    private static final long serialVersionUID = 4196020659994845804L;
    private static final LongId0Util LONG_ID_UTIL = new LongId0Util();

    //运行时数据
    private transient ObjectAttribute<Object> tmpOthers = null;
    //任务创建的时间
    private long createTime;
    //任务的唯一id
    private long _taskId;
    /**
     * 当前执行的次数
     */
    protected int execCount;
    //取消的任务
    private boolean cancel = false;

    public TaskModel() {
        this.createTime = System.currentTimeMillis();
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public ObjectAttribute<Object> getTmpOthers() {
        if (tmpOthers == null) {
            tmpOthers = new ObjectAttribute<Object>();
        }
        return tmpOthers;
    }

    public boolean isCancel() {
        return cancel;
    }

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

    public abstract void run();

    @Override
    public Object clone() throws CloneNotSupportedException {
        TaskModel tm = (TaskModel) super.clone();
        tm._taskId = LONG_ID_UTIL.getId();
        if (this.tmpOthers == null) {
            tm.tmpOthers = null;
        } else {
            tm.tmpOthers = (ObjectAttribute<Object>) tmpOthers.clone();
        }
        return super.clone();
    }

    @Override
    public String toString() {
        return "createTime=" + createTime + ", _taskEventId=" + _taskId + ", cancel=" + cancel;
    }

}
