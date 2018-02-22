package net.sz.framework.szlog;

import java.io.Closeable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 如果没开 console 这里是不开启线程的
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class WriterConsole extends Thread implements Closeable {

    /* ConcurrentLinkedQueue 在不调用size的情况下，性能高效*/
    final ConcurrentLinkedQueue<LogMsg> logs = new ConcurrentLinkedQueue<>();

    WriterConsole() {
        super(CommUtil.LOG_THREAD_GROUP, "SZ_LOG_CONSOLE_THREAD");
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
        }));
    }

    boolean ISRUN = true;

    void add(String msg, Throwable throwable) {
        logs.add(LogMsg.copyMsg(msg, throwable));
    }

    public boolean isEmpty() {
        return this.logs.isEmpty();
    }

    @Override
    @Deprecated
    public void run() {
        while (ISRUN) {
            try {
                while (ISRUN && logs.isEmpty()) {
                    synchronized (logs) {
                        /*间隔写入*/
                        logs.wait(2);
                    }
                }
                if (ISRUN && !logs.isEmpty()) {
                    LogMsg take = logs.poll();
                    if (take == null) {
                        continue;
                    }
                    write(take);
                }
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }
    }

    /**
     * 书写日志
     *
     * @param msg
     */
    void write(LogMsg msg) {
        SzLogger.writeConsole(msg.getMsg(), msg.getThrowable());
    }

    @Override
    public void close() {
        this.ISRUN = false;
    }

}
