package net.sz;

import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestSyncString {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            test(1);
        }
    }

    public static void test(Integer tt) {
        new Thread(() -> {
            synchronized (tt) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
            }
            log.error(tt + " " + Thread.currentThread().getId());
        }).start();
    }

}
