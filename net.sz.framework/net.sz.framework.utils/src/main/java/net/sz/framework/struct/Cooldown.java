package net.sz.framework.struct;

import java.io.Serializable;
import net.sz.framework.utils.TimeUtil;

/**
 * 冷却信息类
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Cooldown implements Cloneable, Serializable {

    private static final long serialVersionUID = 2294363482787659688L;

    //开始时间
    private volatile long start;
    //持续时间
    private volatile long delay;
    //结束时间
    protected volatile transient long endTime;
//    //剩余时间
//    protected transient long remainTime;

    /**
     * 
     * @return 
     */
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    /**
     * 
     * @return 
     */
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * 获取结束时间
     *
     * @return
     */
    public long endTime() {
        return start + delay;
    }

    /**
     * 获取剩余时间
     *
     * @return
     */
    public long remainTime() {
        return remainTime(0);
    }

    public long remainTime(int allow) {
        long timer = endTime() - TimeUtil.currentTimeMillis() - allow;
        return timer > 0 ? timer : 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

}
