package net.sz;

import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.SzThread;
import net.sz.game.engine.thread.TimerTaskModel;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class RestThread {

    private static SzLogger log = SzLogger.getLogger();
    static SzThread threadRunnable;

    public static void main(String[] args) {

        threadRunnable = new SzThread("s");

        threadRunnable.addTimerTask(new TimerTaskModel(10000) {

            @Override
            public void run() {
                threadRunnable.reset(System.currentTimeMillis() + "");
                log.error(threadRunnable.toString());
            }

        });

        ThreadPool.addThread(threadRunnable);
        ThreadPool.setStarEnd(true);
    }
}
