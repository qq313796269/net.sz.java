package net.sz.game.engine.map;

import java.io.Serializable;
import net.sz.game.engine.map.manager.AbsMapManager;
import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.struct.ObjectBase;
import net.sz.game.engine.struct.Vector;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.util.ConcurrentHashSet;
import net.sz.game.engine.utils.BitUtil;
import net.sz.game.engine.utils.MoveUtil;

/**
 * 一切地图场景对象基对象
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapObject extends ObjectBase implements Serializable, Cloneable {

    private static SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 6980610322867696907L;

    // <editor-fold defaultstate="collapsed" desc="场景对象类型 public enum SpiritType">
    /**
     * 场景对象类型
     */
    public enum SpiritType {
        /**
         * 0, 0, "无特殊"
         */
        NONE(0, 0, "无特殊"),
        /**
         * 1, 1, "玩家"
         */
        PLAYER(1, 1, "玩家"), // 玩家
        /**
         * 2, 2, "怪物"
         */
        MONSTER(2, 2, "怪物"), // 怪物
        /**
         * 3, 2, "NPC"
         */
        NPC(3, 2, "NPC"), // NPC
        /**
         * 4, 4, "宠物"
         */
        PET(4, 4, "宠物"), // 宠物
        /**
         * 5, 5, "特效"
         */
        EFFECT(5, 5, "特效"),
        /**
         * 6, 6, "掉落物"
         */
        DROP(6, 6, "掉落物"),
        /**
         * 7, 7, "地面魔法"
         */
        MAGIC(7, 7, "地面魔法"),
        /**
         * 8, 8, "链接特效"
         */
        LINKEFFECT(8, 8, "链接特效"),
        /**
         * 9, 1, "内部机器人"
         */
        ROBOT(9, 1, "内部机器人"), // 内部机器人
        /**
         * 15, 2, "陷阱"
         */
        TRAP(15, 2, "陷阱"), // 陷阱
        /**
         * 16, 2, "炮台"
         */
        BATTERY(16, 2, "炮台"), // 炮台
        /**
         * 17, 2, "阻挡门"
         */
        DOOR(17, 2, "阻挡门"), // 阻挡门
        ;

        private final byte _type;

        private final byte _group;
        private long _value = 0;
        private final String _msg;

        private SpiritType(int _type, int _group, String _msg) {
            this._type = (byte) _type;
            this._group = (byte) _group;
            this._value = BitUtil.addBitLong(this._value, _type);
            this._msg = _msg;
        }

        public byte getType() {
            return _type;
        }

        public String getMsg() {
            return _msg;
        }

        public byte getGroup() {
            return _group;
        }

        public long getValue() {
            return _value;
        }

        /**
         *
         * @param type
         * @return
         */
        public static SpiritType getPersonType(int type) {
            SpiritType[] values = SpiritType.values();
            for (int i = 0; i < values.length; i++) {
                SpiritType personType = values[i];
                if (personType.getType() == type) {
                    return personType;
                }
            }
            throw new UnsupportedOperationException("不存在");
        }

    }
    // </editor-fold>

    protected int lineId;
    protected long mapId;
    protected int mapModelId;
    /* 地图键值 */
    protected String mapKey;
    /*模型id*/
    protected int modelId;
    /*模型半径,一般用于体形巨大的怪物上,如炎魔*/
    protected float modelRadius;
    /*进入地图时间*/
    protected long enterMapTime;
    /*坐标位置*/
    protected Vector3 position = null;
    /*方向信息*/
    protected Vector vectorDir = null;
    /*该对象的隐藏对象*/
    protected ConcurrentHashSet<Long> hideSet = new ConcurrentHashSet<>();
    /*显示列表*/
    protected ConcurrentHashSet<Long> showSet = new ConcurrentHashSet<>();
    /*对象类型*/
    protected SpiritType spiritType = SpiritType.NONE;
    /*地图区域Id*/
    protected transient int mapAreaId;
    /*显示状态*/
    protected transient boolean show = true;
    /*该对象 true 不能移动*/
    protected transient boolean canNotMove = false;
    /*该对象 true 不能攻击别人*/
    protected transient boolean canNotAttack = false;
    /*该对象 true 不能被攻击*/
    protected transient boolean canNotCoAttack = false;

    /**
     * 设置对象的唯一Id
     *
     * @param id
     */
    public MapObject(long id) {
        this.id = id;
    }

    public void inMapObject(MapObject mapObject) {
        this.lineId = mapObject.lineId;
        this.mapId = mapObject.mapId;
        this.mapModelId = mapObject.mapModelId;
        this.mapKey = mapObject.mapKey;
    }

    public void inMapObject(int lineId, long mapId, int mapModelId) {
        this.lineId = lineId;
        this.mapId = mapId;
        this.mapModelId = mapModelId;
        this.mapKey = AbsMapManager.getMapSkey(mapId, mapModelId, lineId);
    }

    public Vector3 getPosition() {
        if (position == null) {
            position = new Vector3();
        }
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector getVectorDir() {
        if (vectorDir == null) {
            vectorDir = new Vector();
        }
        return vectorDir;
    }

    public void setVectorDir(Vector vectorDir) {
        this.vectorDir = vectorDir;
    }

    public final SpiritType getSpiritType() {
        return spiritType;
    }

    public final void setSpiritType(SpiritType spiritType) {
        this.spiritType = spiritType;
    }

    public int getMapAreaId() {
        return mapAreaId;
    }

    public void setMapAreaId(int mapAreaId) {
        this.mapAreaId = mapAreaId;
    }

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

    public long getEnterMapTime() {
        return enterMapTime;
    }

    public void setEnterMapTime(long enterMapTime) {
        this.enterMapTime = enterMapTime;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
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

    public void addHide(long hide) {
        this.hideSet.add(hide);
    }

    public void removeHide(long hide) {
        this.hideSet.remove(hide);
    }

    public void addShow(long show) {
        this.showSet.add(show);
    }

    public void removeShow(long show) {
        this.showSet.add(show);
    }

    public float getModelRadius() {
        return modelRadius;
    }

    public void setModelRadius(float modelRadius) {
        this.modelRadius = modelRadius;
    }

    public boolean isCanNotMove() {
        return canNotMove;
    }

    public void setCanNotMove(boolean canNotMove) {
        this.canNotMove = canNotMove;
    }

    public boolean isCanNotAttack() {
        return canNotAttack;
    }

    public void setCanNotAttack(boolean canNotAttack) {
        this.canNotAttack = canNotAttack;
    }

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
        return !this.canNotMove;
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

        if (!show) {
            if (this.spiritType.getGroup() == SpiritType.PLAYER.getGroup()) {
                this.show = true;
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

        if (!person.show) {
            if (person.spiritType.getGroup() == SpiritType.PLAYER.getGroup()) {
                this.show = true;
            } else /*如果在显示列表里面*/ if (!person.getShowSet().contains(this.getModelId() * 1l)
                    && !person.getShowSet().contains(this.getId())) {
                return false;
            }
        }
        return show;
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
        return super.toString() + ",lineId=" + lineId + ", mapId=" + mapId + ", mapModelId=" + mapModelId + ", modelId=" + modelId + ", position=" + position + ", vectorDir=" + vectorDir + ", spiritType=" + spiritType.getMsg();
    }

    @Override
    public String showString() {
        return super.showString() + ", position=" + position.showString() + ", vectorDir=" + vectorDir.showString() + ", spiritType=" + spiritType.getMsg();
    }

}
