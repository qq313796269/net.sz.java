package net.sz.framework.szlog;

import java.io.Closeable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class WriterLog extends Thread implements Closeable {

    /* ConcurrentLinkedQueue 在不调用size的情况下，性能高效, 如果需要size 请使用 java.util.concurrent.LinkedBlockingQueue;*/
    final ConcurrentLinkedQueue<LogMsg> logs = new ConcurrentLinkedQueue<>();

    boolean ISRUN = true;
    WriterFile writerFile = new WriterFile();
    WriterFile writerErrorFile = new WriterFile();

    public WriterLog() {
        super(CommUtil.LOG_THREAD_GROUP, "SZ_LOG_THREAD");
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
        }));
    }

    public boolean isEmpty() {
        return this.logs.isEmpty();
    }

    void add(String msg, Throwable throwable) {
        logs.add(LogMsg.copyMsg(msg, throwable));
    }
    long lastCreateTime = 0;

    @Override
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
                    /*间隔一秒钟检查一次文件备份*/
                    if (System.currentTimeMillis() - lastCreateTime > 1000) {
                        writerFile.createFileWriter(CommUtil.LOG_PRINT_PATH);
                        writerErrorFile.createFileWriter(CommUtil.LOG_PRINT_PATH + "_error.log");
                        lastCreateTime = System.currentTimeMillis();
                    }
                    for (int i = 0; i < CommUtil.BUFFER_TIME_LOGSIZE; i++) {
                        LogMsg take = logs.poll();
                        if (take == null) {
                            break;
                        }
                        if (take.getThrowable() != null) {
                            writerErrorFile.write(take.getMsg());
                        }
                        writerFile.write(take.getMsg());
                    }
                    writerFile.flush();
                    writerErrorFile.flush();
                }
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void close() {
        ISRUN = false;
    }

}
