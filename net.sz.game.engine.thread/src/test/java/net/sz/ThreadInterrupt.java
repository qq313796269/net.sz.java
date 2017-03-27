package net.sz;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ThreadInterrupt extends Thread {

    private static SzLogger log = SzLogger.getLogger();

    public ThreadInterrupt() {
        super(Thread.currentThread().getThreadGroup(), "s");
    }

    public void run() {
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) throws Exception {
        Thread thread = new ThreadInterrupt();
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace(System.out);
            }
        });
        thread.start();
        log.error("在50秒之内按任意键中断线程!");
        log.error(thread.getState());
        System.in.read();
        log.error("中断线程!");
        thread.interrupt();
        thread.join();
        thread.interrupt();
        log.error("线程已经退出!");
        System.exit(3);
    }
}
