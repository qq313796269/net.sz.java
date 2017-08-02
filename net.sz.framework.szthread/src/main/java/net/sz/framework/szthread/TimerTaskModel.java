package net.sz.framework.szthread;

/**
 * 定时器执行器
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class TimerTaskModel extends TaskModel implements Cloneable {

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
     * 最大执行次数
     */
    protected int maxExecCount;

    /**
     * 间隔执行时间
     */
    protected int intervalTime;
    /**
     * 上一次执行结束
     */
    protected boolean beforeEnd = true;
    /**
     * 最有一次执行时间
     */
    protected long lastTime = 0;

    /**
     * 是否执行结束
     *
     * @return
     */
    public boolean isBeforeEnd() {
        return beforeEnd;
    }

    public void setBeforeEnd(boolean beforeEnd) {
        this.beforeEnd = beforeEnd;
    }

    /**
     * 最后执行时间
     *
     * @return
     */
    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    /**
     *
     * @param startTime 指定开始时间
     * @param isStartAction 是否一开始就执行一次
     * @param endTime 指定结束时间
     * @param maxExecCount 指定执行次数
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskModel(long startTime, boolean isStartAction, long endTime, int maxExecCount, int intervalTime) {
        super();
        this.startTime = startTime;
        this.startAction = isStartAction;
        this.endTime = endTime;
        this.maxExecCount = maxExecCount;
        this.intervalTime = intervalTime;
    }

    /**
     * 指定任务的开始执行时间
     *
     * @param startTime 指定开始时间
     * @param isStartAction 是否一开始就执行一次
     * @param maxExecCount 指定执行次数
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskModel(long startTime, boolean isStartAction, int maxExecCount, int intervalTime) {
        this(startTime, isStartAction, 0, maxExecCount, intervalTime);
    }

    /**
     * 指定结束时间已结束时间为准，执行次数不一定够
     *
     * @param isStartAction 是否一开始就执行一次
     * @param endTime 指定结束时间
     * @param maxExecCount 指定执行次数
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskModel(boolean isStartAction, long endTime, int maxExecCount, int intervalTime) {
        this(0, isStartAction, endTime, maxExecCount, intervalTime);
    }

    /**
     * 指定开始时间，和结束时间
     *
     * @param startTime 指定开始时间
     * @param endTime 指定结束时间
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskModel(long startTime, long endTime, int intervalTime) {
        this(startTime, false, endTime, -1, intervalTime);
    }

    /**
     * 指定的执行次数和间隔时间
     *
     * @param maxExecCount 指定执行次数
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskModel(int maxExecCount, int intervalTime) {
        this(0, false, 0, maxExecCount, intervalTime);
    }

    /**
     * 提交后指定的时间无限制执行
     *
     * @param intervalTime 指定间隔时间
     */
    public TimerTaskModel(int intervalTime) {
        this(0, false, 0, -1, intervalTime);
    }

    /**
     * 模型开始时间
     *
     * @return
     */
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 加入线程组立即执行一次
     *
     * @return
     */
    public boolean isStartAction() {
        return startAction;
    }

    public void setStartAction(boolean startAction) {
        this.startAction = startAction;
    }

    /**
     * 结束时间
     *
     * @return
     */
    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * 最大执行次数
     *
     * @return
     */
    public int getMaxExecCount() {
        return maxExecCount;
    }

    public void setMaxExecCount(int maxExecCount) {
        this.maxExecCount = maxExecCount;
    }

    /**
     * 间隔执行时间
     *
     * @return
     */
    public int getIntervalTime() {
        return intervalTime;
    }

    /**
     * 间隔执行时间
     *
     * @param intervalTime
     */
    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString() + ",startTime=" + startTime + ", startAction=" + startAction + ", endTime=" + endTime + ", intervalTime=" + intervalTime + ", beforeEnd=" + beforeEnd + ", maxExecCount=" + maxExecCount + ", execCount=" + getExecCount() + ", lastTime=" + lastTime;
    }

}
