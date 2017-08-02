package net.sz;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.szthread.TimerTaskModel;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ThreadTest33 {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        ThreadPool.GlobalThread.addTimerTask(new TimerTaskModel(10) {
            @Override
            public void run() {
                log.error("100");
            }
        });
    }
}
