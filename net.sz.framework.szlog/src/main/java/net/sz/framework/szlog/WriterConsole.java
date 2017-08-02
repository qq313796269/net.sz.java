package net.sz.framework.szlog;

import java.io.Closeable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 如果没开 console 这里是不开启线程的
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class WriterConsole extends Thread implements Closeable {

    final ConcurrentLinkedQueue<LogMsg> logs = new ConcurrentLinkedQueue<>();

    WriterConsole() {
        super(CommUtil.LOG_THREAD_GROUP, "SZ_LOG_CONSOLE_THREAD");
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));
    }

    boolean ISRUN = true;

    void add(String msg, Throwable throwable) {
        logs.add(new LogMsg(msg, throwable));
        synchronized (logs) {
            logs.notify();
        }
    }

    @Override
    @Deprecated
    public void run() {
        while (ISRUN) {
            try {
                synchronized (logs) {
                    if (logs.isEmpty()) {
                        /*间隔写入*/
                        logs.wait();
                    }
                }
                for (int i = 0; i < CommUtil.BUFFER_TIME_LOGSIZE; i++) {
                    LogMsg poll = logs.poll();
                    if (poll == null) {
                        break;
                    }
                    write(poll);
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
        if (logs != null) {
            synchronized (logs) {
                logs.notify();
            }
        }
        try {
            this.interrupt();
            this.join();
        } catch (Exception e) {
        }
    }

}
