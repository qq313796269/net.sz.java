package net.sz;

import java.util.ArrayList;
import net.sz.game.engine.szlog.CommUtil;
import net.sz.game.engine.szlog.LogLevel;
import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
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
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 100000; i++) {
                        log.error(i + " cssssssssssssssssdgdfgdfgdyrsbsfgsrtyhshstjhsrthsbsdhae063.00365ssssssssssssssssssssssssss");
                    }
                }
            });
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        while (true) {
            if (log.logSize() == 0) {
                System.out.println((System.currentTimeMillis() - bigen));
                System.exit(0);
            }
        }
    }

}
