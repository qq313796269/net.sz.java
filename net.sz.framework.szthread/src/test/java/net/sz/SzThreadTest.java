package net.sz;

import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szthread.TaskModel;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.szthread.TimerTaskModel;
import net.sz.framework.utils.GlobalUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SzThreadTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
//        SzQueueThread thread = new SzQueueThread("ttt");
//        thread.addTask(new TaskModel() {
//            @Override
//            public void run() {
//                log.error("1");
//            }
//        });
        GlobalUtil.SERVERSTARTEND = true;
        BaseThreadRunnable thread = ThreadPool.addThread("test", 3);

        thread.addTimerTask(new TimerTaskModel(-1, 1000) {
            @Override
            public void run() {
                log.error("1");
            }
        });

        thread.addTimerTask(new TimerTaskModel(-1, 1000) {
            @Override
            public void run() {
                log.error("2");
            }
        });

        ThreadPool.GlobalThread.addTimerTask(new TimerTaskModel(-1, 1000) {
            @Override
            public void run() {
                log.error("3");
            }
        });

    }
}
