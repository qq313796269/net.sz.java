package net.sz.game.engine.util;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <K>
 * @param <V>
 */
public class NodeEnty1<K, V> implements Serializable {

    private static final long serialVersionUID = 6996874619397305032L;

    private K k;
    private V v;

    public NodeEnty1() {
    }

    public NodeEnty1(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getKey() {
        return k;
    }

    public V getValue() {
        return v;
    }

    public void setK(K k) {
        this.k = k;
    }

    public void setV(V v) {
        this.v = v;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.k);
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
        final NodeEnty1<?, ?> other = (NodeEnty1<?, ?>) obj;
        if (!Objects.equals(this.k, other.k)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NodeEnty{" + "k=" + k + ", v=" + v + '}';
    }

}
