package net.sz.framework.util.concurrent;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <K>
 * @param <V>
 */
public class SzConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V>
        implements ConcurrentMap<K, V>, Serializable, Cloneable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 7702287143229833756L;

    public SzConcurrentHashMap() {
    }

    public SzConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public SzConcurrentHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public SzConcurrentHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SzConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }


}
