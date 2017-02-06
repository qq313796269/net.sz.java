package net.sz.game.engine.util;

import java.util.Collection;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 * 实现线程安全的
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <E>
 */
public class ConcurrentLinkedList<E> extends LinkedList<E> implements Cloneable, java.io.Serializable {

    private static final Logger log = Logger.getLogger(ConcurrentLinkedList.class);
    private static final long serialVersionUID = -8672434390186312157L;

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
    public E pop() {
        synchronized (this) {
            return super.pop(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E pollLast() {
        synchronized (this) {
            return super.pollLast(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E pollFirst() {
        synchronized (this) {
            return super.pollFirst(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E peekLast() {
        synchronized (this) {
            return super.peekLast(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E peekFirst() {
        synchronized (this) {
            return super.peekFirst(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E remove() {
        synchronized (this) {
            return super.remove(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E poll() {
        synchronized (this) {
            return super.poll(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E peek() {
        synchronized (this) {
            return super.peek(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        synchronized (this) {
            return super.lastIndexOf(o); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int indexOf(Object o) {
        synchronized (this) {
            return super.indexOf(o); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E remove(int index) {
        synchronized (this) {
            return super.remove(index); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E get(int index) {
        synchronized (this) {
            return super.get(index); //To change body of generated methods, choose Tools | Templates.
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
    public boolean add(E e) {
        synchronized (this) {
            return super.add(e); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            return super.size(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void addLast(E e) {
        synchronized (this) {
            super.addLast(e); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void addFirst(E e) {
        synchronized (this) {
            super.addFirst(e); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E removeLast() {
        synchronized (this) {
            return super.removeLast(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E removeFirst() {
        synchronized (this) {
            return super.removeFirst(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E getLast() {
        synchronized (this) {
            return super.getLast(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public E getFirst() {
        synchronized (this) {
            return super.getFirst(); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
