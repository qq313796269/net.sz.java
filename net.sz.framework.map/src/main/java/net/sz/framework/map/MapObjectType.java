package net.sz.framework.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.sz.framework.utils.BitUtil;

/**
 * 场景对象类型
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public final class MapObjectType implements Serializable {

    private static HashMap<Byte, Long> typeValueMap = new HashMap<>();
    private static HashMap<Byte, MapObjectType> typeMap = new HashMap<>();

    /**
     * 0, 0, 0, "无特殊"
     */
    public static final MapObjectType NONE = new MapObjectType(0, 0, 0, "无特殊");
    /**
     * 1, 1, 1, "玩家"
     */
    public static final MapObjectType PLAYER = new MapObjectType(1, 1, 1, "玩家"); // 玩家
    /**
     * 2, 2, 2, "怪物"
     */
    public static final MapObjectType MONSTER = new MapObjectType(2, 2, 2, "怪物"); // 怪物
    /**
     * 3, 2, 2, "NPC"
     */
    public static final MapObjectType NPC = new MapObjectType(3, 2, 2, "NPC"); // NPC
    /**
     * 4, 4, 4, "宠物"
     */
    public static final MapObjectType PET = new MapObjectType(4, 4, 1, "宠物"); // 宠物
    /**
     * 5, 5, 2, "特效"
     */
    public static final MapObjectType EFFECT = new MapObjectType(5, 5, 2, "特效");
    /**
     * 6, 6, 6, "掉落物"
     */
    public static final MapObjectType DROP = new MapObjectType(6, 6, 6, "掉落物");
    /**
     * 7, 7, 2, "地面魔法"
     */
    public static final MapObjectType MAGIC = new MapObjectType(7, 7, 2, "地面魔法");
    /**
     * 8, 8, 2, "链接特效"
     */
    public static final MapObjectType LINKEFFECT = new MapObjectType(8, 8, 2, "链接特效");
    /**
     * 9, 1, 1, "内部机器人"
     */
    public static final MapObjectType PLAYER_ROBOT = new MapObjectType(9, 1, 1, "内部机器人");

    private final byte _type;
    private final byte _clientGroup;
    private final byte _serverGroup;
    private long _value = 0;
    private final String _msg;

    /**
     *
     * @param _type1 类型，数据库配置
     * @param _clientGroup 和客户端保持一致的分组信息
     * @param _serverGroup 服务器专用分组信息
     * @param _msg 文字描述
     */
    private MapObjectType(int _type1, int _clientGroup, int _serverGroup, String _msg) {

        if (typeMap.containsKey((byte) _type1)) {
            throw new UnsupportedOperationException("类型重复：" + _type1);
        }

        this._type = (byte) _type1;
        this._clientGroup = (byte) _clientGroup;
        this._serverGroup = (byte) _serverGroup;
        this._value = BitUtil.addBitLong(this._value, _type1);
        this._msg = _msg;

        Long v1 = typeValueMap.get(this._clientGroup);
        if (v1 == null) {
            v1 = 0L;
        }

        v1 = BitUtil.addBitLong(v1, this._clientGroup);
        typeValueMap.put(this._clientGroup, v1);
        typeMap.put(this._type, this);
    }

    /**
     * 数据库验证对比类型
     *
     * @return
     */
    public byte getType() {
        return _type;
    }

    /**
     * 获取和客户端一样的分组信息
     *
     * @return
     */
    public byte getClientGroup() {
        return _clientGroup;
    }

    /**
     * 服务器单独分组信息
     *
     * @return
     */
    public byte getServerGroup() {
        return _serverGroup;
    }

    /**
     * 分组的二进制检测数据
     *
     * @return
     */
    public long getGroupValue() {
        return typeValueMap.get(_serverGroup);
    }

    /**
     * 分组信息对应的二进制存储形式
     *
     * @return
     */
    public long getValue() {
        return _value;
    }

    public String getMsg() {
        return _msg;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this._type;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapObjectType other = (MapObjectType) obj;
        if (this._type != other._type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MapObjectType{" + "_type=" + _type + ", _clientGroup=" + _clientGroup + ", _serverGroup=" + _serverGroup + ", _value=" + _value + ", _msg=" + _msg + '}';
    }

    /**
     *
     * @param type
     * @return
     */
    public static MapObjectType getPersonType(int type) {
        for (Map.Entry<Byte, MapObjectType> entry : typeMap.entrySet()) {
            MapObjectType personType = entry.getValue();
            if (personType.getType() == type) {
                return personType;
            }
        }
        throw new UnsupportedOperationException("不存在");
    }
}
