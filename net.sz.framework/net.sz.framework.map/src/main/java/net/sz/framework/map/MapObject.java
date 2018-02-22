package net.sz.framework.map;

import java.io.Serializable;
import net.sz.framework.map.manager.AbsMapManager;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.struct.ObjectBase;
import net.sz.framework.struct.Vector;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.concurrent.ConcurrentHashSet;
import net.sz.framework.utils.MoveUtil;
import net.sz.framework.way.navmesh.Vector3;

/**
 * 一切地图场景对象基对象
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapObject extends ObjectBase implements Serializable, Cloneable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 6980610322867696907L;

    private volatile int lineId;
    private volatile long mapId;
    private volatile int mapModelId;
    /* 地图键值 */
    private volatile String mapKey;
    /*模型id*/
    private volatile int modelId;
    /*模型半径,一般用于体形巨大的怪物上,如炎魔*/
    private volatile float modelRadius = 0.5f;
    /*进入地图时间*/
    private volatile long enterMapTime;
    /*进入地图时间*/
    private volatile boolean enterMap;
    /*坐标位置*/
    private volatile Vector3 position = null;
    /*方向信息*/
    private volatile Vector vectorDir = null;
    /*该对象的隐藏对象*/
    private ConcurrentHashSet<Long> hideSet = new ConcurrentHashSet<>();
    /*显示列表*/
    private ConcurrentHashSet<Long> showSet = new ConcurrentHashSet<>();
    /*对象类型*/
    private MapObjectType mapObjectType = MapObjectType.NONE;
    /*地图区域Id*/
    private volatile transient int mapAreaId;
    /*显示状态,如果是true则不发送给客户端*/
    private volatile transient boolean notShow = false;
    /*客户端相对玩家是否可见*/
    private volatile transient boolean notClientShow = false;
    /*对象当前在位移中*/
    private volatile transient boolean driftMove = false;
    /*该对象 true 不能移动*/
    private volatile transient boolean canNotMove = false;
    /*该对象 true 不能攻击别人*/
    private volatile transient boolean canNotAttack = false;
    /*该对象 true 不能被攻击*/
    private volatile transient boolean canNotCoAttack = false;

    private MapObject() {
    }

    /**
     * 设置对象的唯一Id
     *
     * @param id
     */
    public MapObject(long id) {
        super.setId(id);
    }

    /**
     * 把当前对象，拷贝负责传入对象的地图信息
     *
     * @param mapObject 传入需要拷贝地图信息的对象
     */
    public final void inMapObject(MapObject mapObject) {
        this.lineId = mapObject.lineId;
        this.mapId = mapObject.mapId;
        this.mapModelId = mapObject.mapModelId;
        this.mapKey = mapObject.mapKey;
    }

    /**
     * 把当前对象，拷贝负责传入对象的地图信息
     *
     * @param lineId
     * @param mapId
     * @param mapModelId
     */
    public final void inMapObject(int lineId, long mapId, int mapModelId) {
        this.lineId = lineId;
        this.mapId = mapId;
        this.mapModelId = mapModelId;
        this.mapKey = AbsMapManager.getMapSkey(mapId, mapModelId, lineId);
    }

    /**
     * 获取坐标点
     *
     * @return
     */
    public Vector3 getPosition() {
        if (position == null) {
            position = Vector3.getVector3();
        }
        return position;
    }

    /**
     * 设置坐标点
     *
     * @param position
     */
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    /**
     * 当前对象朝向
     *
     * @return
     */
    public Vector getVectorDir() {
        if (vectorDir == null) {
            vectorDir = new Vector();
        }
        return vectorDir;
    }

    /**
     * 设置朝向
     *
     * @param vectorDir
     */
    public void setVectorDir(Vector vectorDir) {
        this.vectorDir = vectorDir;
    }

    /**
     * 对象类型
     *
     * @return
     */
    public final MapObjectType getMapObjectType() {
        return mapObjectType;
    }

    public final void setMapObjectType(MapObjectType mapObjectType) {
        this.mapObjectType = mapObjectType;
    }

    /**
     * 对象所在区域id
     *
     * @return
     */
    public int getMapAreaId() {
        return mapAreaId;
    }

    public void setMapAreaId(int mapAreaId) {
        this.mapAreaId = mapAreaId;
    }

    /**
     * 线路ID
     *
     * @return
     */
    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public int getMapModelId() {
        return mapModelId;
    }

    public void setMapModelId(int mapModelId) {
        this.mapModelId = mapModelId;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public boolean isEnterMap() {
        return enterMap;
    }

    public void setEnterMap(boolean enterMap) {
        this.enterMap = enterMap;
    }

    /**
     * 进入地图的时间
     *
     * @return
     */
    public long getEnterMapTime() {
        return enterMapTime;
    }

    public void setEnterMapTime(long enterMapTime) {
        this.enterMapTime = enterMapTime;
    }

    /**
     * true表示隐藏对象
     *
     * @return
     */
    public boolean isNotShow() {
        return notShow;
    }

    public void setNotShow(boolean notShow) {
        this.notShow = notShow;
    }

    public boolean isNotClientShow() {
        return notClientShow;
    }

    public void setNotClientShow(boolean notClientShow) {
        this.notClientShow = notClientShow;
    }

    public ConcurrentHashSet<Long> getHideSet() {
        if (hideSet == null) {
            hideSet = new ConcurrentHashSet<>();
        }
        return hideSet;
    }

    public void setHideSet(ConcurrentHashSet<Long> hideSet) {
        this.hideSet = hideSet;
    }

    public ConcurrentHashSet<Long> getShowSet() {
        if (showSet == null) {
            showSet = new ConcurrentHashSet<>();
        }
        return showSet;
    }

    public void setShowSet(ConcurrentHashSet<Long> showSet) {
        this.showSet = showSet;
    }

    /**
     * 添加隐藏对象
     *
     * @param hide
     */
    public void addHide(long hide) {
        this.hideSet.add(hide);
        removeShow(hide);
    }

    public void removeHide(long hide) {
        this.hideSet.remove(hide);
    }

    /**
     * 添加显示对象
     *
     * @param show
     */
    public void addShow(long show) {
        this.showSet.add(show);
        removeHide(show);
    }

    public void removeShow(long show) {
        this.showSet.remove(show);
    }

    /**
     * 模型半径
     *
     * @return
     */
    public float getModelRadius() {
        return modelRadius;
    }

    /**
     * 模型半径不能小于0.5
     *
     * @param modelRadius
     */
    public void setModelRadius(float modelRadius) {
        if (modelRadius > 0.5f) {
            this.modelRadius = modelRadius;
        }
    }

    /**
     * 不能移动
     *
     * @return
     */
    public boolean isCanNotMove() {
        return canNotMove;
    }

    public void setCanNotMove(boolean canNotMove) {
        this.canNotMove = canNotMove;
    }

    /**
     * 对象当前是否在位移
     * <br>
     * true 表示位移
     *
     * @return
     */
    public boolean isDriftMove() {
        return driftMove;
    }

    /**
     * 对象当前是否在位移
     *
     * @param driftMove true 表示位移中
     */
    public void setDriftMove(boolean driftMove) {
        this.driftMove = driftMove;
    }

    /**
     * 不能攻击
     *
     * @return
     */
    public boolean isCanNotAttack() {
        return canNotAttack;
    }

    public void setCanNotAttack(boolean canNotAttack) {
        this.canNotAttack = canNotAttack;
    }

    /**
     * 不能被攻击
     *
     * @return
     */
    public boolean isCanNotCoAttack() {
        return canNotCoAttack;
    }

    public void setCanNotCoAttack(boolean canNotCoAttack) {
        this.canNotCoAttack = canNotCoAttack;
    }

    /**
     * 检查是否可以攻击
     *
     * @return boolean true 能 false 不能
     */
    public boolean canAttack() {
        return !this.canNotAttack;
    }

    /**
     * 检查是否可以攻击
     *
     * @param target 目标对象
     * @return boolean true 能 false 不能
     */
    public boolean canAttack(Person target) {
        if (target == null) {
            return false;
        }
        if (this.canAttack()) {
            /*自己可攻击的情况下判断对方是否是可被攻击状态*/
            return target.canUnCoAttack();
        }
        return false;
    }

    /**
     * 是否允许释放技能,不能使用技能的时候是可以使用普通攻击
     *
     * @return boolean true 能 false 不能
     */
    public boolean canUseSkill() {
        return true;
    }

    /**
     * 检查是否可以被攻击
     *
     * @return boolean true 能 false 不能
     */
    public boolean canUnCoAttack() {
        return !this.canNotCoAttack;
    }

    /**
     * 检查是否能移动
     *
     * @return boolean true 能 false 不能
     */
    public boolean canMove() {
        return !this.canNotMove && !this.driftMove;
    }

    /**
     * 检查是否可以被拉取
     *
     * @return boolean true 能 false 不能
     */
    public boolean canMoveLaQu() {
        return true;
    }

    /**
     * 检查是否可以被击退
     *
     * @return boolean true 能 false 不能
     */
    public boolean canMoveJiTui() {
        return true;
    }

    /**
     * 是否可以看见
     *
     * @param person
     * @return
     */
    public boolean canSee(MapObject person) {

        /*如果在隐藏列表*/
        if (person == null
                || this.getHideSet().contains(person.getModelId())
                || this.getHideSet().contains(person.getId())) {
            return false;
        }

        if (notShow) {
            if (this.mapObjectType.getClientGroup() == MapObjectType.PLAYER.getClientGroup()) {
                this.notShow = false;
            } else /*如果在显示列表里面*/ if (!this.getShowSet().contains(person.getModelId() * 1l)
                    && !this.getShowSet().contains(person.getId())) {
                return false;
            }
        }

        /*相互判断*/
        if (person.getHideSet().contains(this.getModelId() * 1l)
                || person.getHideSet().contains(this.getId())) {
            /*如果在隐藏列表*/
            return false;
        }

        if (person.notShow) {
            if (person.mapObjectType.getClientGroup() == MapObjectType.PLAYER.getClientGroup()) {
                this.notShow = false;
            } else /*如果在显示列表里面*/ if (!person.getShowSet().contains(this.getModelId() * 1l)
                    && !person.getShowSet().contains(this.getId())) {
                return false;
            }
        }
        return !notShow;
    }

    /**
     * 计算两个场景对象距离
     *
     * @param object
     * @return
     */
    public double distance(MapObject object) {
        if (object == null) {
            return 0;
        }
        return distance(object.position.getX(), object.position.getZ(), object.modelRadius);
    }

    /**
     * 计算两个场景对象距离
     *
     * @param x
     * @param z
     * @param r 计算距离的时候，场景对象半径
     * @return
     */
    public double distance(double x, double z, double r) {
        double distance = MoveUtil.distance(this.position.getX(), this.position.getZ(), x, z);
        return distance - this.modelRadius - r;
    }

    @Override
    public String toString() {
        return super.toString() + "lineId=" + lineId + ", mapId=" + mapId + ", mapModelId=" + mapModelId + ", mapKey=" + mapKey + ", modelId=" + modelId + ", modelRadius=" + modelRadius + ", position=" + position + ", vectorDir=" + vectorDir + ", spiritType=" + mapObjectType + ", mapAreaId=" + mapAreaId;
    }

    @Override
    public String showString() {
        return super.showString() + ", mapId=" + mapId + ", mapModelId=" + mapModelId + ", modelId=" + modelId + ", position=" + position.showString() + ", spiritType=" + mapObjectType + ", mapAreaId=" + mapAreaId;
    }

}
