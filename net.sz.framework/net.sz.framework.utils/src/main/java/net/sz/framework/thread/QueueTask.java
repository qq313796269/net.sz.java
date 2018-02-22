package net.sz.framework.thread;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class QueueTask extends BaseTask implements Serializable, Cloneable {

    private static final long serialVersionUID = 1651000517288972302L;
    private final ExecutorKey key;
    private volatile Boolean putEnd = false;
    private final LinkedBlockingQueue<BaseTask> taskModels = new LinkedBlockingQueue<>();

    public QueueTask(ExecutorKey key) {
        this.key = key;
    }

    public ExecutorKey getKey() {

        return key;
    }

    public int size() {
        return taskModels.size();
    }

    public boolean isEmpty() {
        return taskModels.isEmpty();
    }

    public boolean add(BaseTask taskModel) {
        return taskModels.add(taskModel);
    }

    /**
     * 非阻塞效果
     *
     * @return
     */
    public BaseTask poll() {
        return taskModels.poll();
    }

    /**
     * 阻塞效果
     *
     * @return
     * @throws java.lang.InterruptedException
     */
    public BaseTask take() throws InterruptedException {
        return taskModels.take();
    }

    public Boolean isPutEnd() {
        return putEnd;
    }

    public void setPutEnd(Boolean putEnd) {
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
        final QueueTask other = (QueueTask) obj;
        return Objects.equals(this.key, other.key);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("尚未实现");
    }

}
