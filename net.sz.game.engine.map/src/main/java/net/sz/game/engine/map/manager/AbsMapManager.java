package net.sz.game.engine.map.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.util.NodeEnty2;
import net.sz.game.engine.map.MapArea;
import net.sz.game.engine.map.MapInfo;
import net.sz.game.engine.map.MapObject;
import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.map.thread.MapThread;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.thread.TaskModel;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.TimerTaskModel;
import net.sz.game.engine.utils.MoveUtil;
import net.sz.game.engine.utils.RandomUtils;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AbsMapManager {

    private static SzLogger log = SzLogger.getLogger();

    protected static final ConcurrentHashMap<String, MapInfo> MapInfo_Map = new ConcurrentHashMap<>();
    /**
     * 只会记录副本和战场
     */
    protected static final ConcurrentHashMap<Long, MapInfo> ZoneMapInfo_Map = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<Long, MapThread> MAPTHREAD_MAP = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log.error(getMapSkey(0, 0, 0));
    }

    /**
     * 所有地图信息
     *
     * @return
     */
    public static ConcurrentHashMap<String, MapInfo> getMapInfo_Map() {
        return MapInfo_Map;
    }

    /**
     * 只会记录副本和战场
     *
     * @return
     */
    public static ConcurrentHashMap<Long, MapInfo> getZoneMapInfo_Map() {
        return ZoneMapInfo_Map;
    }

    /**
     * 所有线程
     *
     * @return
     */
    public static ConcurrentHashMap<Long, MapThread> getMAPTHREAD_MAP() {
        return MAPTHREAD_MAP;
    }

    /**
     * 获取一个线程
     *
     * @return
     */
    public static MapThread getNextMAPTHREAD(int size) {
        HashMap<Long, MapThread> tmpMap = new HashMap<>(MAPTHREAD_MAP);
        MapThread mapThread = null;
        for (Map.Entry<Long, MapThread> entry : tmpMap.entrySet()) {
            if (entry.getValue().personSize() < size && (mapThread == null || mapThread.personSize() > entry.getValue().personSize())) {
                mapThread = entry.getValue();
            }
//            if (value.getMapType().getValue2() == mapInfo.getMapType().getValue2()) {
//                /**
//                 * 设置共享线程，剧情动画副本，一个线程控制200个地图
//                 */
//                if (mapInfo.getMapType() == MapInfo.MapType.PLOTMAP) {
//                    if (value.mapSize() < 200) {
//                        return value;
//                    }
//                } else {
//                    /**
//                     * 如果副本或者战场，一个线程控制1000个对象
//                     */
//                    if (value.mapPersonSize() < 1000) {
//                        return value;
//                    }
//                }
//            }
        }
        return mapThread;
    }

    /**
     * 获取地图值
     *
     * @param mapId
     * @param mapModelId
     * @param lineId
     * @return
     */
    public static String getMapSkey(long mapId, int mapModelId, int lineId) {
        return mapId + "_" + mapModelId + "_" + lineId;
    }

    /**
     * 获取地图值
     *
     * @param mapObject
     * @return
     */
    public static String getMapSkey(MapObject mapObject) {
        return getMapSkey(mapObject.getMapId(), mapObject.getMapModelId(), mapObject.getLineId());
    }

    public static MapInfo getMapInfo(MapObject mapObject) {
        String mapSkey = getMapSkey(mapObject);
        return AbsMapManager.getMapInfo(mapSkey);
    }

    public static MapInfo getMapInfo(long mapId, int mapModelId, int lineId) {
        String mapSkey = getMapSkey(mapId, mapModelId, lineId);
        return AbsMapManager.getMapInfo(mapSkey);
    }

    /**
     *
     * @param mapId
     * @return
     */
    public static MapInfo removeMap(long mapId) {
        MapInfo get = ZoneMapInfo_Map.get(mapId);
        removeMap(get);
        return get;
    }

    /**
     *
     * @param mapKey
     * @return
     */
    public static MapInfo removeMap(String mapKey) {
        MapInfo get = MapInfo_Map.get(mapKey);
        removeMap(get);
        return get;
    }

    /**
     * 销毁副本
     *
     * @param mapInfo
     */
    public static void removeMap(MapInfo mapInfo) {
//        MapThread get = AbsMapManager.getMAPTHREAD_MAP().get(mapInfo.getMapThreadId());
//        if (get.isMapEmpty()) {
//            ThreadPool.remove(mapInfo.getMapThreadId());
//            AbsMapManager.getMAPTHREAD_MAP().remove(mapInfo.getMapThreadId());
//        }
        log.error("销毁 战场 或者 副本，当前地图数量：" + AbsMapManager.getMapInfo_Map().size() + "，当前地图线程总数量：" + AbsMapManager.getMAPTHREAD_MAP().size());
        ZoneMapInfo_Map.remove(mapInfo.getMapId());
        MapInfo_Map.remove(mapInfo.getMapKey());
    }

    /**
     * 只会记录副本和战场
     *
     * @param mapId
     * @return
     */
    @Deprecated
    public static MapInfo getMapInfo(long mapId) {
        if (mapId == 0) {
            return null;
        }
        return ZoneMapInfo_Map.get(mapId);
    }

    /**
     *
     * @param mapSkey
     * @return
     */
    public static MapInfo getMapInfo(String mapSkey) {
        return MapInfo_Map.get(mapSkey);
    }

    /**
     * 获取坐标点在地图格子区域
     *
     * @param mapObject
     * @return
     */
    public static int getAreaId(MapObject mapObject) {
        MapInfo map = AbsMapManager.getMapInfo(mapObject);
        return getAreaId(map, mapObject);
    }

    /**
     * 获取坐标点在地图格子区域
     *
     * @param mapInfo
     * @param mapObject
     * @return
     */
    public static int getAreaId(MapInfo mapInfo, MapObject mapObject) {
        return getAreaId(mapInfo, mapObject.getPosition());
    }

    /**
     * 获取坐标点在地图格子区域
     *
     * @param mapInfo
     * @param position
     * @return
     */
    public static int getAreaId(MapInfo mapInfo, Vector3 position) {
        return getAreaId(mapInfo, position.getX(), position.getZ());
    }

    /**
     * 获取坐标点在地图格子区域
     *
     * @param mapInfo
     * @param x
     * @param z
     * @return
     */
    public static int getAreaId(MapInfo mapInfo, double x, double z) {
        /* 当前坐标点的所在的格子的信息 */
        int a1 = MoveUtil.seat(x, mapInfo.getArea_width());
        int a2 = MoveUtil.seat(z, mapInfo.getArea_height());
        return getAreaId(a1, a2);
    }

    /**
     * 获取坐标点在地图格子区域
     *
     * @param x
     * @param z
     * @return
     */
    public static int getAreaId(int x, int z) {
        return x * 1000 + z;
    }

    public static MapArea getArea(long mapId, int mapModelId, int lineId, int areaId) {
        String mapSkey = getMapSkey(mapId, mapModelId, lineId);
        MapInfo mapInfo = MapInfo_Map.get(mapSkey);
        if (mapInfo != null) {
            return mapInfo.getArea(areaId);
        }
        return null;
    }

    /**
     * 获取区域信息，如果不存区域信息，则增加
     *
     * @param mapId
     * @param mapModelId
     * @param lineId
     * @param areaId
     * @return
     */
    public static MapArea getAreaOrIn(long mapId, int mapModelId, int lineId, int areaId) {
        String mapSkey = getMapSkey(mapId, mapModelId, lineId);
        MapInfo mapInfo = MapInfo_Map.get(mapSkey);
        MapArea mapArea = null;
        if (mapInfo != null) {
            mapArea = mapInfo.getAreaOrIn(areaId);
        }
        return mapArea;
    }

    public static void addAllThreadTask(TaskModel taskModel) {
        /*全局地图线程和副本线程*/
        MapThread[] mapThreads = AbsMapManager.getMAPTHREAD_MAP().values().toArray(new MapThread[0]);
        for (int i = 0; i < mapThreads.length; i++) {
            MapThread mapThread = mapThreads[i];
            mapThread.addTask(taskModel);
        }
    }

    public static void addAllThreadTimerTask(TimerTaskModel timerTask) {
        /*全局地图线程和副本线程*/
        MapThread[] mapThreads = AbsMapManager.getMAPTHREAD_MAP().values().toArray(new MapThread[0]);
        for (int i = 0; i < mapThreads.length; i++) {
            MapThread mapThread = mapThreads[i];
            mapThread.addTimerTask(timerTask);
        }
    }

    /**
     * 添加对象所属线程
     *
     * @param person
     * @param taskModel
     */
    public static void addTask(Person person, TaskModel taskModel) {
        MapThread mapThread = AbsMapManager.getMAPTHREAD_MAP().get(person.getMapThreadId());
        mapThread.addTask(taskModel);
    }

    /**
     * 添加对象所属线程
     *
     * @param person
     * @param timerEvent
     */
    public static void addTimerTask(Person person, TimerTaskModel timerEvent) {
        MapThread mapThread = AbsMapManager.getMAPTHREAD_MAP().get(person.getMapThreadId());
        mapThread.addTimerTask(timerEvent);
    }

    /**
     * 添加到地图里面
     *
     * @param mapObject
     * @param timerEvent
     */
    public static void addMapTimerTask(MapObject mapObject, TimerTaskModel timerEvent) {
        MapInfo map = AbsMapManager.getMapInfo(mapObject);
        map.addTimerTask(timerEvent);
    }

    /**
     * 获取指定范围不是阻挡点的格子列表(根据传入数量取)
     *
     * @param mapInfo
     * @param center 中心点
     * @param radius 半径
     * @param num 数量
     * @return
     */
    public static ArrayList<Vector3> getRoundPosition(MapInfo mapInfo, Vector3 center, int radius, int num) {
        return getRoundPosition(mapInfo, center.getX(), center.getZ(), radius, num);
    }

    public static ArrayList<Vector3> getRoundPosition(MapInfo mapInfo, double px, double pz, int radius, int num) {
        ArrayList<Vector3> vector3s = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Vector3 position = AbsMapManager.getRoundPosition(mapInfo, px, pz, radius);
            if (position != null) {
                vector3s.add(position);
            }
        }
        return vector3s;
    }

    public static Vector3 getRoundPosition(MapInfo mapInfo, Vector3 center, double r) {
        return getRoundPosition(mapInfo, center.getX(), center.getZ(), r);
    }

    /**
     * 返回给定点的周围4米随机坐标点
     *
     * @param mapInfo
     * @param px
     * @param pz
     * @param r
     * @return
     */
    public static Vector3 getRoundPosition(MapInfo mapInfo, double px, double pz, double r) {
//        return map.getNavMap().getRandomPointInPaths(px, py, r, 0);
        double cx;
        double cz;
        for (int i = 0; i < 10; i++) {
            cx = RandomUtils.randomDoubleValue(px - r, px + r);
            /*如果玩家在阻挡外  跳到复活点*/
            cz = RandomUtils.randomDoubleValue(pz - r, pz + r);
            if (!mapInfo.getNavMap().isBlock(cx, cz) && mapInfo.getNavMap().isPointInPaths(cx, cz)) {
                return new Vector3(cx, cz);
            }
        }
        String exce = "随机10次都是阻挡 px=" + px + " ,pz=" + pz;
        log.error(exce, new UnsupportedOperationException(exce));
        return null;
    }

}
