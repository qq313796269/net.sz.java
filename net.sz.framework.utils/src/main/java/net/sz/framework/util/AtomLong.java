package net.sz.framework.util;

import java.io.Serializable;

/**
 * Long 线程安全变更值
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AtomLong implements Serializable, Cloneable {

    private static final long serialVersionUID = 8083824567604574070L;

    private long value = 0;

    public AtomLong() {
    }

    public AtomLong(long value) {
        if (value < 0) {
            throw new UnsupportedOperationException("参数不能小于0");
        }
        this.value = value;
    }

    /**
     * 如果小于0，归零
     *
     * @param vi
     */
    public long changeZero(long vi) {
        synchronized (this) {
            if (vi > 0) {
                if (Long.MAX_VALUE - vi < value) {
                    value = Long.MAX_VALUE;
                } else {
                    value += vi;
                }
            } else if (vi < 0) {
                if (Long.MIN_VALUE - vi > value) {
                    value = Long.MIN_VALUE;
                } else {
                    value += vi;
                }
            }
            value = value < 0 ? 0 : value;
            return value;
        }
    }

    public long getValue() {
        return value;
    }

    @Deprecated
    public void setValue(long value) {
        synchronized (this) {
            this.value = value < 0 ? 0 : value;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return value + "";
    }

    public static void main(String[] args) {
        AtomLong atomLong = new AtomLong();
        atomLong.changeZero(10);
        System.out.println(atomLong.getValue());
        atomLong.changeZero(-20);
        System.out.println(atomLong.getValue());
    }
}
