package net.sz.game.engine.thread;

/**
 * 定时器执行器
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class TimerTaskEvent extends TaskEvent {

    private static final long serialVersionUID = -8331296295264699207L;

    /**
     * 开始执行的时间
     */
    protected long startTime;

    /**
     * 是否一开始执行一次
     */
    protected boolean startAction;

    /**
     * 结束时间
     */
    protected long endTime;

    /**
     * 执行次数
     */
    protected int actionCount;

    /**
     * 间隔执行时间
     */
    protected int intervalTime;
    /**
     * 上一次执行结束
     */
    boolean beforeEnd = true;
    /**
     * 当前执行次数
     */
    int execCount = 0;
    /**
     * 最有一次执行时间
     */
    long lastTime = 0;

    /**
     *
     * @param startTime 指定开始时间
     * @param isStartAction 是否一开始就执行一次
     * @param endTime 指定结束时间
     * @param actionCount 指定执行次数
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskEvent(long startTime, boolean isStartAction, long endTime, int actionCount, int intervalTime) {
        super();
        this.startTime = startTime;
        this.startAction = isStartAction;
        this.endTime = endTime;
        this.actionCount = actionCount;
        this.intervalTime = intervalTime;
    }

    /**
     * 指定任务的开始执行时间
     *
     * @param startTime 指定开始时间
     * @param isStartAction 是否一开始就执行一次
     * @param actionCount 指定执行次数
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskEvent(long startTime, boolean isStartAction, int actionCount, int intervalTime) {
        this(startTime, isStartAction, 0, actionCount, intervalTime);
    }

    /**
     * 指定结束时间已结束时间为准，执行次数不一定够
     *
     * @param isStartAction 是否一开始就执行一次
     * @param endTime 指定结束时间
     * @param actionCount 指定执行次数
     * @param intervalTime 指定间隔时间
     *
     */
    public TimerTaskEvent(boolean isStartAction, long endTime, int actionCount, int intervalTime) {
        this(0, isStartAction, endTime, actionCount, intervalTime);
    }

    /**
     * 指定开始时间，和结束时间
     *
     * @param startTime 指定开始时间
     * @param endTime 指定结束时间
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskEvent(long startTime, long endTime, int intervalTime) {
        this(startTime, false, endTime, -1, intervalTime);
    }

    /**
     * 指定的执行次数和间隔时间
     *
     * @param actionCount 指定执行次数
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskEvent(int actionCount, int intervalTime) {
        this(0, false, 0, actionCount, intervalTime);
    }

    /**
     * 提交后指定的时间无限制执行
     *
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskEvent(int intervalTime) {
        this(0, false, 0, -1, intervalTime);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isStartAction() {
        return startAction;
    }

    public void setStartAction(boolean startAction) {
        this.startAction = startAction;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getActionCount() {
        return actionCount;
    }

    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getExecCount() {
        return execCount;
    }

}
