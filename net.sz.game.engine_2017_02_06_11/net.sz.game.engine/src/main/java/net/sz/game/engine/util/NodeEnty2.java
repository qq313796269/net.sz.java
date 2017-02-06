package net.sz.game.engine.util;

import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * 键值多对，多value
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <K>
 * @param <V>
 */
public class NodeEnty2<K, V> {

    private static final Logger log = Logger.getLogger(NodeEnty2.class);

    public static void main(String[] args) {
        NodeEnty2<String, Object> nodeEnty2 = new NodeEnty2<>("1", "2", 5);
        log.error(nodeEnty2.getValue0());
    }

    private K key = null;
    private V[] values = null;

    public NodeEnty2(K k, V v) {
        this.key = k;
        this.values = (V[]) new Object[1];
        this.values[0] = v;
    }

    public NodeEnty2(K k, V... vs) {
        this.key = k;
        this.values = vs;
    }

    public K getKey() {
        return key;
    }

    /**
     * 返回第 0 个元素
     *
     * @return
     */
    public V getValue0() {
        return values[0];
    }

    public <T> T getValue0(Class<T> t) {
        return (T) values[0];
    }

    public V getValue(int index) {
        return values[index];
    }

    public <T> T getValue(int index, Class<T> t) {
        return (T) values[index];
    }

    public V setValue(int index, V v) {
        values[index] = v;
        return values[index];
    }

    public V[] getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.key);
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
        final NodeEnty2<?, ?> other = (NodeEnty2<?, ?>) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

}
