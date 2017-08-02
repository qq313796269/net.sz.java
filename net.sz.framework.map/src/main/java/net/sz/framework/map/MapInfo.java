package net.sz.framework.map;

import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.map.manager.AbsMapManager;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.map.thread.MapThreadExcutor;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.TaskModel;
import net.sz.framework.szthread.TimerTaskModel;
import net.sz.framework.utils.BitUtil;
import net.sz.framework.utils.MoveUtil;
import net.sz.framework.utils.RandomUtils;
import net.sz.framework.way.navmesh.Vector3;
import net.sz.framework.way.navmesh.path.NavMap;

/**
 * 地图信息
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapInfo extends MapObject {

    private static final long serialVersionUID = -2839255230502612896L;
    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        MapInfo mapinfo = new MapInfo("111111", 1, 2, 2, false, 140, 140, MapType.ZONEMAP);

        round(mapinfo, 36.95f, 51.83f);

        round(mapinfo, 20.95f, 55.65f);

        System.exit(0);
    }

    @Deprecated
    static void round(MapInfo mapinfo, float x1, float z1) {
        mapinfo.getRoundAreaIds(x1, z1);
    }

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
    /* 因为单线程处理不考虑线程安全性 */
    //玩家列表
    private final ConcurrentHashMap<Long, Person> players = new ConcurrentHashMap<>();
    //npc列表
    private final ConcurrentHashMap<Long, Person> npcs = new ConcurrentHashMap<>();
    //宠物列表
    private final ConcurrentHashMap<Long, Person> pets = new ConcurrentHashMap<>();
    //怪物列表
    private final ConcurrentHashMap<Long, Person> monsters = new ConcurrentHashMap<>();
    //等待复活怪物列表
    private final ConcurrentHashMap<Long, Person> revives = new ConcurrentHashMap<>();
    /* 地面魔法 */
    private final ConcurrentHashMap<Long, MapObject> magics = new ConcurrentHashMap<>();
    /* 场景特效 */
    private final ConcurrentHashMap<Long, MapObject> effects = new ConcurrentHashMap<>();
    /* 场景掉落物 */
    private final ConcurrentHashMap<Long, MapObject> dropGoodss = new ConcurrentHashMap<>();
    /* 链接特效列表 */
    private final ConcurrentHashMap<Long, Message> linkEffects = new ConcurrentHashMap<>();
    /* 场景所有区域信息 */
    private final ConcurrentHashMap<Integer, MapArea> areas = new ConcurrentHashMap<>();
    /* 场景对应小地图刷怪信息 */
    private final HashSet<Message> mapAllQsceneMessage = new HashSet<>();
    /* 场景怪物NPC刷怪ID*/
    private final HashSet<Integer> mapSceneId = new HashSet<>();

    private int playerNumber;

    private boolean mapAllShow;

    private int zoneOrBattleModelId;

    /*死亡后的复活点*/
    private ArrayList<Float[]> diePoss = new ArrayList<>();

    /**
     * 朝向
     */
    private double vector_die = -1;

    /**
     *
     * @param mapKey
     * @param mapId
     * @param round_width
     * @param round_hieght
     * @param mapAllShow
     * @param map_width
     * @param map_height
     * @param mapType
     */
    public MapInfo(String mapKey, long mapId, int round_width, int round_hieght, boolean mapAllShow, int map_width, int map_height, MapType mapType) {
        super(mapId);

        super.setMapId(mapId);
        super.setMapKey(mapKey);

        if (mapAllShow) {
            this.setArea_width(map_width);
            this.setArea_height(map_height);
        } else {
            this.setArea_width(MoveUtil.Const_Area_Width);
            this.setArea_height(MoveUtil.Const_Area_Height);
        }

        this.setRound_width(round_width);
        this.setRound_hieght(round_hieght);

        this.round_width *= this.area_width;
        this.round_hieght *= this.area_height;

        this.map_width = map_width;
        this.map_height = map_height;
        this.mapType = mapType;
        this.area_Max_Width = MoveUtil.seat(this.map_width, this.area_width);
        this.area_Max_Height = MoveUtil.seat(this.map_height, this.area_height);

    }

    @Deprecated
    @Override
    public void setMapKey(String mapKey) {
        throw new UnsupportedOperationException("can not function");
    }

    @Deprecated
    @Override
    public long getId() {
        throw new UnsupportedOperationException("can not function");
    }

    @Deprecated
    @Override
    public void setId(long id) {
        throw new UnsupportedOperationException("can not function");
    }

    @Deprecated
    @Override
    public int getModelId() {
        throw new UnsupportedOperationException("can not function");
    }

    @Deprecated
    @Override
    public void setModelId(int modelId) {
        throw new UnsupportedOperationException("can not function");
    }

    @Deprecated
    @Override
    public Vector3 getPosition() {
        throw new UnsupportedOperationException("can not function");
    }

    @Deprecated
    @Override
    public void setPosition(Vector3 position) {
        throw new UnsupportedOperationException("can not function");
    }

    /**
     * 是否已经刷新
     *
     * @return
     */
    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * 最后一次玩家退出地图时间
     *
     * @return
     */
    public long getPlayerQuitMapTime() {
        return playerQuitMapTime;
    }

    public void setPlayerQuitMapTime(long playerQuitMapTime) {
        this.playerQuitMapTime = playerQuitMapTime;
    }

    /**
     * 战场或者副本模板id
     *
     * @return
     */
    public int getZoneOrBattleModelId() {
        return zoneOrBattleModelId;
    }

    public void setZoneOrBattleModelId(int zoneOrBattleModelId) {
        this.zoneOrBattleModelId = zoneOrBattleModelId;
    }

    /**
     * 是否是全地图显示
     *
     * @return
     */
    public boolean isMapAllShow() {
        return mapAllShow;
    }

    public void setMapAllShow(boolean mapAllShow) {
        this.mapAllShow = mapAllShow;
    }

    /**
     * 地图宽度
     *
     * @return
     */
    public final float getMap_width() {
        return map_width;
    }

    public final void setMap_width(float map_width) {
        this.map_width = map_width;
    }

    public final float getMap_height() {
        return map_height;
    }

    public final void setMap_height(float map_height) {
        this.map_height = map_height;
    }

    /**
     * 玩家数量
     *
     * @return
     */
    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    /**
     * 同步周围宽度
     *
     * @return
     */
    public final float getRound_width() {
        return round_width;
    }

    public final void setRound_width(float round_width) {
        if (this.round_width < MoveUtil.Const_Round_Width) {
            this.round_width = MoveUtil.Const_Round_Width;
        }

        if (this.round_width > MoveUtil.Const_Round_Width_Max) {
            this.round_width = MoveUtil.Const_Round_Width_Max;
        }
        this.round_width = round_width;
    }

    /**
     * 同步周围高度
     *
     * @return
     */
    public final float getRound_hieght() {
        return round_hieght;
    }

    public final void setRound_hieght(float round_hieght) {
        if (this.round_hieght < MoveUtil.Const_Round_Height) {
            this.round_hieght = MoveUtil.Const_Round_Height;
        }
        if (this.round_hieght > MoveUtil.Const_Round_Height_Max) {
            this.round_hieght = MoveUtil.Const_Round_Height_Max;
        }
        this.round_hieght = round_hieght;
    }

    /**
     * 一个同步格子大小
     *
     * @return
     */
    public final float getArea_width() {
        return area_width;
    }

    public final void setArea_width(float area_width) {
        if (this.area_width < MoveUtil.Const_Area_Width) {
            this.area_width = MoveUtil.Const_Area_Width;
        }
        this.area_width = area_width;
    }

    /**
     * 一个同步格子大小
     *
     * @return
     */
    public final float getArea_height() {
        if (this.area_height < MoveUtil.Const_Area_Height) {
            this.area_height = MoveUtil.Const_Area_Height;
        }
        return area_height;
    }

    public final void setArea_height(float area_height) {
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

    /**
     * 地图寻路层
     *
     * @return
     */
    public NavMap getNavMap() {
        return navMap;
    }

    public void setNavMap(NavMap navMap) {
        this.navMap = navMap;
    }

    /**
     * 阻挡门数量
     *
     * @return
     */
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

    public ConcurrentHashMap<Long, Person> getPlayers() {
        return players;
    }

    public ConcurrentHashMap<Long, Person> getNpcs() {
        return npcs;
    }

    public ConcurrentHashMap<Long, Person> getPets() {
        return pets;
    }

    public ConcurrentHashMap<Long, Person> getMonsters() {
        return monsters;
    }

    public ConcurrentHashMap<Long, Person> getRevives() {
        return revives;
    }

    public HashSet<Integer> getMapSceneId() {
        return mapSceneId;
    }

    public ConcurrentHashMap<Long, MapObject> getMagics() {
        return magics;
    }

    public ConcurrentHashMap<Long, Message> getLinkEffects() {
        return linkEffects;
    }

    /**
     * 场景对应小地图刷怪信息
     *
     * @return
     */
    public HashSet<Message> getMapAllQsceneMessage() {
        return mapAllQsceneMessage;
    }

    public ConcurrentHashMap<Integer, MapArea> getAreas() {
        return areas;
    }

    public ArrayList<Float[]> getDiePoss() {
        return diePoss;
    }

    public void setDiePoss(ArrayList<Float[]> diePoss) {
        this.diePoss = diePoss;
    }

    /**
     * 随机死亡复活点
     *
     * @param index
     * @return
     */
    public Float[] getDiePos(int index) {
        Float[] pos;
        if (index < 0 || index >= this.getDiePoss().size()) {
            pos = this.getDiePoss().get(RandomUtils.random(this.getDiePoss().size()));
        } else {
            pos = this.getDiePoss().get(index);
        }
        return getDiePos(pos);
    }

    /**
     * 获取最近的复活点
     *
     * @param mapObject
     * @return
     */
    public Float[] getDiePos(MapObject mapObject) {
        return getDiePos(mapObject.getPosition().getX(), mapObject.getPosition().getZ());
    }

    /**
     * 获取最近的复活点
     *
     * @param x
     * @param z
     * @return
     */
    public Float[] getDiePos(double x, double z) {
        Float[] pos = null;
        double dis = 0;
        for (int i = 0; i < this.getDiePoss().size(); i++) {
            Float[] nodeEnty1 = this.getDiePoss().get(i);
            double distance = MoveUtil.distance(x, z, nodeEnty1[0], nodeEnty1[1]);
            if (distance > dis) {
                pos = nodeEnty1;
                dis = distance;
            }
        }
        return getDiePos(pos);
    }

    /**
     * 根据传入的复活点随机
     *
     * @param pos
     * @return
     */
    public Float[] getDiePos(Float[] pos) {
        if (pos != null) {
            if (this.mapType.getGroup() == MapType.WORLDMAP.getGroup()) {
                Vector3 roundPosition = AbsMapManager.getRoundPosition(this, pos[0], pos[1], 2);
                double aTan360 = MoveUtil.getATan360(roundPosition.getX(), roundPosition.getZ(), pos[2], pos[3]);
                if (log.isDebugEnabled()) {
                    log.debug("获取死亡坐标朝向随机：" + aTan360 + " " + roundPosition.getX() + " " + roundPosition.getZ() + " " + pos[2] + " " + pos[3]);
                }
                return new Float[]{roundPosition.xF(), roundPosition.zF(), (float) aTan360};
            } else {
                double aTan360 = MoveUtil.getATan360(pos[0], pos[1], pos[2], pos[3]);
                if (log.isDebugEnabled()) {
                    log.debug("获取死亡坐标朝向固定：" + aTan360 + " " + pos[0] + " " + pos[1] + " " + pos[2] + " " + pos[3]);
                }
                return new Float[]{pos[0], pos[1], (float) aTan360};
            }
        }
        return null;
    }

    public double getVector_die() {
        return vector_die;
    }

    public void setVector_die(double vector_die) {
        this.vector_die = vector_die;
    }

    /**
     * 加入到场景对象中而不是线程中
     *
     * @param taskModel
     * @return
     */
    public TaskModel addTask(TaskModel taskModel) {
        return (TaskModel) MapThreadExcutor.MapAllExcutor.addTask(getMapKey(), taskModel);
    }

    /**
     * 加入到场景对象中而不是线程中
     *
     * @param timerEvent
     * @return
     */
    public TimerTaskModel addTimerTask(TimerTaskModel timerEvent) {
        return (TimerTaskModel) MapThreadExcutor.MapAllExcutor.addTimerTask(getMapKey(), timerEvent);
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

        a2Min = a2 - a4;
        a2Max = a2 + a4;

        if (a2Min < 0) {
            a2Min = 0;
        }
        if (a2Max > this.area_Max_Height) {
            a2Max = this.area_Max_Height;
        }

        a1Min = a1 - a3;
        a1Max = a1 + a3;

        if (a1Min < 0) {
            a1Min = 0;
        }
        if (a1Max > this.area_Max_Width) {
            a1Max = this.area_Max_Width;
        }
        if (a2 < a2Min || a2 > a2Max || a1 < a1Min || a1 > a1Max) {
            log.error("地图大小，配置有误刷怪点都在地图外了@策划@策划@策划@策划@策划@策划" + getMapKey() + " " + AbsMapManager.getAreaId(a1, a2), new Exception());
        }
//        log.console("======================“" + AbsMapManager.getAreaId(a1, a2) + "”=========================\n");
        for (int i = a2Min; i <= a2Max; i++) {
            for (int j = a1Min; j <= a1Max; j++) {
//                log.console(j + "," + i + "(" + MoveUtil.position(j, this.area_width) + "," + MoveUtil.position(i, this.area_height) + ")" + "  ");
                roundSeat.add(AbsMapManager.getAreaId(j, i));
            }
//            log.console("\n");
        }
//        log.console("======================“" + AbsMapManager.getAreaId(a1, a2) + "”=========================\n");
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
                MoveUtil.position(h2, area_height),
                round_width, round_hieght);

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

    /**
     * 返回结果已经做了就近原则排序
     *
     * @param mapObject
     * @param spiritType
     * @param mr 模型半径
     * @param modelId 指定模型id
     * @return
     */
    public ArrayList<MapObject> getRoundMapObjects(MapObject mapObject, long spiritType, float mr, int modelId) {
        return getRoundMapObjects(mapObject.getId(), mapObject.getPosition(), spiritType, mr, modelId);
    }

    /**
     * 返回结果已经做了就近原则排序
     *
     * @param selfId
     * @param position
     * @param spiritType
     * @param mr 模型半径
     * @param modelId 指定模型id
     * @return
     */
    public ArrayList<MapObject> getRoundMapObjects(long selfId, Vector3 position, long spiritType, float mr, int modelId) {
        return getRoundMapObjects(selfId, position.getX(), position.getZ(), -1, -1, spiritType, mr, modelId);
    }

    /**
     * 返回结果已经做了就近原则排序
     *
     * @param mapObject
     * @param r
     * @param spiritType
     * @param mr 模型半径
     * @param modelId 指定模型id
     * @return
     */
    public ArrayList<MapObject> getRoundMapObjects(MapObject mapObject, double r, long spiritType, float mr, int modelId) {
        return getRoundMapObjects(mapObject.getId(), mapObject.getPosition(), r, spiritType, mr, modelId);
    }

    /**
     * 返回结果已经做了就近原则排序
     *
     * @param selfId
     * @param position
     * @param r
     * @param spiritType
     * @param mr 模型半径
     * @param modelId 指定模型id
     * @return
     */
    public ArrayList<MapObject> getRoundMapObjects(long selfId, Vector3 position, double r, long spiritType, float mr, int modelId) {
        return getRoundMapObjects(selfId, position.getX(), position.getZ(), r, spiritType, mr, modelId);
    }

    /**
     * 返回结果已经做了就近原则排序
     *
     * @param x
     * @param y
     * @param r
     * @param spiritType
     * @param mr 模型半径
     * @param modelId 指定模型id
     * @return
     */
    public ArrayList<MapObject> getRoundMapObjects(long selfId, double x, double y, double r, long spiritType, float mr, int modelId) {
        return getRoundMapObjects(selfId, x, y, r, r, spiritType, mr, modelId);
    }

    /**
     * 返回结果已经做了就近原则排序
     *
     * @param selfId 自己id，如果需要过滤的
     * @param x
     * @param y
     * @param rx
     * @param ry
     * @param spiritType
     * @param mr 模型半径
     * @param modelId 指定模型id
     * @return
     */
    public ArrayList<MapObject> getRoundMapObjects(long selfId, double x, double y, double rx, double ry, long spiritType, float mr, int modelId) {
        ArrayList<MapObject> tmps = new ArrayList<>();

        ArrayList<MapArea> roundAreas;
        if (rx == -1 || ry == -1) {
            roundAreas = getRoundAreas(x, y, this.round_width, this.round_hieght);
        } else {
            roundAreas = getRoundAreas(x, y, rx, ry);
        }

        for (MapArea roundArea : roundAreas) {
            if (BitUtil.hasFlagBitLong(spiritType, MapObjectType.DROP.getValue())) {
                Long[] toArray = roundArea.getDropGoodss().toArray(new Long[0]);
                for (int i = 0; i < toArray.length; i++) {
                    Long next = toArray[i];
                    MapObject mapObject = this.getDropGoodss().get(next);
                    if (checkDis(mapObject, selfId, modelId, x, y, rx, ry, mr)) {
                        tmps.add(mapObject);
                    }
                }
            }

            if (BitUtil.hasFlagBitLong(spiritType, MapObjectType.LINKEFFECT.getValue())) {
//                  for (Iterator<Long> iterator1 = roundArea.getLinkEffects().iterator(); iterator1.hasNext();) {
//                    Long next = iterator1.next();
//                    Message mapObject = this.getLinkEffects().get(next);
//                    if (mapObject != null) {
//                        tmps.add(mapObject);
//                    }
//                }
            }

            if (BitUtil.hasFlagBitLong(spiritType, MapObjectType.MAGIC.getValue())) {
                Long[] toArray = roundArea.getMagics().toArray(new Long[0]);
                for (int i = 0; i < toArray.length; i++) {
                    Long next = toArray[i];
                    MapObject mapObject = this.getMagics().get(next);
                    if (checkDis(mapObject, selfId, modelId, x, y, rx, ry, mr)) {
                        tmps.add(mapObject);
                    }
                }
            }

            if (BitUtil.hasFlagBitLong(spiritType, MapObjectType.EFFECT.getValue())) {
                Long[] toArray = roundArea.getEffects().toArray(new Long[0]);
                for (int i = 0; i < toArray.length; i++) {
                    Long next = toArray[i];
                    MapObject mapObject = this.getEffects().get(next);
                    if (checkDis(mapObject, selfId, modelId, x, y, rx, ry, mr)) {
                        tmps.add(mapObject);
                    }
                }
            }
            {
                Long[] toArray = roundArea.getMonsters().toArray(new Long[0]);
                for (int i = 0; i < toArray.length; i++) {
                    Long next = toArray[i];
                    MapObject mapObject = this.getMonsters().get(next);
                    if (BitUtil.hasFlagBitLong(spiritType, mapObject.getMapObjectType().getValue())) {
                        if (checkDis(mapObject, selfId, modelId, x, y, rx, ry, mr)) {
                            tmps.add(mapObject);
                        }
                    }
                }
            }

            if (BitUtil.hasFlagBitLong(spiritType, MapObjectType.NPC.getValue())) {
                Long[] toArray = roundArea.getNpcs().toArray(new Long[0]);
                for (int i = 0; i < toArray.length; i++) {
                    Long next = toArray[i];
                    MapObject mapObject = this.getNpcs().get(next);
                    if (checkDis(mapObject, selfId, modelId, x, y, rx, ry, mr)) {
                        tmps.add(mapObject);
                    }
                }
            }

            /*包含玩家和机器人*/
            if (BitUtil.hasFlagBitLong(spiritType, MapObjectType.PLAYER.getValue())
                    || BitUtil.hasFlagBitLong(spiritType, MapObjectType.PLAYER_ROBOT.getValue())) {
                Long[] toArray = roundArea.getPlayers().toArray(new Long[0]);
                for (int i = 0; i < toArray.length; i++) {
                    Long next = toArray[i];
                    MapObject mapObject = this.getPlayers().get(next);
                    if (checkDis(mapObject, selfId, modelId, x, y, rx, ry, mr)) {
                        tmps.add(mapObject);
                    }
                }
            }

            if (BitUtil.hasFlagBitLong(spiritType, MapObjectType.PET.getValue())) {
                Long[] toArray = roundArea.getPets().toArray(new Long[0]);
                for (int i = 0; i < toArray.length; i++) {
                    Long next = toArray[i];
                    MapObject mapObject = this.getPets().get(next);
                    if (checkDis(mapObject, selfId, modelId, x, y, rx, ry, mr)) {
                        tmps.add(mapObject);
                    }
                }
            }
        }
        if (tmps.size() > 2) {
            MapObject[] mds = tmps.toArray(new MapObject[0]);

            //先冒泡排序 ->按照从远到近
            for (int i = 1; i < mds.length; i++) {
                for (int j = 0; j < mds.length - i; j++) {
                    MapObject d1 = mds[j];
                    MapObject d2 = mds[j + 1];
                    if (d1.distance(x, y, 0) > d2.distance(x, y, 0)) {
                        mds[j] = d2;
                        mds[j + 1] = d1;
                    }
                }
            }

            tmps.clear();

            for (MapObject md : mds) {
                tmps.add(md);
            }
        }
        return tmps;
    }

    /**
     *
     * @param mapObject
     * @param x
     * @param y
     * @param rx
     * @param ry
     * @param mr 模型半径
     * @return
     */
    boolean checkDis(MapObject mapObject, long selfId, int modelId, double x, double y, double rx, double ry, float mr) {
        if (mapObject == null) {
            return false;
        }
        if (modelId != 0 && mapObject.getModelId() != modelId) {
            return false;
        }
        if (selfId == mapObject.getId()) {
            return false;
        }
        if (rx == -1 || ry == -1) {
            return true;
        }
        double dis = mapObject.distance(x, y, mr);
        return dis <= rx || dis <= ry;
    }

    /**
     * 该地图是否有玩家
     *
     * @return
     */
    public boolean isEmpty() {
        return this.getPlayers().isEmpty();
    }

    @Override
    public String toString() {
        return super.toString() + ", mapType" + mapType.getMsg();
    }

    @Override
    public String showString() {
        return super.toString() + ",map_width=" + map_width + ", map_height=" + map_height + ", mapType=" + mapType.getMsg() + ", doorsize=" + doorsize + ", playerQuitMapTime=" + playerQuitMapTime + ", players=" + players.size() + ", npcs=" + npcs.size() + ", pets=" + pets.size() + ", monsters=" + monsters.size() + ", revives=" + revives.size() + ", magics=" + magics.size() + ", effects=" + effects.size() + ", linkEffects=" + linkEffects.size() + ", dropGoodss=" + dropGoodss.size() + ", zoneOrBattleModelId=" + zoneOrBattleModelId;
    }

}
