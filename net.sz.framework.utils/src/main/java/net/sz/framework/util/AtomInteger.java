package net.sz.framework.util;

import java.io.Serializable;

/**
 * Integer 线程安全变更
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AtomInteger implements Serializable, Cloneable {

    private static final long serialVersionUID = 5778431268716308330L;

    private volatile int value = 0;

    public AtomInteger() {
    }

    /**
     * 初始值
     *
     * @param value
     */
    public AtomInteger(int value) {
        if (value < 0) {
            throw new UnsupportedOperationException("参数不能小于0");
        }
        this.value = value;
    }

    /**
     * 如果小于0，归零
     *
     * @param vi
     * @return
     */
    public int changeZero(int vi) {
        synchronized (this) {
            if (vi > 0) {
                if (Integer.MAX_VALUE - vi < value) {
                    value = Integer.MAX_VALUE;
                } else {
                    value += vi;
                }
            } else if (vi < 0) {
                if (Integer.MIN_VALUE - vi > value) {
                    value = Integer.MIN_VALUE;
                } else {
                    value += vi;
                }
            }
            value = value < 0 ? 0 : value;
            return value;
        }
    }

    /**
     * 并非线程安全返回值
     *
     * @return
     */
    public int getValue() {
        return value;
    }

    @Deprecated
    public void setValue(int value) {
        synchronized (this) {
            this.value = value < 0 ? 0 : value;
        }
    }

    @Override
    public String toString() {
        return value + "";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

}
