package net.sz.game.engine.thread;

import java.io.Serializable;
import net.sz.game.engine.struct.ObjectAttribute;
import net.sz.game.engine.utils.LongIdUtil;
import org.apache.log4j.Logger;

/**
 * 任务模型
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class TaskEvent implements Serializable, Cloneable {

    public static final Logger log = Logger.getLogger(TaskEvent.class);
    private static final long serialVersionUID = 4196020659994845804L;

    private static final LongIdUtil LONG_ID_UTIL = new LongIdUtil(1);

    //运行时数据
    private transient final ObjectAttribute<Object> tmpParameter = new ObjectAttribute<Object>();
    //任务创建的时间
    private long createTime;
    //任务的唯一id
    private long _taskEventId;
    //取消的任务
    private boolean cancel = false;

    public TaskEvent() {
        this.tmpParameter.put("submitTime", System.currentTimeMillis());
        this.createTime = System.currentTimeMillis();
        this.cancel = false;
        this._taskEventId = LONG_ID_UTIL.getId();
    }

    /**
     * 当前任务的id
     *
     * @return
     */
    public long getTaskEventId() {
        return _taskEventId;
    }

    @Deprecated
    public void setTaskEventId(long _taskEventId) {
        this._taskEventId = _taskEventId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getSubmitTime() {
        return this.tmpParameter.getlongValue("submitTime");
    }

    public ObjectAttribute<Object> getTmpParameter() {
        return tmpParameter;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public abstract void run();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

}
