package net.sz;

import java.util.ArrayList;
import net.sz.framework.szlog.CommUtil;
import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SzLogTest {

    private static SzLogger log = null;

    public static void main(String[] args) throws Exception {

        CommUtil.LOG_PRINT_CONSOLE = false;
        log = SzLogger.getLogger();

        System.out.print("准备就绪请敲回车");
        System.in.read();

        long bigen = System.currentTimeMillis();

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                //for (int i = 0; i < 100000; i++) {
                for (;;) {
//                    try {
//                        synchronized (Thread.currentThread()) {
//                            Thread.currentThread().wait(1);
//                        }
//                    } catch (Exception e) {
//                    }
                    log.error(" cssssssssssssssssdgdfgdfgdyrsbsfgsrtyhshstjhsrthsbsdhae063.00365ssssssssssssssssssssssssss");
                }
            });
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        while (true) {
            if (log.isEmpty()) {
                System.out.println((System.currentTimeMillis() - bigen));
                System.exit(0);
            }
        }
    }

}
