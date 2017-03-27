package net.sz.game.engine.utils;

/**
 * 原子级别操作
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AtomUtil {

    /**
     * 递增
     *
     * @param vi
     * @return
     */
    static public long inc(long vi) {
        synchronized (AtomUtil.class) {
            if (vi < Long.MAX_VALUE) {
                return vi++;
            }
            throw new UnsupportedOperationException("已经到达 " + Long.MAX_VALUE);
        }
    }

    /**
     * 递减
     *
     * @param vi
     * @return
     */
    static public long dec(long vi) {
        synchronized (AtomUtil.class) {
            if (vi > Long.MIN_VALUE) {
                return vi--;
            }
            throw new UnsupportedOperationException("已经到达 " + Long.MIN_VALUE);
        }
    }

    /**
     * 递增
     *
     * @param vi
     * @return
     */
    static public int inc(int vi) {
        synchronized (AtomUtil.class) {
            if (vi < Integer.MAX_VALUE) {
                return vi++;
            }
            throw new UnsupportedOperationException("已经到达 " + Integer.MAX_VALUE);
        }
    }

    /**
     * 递减
     *
     * @param vi
     * @return
     */
    static public int dec(int vi) {
        synchronized (AtomUtil.class) {
            if (vi > Integer.MIN_VALUE) {
                return vi--;
            }
            throw new UnsupportedOperationException("已经到达 " + Integer.MIN_VALUE);
        }
    }

}
