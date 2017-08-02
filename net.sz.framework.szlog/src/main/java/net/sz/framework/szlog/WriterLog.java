package net.sz.framework.szlog;

import java.io.Closeable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class WriterLog extends Thread implements Closeable {

    final ConcurrentLinkedQueue<LogMsg> logs = new ConcurrentLinkedQueue<>();
    boolean ISRUN = true;
    WriterFile writerFile = new WriterFile();
    WriterFile writerErrorFile = new WriterFile();

    public WriterLog() {
        super(CommUtil.LOG_THREAD_GROUP, "SZ_LOG_THREAD");
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));
    }

    void add(String msg, Throwable throwable) {
        logs.add(new LogMsg(msg, throwable));
        synchronized (logs) {
            logs.notify();
        }
    }
    long lastCreateTime = 0;

    @Override
    public void run() {
        while (ISRUN) {
            try {

                synchronized (logs) {
                    if (logs.isEmpty()) {
                        /*间隔写入*/
                        logs.wait();
                    }
                }
                
                /*间隔一秒钟检查一次文件备份*/
                if (System.currentTimeMillis() - lastCreateTime > 1000) {
                    writerFile.createFileWriter(CommUtil.LOG_PRINT_PATH);
                    writerErrorFile.createFileWriter(CommUtil.LOG_PRINT_PATH + "_error.log");
                    lastCreateTime = System.currentTimeMillis();
                }

                for (int i = 0; i < CommUtil.BUFFER_TIME_LOGSIZE; i++) {
                    LogMsg poll = logs.poll();
                    if (poll == null) {
                        break;
                    }
                    if (poll.getThrowable() != null) {
                        writerErrorFile.write(poll.getMsg());
                    }
                    writerFile.write(poll.getMsg());
                }
                writerFile.flush();
                writerErrorFile.flush();
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void close() {

    }

}
