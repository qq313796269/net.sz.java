package net.sz.game.engine.thread;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class TimerThread extends Thread {

    private static final Object SYN_OBJECT = new Object();

    public static final int TIMER_WAIT = 2;

    public TimerThread() {
        super(ThreadPool.GlobalThreadGroup, "Global Timer Thread");
    }

    @Override
    public void run() {
        while (true) {
            synchronized (SYN_OBJECT) {
                try {
                    SYN_OBJECT.wait(TIMER_WAIT);
                } catch (InterruptedException ex) {
                }
            }

            HashMap<Long, SzThread> hashMap = new HashMap<>(ThreadPool.getThreadMap());
            for (Map.Entry<Long, SzThread> entry : hashMap.entrySet()) {
                Long key = entry.getKey();
                SzThread value = entry.getValue();
                if (value.getThreadType() == ThreadType.Sys || ThreadPool.isStarEnd()) {
                    try {
                        value.timerRun();
                    } catch (Throwable ex) {

                    }
                }
            }

        }
    }
}
