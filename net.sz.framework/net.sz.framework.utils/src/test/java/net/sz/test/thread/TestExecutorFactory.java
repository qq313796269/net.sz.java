package net.sz.test.thread;

import net.sz.framework.thread.ExecutorFactory;
import net.sz.framework.thread.ExecutorKey;
import net.sz.framework.thread.ServerExecutorQueue2Timer;
import net.sz.framework.thread.timer.TimerTask;
import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestExecutorFactory {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        ExecutorFactory.setInitEnd(true);
//        ServerExecutor newServerExecutor = ExecutorFactory.newServerExecutor(ExecutorType.User, ExecutorKey.DEFAULT_KEY, BaseThread.GlobalThreadGroup);
        ServerExecutorQueue2Timer executorService = ExecutorFactory.newServerExecutorQueue2Timer(ExecutorKey.DEFAULT_KEY);
//        executorService.addTask(new BaseTask() {
//            @Override
//            public void run() {
//                log.error("1111111111111");
//            }
//        });

//        executorService.addTask(ExecutorKey.valueOf("dd"), new BaseTask() {
//            @Override
//            public void run() {
//                log.error("dddd");
//            }
//        });
        executorService.addTimerTask(new TimerTask(10) {
            @Override
            public void run() {
                log.error("TimerTask");
            }
        });

        executorService.addTimerTask("dd", new TimerTask(15) {
            @Override
            public void run() {
                /*获取当前线程*/
                ServerExecutorQueue2Timer currentExecutor = ExecutorFactory.currentExecutor(ServerExecutorQueue2Timer.class);
                log.error(currentExecutor.getExecutorKey() + " dd TimerTask");
            }
        });

        executorService.addTimerTask("dd", new TimerTask(22) {
            @Override
            public void run() {
                log.error("dd1 TimerTask");
            }
        });
//        executorService.close();
    }
}
