package net.sz.framework.szthread;

import java.util.HashMap;
import java.util.Map;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 * TODO 该脚本,上线版本最好从本质上解决
 * 验证服务器所有线程是否阻塞,如果阻塞,那么停止当前线程和定时器,创建新的线程和定时器,并且执行上个线程未执行完毕的消息.
 * 目前卡线程的情况,只有数据库处理的时候才会发生,如果解决了数据库卡的问题,那么这个处理最好取消,不能保留,或者说,仅仅打印提示.而不停止和新建线程
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class CheckThreadTimerThread extends Thread {

    private static final SzLogger log = SzLogger.getLogger();
    private static final Object SYN_OBJECT = new Object();

    public CheckThreadTimerThread() {
        super(BaseThreadRunnable.GlobalThreadGroup, "Check Thread Timer Event");
    }

    @Override
    public void run() {
        while (true) {
            synchronized (SYN_OBJECT) {
                try {
                    SYN_OBJECT.wait(10000);//10秒玩一次
                } catch (InterruptedException ex) {
                }
            }
            long begin = TimeUtil.currentTimeMillis();
            HashMap<Long, BaseThreadRunnable> hashMap = new HashMap<>(ThreadPool.getThreadMap());
            for (Map.Entry<Long, BaseThreadRunnable> entrySet : hashMap.entrySet()) {
                BaseThreadRunnable value = entrySet.getValue();
                value.checkThreadStackTrace();
            }
            long tmp = (TimeUtil.currentTimeMillis() - begin);
            if (tmp > 2000) {
                log.error("CheckThreadTimerEventScript cost:" + (TimeUtil.currentTimeMillis() - begin));
            }
        }
    }
}
