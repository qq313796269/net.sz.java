package net.sz.framework.szthread;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class QueueTaskModel implements Serializable {

    private static final long serialVersionUID = 1651000517288972302L;
    private final String key;
    private boolean cancel = false;
    private boolean putEnd = false;
    private final ConcurrentLinkedQueue<TaskModel> taskModels = new ConcurrentLinkedQueue<>();

    public QueueTaskModel(String key) {
        this.key = key;
    }

    public String getKey() {

        return key;
    }

    public int size() {
        return taskModels.size();
    }

    public boolean isEmpty() {
        return taskModels.isEmpty();
    }

    public boolean add(TaskModel taskModel) {
        return taskModels.add(taskModel);
    }

    public TaskModel poll() {
        return taskModels.poll();
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isPutEnd() {
        return putEnd;
    }

    public void setPutEnd(boolean putEnd) {
        this.putEnd = putEnd;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueueTaskModel other = (QueueTaskModel) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

}
