package net.sz.framework.utils;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AtomIntUtil {

    private static final AtomIntUtil ME = new AtomIntUtil();

    public static AtomIntUtil getInstance() {
        return ME;
    }

    protected AtomIntUtil() {

    }

    /**
     * 递增
     *
     * @param vi
     * @return
     */
    public int inc(int vi) {
        return atom(vi, -1);
    }

    /**
     * 递减
     *
     * @param vi
     * @return
     */
    public int dec(int vi) {
        return atom(vi, -1);
    }

    /**
     * 当到达最大值，或者最小值，抛错异常 UnsupportedOperationException
     *
     * @param vi 当前变量
     * @param a 当前变更量
     * @return
     */
    public int atom(int vi, int a) {
        synchronized (this) {
            if (a > 0) {
                if (Integer.MAX_VALUE - a < vi) {
                    throw new UnsupportedOperationException("已经到达 " + Integer.MAX_VALUE);
                }
            } else if (a < 0) {
                if (Integer.MIN_VALUE - a > vi) {
                    throw new UnsupportedOperationException("已经到达 " + Integer.MIN_VALUE);
                }
            }
            return vi + a;
        }
    }

    /**
     * 原子操作,如果达到最大值返回最大值，如果到达最小值返回最小值
     *
     * @param vi 当前变量
     * @param a 当前变更量
     * @return
     */
    public int tryAtom(int vi, int a) {
        synchronized (this) {
            if (a > 0) {
                if (Integer.MAX_VALUE - a < vi) {
                    return Integer.MAX_VALUE;
                }
            } else if (a < 0) {
                if (Integer.MIN_VALUE - a > vi) {
                    return Integer.MIN_VALUE;
                }
            }
            return vi + a;
        }
    }

    /**
     *
     * @param vi
     * @param a
     * @return
     */
    public int tryChangeZero(int vi, int a) {
        synchronized (this) {
            if (a > 0) {
                if (Integer.MAX_VALUE - a < vi) {
                    vi = Integer.MAX_VALUE;
                } else {
                    vi += a;
                }
            } else if (a < 0) {
                if (Integer.MIN_VALUE - a < vi) {
                    vi = Integer.MIN_VALUE;
                } else {
                    vi += a;
                }
            }
            return vi < 0 ? 0 : vi;
        }
    }

}
