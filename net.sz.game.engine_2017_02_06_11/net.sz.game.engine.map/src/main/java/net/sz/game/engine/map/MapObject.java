package net.sz.game.engine.map;

import java.io.Serializable;
import java.util.HashSet;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.struct.ObjectBase;
import net.sz.game.engine.struct.Vector;
import net.sz.game.engine.util.ConcurrentHashSet;
import net.sz.game.engine.utils.BitUtil;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapObject extends ObjectBase implements Serializable {

    private static final Logger log = Logger.getLogger(MapObject.class);
    private static final long serialVersionUID = 6980610322867696907L;

    protected int lineId;
    protected long mapId;
    protected int mapModelId;
    /*地图线程分组线程组*/
    protected int mapThreadNext;
    /*地图线程*/
    protected long mapThread;
    /*模型id*/
    protected int modelId;
    /**
     * 进入地图时间
     */
    protected long enterMapTime;
    protected Vector3 position = null;
    protected Vector vectorDir = null;
    //是否显示
    protected transient boolean show = true;
    protected ConcurrentHashSet<Long> hideSet = new ConcurrentHashSet<>();
    protected ConcurrentHashSet<Long> showSet = new ConcurrentHashSet<>();

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

//对象类型
    protected SpiritType spiritType;

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
    }

    public void inMapObject(int lineId, long mapId, int mapModelId) {
        this.lineId = lineId;
        this.mapId = mapId;
        this.mapModelId = mapModelId;
    }

    public void inMapObject(int lineId, long mapId, int mapModelId, int mapThreadNext, long mapThread) {
        this.lineId = lineId;
        this.mapId = mapId;
        this.mapModelId = mapModelId;
        this.mapThreadNext = mapThreadNext;
        this.mapThread = mapThread;
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

    public int getMapThreadNext() {
        return mapThreadNext;
    }

    public void setMapThreadNext(int mapThreadNext) {
        this.mapThreadNext = mapThreadNext;
    }

    public long getMapThread() {
        return mapThread;
    }

    public void setMapThread(long mapThread) {
        this.mapThread = mapThread;
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

    @Override
    public String toString() {
        return super.toString() + ",lineId=" + lineId + ", mapId=" + mapId + ", mapModelId=" + mapModelId + ", modelId=" + modelId + ", position=" + position + ", vectorDir=" + vectorDir + ", spiritType=" + spiritType.getMsg();
    }

    public boolean canSee(MapObject person) {

        /*如果在隐藏列表*/
        if (this.getHideSet().contains(person.getModelId() * 1l)
                || this.getHideSet().contains(person.getId())) {
            return false;
        }

        if (!show) {
            if (this.spiritType.getGroup() == SpiritType.PLAYER.getGroup()) {
                this.show = true;
            } else {
                /*如果在显示列表里面*/
                if (!this.getShowSet().contains(person.getModelId() * 1l)
                        && !this.getShowSet().contains(person.getId())) {
                    return false;
                }
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
            } else {
                /*如果在显示列表里面*/
                if (!person.getShowSet().contains(this.getModelId() * 1l)
                        && !person.getShowSet().contains(this.getId())) {
                    return false;
                }
            }
        }
        return show;
    }
}
