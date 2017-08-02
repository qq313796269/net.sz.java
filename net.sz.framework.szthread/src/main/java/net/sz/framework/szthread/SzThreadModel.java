package net.sz.framework.szthread;

import net.sz.framework.struct.thread.BaseThreadModel;

/**
 * 自定义线程
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SzThreadModel extends BaseThreadModel {

    private static final long serialVersionUID = 1320664122444307127L;

    private Runnable run;
    //最后的线程队列
    private volatile QueueTaskModel lastTaskModelQueue = null;

    /**
     *
     * @param myId
     * @param tid 自定义线程id
     * @param group 分组
     * @param run 执行方法
     * @param name 线程名称
     */
    public SzThreadModel(int myId, long tid, ThreadGroup group, Runnable run, String name) {
        super(group, run, name);
        this.setMyId(myId);
        this.setTid(tid);
        this.settName(name);
        this.run = run;
    }

    /**
     * 线程运行的 Runnable
     *
     * @return
     */
    public Runnable getRun() {
        return run;
    }

    /**
     * 最后的线程队列
     *
     * @return
     */
    public QueueTaskModel getLastTaskModelQueue() {
        return lastTaskModelQueue;
    }

    public void setLastTaskModelQueue(QueueTaskModel lastTaskModelQueue) {
        this.lastTaskModelQueue = lastTaskModelQueue;
    }

}
