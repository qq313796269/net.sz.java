package net.sz.game.engine.map;

import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.map.manager.AbsMapManager;
import net.sz.game.engine.map.run.RunTimerTask;
import net.sz.game.engine.map.thread.MapThread;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.navmesh.path.NavMap;
import net.sz.game.engine.thread.TaskEvent;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.TimerTaskEvent;
import net.sz.game.engine.util.NodeEnty2;
import net.sz.game.engine.utils.BitUtil;
import net.sz.game.engine.utils.MoveUtil;
import org.apache.log4j.Logger;

/**
 * 暂时未使用的
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapInfo extends MapObject {

    private static final Logger log = Logger.getLogger(MapInfo.class);

    private static final long serialVersionUID = -2839255230502612896L;

    public static void main(String[] args) {
        MapInfo mapinfo = new MapInfo(1, 5, 5, 0.5f, 0.5f, 10, 10, MapType.HANGUP);

        float x = 10f;
        float h = 11.3f;

        ArrayList<Integer> roundSeats = mapinfo.getRoundAreaIds(x, h);

        log.error(MoveUtil.seat(x, mapinfo.getArea_width()) + "," + MoveUtil.seat(h, mapinfo.getArea_height()));
        log.error(MoveUtil.position(MoveUtil.seat(x, mapinfo.getArea_width()), mapinfo.getArea_width()) + "," + MoveUtil.position(MoveUtil.seat(h, mapinfo.getArea_height()), mapinfo.getArea_height()));

    }

    @Override
    public String toString() {
        return "MapInfo{" + "round_width=" + round_width + ", round_hieght=" + round_hieght + ", area_width=" + area_width + ", area_height=" + area_height + ", area_Max_Width=" + area_Max_Width + ", area_Max_Height=" + area_Max_Height + ", map_width=" + map_width + ", map_height=" + map_height + ", mapType=" + mapType + ", players=" + players.size() + ", npcs=" + npcs.size() + ", pets=" + pets.size() + ", monsters=" + monsters.size() + ", revives=" + revives.size() + ", magics=" + magics.size() + ", effects=" + effects.size() + ", linkEffects=" + linkEffects.size() + ", dropGoodss=" + dropGoodss.size() + '}';
    }

    // <editor-fold defaultstate="collapsed" desc="地图类型(0普通世界地图 1副本地图 2世界BOSS地图 3战场地图) public static enum MapType">
    public static enum MapType {

        /**
         * 0 普通世界地图
         */
        WORLDMAP(0, "普通世界地图"),
        /**
         * 1 副本地图
         */
        ZONEMAP(1, "副本地图"),
        /**
         * 世界BOSS地图
         */
        WORLDBOSSMAP(2, "世界BOSS地图"),
        /**
         * 3 战场地图
         */
        BATTLEMAP(3, "战场地图"),
        /**
         * 8 挂机地图
         */
        HANGUP(8, "挂机地图"),
        /**
         * 9 工会战场地图
         */
        GUILDBATTLEMAP(9, "工会战场地图"),
        /**
         * 13 平衡战场地图
         */
        BALANCEBATTLEMAP(13, "平衡战场地图"),
        /**
         * 14 城战地图
         */
        CITYBATTLEMAP(14, "城战地图"),;

        private final int value;
        private final String msg;

        private MapType(int value, String msg) {
            this.value = value;
            this.msg = msg;
        }

        public int getValue() {
            return value;
        }

        public String getMsg() {
            return msg;
        }

        public static MapType getMapType(int mapType) {
            MapType[] values = MapType.values();
            for (int i = 0; i < values.length; i++) {
                MapType value = values[i];
                if (value.getValue() == mapType) {
                    return value;
                }
            }
            return null;
        }
    }
    // </editor-fold>

    /*同步周围格子信息*/
    private float round_width;
    /*同步周围格子信息*/
    private float round_hieght;
    /*同步周围格子信息*/
    private float area_width;
    /*同步周围格子信息*/
    private float area_height;
    /*当前地图格子宽度最大 信息*/
    private int area_Max_Width;
    /*当前地图格子长度最大 信息*/
    private int area_Max_Height;
    /*地图信息*/
    private float map_width;
    /*地图信息*/
    private float map_height;
    /*当前地图类型 */
    private MapType mapType;
    /* navmesh 寻路*/
    private NavMap navMap;
    /*地图门数量*/
    private int doorsize = 0;
    /*最后一个玩家退出地图的时间*/
    private long playerQuitMapTime;
    /*地图是否已经刷新*/
    private boolean refresh;
    /*处理地图寻路设置，又服务器寻路路径移动，怪物，npc，玩家*/
    private RunTimerTask personRunTimerTask;
    /**/
//    private RunTimerTask playerRunTimerTask;
    /* 在创建 npc 怪物，玩家，和 机器人等情况的时候，分配线程 */
    private final ConcurrentHashMap<Integer, NodeEnty2<Long, Object>> mapthreadMap = new ConcurrentHashMap<>();

    /* 因为单线程处理不考虑线程安全性 */
    //玩家列表
    private final ConcurrentHashMap<Long, MapObject> players = new ConcurrentHashMap<>();
    //npc列表
    private final ConcurrentHashMap<Long, MapObject> npcs = new ConcurrentHashMap<>();
    //宠物列表
    private final ConcurrentHashMap<Long, MapObject> pets = new ConcurrentHashMap<>();
    //怪物列表
    private final ConcurrentHashMap<Long, MapObject> monsters = new ConcurrentHashMap<>();
    //等待复活怪物列表
    private final ConcurrentHashMap<Long, MapObject> revives = new ConcurrentHashMap<>();
    /* 地面魔法 */
    private final ConcurrentHashMap<Long, MapObject> magics = new ConcurrentHashMap<>();
    /* 场景特效 */
    private final ConcurrentHashMap<Long, MapObject> effects = new ConcurrentHashMap<>();
    /* 链接特效列表 */
    private final ConcurrentHashMap<Long, Message> linkEffects = new ConcurrentHashMap<>();
    /* 场景掉落物 */
    private final ConcurrentHashMap<Long, MapObject> dropGoodss = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, MapArea> areas = new ConcurrentHashMap<>();

    private int playerNumber;

    private boolean mapAllShow;

    private int zoneOrBattleModelId;

    public MapInfo() {
        super(0);
    }

    public MapInfo(long mapId, float round_width, float round_hieght, float area_width, float area_height, float map_width, float map_height, MapType mapType) {
        super(mapId);

        this.mapId = mapId;
        this.id = mapId;

        this.round_width = round_width;
        this.round_hieght = round_hieght;

        this.area_width = area_width;
        this.area_height = area_height;

        getArea_width();
        getArea_height();

        getRound_width();
        getRound_hieght();

        this.map_width = map_width;
        this.map_height = map_height;
        this.mapType = mapType;
        this.area_Max_Width = MoveUtil.seat(this.map_width, this.area_width);
        this.area_Max_Height = MoveUtil.seat(this.map_height, this.area_height);
    }

    @Deprecated
    @Override
    public long getId() {
        return id;
    }

    @Deprecated
    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Deprecated
    @Override
    public int getModelId() {
        return modelId;
    }

    @Deprecated
    @Override
    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    @Deprecated
    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Deprecated
    @Override
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public RunTimerTask getPersonRunTimerTask() {
        return personRunTimerTask;
    }

    public void setPersonRunTimerTask(RunTimerTask personRunTimerTask) {
        this.personRunTimerTask = personRunTimerTask;
    }

//    public RunTimerTask getPlayerRunTimerTask() {
//        return playerRunTimerTask;
//    }
//
//    public void setPlayerRunTimerTask(RunTimerTask playerRunTimerTask) {
//        this.playerRunTimerTask = playerRunTimerTask;
//    }
    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public long getPlayerQuitMapTime() {
        return playerQuitMapTime;
    }

    public void setPlayerQuitMapTime(long playerQuitMapTime) {
        this.playerQuitMapTime = playerQuitMapTime;
    }

    public int getZoneOrBattleModelId() {
        return zoneOrBattleModelId;
    }

    public void setZoneOrBattleModelId(int zoneOrBattleModelId) {
        this.zoneOrBattleModelId = zoneOrBattleModelId;
    }

    public boolean isMapAllShow() {
        return mapAllShow;
    }

    public void setMapAllShow(boolean mapAllShow) {
        this.mapAllShow = mapAllShow;
    }

    public float getMap_width() {
        return map_width;
    }

    public void setMap_width(float map_width) {
        this.map_width = map_width;
    }

    public float getMap_height() {
        return map_height;
    }

    public void setMap_height(float map_height) {
        this.map_height = map_height;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public float getRound_width() {
        if (this.round_width < MoveUtil.Const_Round_Width) {
            this.round_width = MoveUtil.Const_Round_Width;
        }
        return round_width;
    }

    public void setRound_width(float round_width) {
        this.round_width = round_width;
    }

    public float getRound_hieght() {
        if (this.round_hieght < MoveUtil.Const_Round_Height) {
            this.round_hieght = MoveUtil.Const_Round_Height;
        }
        return round_hieght;
    }

    public void setRound_hieght(float round_hieght) {
        this.round_hieght = round_hieght;
    }

    public float getArea_width() {
        if (this.area_width < MoveUtil.Const_Area_Width) {
            this.area_width = MoveUtil.Const_Area_Width;
        }
        return area_width;
    }

    public void setArea_width(float area_width) {
        this.area_width = area_width;
    }

    public float getArea_height() {
        if (this.area_height < MoveUtil.Const_Area_Height) {
            this.area_height = MoveUtil.Const_Area_Height;
        }
        return area_height;
    }

    public void setArea_height(float area_height) {
        this.area_height = area_height;
    }

    public int getArea_Max_Width() {
        return area_Max_Width;
    }

    public void setArea_Max_Width(int area_Max_Width) {
        this.area_Max_Width = area_Max_Width;
    }

    public int getArea_Max_Height() {
        return area_Max_Height;
    }

    public void setArea_Max_Height(int area_Max_Height) {
        this.area_Max_Height = area_Max_Height;
    }

    public MapType getMapType() {
        return mapType;
    }

    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }

    public NavMap getNavMap() {
        return navMap;
    }

    public void setNavMap(NavMap navMap) {
        this.navMap = navMap;
    }

    public int getDoorsize() {
        return doorsize;
    }

    public void setDoorsize(int doorsize) {
        this.doorsize = doorsize;
    }

    public ConcurrentHashMap<Long, MapObject> getEffects() {
        return effects;
    }

    public ConcurrentHashMap<Long, MapObject> getDropGoodss() {
        return dropGoodss;
    }

    public ConcurrentHashMap<Long, MapObject> getPlayers() {
        return players;
    }

    public ConcurrentHashMap<Long, MapObject> getNpcs() {
        return npcs;
    }

    public ConcurrentHashMap<Long, MapObject> getPets() {
        return pets;
    }

    public ConcurrentHashMap<Long, MapObject> getMonsters() {
        return monsters;
    }

    public ConcurrentHashMap<Long, MapObject> getRevives() {
        return revives;
    }

    public ConcurrentHashMap<Long, MapObject> getMagics() {
        return magics;
    }

    public ConcurrentHashMap<Long, Message> getLinkEffects() {
        return linkEffects;
    }

    public ConcurrentHashMap<Integer, MapArea> getAreas() {
        return areas;
    }

    public ConcurrentHashMap<Integer, NodeEnty2<Long, Object>> getMapthreadMap() {
        return mapthreadMap;
    }

    public MapThread nextMapThread() {
        for (Map.Entry<Integer, NodeEnty2<Long, Object>> entry : this.mapthreadMap.entrySet()) {
            NodeEnty2<Long, Object> nodeEnty2 = entry.getValue();
            MapArea tmpArea = nodeEnty2.getValue(1, MapArea.class);
            if (tmpArea.size() < 20) {

            }
        }
        return null;
    }

    public void addTask(MapObject mapObject, TaskEvent taskModel) {
        ThreadPool.addTask(mapObject.getMapThread(), taskModel);
    }

    public void addTimerTask(MapObject mapObject, TimerTaskEvent timerEvent) {
        ThreadPool.addTimerTask(mapObject.getMapThread(), timerEvent);
    }

    public void addTask(TaskEvent taskModel) {
        for (Map.Entry<Integer, NodeEnty2<Long, Object>> entry : this.mapthreadMap.entrySet()) {
            NodeEnty2<Long, Object> nodeEnty2 = entry.getValue();
            MapThread tmpThread = nodeEnty2.getValue0(MapThread.class);
            tmpThread.addTask(taskModel);
        }
    }

    public void addTimerTask(TimerTaskEvent timerEvent) {
        for (Map.Entry<Integer, NodeEnty2<Long, Object>> entry : this.mapthreadMap.entrySet()) {
            NodeEnty2<Long, Object> nodeEnty2 = entry.getValue();
            MapThread tmpThread = nodeEnty2.getValue0(MapThread.class);
            tmpThread.addTimerTask(timerEvent);
        }
    }

    /**
     * 返回周围的格子
     *
     * @param mapObject
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(MapObject mapObject) {
        return getRoundAreaIds(mapObject.getPosition());
    }

    /**
     * 返回周围的格子
     *
     * @param mapObject
     * @param r 同步 x 和y 方向 相同半径
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(MapObject mapObject, double r) {
        return getRoundAreaIds(mapObject.getPosition(), r);
    }

    /**
     * 返回周围的格子
     *
     * @param mapObject
     * @param rx 同步 x方向半径
     * @param ry 同步 y方向半径
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(MapObject mapObject, double rx, double ry) {
        return getRoundAreaIds(mapObject.getPosition(), rx, ry);
    }

    /**
     * 返回周围的格子
     *
     * @param position
     * @param r 同步 x 和y 方向 相同半径
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(Vector3 position, double r) {
        return getRoundAreaIds(position.getX(), position.getZ(), r, r);
    }

    /**
     * 返回周围的格子
     *
     * @param position
     * @param rx 同步 x方向半径
     * @param ry 同步 y方向半径
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(Vector3 position, double rx, double ry) {
        return getRoundAreaIds(position.getX(), position.getZ(), rx, ry);
    }

    /**
     * 返回周围的格子
     *
     * @param position
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(Vector3 position) {
        return getRoundAreaIds(position.getX(), position.getZ(), this.round_width, this.round_hieght);
    }

    /**
     * 返回周围的格子
     *
     * @param x
     * @param y
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(double x, double y) {
        return getRoundAreaIds(x, y, this.round_width, this.round_hieght);
    }

    /**
     * 返回周围的格子
     *
     * @param x
     * @param y
     * @param r 同步 x 和y 方向 相同半径
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(double x, double y, double r) {
        return getRoundAreaIds(x, y, r, r);
    }

    /**
     * 返回周围的格子
     *
     * @param x
     * @param y
     * @param rx 同步 x方向半径
     * @param ry 同步 y方向半径
     * @return
     */
    public ArrayList<Integer> getRoundAreaIds(double x, double y, double rx, double ry) {
        ArrayList<Integer> roundSeat = new ArrayList<>();

        /* 当前坐标点的所在的格子的信息 */
        int a1 = MoveUtil.seat(x, this.area_width);
        int a2 = MoveUtil.seat(y, this.area_height);

        /* 当前半径跨越的格子的信息 */
        int a3 = MoveUtil.seat(rx, this.area_width) + 1;
        int a4 = MoveUtil.seat(ry, this.area_height) + 1;

        int a1Max = 0;
        int a2Max = 0;

        int a1Min = 0;
        int a2Min = 0;

        if (a2 < a4) {//当前格子区域小于跨越格子区域，上边边缘
            a2Min = 0;
            a2Max = a4 * 2;
        } else if (this.area_Max_Height - a2 < a4) {//当前格子区域小于跨越格子区域，下边边缘
            a2Min = this.area_Max_Height - a4 * 2;
            a2Max = this.area_Max_Height;
        } else {
            a2Max = a2 + a4;
            a2Min = a2 - a4;
        }

        if (a1 < a3) {//当前格子区域小于跨越格子区域，上边边缘
            a1Min = 0;
            a1Max = a3 * 2;
        } else if (this.area_Max_Width - a1 < a3) {//当前格子区域小于跨越格子区域，下边边缘
            a1Min = this.area_Max_Width - a3 * 2;
            a1Max = this.area_Max_Width;
        } else {
            a1Max = a1 + a3;
            a1Min = a1 - a3;
        }
//        System.out.println("======================“" + AbsMapManager.getAreaId(a1, a2) + "”=========================");
        for (int i = a2Min; i <= a2Max; i++) {
            for (int j = a1Min; j <= a1Max; j++) {
//                System.out.print(j + "," + i + "(" + Move.position(j, this.area_width) + "," + Move.position(i, this.area_height) + ")" + "  ");
                roundSeat.add(AbsMapManager.getAreaId(j, i));
            }
//            System.out.println("");
        }
//        System.out.println("======================“" + AbsMapManager.getAreaId(a1, a2) + "”=========================");
        return roundSeat;
    }

    public ArrayList<MapArea> getRoundAreas(Vector3 position) {
        return getRoundAreas(position.getX(), position.getZ(), this.round_width, this.round_hieght);
    }

    public ArrayList<MapArea> getRoundAreas(Vector3 position, double r) {
        return getRoundAreas(position.getX(), position.getZ(), r, r);
    }

    public ArrayList<MapArea> getRoundAreas(Vector3 position, double rx, double ry) {
        return getRoundAreas(position.getX(), position.getZ(), rx, ry);
    }

    public ArrayList<MapArea> getRoundAreas(MapObject mapObject) {
        return getRoundAreas(mapObject.getPosition(), this.round_width, this.round_hieght);
    }

    public ArrayList<MapArea> getRoundAreas(MapObject mapObject, double r) {
        return getRoundAreas(mapObject.getPosition(), r, r);
    }

    public ArrayList<MapArea> getRoundAreas(MapObject mapObject, double rx, double ry) {
        return getRoundAreas(mapObject.getPosition(), rx, ry);
    }

    public ArrayList<MapArea> getRoundAreas(double x, double y) {
        return getRoundAreas(x, y, this.round_width, this.round_hieght);
    }

    /**
     * 获取坐标点周围格子
     *
     * @param x
     * @param y
     * @param r
     * @return
     */
    public ArrayList<MapArea> getRoundAreas(double x, double y, double r) {
        return getRoundAreas(x, y, r, r);
    }

    /**
     * 获取坐标点周围格子
     *
     * @param x
     * @param y
     * @param rx
     * @param ry
     * @return
     */
    public ArrayList<MapArea> getRoundAreas(double x, double y, double rx, double ry) {
        ArrayList<Integer> roundSeats = getRoundAreaIds(x, y, rx, ry);
        ArrayList<MapArea> tmpAreas = new ArrayList<>();
        for (Integer seat : roundSeats) {
            MapArea get = getArea(seat);
            if (get != null) {
                tmpAreas.add(get);
            }
        }
        return tmpAreas;
    }

    public ArrayList<ArrayList<MapArea>> chaji(int oldAreaId, int newAreaId) {
        int w1 = (int) oldAreaId / 1000;
        int h1 = (int) (oldAreaId) % 1000;

        int w2 = (int) newAreaId / 1000;
        int h2 = (int) (newAreaId) % 1000;

        return chaji(
                MoveUtil.position(w1, area_width),
                MoveUtil.position(h1, area_height),
                MoveUtil.position(w2, area_width),
                MoveUtil.position(h2, area_height), round_width, round_hieght);

    }

    /**
     * 获取两个坐标点的差集
     *
     * @param position
     * @param position1
     * @param rx
     * @param ry
     * @return
     */
    public ArrayList<ArrayList<MapArea>> chaji(Vector3 position, Vector3 position1, double rx, double ry) {
        return chaji(position.getX(), position.getZ(), position1.getX(), position1.getZ(), rx, ry);
    }

    /**
     * 获取两个坐标点的差集
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param rx
     * @param ry
     * @return
     */
    public ArrayList<ArrayList<MapArea>> chaji(double x, double y, double x1, double y1, double rx, double ry) {

        ArrayList<Integer> oldRound = getRoundAreaIds(x, y, rx, ry);
        Iterator<Integer> iterator = oldRound.iterator();
        ArrayList<Integer> newRound = getRoundAreaIds(x1, y1, rx, ry);
        ArrayList<ArrayList<MapArea>> tmpAreas = new ArrayList<>();
        while (iterator.hasNext()) {
            Integer areaId = iterator.next();
            if (newRound.contains(areaId)) {
                iterator.remove();
                newRound.remove(areaId);
            }
        }

        ArrayList<MapArea> area1 = new ArrayList<>();
        for (Integer roundSeat : oldRound) {
            MapArea get = getArea(roundSeat);
            if (get != null) {
                area1.add(get);
            }
        }

        tmpAreas.add(area1);

        area1 = new ArrayList<>();
        for (Integer roundSeat : newRound) {
            MapArea get = getArea(roundSeat);
            if (get != null) {
                area1.add(get);
            }
        }

        tmpAreas.add(area1);

        return tmpAreas;
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param areaId
     * @return
     */
    public MapArea getArea(int areaId) {
        return this.areas.get(areaId);
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param mapObject
     * @return
     */
    public MapArea getArea(MapObject mapObject) {
        int areaId = AbsMapManager.getAreaId(this, mapObject.getPosition());
        return getArea(areaId);
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param position
     * @return
     */
    public MapArea getArea(Vector3 position) {
        int areaId = AbsMapManager.getAreaId(this, position.getX(), position.getZ());
        return getArea(areaId);
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param x
     * @param z
     * @return
     */
    public MapArea getArea(double x, double z) {
        int areaId = AbsMapManager.getAreaId(this, x, z);
        return getArea(areaId);
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param mapObject
     * @return
     */
    public MapArea getAreaOrIn(MapObject mapObject) {
        return getAreaOrIn(mapObject.getPosition());
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param position
     * @return
     */
    public MapArea getAreaOrIn(Vector3 position) {
        int areaId = AbsMapManager.getAreaId(this, position.getX(), position.getZ());
        return getAreaOrIn(areaId);
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param x
     * @param z
     * @return
     */
    public MapArea getAreaOrIn(float x, float z) {
        int areaId = AbsMapManager.getAreaId(this, x, z);
        return getAreaOrIn(areaId);
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param areaId
     * @return
     */
    public MapArea getAreaOrIn(int areaId) {
        MapArea mapArea = null;
        mapArea = getArea(areaId);
        if (mapArea == null) {
            int w = (int) areaId / 1000;
            int h = (int) (areaId) % 1000;
            /* 区域信息 */
            mapArea = new MapArea(areaId, w, h, this);
            this.areas.put(areaId, mapArea);
        }
        return mapArea;
    }

    public ArrayList<MapObject> getRoundMapObjects(MapObject mapObject, long spiritType, int modelId) {
        return getRoundMapObjects(mapObject.getPosition(), spiritType, modelId);
    }

    public ArrayList<MapObject> getRoundMapObjects(Vector3 position, long spiritType, int modelId) {
        return getRoundMapObjects(position.getX(), position.getZ(), round_width, round_hieght, spiritType, modelId);
    }

    public ArrayList<MapObject> getRoundMapObjects(MapObject mapObject, double r, long spiritType, int modelId) {
        return getRoundMapObjects(mapObject.getPosition(), r, spiritType, modelId);
    }

    public ArrayList<MapObject> getRoundMapObjects(Vector3 position, double r, long spiritType, int modelId) {
        return getRoundMapObjects(position.getX(), position.getZ(), r, spiritType, modelId);
    }

    public ArrayList<MapObject> getRoundMapObjects(double x, double y, double r, long spiritType, int modelId) {
        return getRoundMapObjects(x, y, r, r, spiritType, modelId);
    }

    /**
     *
     * @param x
     * @param y
     * @param rx
     * @param ry
     * @param spiritType
     * @param modelId
     * @return
     */
    public ArrayList<MapObject> getRoundMapObjects(double x, double y, double rx, double ry, long spiritType, int modelId) {
        ArrayList<MapObject> tmps = new ArrayList<>();
        ArrayList<MapArea> roundAreas = getRoundAreas(x, y, rx, ry);
        for (MapArea roundArea : roundAreas) {

            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.DROP.getValue())) {
                for (Iterator<Long> iterator1 = roundArea.getDropGoodss().iterator(); iterator1.hasNext();) {
                    Long next = iterator1.next();
                    MapObject mapObject = this.getDropGoodss().get(next);
                    if (mapObject != null) {
                        if (modelId == 0 || mapObject.getModelId() == modelId) {
                            tmps.add(mapObject);
                        }
                    }
                }
            }

            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.LINKEFFECT.getValue())) {
//                    for (Iterator<Long> iterator1 = roundArea.getLinkEffects().iterator(); iterator1.hasNext();) {
//                        Long next = iterator1.next();
//                        Message mapObject = this.getLinkEffects().get(next);
//                        if (mapObject != null) {
//                            tmps.add(mapObject);
//                        }
//                    }
            }

            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.MAGIC.getValue())) {
                for (Iterator<Long> iterator1 = roundArea.getMagics().iterator(); iterator1.hasNext();) {
                    Long next = iterator1.next();
                    MapObject mapObject = this.getMagics().get(next);
                    if (mapObject != null) {
                        if (modelId == 0 || mapObject.getModelId() == modelId) {
                            tmps.add(mapObject);
                        }
                    }
                }
            }
            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.EFFECT.getValue())) {
                for (Iterator<Long> iterator1 = roundArea.getEffects().iterator(); iterator1.hasNext();) {
                    Long next = iterator1.next();
                    MapObject mapObject = this.getEffects().get(next);
                    if (mapObject != null) {
                        if (modelId == 0 || mapObject.getModelId() == modelId) {
                            tmps.add(mapObject);
                        }
                    }
                }
            }
            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.MONSTER.getValue())) {
                for (Iterator<Long> iterator1 = roundArea.getMonsters().iterator(); iterator1.hasNext();) {
                    Long next = iterator1.next();
                    MapObject mapObject = this.getMonsters().get(next);
                    if (mapObject != null) {
                        if (modelId == 0 || mapObject.getModelId() == modelId) {
                            tmps.add(mapObject);
                        }
                    }
                }
            }

//            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.NONE.getValue())) {
//            }
            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.NPC.getValue())) {
                for (Iterator<Long> iterator1 = roundArea.getNpcs().iterator(); iterator1.hasNext();) {
                    Long next = iterator1.next();
                    MapObject mapObject = this.getNpcs().get(next);
                    if (mapObject != null) {
                        if (modelId == 0 || mapObject.getModelId() == modelId) {
                            tmps.add(mapObject);
                        }
                    }
                }
            }
            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.PLAYER.getValue())) {
                for (Iterator<Long> iterator1 = roundArea.getPlayers().iterator(); iterator1.hasNext();) {
                    Long next = iterator1.next();
                    MapObject mapObject = this.getPlayers().get(next);
                    if (mapObject != null) {
                        tmps.add(mapObject);
                    }
                }
            }
            if (BitUtil.hasFlagBitLong(spiritType, MapObject.SpiritType.PET.getValue())) {
                for (Iterator<Long> iterator1 = roundArea.getPets().iterator(); iterator1.hasNext();) {
                    Long next = iterator1.next();
                    MapObject mapObject = this.getPets().get(next);
                    if (mapObject != null) {
                        tmps.add(mapObject);
                    }
                }
            }
        }
        return tmps;
    }

    /**
     * 该地图是否有玩家
     *
     * @return
     */
    public boolean isEmpty() {
        return this.getPlayers().isEmpty();
    }

}
