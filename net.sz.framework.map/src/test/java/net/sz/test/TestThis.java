package net.sz.test;


import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestThis {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        S1 s1 = new S2();
        log.error(s1.T3());
    }

    static class S1 {

        public boolean T3() {
            return T1();
        }

        public boolean T1() {
            return true;
        }
    }

    static class S2 extends S1 {

        @Override
        public boolean T1() {
            return false;
        }

    }
}
