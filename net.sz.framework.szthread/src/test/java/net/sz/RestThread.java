package net.sz;

import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.szthread.TimerTaskModel;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class RestThread {

    private static final SzLogger log = SzLogger.getLogger();
    static SzQueueThread threadRunnable;

    public static void main(String[] args) {

        threadRunnable = new SzQueueThread("s");

        threadRunnable.addTimerTask(new TimerTaskModel(10000) {

            @Override
            public void run() {
                threadRunnable.reset(TimeUtil.currentTimeMillis() + "");
                log.error(threadRunnable.toString());
            }

        });

        ThreadPool.addThread(threadRunnable);
        GlobalUtil.SERVERSTARTEND = true;
    }
}
