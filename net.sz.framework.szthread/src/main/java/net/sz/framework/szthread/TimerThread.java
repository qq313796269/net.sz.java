package net.sz.framework.szthread;

import net.sz.framework.struct.thread.ThreadType;
import java.util.HashMap;
import java.util.Map;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.GlobalUtil;

/**
 * 定时器执行线程
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class TimerThread extends Thread {

    private static final SzLogger log = SzLogger.getLogger();

    public static final int TIMER_WAIT = 2;

    public TimerThread() {
        super(BaseThreadRunnable.GlobalThreadGroup, "Global Timer Thread");
    }

    HashMap<Long, BaseThreadRunnable> hashMap = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            try {
                sleep(TIMER_WAIT);
            } catch (Exception e) {
                log.error("", e);
            }

            hashMap.putAll(ThreadPool.getThreadMap());

            for (Map.Entry<Long, BaseThreadRunnable> entry : hashMap.entrySet()) {
                BaseThreadRunnable value = entry.getValue();
                if (value.getThreadType() == ThreadType.Sys || GlobalUtil.SERVERSTARTEND) {
                    try {
                        value.timerRun();
                    } catch (Throwable ex) {
                        log.error("定时器执行异常", ex);
                    }
                }
            }

            hashMap.clear();
        }
    }
}
