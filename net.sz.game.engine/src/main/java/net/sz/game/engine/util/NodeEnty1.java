package net.sz.game.engine.util;

import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NodeEnty1<K, V> {

    private static final Logger log = Logger.getLogger(NodeEnty1.class);
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
    public String toString() {
        return "NodeEnty{" + "k=" + k + ", v=" + v + '}';
    }

}
