package net.sz.framework.map;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public final class MapType {

    private static HashMap<Integer, MapType> values = new HashMap<>();
    /**
     * 0 普通世界地图
     */
    public static final MapType WORLDMAP = new MapType(0, 0, "普通世界地图");
    /**
     * 1 副本地图
     */
    public static final MapType ZONEMAP = new MapType(1, 1, "副本地图");
    /**
     * 世界BOSS地图
     */
    public static final MapType WORLDBOSSMAP = new MapType(2, 0, "世界BOSS地图");
    /**
     * 3 战场地图
     */
    public static final MapType BATTLEMAP = new MapType(3, 3, "战场地图");
    /**
     * 8 挂机地图
     */
    public static final MapType HANGUP = new MapType(8, 0, "挂机地图");
    /**
     * 9 工会战场地图
     */
    public static final MapType GUILDBATTLEMAP = new MapType(9, 3, "工会战场地图");
    /**
     * 13 平衡战场地图
     */
    public static final MapType BALANCEBATTLEMAP = new MapType(13, 3, "平衡战场地图");
    /**
     * 14 城战地图
     */
    public static final MapType CITYBATTLEMAP = new MapType(14, 3, "城战地图");
    /**
     * 15, "新手引导地图"
     */
    public static final MapType PLOTMAP = new MapType(15, 1, "新手引导地图");
    /**
     * 16, 2, "团队副本地图"
     */
    public static final MapType TEAMZONEMAP = new MapType(16, 1, "团队副本地图");

    private final int value;
    private final int group;
    private final String msg;

    public MapType(int value, int group, String msg) {
        if (values.containsKey(value)) {
            throw new UnsupportedOperationException("类型重复");
        }

        this.value = value;
        this.group = group;
        this.msg = msg;

        values.put(value, this);
    }

    /**
     * 具体地图类型
     *
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     * 地图类型分组，1表示世界地图，2表示副本地图,3战场地图
     *
     * @return
     */
    public final int getGroup() {
        return group;
    }

    /**
     * 文字描述
     *
     * @return
     */
    public String getMsg() {
        return msg;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.value;
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
        final MapType other = (MapType) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    public static MapType getMapType(int mapType) {
        for (Map.Entry<Integer, MapType> entry : values.entrySet()) {
            MapType value = entry.getValue();
            if (value.getValue() == mapType) {
                return value;
            }
        }
        return null;
    }
}
