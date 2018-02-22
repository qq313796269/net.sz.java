package net.sz.framework.thread;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ExecutorKey implements Serializable, Cloneable {

    static private final ConcurrentHashMap<Serializable, ExecutorKey> ExecutorKeyValues = new ConcurrentHashMap<>();

    /**
     * __default__key
     */
    public static final ExecutorKey DEFAULT_KEY = ExecutorKey.valueOf("__default__key");

    /**
     *
     * @param key
     * @return
     */
    public static synchronized ExecutorKey valueOf(Serializable key) {
        ExecutorKey executorKey;
        if (key == null) {
            executorKey = DEFAULT_KEY;
        } else {
            executorKey = ExecutorKeyValues.get(key);
            if (executorKey == null) {
                executorKey = new ExecutorKey(key);
                ExecutorKeyValues.put(key, executorKey);
            }
        }
        executorKey.update();
        return executorKey;
    }

    Serializable key;
    long updateTime;

    private ExecutorKey(Serializable key) {
        this.key = key;
        this.updateTime = System.currentTimeMillis();
        ExecutorKeyValues.put(key, this);

    }

    /**
     * 指定字符串的唯一实例
     *
     * @return
     */
    public Serializable getKey() {
        return key;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void update() {
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.key);
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
        final ExecutorKey other = (ExecutorKey) obj;
        return this.key.equals(other.key);
    }

    @Override
    public String toString() {
        return "{" + "key=" + key + ", updateTime=" + updateTime + '}';
    }

}
