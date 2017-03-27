package net.sz;

import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.TimerTaskModel;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ThreadTest33 {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        ThreadPool.addTimerTask(ThreadPool.GlobalThread, new TimerTaskModel(10) {
            @Override
            public void run() {
                log.error("100");
            }
        });
    }
}
