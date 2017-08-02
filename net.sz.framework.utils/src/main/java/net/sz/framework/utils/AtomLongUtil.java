package net.sz.framework.utils;

/**
 * 原子级别操作
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AtomLongUtil {

    private static final AtomLongUtil ME = new AtomLongUtil();

    public static AtomLongUtil getInstance() {
        return ME;
    }

    protected AtomLongUtil() {

    }

    /**
     * 递增
     *
     * @param vi
     * @return
     */
    public long inc(long vi) {
        return atom(vi, 1);
    }

    /**
     * 递减
     *
     * @param vi
     * @return
     */
    public long dec(long vi) {
        return atom(vi, -1);
    }

    /**
     * 当到达最大值，或者最小值，抛错异常 UnsupportedOperationException
     *
     * @param vi 当前变量
     * @param a 当前变更量
     * @return
     */
    public long atom(long vi, long a) {
        synchronized (this) {
            if (a > 0) {
                if (Long.MAX_VALUE - a < vi) {
                    throw new UnsupportedOperationException("已经到达 " + Long.MAX_VALUE);
                }
            } else if (a < 0) {
                if (Long.MIN_VALUE - a < vi) {
                    throw new UnsupportedOperationException("已经到达 " + Long.MIN_VALUE);
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
    public long tryAtom(long vi, long a) {
        synchronized (this) {
            if (a > 0) {
                if (Long.MAX_VALUE - a < vi) {
                    return Long.MAX_VALUE;
                }
            } else if (a < 0) {
                if (Long.MIN_VALUE - a < vi) {
                    return Long.MIN_VALUE;
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
    public long tryChangeZero(long vi, long a) {
        synchronized (this) {
            if (a > 0) {
                if (Long.MAX_VALUE - a < vi) {
                    vi = Long.MAX_VALUE;
                } else {
                    vi += a;
                }
            } else if (a < 0) {
                if (Long.MIN_VALUE - a < vi) {
                    vi = Long.MIN_VALUE;
                } else {
                    vi += a;
                }
            }
            return vi < 0 ? 0 : vi;
        }
    }

}
