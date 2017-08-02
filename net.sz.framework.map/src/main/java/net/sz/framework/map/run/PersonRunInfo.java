package net.sz.framework.map.run;

import java.util.concurrent.atomic.AtomicBoolean;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.utils.MoveUtil;
import net.sz.framework.utils.TimeUtil;
import net.sz.framework.way.navmesh.Vector3;
import net.sz.framework.way.navmesh.path.PathData;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PersonRunInfo {

    protected Vector3 end;    // 当前终点
    protected long theId; //追击id
    protected Vector3 theEnd;   // 最终终点
    protected float theModelVr; //追踪对象半径，之所以不直接引用对象，万一对象消失怎么办
    //最后一次移动的时间
    protected long lastTime;
    protected PathData pathData; //移动点位
    /*上一次从起点到当前终点移动距离*/
    protected double preDistance;
    protected Person person;
    protected long Cooldown = 0;
    /*修正值，如果大于0，那么移动满足值，就停止*/
    protected Float vr;
    /*表示之前所在的视野区域格子*/
    protected int oldAreaId = 0;
    protected RunCallBack callbackRun = null;
    public AtomicBoolean isStoped = new AtomicBoolean(false);

    public PersonRunInfo(Person person, PathData points, long cooldown, Vector3 theend, float theModelVr, long theid, int oldAreaId) {
        this(person, points, cooldown, theend, theModelVr, theid, oldAreaId, null, null);
    }

    public PersonRunInfo(Person person, PathData points, long cooldown, Vector3 theend, float theModelVr, long theid, int oldAreaId, Float vr, RunCallBack callback) {
        this.pathData = points;
        this.person = person;

        this.oldAreaId = oldAreaId;

        this.lastTime = TimeUtil.currentTimeMillis();

        this.theEnd = theend;
        this.theId = theid;
        this.theModelVr = theModelVr;

        this.Cooldown = cooldown;

        this.vr = vr;
        this.callbackRun = callback;
        this.end = nextVector3();
        //取消 如果起点和当前坐标在同一个格子，就不在读取
        double distance = Vector3.distance(this.person.getPosition(), this.end);
        if (distance < 0.05f) {
            removeVector3();
            if (hasNextVector3()) {
                this.end = nextVector3();
            } else {
                throw new UnsupportedOperationException("没有可用的移动节点");
            }
        }

        MoveUtil.getV12Vector(
                this.person.getVectorDir(),
                this.person.getPosition().getX(),
                this.person.getPosition().getZ(),
                this.end.getX(),
                this.end.getZ());

        double distance1 = MoveUtil.distance(
                this.person.getPosition().getX(),
                this.person.getPosition().getZ(),
                this.end.getX(),
                this.end.getZ());

        /*设置当前距离*/
        this.preDistance = distance1;
    }

    /**
     * 调用前，一般需要删除上一个节点
     *
     * @return
     */
    public final boolean hasNextVector3() {
        return this.pathData != null && this.pathData.points.size() > 0;
    }

    /**
     * 获取，并不会移除当前节点
     *
     * @return
     */
    public final Vector3 nextVector3() {
        if (this.pathData == null || this.pathData.points.size() < 1) {
            return this.theEnd;
        } else {
            return this.pathData.points.get(0);
        }
    }

    /**
     * 并且移除
     *
     */
    public final void removeVector3() {
        if (hasNextVector3()) {
            this.pathData.points.remove(0);
        }
    }

    public double getPreDistance() {
        return preDistance;
    }

    public void setPreDistance(double preDistance) {
        this.preDistance = preDistance;
    }

    public Vector3 getEnd() {
        return end;
    }

    public void setEnd(Vector3 end) {
        this.end = end;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public AtomicBoolean getIsStoped() {
        return isStoped;
    }

    public void setIsStoped(AtomicBoolean isStoped) {
        this.isStoped = isStoped;
    }

    public Vector3 getTheEnd() {
        return theEnd;
    }

    public void setTheEnd(Vector3 theEnd) {
        this.theEnd = theEnd;
    }

    public PathData getPathData() {
        return pathData;
    }

    public void setPathData(PathData pathData) {
        this.pathData = pathData;
    }

    public long getTheId() {
        return theId;
    }

    public void setTheId(long theId) {
        this.theId = theId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public long getCooldown() {
        return Cooldown;
    }

    public void setCooldown(long Cooldown) {
        this.Cooldown = Cooldown;
    }

    public Float getVr() {
        return vr;
    }

    public void setVr(Float vr) {
        this.vr = vr;
    }

    public int getOldAreaId() {
        return oldAreaId;
    }

    public void setOldAreaId(int oldAreaId) {
        this.oldAreaId = oldAreaId;
    }

    public RunCallBack getCallbackRun() {
        return callbackRun;
    }

    public void setCallbackRun(RunCallBack callbackRun) {
        this.callbackRun = callbackRun;
    }

    public float getTheModelVr() {
        return theModelVr;
    }

    public void setTheModelVr(float theModelVr) {
        this.theModelVr = theModelVr;
    }

}
