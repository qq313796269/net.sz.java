package net.sz.framework.util;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.utils.ConvertTypeUtil;

/**
 * 辅助键值对存储
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ObjectAttribute<V> extends ConcurrentHashMap<String, V> implements Serializable, Cloneable {

    private static final long serialVersionUID = -5320260807959251398L;

    /**
     * 如果不存在键，返回 null
     *
     * @param <T>
     * @param key
     * @param clazz
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        V obj = super.get(key);
        return (T) ConvertTypeUtil.changeType(obj, clazz);
    }

    /**
     * 调用此方法 删除值是需要保证存在key值和value值 否则空指针报错
     *
     * @param <T>
     * @param key
     * @param clazz
     * @return
     * @deprecated 需要保证存在key值和value值 否则空指针报错 慎重
     */
    @Deprecated
    public <T> T remove(String key, Class<T> clazz) {
        Object obj = super.remove(key);
        return (T) ConvertTypeUtil.changeType(obj, clazz);
    }

    /**
     * 如果未找到也返回 null
     *
     * @param key
     * @return
     */
    public String getStringValue(String key) {
        return this.get(key, String.class);
    }

    /**
     * 如果未找到也返回 0
     *
     * @param key
     * @return
     */
    public int getintValue(String key) {
        Integer get = this.get(key, Integer.class);
        if (get != null) {
            return get;
        }
        return 0;
    }

    /**
     * 如果未找到也返回 null
     *
     * @param key
     * @return
     */
    public Integer getIntegerValue(String key) {
        return this.get(key, Integer.class);
    }

    /**
     * 如果未找到也返回 0
     *
     * @param key
     * @return
     */
    public long getlongValue(String key) {
        Long get = this.get(key, Long.class);
        if (get != null) {
            return get;
        }
        return 0;
    }

    /**
     * 如果未找到也返回 null
     *
     * @param key
     * @return
     */
    public Long getLongValue(String key) {
        return this.get(key, Long.class);
    }

    /**
     * 如果未找到也返回 0
     *
     * @param key
     * @return
     */
    public float getfloatValue(String key) {
        Float get = this.get(key, Float.class);
        if (get != null) {
            return get;
        }
        return 0;
    }

    /**
     * 如果未找到也返回 null
     *
     * @param key
     * @return
     */
    public Float getFloatValue(String key) {
        return this.get(key, Float.class);
    }

    /**
     * 如果未找到也返回 false
     *
     * @param key
     * @return
     */
    public boolean getbooleanValue(String key) {
        Boolean get = this.get(key, Boolean.class);
        if (get != null) {
            return get;
        }
        return false;
    }

    /**
     * 如果未找到也返回 null
     *
     * @param key
     * @return
     */
    public Boolean getBooleanValue(String key) {
        return this.get(key, Boolean.class);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "ObjectAttribute{" + '}';
    }

}
