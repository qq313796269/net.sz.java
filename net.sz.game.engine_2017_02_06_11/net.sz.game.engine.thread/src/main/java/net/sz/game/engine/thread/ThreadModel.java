package net.sz.game.engine.thread;

import org.apache.log4j.Logger;

/**
 * 自定义线程
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ThreadModel extends Thread {

    private static final Logger log = Logger.getLogger(ThreadModel.class);

    private Runnable run;
    //线程的自定义id
    private long _id;
    //线程的自定义id
    private int myId;

    private String tName;
    //正在执行的任务
    private volatile TaskEvent lastCommand;
    //开始执行任务的时间
    private volatile long lastExecuteTime = 0;

    /**
     *
     * @param myId
     * @param tid 自定义线程id
     * @param group 分组
     * @param run 执行方法
     * @param name 线程名称
     */
    public ThreadModel(int myId, long tid, ThreadGroup group, Runnable run, String name) {
        super(group, run);
        this.setName(name);
        this._id = tid;
        this.tName = name;
        this.run = run;
    }

    public TaskEvent getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(TaskEvent lastCommand) {
        this.lastCommand = lastCommand;
    }

    public long getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(long lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    /**
     * 返回线程自定义id
     *
     * @return
     */
    @Override
    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public Runnable getRun() {
        return run;
    }

    public int getMyId() {
        return myId;
    }

}
