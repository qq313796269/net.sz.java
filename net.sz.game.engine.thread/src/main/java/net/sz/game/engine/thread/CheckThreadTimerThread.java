package net.sz.game.engine.thread;

import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO 该脚本,上线版本最好从本质上解决
 * 验证服务器所有线程是否阻塞,如果阻塞,那么停止当前线程和定时器,创建新的线程和定时器,并且执行上个线程未执行完毕的消息.
 * 目前卡线程的情况,只有数据库处理的时候才会发生,如果解决了数据库卡的问题,那么这个处理最好取消,不能保留,或者说,仅仅打印提示.而不停止和新建线程
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class CheckThreadTimerThread extends Thread {

    private static final Logger log = Logger.getLogger(CheckThreadTimerThread.class);
    private static final Object SYN_OBJECT = new Object();

    public CheckThreadTimerThread() {
        super(ThreadPool.GlobalThreadGroup, "Check Thread Timer Event");
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
            long begin = System.currentTimeMillis();
            HashMap<Long, ThreadRunnable> hashMap = new HashMap<>(ThreadPool.getThreadMap());
            for (Map.Entry<Long, ThreadRunnable> entrySet : hashMap.entrySet()) {
                Long key = entrySet.getKey();
                ThreadRunnable value = entrySet.getValue();
                value.showStackTrace();
            }
            long tmp = (System.currentTimeMillis() - begin);
            if (tmp > 2000) {
                log.error("CheckThreadTimerEventScript cost:" + (System.currentTimeMillis() - begin));
            }
        }
    }
}
