package net.sz;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class ThreadTest implements Runnable {

    private static final SzLogger log = SzLogger.getLogger();

    public ConcurrentLinkedQueue<Runnable> runs = new ConcurrentLinkedQueue<>();

    Runnable lastRun;
    long lastTimer;

    @Override
    public void run() {
        while (true) {
            lastRun = null;
            lastTimer = 0;
            try {
                /*如果队列为空强制线程等待*/
                while (runs.isEmpty()) {
                    synchronized (runs) {
                        /*直到收到通知消息*/
                        runs.wait();
                    }
                }

                /*取出任务*/
                lastRun = runs.poll();
                lastTimer = TimeUtil.currentTimeMillis();
                if (lastRun != null) {
                    /*执行任务*/
                    lastRun.run();
                }
            } catch (Exception e) {
                /*捕获异常*/
                log.error("", e);
            }
        }
    }
}

class MyThread extends Thread {

    Runnable run;

    public MyThread(Runnable run) {
        super(run);
        this.run = run;
    }
}

public class TestMain {

    private static final SzLogger log = SzLogger.getLogger();
    static ThreadTest threadTest = new ThreadTest();
    static MyThread thread;
    static Thread thread1;
    static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public static void main(String[] args) throws InterruptedException {
        /*创建线程任务队列*/
        thread = new MyThread(threadTest);

        uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("收到未能正常捕获的异常", e);
                if (t instanceof MyThread) {
                    /*判断是我们自定义线程模型，创建新的线程*/
                    thread = new MyThread(((MyThread) t).run);
                    thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
                    /*启动线程*/
                    thread.start();
                }
            }
        };

        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        /*启动线程*/
        thread.start();
        long i = 0;

        Random random = new Random();

        thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        /*相当于没 2秒 有一个任务需要处理*/
                        synchronized (this) {
                            this.wait(2000);
                        }
                    } catch (Exception e) {
                    }
                    /*任务队列一直不能执行*/
                    if (threadTest.runs.isEmpty()) {
                        /*创建任务*/
                        threadTest.runs.add(new Runnable() {
                            @Override
                            public void run() {
                                /*20%概率增加错误异常*/
                                if (random.nextInt(10000) < 2000) {
                                    new TestRun();
                                }
                                log.error(TimeUtil.currentTimeMillis());
                            }
                        });
                        /*通知线程有新任务了*/
                        synchronized (threadTest.runs) {
                            threadTest.runs.notify();
                        }
                    }
                }
            }
        });
        thread1.start();
        while (true) {
            try {
                /*相当于没 1秒 检查*/
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            long timer = TimeUtil.currentTimeMillis() - threadTest.lastTimer;
            Runnable lastRun = threadTest.lastRun;
            if (lastRun != null) {
                if (timer > 500) {
                    showStackTrace();
                } else {
                    log.error("线程执行耗时：" + timer + " " + lastRun.getClass().getName());
                }
            }
        }
    }

    /**
     *
     * 查看线程堆栈
     */
    public static void showStackTrace() {
        StringBuilder buf = new StringBuilder();
        /*如果现场意外终止*/
        long procc = TimeUtil.currentTimeMillis() - threadTest.lastTimer;
        if (procc > 5 * 1000 && procc < 864000000L) {//小于10天//因为多线程操作时间可能不准确
            buf.append("线程[")
                    .append(thread.getName())
                    .append("]")
                    .append("]当前状态->")
                    .append(thread.getState())
                    .append("可能已卡死 -> ")
                    .append(procc / 1000f)
                    .append("s\n    ")
                    .append("执行任务：")
                    .append(threadTest.lastRun.getClass().getName());
            try {
                StackTraceElement[] elements = thread.getStackTrace();
                for (int i = 0; i < elements.length; i++) {
                    buf.append("\n    ")
                            .append(elements[i].getClassName())
                            .append(".")
                            .append(elements[i].getMethodName())
                            .append("(").append(elements[i].getFileName())
                            .append(";")
                            .append(elements[i].getLineNumber()).append(")");
                }
            } catch (Exception e) {
                buf.append(e);
            }
            buf.append("\n++++++++++++++++++++++++++++++++++");
            String toString = buf.toString();
            log.error(toString);
        }
    }
}
