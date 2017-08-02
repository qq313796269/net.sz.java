//package net.sz;
//
//import java.io.IOException;
//import net.sz.framework.szlog.SzLogger;
//import net.sz.framework.szthread.SzQueueThread;
//import net.sz.framework.szthread.TaskModel;
//import net.sz.framework.szthread.ThreadPool;
//import net.sz.framework.struct.thread.ThreadType;
//import net.sz.framework.szthread.TimerTaskModel;
//
///**
// *
// * <br>
// * author 失足程序员<br>
// * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
// * mail 492794628@qq.com<br>
// * phone 13882122019<br>
// */
//public class TestCellBack {
//
//    private static final SzLogger log = SzLogger.getLogger();
//
//    public static void main(String[] args) throws IOException {
//
//        SzQueueThread thread = new SzQueueThread(ThreadType.Sys, null, "..", 3);
//
//        ThreadPool.addThread(thread);
//        thread.addKey("key1");
//        thread.addKey("key2");
//
//        SzQueueThread thread1 = new SzQueueThread(ThreadType.Sys, null, "..", 3);
//
//        ThreadPool.addThread(thread1);
//        thread1.addKey("key1");
//        thread1.addKey("key2");
//
//        thread.addTimerTask("key1", new TimerTaskModel(10) {
//            @Override
//            public void run() {
//
//                /*耗时的执行，需要异步*/
//                thread.addTask("key1", new TaskModel() {
//                    @Override
//                    public void run() {
//                        log.error("run");
//                        /*写入数据库*/
//                        try {
//                            Thread.sleep(3);
//                        } catch (Exception e) {
//                        }
//                    }
//
//                    /*异步完成后回调*/
//                    @Override
//                    public void onSuccessCallBack() {
//                        log.error("onSuccessCallBack");
//                    }
//
//                }).awaitEnd(5);
//
//                thread.addTask("key2", new TaskModel() {
//                    @Override
//                    public void run() {
//                        log.error("run");
//                        /*写入数据库*/
//                        try {
//                            Thread.sleep(3);
//                        } catch (Exception e) {
//                        }
//                    }
//
//                    /*异步完成后回调*/
//                    @Override
//                    public void onSuccessCallBack() {
//                        log.error("onSuccessCallBack");
//                    }
//
//                }).awaitEnd(5);
//
//                log.error("4");
//            }
//        });
//
//        thread.addTimerTask("key2", new TimerTaskModel(12) {
//
//            @Override
//            public void run() {
//
//                /*耗时的执行，需要异步*/
//                thread.addTask("key1", new TaskModel() {
//                    @Override
//                    public void run() {
//                        log.error("run");
//                        /*写入数据库*/
//                        try {
//                            Thread.sleep(3);
//                        } catch (Exception e) {
//                        }
//                    }
//
//                    /*异步完成后回调*/
//                    @Override
//                    public void onSuccessCallBack() {
//                        log.error("onSuccessCallBack");
//                    }
//
//                }).awaitEnd(5);
//
//                thread.addTask("key2", new TaskModel() {
//                    @Override
//                    public void run() {
//                        log.error("run");
//                        /*写入数据库*/
//                        try {
//                            Thread.sleep(3);
//                        } catch (Exception e) {
//                        }
//                    }
//
//                    /*异步完成后回调*/
//                    @Override
//                    public void onSuccessCallBack() {
//                        log.error("onSuccessCallBack");
//                    }
//
//                }).awaitEnd(5);
//
//                log.error("4");
//            }
//        });
//    }
//}
