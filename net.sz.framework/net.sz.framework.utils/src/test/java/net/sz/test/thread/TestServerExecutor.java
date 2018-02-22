package net.sz.test.thread;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.thread.BaseTask;
import net.sz.framework.thread.ExecutorFactory;
import net.sz.framework.thread.ExecutorKey;
import net.sz.framework.thread.ServerExecutor;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestServerExecutor {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        ExecutorFactory.setInitEnd(true);
        ServerExecutor newServerExecutor = ExecutorFactory.newServerExecutor(ExecutorKey.DEFAULT_KEY);

        while (true) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
            newServerExecutor.addTask(new BaseTask() {
                @Override
                public void run() {

                    log.error("d ");
                }
            });
        }

    }
}
