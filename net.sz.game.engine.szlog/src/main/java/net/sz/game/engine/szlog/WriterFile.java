package net.sz.game.engine.szlog;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class WriterFile extends Thread implements Closeable {

    final ConcurrentLinkedQueue<String> logs;

    public WriterFile() {
        super(CommUtil.LOG_THREAD_GROUP, "SZ_LOG_FILE_THREAD");
        this.logs = new ConcurrentLinkedQueue<>();
        this.setUncaughtExceptionHandler((Thread t, Throwable e) -> {

        });
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));
    }
    /*特殊文件*/
    String fileName = null;
    boolean errorName = false;
    boolean ISRUN = true;

    public WriterFile(boolean error) {
        super(CommUtil.LOG_THREAD_GROUP, "SZ_LOG_FILE_ERROR_THREAD");
        this.logs = new ConcurrentLinkedQueue<>();
        errorName = error;
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                SzLogger.writeConsoleln("钩子函数执行");
                close();
            }
        }));
    }

    /**
     * 不会启动线程，只是用于定制输出日志到指定文件夹
     *
     * @param fileName
     */
    public WriterFile(String fileName) {
        this.logs = null;
        this.fileName = fileName;
    }

    void add(String msg) {
        logs.add(msg);
        synchronized (logs) {
            logs.notify();
        }
    }

    @Override
    public void run() {
        createFileWriter();
        Long bigen = System.currentTimeMillis();
        while (ISRUN) {
            try {
                while (!logs.isEmpty()) {
                    if (System.currentTimeMillis() - bigen > 1000) {
                        /*一秒钟检查一次文件备份*/
                        bigen = System.currentTimeMillis();
                        createFileWriter();
                    }
                    if (CommUtil.LOG_PRINT_FILE_BUUFER) {
                        for (int i = 0; i < 500; i++) {
                            String poll = logs.poll();
                            if (poll == null) {
                                break;
                            }
                            write(poll);
                        }
                        flush();
                    } else {
                        /*非缓存单次压入文件*/
                        String poll = logs.poll();
                        if (poll != null) {
                            write(poll);
                            flush();
                        }
                    }
                }
                synchronized (logs) {
                    if (logs.isEmpty()) {
                        /*间隔写入*/
                        logs.wait();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }
    }

    File file = null;
    BufferedWriter out = null;

    /**
     * 创建文件
     */
    void createFileWriter() {
        try {
            String filepath;
            if (this.fileName == null || this.fileName.trim().isEmpty()) {
                filepath = CommUtil.LOG_PRINT_PATH;
            } else {
                filepath = this.fileName;
            }

            if (errorName) {
                filepath += "_error.log";
            }

            if (file == null) {
                file = new File(filepath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            }

            long lastModified = file.lastModified();
            String ctime = CommUtil.DateFormat_File.format(new Date(lastModified));
            String ctimeNew = CommUtil.DateFormat_File.format(new Date());
            /*日志按照每天进行备份*/
            if (!ctimeNew.equalsIgnoreCase(ctime)) {
                File filenew = new File(filepath + "_" + ctime + ".log");
                if (filenew.exists()) {
                    filenew.deleteOnExit();
                }
                file.renameTo(filenew);
                /* 重新 new 新文件 */
                file = new File(filepath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                flush();
                /* 清空 */
                if (out != null) {
                    out.flush();
                    out.close();
                }
                out = null;
            }
            if (out == null) {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * 写入日志内容
     *
     * @param conent
     */
    void write(String conent) {
        try {
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * 刷新缓冲区
     */
    void flush() {
        try {
            if (out != null) {
                out.flush();
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * 资源
     */
    @Override
    public void close() {
        ISRUN = false;
        if (logs != null) {
            synchronized (logs) {
                logs.notify();
            }
        }
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
            this.interrupt();
            this.join();
        } catch (Throwable e) {
        }
    }
}
