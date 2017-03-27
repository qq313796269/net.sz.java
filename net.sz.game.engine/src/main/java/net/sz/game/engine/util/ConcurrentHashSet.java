package net.sz.game.engine.util;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <E>
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, java.io.Serializable {

    private static final long serialVersionUID = -8672117787651310382L;

    private ConcurrentHashMap<E, Boolean> map;

    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return map.put(e, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return Objects.equals(map.remove(o), Boolean.TRUE);
    }

    @Override
    public void clear() {
        map.clear();
    }

}
