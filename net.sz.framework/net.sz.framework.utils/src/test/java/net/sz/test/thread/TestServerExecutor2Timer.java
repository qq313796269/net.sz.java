package net.sz.test.thread;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.thread.BaseThread;
import net.sz.framework.thread.ExecutorFactory;
import net.sz.framework.thread.ExecutorKey;
import net.sz.framework.thread.ExecutorType;
import net.sz.framework.thread.ServerExecutor2Timer;
import net.sz.framework.thread.timer.TimerTask;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestServerExecutor2Timer {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        ExecutorFactory.setInitEnd(true);
        ServerExecutor2Timer newServerExecutor2Timer = ExecutorFactory.newServerExecutor2Timer(ExecutorKey.DEFAULT_KEY);

        newServerExecutor2Timer.addTimerTask(new TimerTask(1000) {
            @Override
            public void run() {
                log.info("TimerTask");
            }
        });
    }
}
