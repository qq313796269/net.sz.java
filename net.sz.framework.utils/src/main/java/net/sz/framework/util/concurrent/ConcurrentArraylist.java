package net.sz.framework.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 安全的list，但是不能把保证并发瓶颈
 * <br>
 * 高并发可能出现 block
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <E>
 */
public class ConcurrentArraylist<E> extends ArrayList<E> implements Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -1148457730425654441L;

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        synchronized (this) {
            return super.removeIf(filter); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public Iterator<E> iterator() {
        synchronized (this) {
            return super.iterator(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        synchronized (this) {
            return super.removeAll(c); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        synchronized (this) {
            return super.addAll(index, c); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        synchronized (this) {
            return super.addAll(c); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (this) {
            return super.remove(o); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E remove(int index) {
        synchronized (this) {
            return super.remove(index); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void add(int index, E element) {
        synchronized (this) {
            super.add(index, element); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean add(E e) {
        synchronized (this) {
            return super.add(e); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * 如果不存在相同就增加，如果存在相同的就更新
     *
     * @param e
     * @return
     */
    public boolean update(E e) {
        synchronized (this) {
            if (contains(e)) {
                remove(e);
            }
            return super.add(e); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E get(int index) {
        synchronized (this) {
            return super.get(index); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int indexOf(Object o) {
        synchronized (this) {
            return super.indexOf(o); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean contains(Object o) {
        synchronized (this) {
            return super.contains(o); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (this) {
            return super.isEmpty(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            return super.size(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void sort(Comparator<? super E> c) {
        synchronized (this) {
            super.sort(c); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        super.forEach(action); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public E set(int index, E element) {
        synchronized (this) {
            return super.set(index, element); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        synchronized (this) {
            return super.toArray(a); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (this) {
            return super.toArray(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        synchronized (this) {
            return super.lastIndexOf(o); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void trimToSize() {
        synchronized (this) {
            super.trimToSize(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public Object clone() {
        synchronized (this) {
            return super.clone(); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
