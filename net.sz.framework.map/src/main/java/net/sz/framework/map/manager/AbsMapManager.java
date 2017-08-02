package net.sz.framework.map.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.map.MapArea;
import net.sz.framework.map.MapInfo;
import net.sz.framework.map.MapObject;
import net.sz.framework.map.MapType;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.map.thread.MapPersonThread;
import net.sz.framework.map.thread.MapThreadExcutor;
import net.sz.framework.struct.Vector;
import net.sz.framework.way.navmesh.Vector3;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.TaskModel;
import net.sz.framework.szthread.TimerTaskModel;
import net.sz.framework.utils.MoveUtil;
import net.sz.framework.utils.RandomUtils;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AbsMapManager {

    private static final SzLogger log = SzLogger.getLogger();
    /*所有地图*/
    protected static final ConcurrentHashMap<String, MapInfo> ALL_MAPINFO_Map = new ConcurrentHashMap<>();
    /**
     * 只会记录副本和战场
     */
    protected static final ConcurrentHashMap<Long, MapInfo> ZONE_MAPINFO_Map = new ConcurrentHashMap<>();
    /**
     * 所有地图信息
     */
    protected static final ConcurrentHashMap<Long, MapPersonThread> MAPINFO_THREAD_Map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log.error(getMapSkey(0, 0, 0));
    }

    /**
     * 所有地图信息
     *
     * @return
     */
    public static ConcurrentHashMap<String, MapInfo> getALL_MAPINFO_Map() {
        return ALL_MAPINFO_Map;
    }

    /**
     * 只会记录副本和战场
     *
     * @return
     */
    public static ConcurrentHashMap<Long, MapInfo> getZONE_MAPINFO_Map() {
        return ZONE_MAPINFO_Map;
    }

    /**
     * 所有地图线程
     *
     * @return
     */
    public static ConcurrentHashMap<Long, MapPersonThread> getMAPINFO_THREAD_Map() {
        return MAPINFO_THREAD_Map;
    }

    /**
     * 获取一个线程
     *
     * @return
     */
    public static MapPersonThread getNextMAPTHREAD(int size) {
        HashMap<Long, MapPersonThread> tmpMap = new HashMap<>(MAPINFO_THREAD_Map);
        MapPersonThread mapThread = null;
        for (Map.Entry<Long, MapPersonThread> entry : tmpMap.entrySet()) {
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
        MapInfo get = ZONE_MAPINFO_Map.get(mapId);
        removeMap(get);
        return get;
    }

    /**
     *
     * @param mapKey
     * @return
     */
    public static MapInfo removeMap(String mapKey) {
        MapInfo get = ALL_MAPINFO_Map.get(mapKey);
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
        if (mapInfo != null) {

            ConcurrentHashMap<Long, Person> monsters = mapInfo.getMonsters();
            for (Map.Entry<Long, Person> entry : monsters.entrySet()) {
                Person monster = entry.getValue();
                quitMapthread(monster);
            }
            if (log.isDebugEnabled()) {
                log.debug("销毁 战场 或者 副本，当前地图数量：" + AbsMapManager.getALL_MAPINFO_Map().size() + "，当前地图线程总数量：" + AbsMapManager.getMAPINFO_THREAD_Map().size());
            }

            MapThreadExcutor.MapAllExcutor.removeKey(mapInfo.getMapKey());

            ZONE_MAPINFO_Map.remove(mapInfo.getMapId());
            ALL_MAPINFO_Map.remove(mapInfo.getMapKey());

        }
    }

    public static void addMap(MapInfo mapInfo) {

        MapThreadExcutor.MapAllExcutor.addKey(mapInfo.getMapKey());

        ALL_MAPINFO_Map.put(mapInfo.getMapKey(), mapInfo);

        if (mapInfo.getMapType().getGroup() != MapType.WORLDMAP.getGroup()) {
            ZONE_MAPINFO_Map.put(mapInfo.getMapId(), mapInfo);
        }
    }

    /**
     * 删除线程
     *
     * @param person
     */
    public static void quitMapthread(Person person) {
        if (person.getMapThreadId() != 0) {
            /*如果之前的所属线程不为空删除线程对象*/
            MapPersonThread tmpMapThread = AbsMapManager.getMapThread(person.getMapThreadId());
            if (tmpMapThread != null) {
                Person remove = tmpMapThread.getPersonMap().remove(person.getId());
                if (log.isDebugEnabled()) {
                    log.debug("退出地图线程：" + person.getName() + "(" + person.getId() + ")" + " 线程ID：" + person.getMapThreadId() + " " + (remove != null));
                }
            }
            person.setMapThreadId(0);
        }
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
        return ZONE_MAPINFO_Map.get(mapId);
    }

    /**
     *
     * @param mapSkey
     * @return
     */
    public static MapInfo getMapInfo(String mapSkey) {
        return ALL_MAPINFO_Map.get(mapSkey);
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
        MapInfo mapInfo = ALL_MAPINFO_Map.get(mapSkey);
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
        MapInfo mapInfo = ALL_MAPINFO_Map.get(mapSkey);
        MapArea mapArea = null;
        if (mapInfo != null) {
            mapArea = mapInfo.getAreaOrIn(areaId);
        }
        return mapArea;
    }

    public static void addAllThreadTask(TaskModel taskModel) {
        /*全局地图线程和副本线程*/
        MapPersonThread[] mapThreads = AbsMapManager.getMAPINFO_THREAD_Map().values().toArray(new MapPersonThread[0]);
        for (int i = 0; i < mapThreads.length; i++) {
            MapPersonThread mapThread = mapThreads[i];
            mapThread.addTask(taskModel);
        }
    }

    public static void addAllThreadTimerTask(TimerTaskModel timerTask) {
        /*全局地图线程和副本线程*/
        MapPersonThread[] mapThreads = AbsMapManager.getMAPINFO_THREAD_Map().values().toArray(new MapPersonThread[0]);
        for (int i = 0; i < mapThreads.length; i++) {
            MapPersonThread mapThread = mapThreads[i];
            mapThread.addTimerTask(timerTask);
        }
    }

    /**
     *
     * @param person
     * @return
     */
    public static MapPersonThread getMapThread(Person person) {
        return getMapThread(person.getMapThreadId());
    }

    /**
     *
     * @param mapthreadId
     * @return
     */
    public static MapPersonThread getMapThread(long mapthreadId) {
        return AbsMapManager.getMAPINFO_THREAD_Map().get(mapthreadId);
    }

    /**
     * 添加到对象线程
     *
     * @param person
     * @param taskModel
     * @return
     */
    public static TaskModel addPersonTask(Person person, TaskModel taskModel) {
        MapPersonThread mapThread = getMapThread(person);
        if (mapThread != null) {
            return (TaskModel) mapThread.addTask(taskModel);
        } else if (log.isDebugEnabled()) {
            log.error("对象当前已经退出地图：" + person.showString());
        }
        return null;
    }

    /**
     * 添加到对象线程
     *
     * @param person
     * @param timerEvent
     * @return
     */
    public static TimerTaskModel addPersonTimerTask(Person person, TimerTaskModel timerEvent) {
        MapPersonThread mapThread = getMapThread(person);
        if (mapThread != null) {
            return (TimerTaskModel) mapThread.addTimerTask(timerEvent);
        } else if (log.isDebugEnabled()) {
            log.error("对象当前已经退出地图：" + person.showString());
        }
        return null;
    }

    /**
     * 添加到对象地图
     *
     * @param person
     * @param taskModel
     */
    public static TaskModel addMapTask(Person person, TaskModel taskModel) {
        MapInfo mapInfo = getMapInfo(person);
        if (mapInfo != null) {
            return mapInfo.addTask(taskModel);
        } else if (log.isDebugEnabled()) {
            log.error("对象当前已经退出地图：" + person.showString());
        }
        return null;
    }

    /**
     * 添加到对象地图
     *
     * @param person
     * @param timerEvent
     */
    public static TimerTaskModel addMapTimerTask(Person person, TimerTaskModel timerEvent) {
        MapInfo mapInfo = getMapInfo(person);
        if (mapInfo != null) {
            return mapInfo.addTimerTask(timerEvent);
        } else if (log.isDebugEnabled()) {
            log.error("对象当前已经退出地图：" + person.showString());
        }
        return null;
    }

//    /**
//     * 添加到地图里面
//     *
//     * @param mapObject
//     * @param timerEvent
//     */
//    public static void addMapTimerTask(MapObject mapObject, TimerTaskModel timerEvent) {
//        MapInfo map = AbsMapManager.getMapInfo(mapObject);
//        if (map != null) {
//            map.addTimerTask(timerEvent);
//        } else if (log.isDebugEnabled()) {
//            log.error("对象当前已经退出地图：" + mapObject.showString());
//        }
//    }
    /**
     * 获取指定范围不是阻挡点的格子列表(根据传入数量取)
     *
     * @param mapInfo
     * @param center 中心点
     * @param rmax 最大半径
     * @param num 数量
     * @return
     */
    public static ArrayList<Vector3> getRoundPosition(MapInfo mapInfo, Vector3 center, float rmax, int num) {
        return getRoundPosition(mapInfo, center.getX(), center.getZ(), 0, rmax, num);
    }

    /**
     * 获取指定范围不是阻挡点的格子列表(根据传入数量取)
     *
     * @param mapInfo
     * @param center 中心点
     * @param rmin 最小半径
     * @param rmax 最大半径
     * @param num 数量
     * @return
     */
    public static ArrayList<Vector3> getRoundPosition(MapInfo mapInfo, Vector3 center, float rmin, float rmax, int num) {
        return getRoundPosition(mapInfo, center.getX(), center.getZ(), rmin, rmax, num);
    }

    /**
     * 获取指定范围不是阻挡点的格子列表(根据传入数量取)
     *
     * @param mapInfo
     * @param px
     * @param pz
     * @param rmax 最大半径
     * @param num 数量
     * @return
     */
    public static ArrayList<Vector3> getRoundPosition(MapInfo mapInfo, double px, double pz, float rmax, int num) {
        return getRoundPosition(mapInfo, px, pz, 0, rmax, num);
    }

    /**
     * 获取指定范围不是阻挡点的格子列表(根据传入数量取)
     *
     * @param mapInfo
     * @param px 中心点
     * @param pz 中心点
     * @param rmin 最小半径
     * @param rmax 最大半径
     * @param num 数量
     * @return
     */
    public static ArrayList<Vector3> getRoundPosition(MapInfo mapInfo, double px, double pz, float rmin, float rmax, int num) {
        ArrayList<Vector3> vector3s = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Vector3 position = AbsMapManager.getRoundPosition(mapInfo, px, pz, rmin, rmax);
            if (position != null) {
                vector3s.add(position);
            }
        }
        return vector3s;
    }

    /**
     *
     * @param mapInfo
     * @param center
     * @param rmax
     * @return
     */
    public static Vector3 getRoundPosition(MapInfo mapInfo, Vector3 center, float rmax) {
        return getRoundPosition(mapInfo, center.getX(), center.getZ(), 0, rmax);
    }

    /**
     *
     * @param mapInfo
     * @param center
     * @param rmin
     * @param rmax
     * @return
     */
    public static Vector3 getRoundPosition(MapInfo mapInfo, Vector3 center, float rmin, float rmax) {
        return getRoundPosition(mapInfo, center.getX(), center.getZ(), rmin, rmax);
    }

    /**
     *
     * @param mapInfo
     * @param px
     * @param pz
     * @param rmax
     * @return
     */
    public static Vector3 getRoundPosition(MapInfo mapInfo, double px, double pz, float rmax) {
        return getRoundPosition(mapInfo, px, pz, 0, rmax);
    }

    /**
     * 返回给定点的周围4米随机坐标点
     *
     * @param mapInfo
     * @param px
     * @param pz
     * @param rmin 最小值是0.1f
     * @param rmax
     * @return
     */
    public static Vector3 getRoundPosition(MapInfo mapInfo, double px, double pz, float rmin, float rmax) {
        for (int i = 0; i < 10; i++) {
            /*随机360°朝向*/
            int random = RandomUtils.random(360);
            Vector vector = MoveUtil.getVectorBy360Atan(random);
            Vector3 roundPosition = getRoundPosition(mapInfo, vector, px, pz, rmin, rmax);
            if (roundPosition != null) {
                return roundPosition;
            }
        }
        /*如果玩家在阻挡外  跳到复活点*/
        String exce = "随机10次都是阻挡 px=" + px + ", pz=" + pz + ", rmin=" + rmin + ", rmax=" + rmax;
        log.error(exce, new UnsupportedOperationException(exce));
        return null;
    }

    /**
     *
     * @param mapInfo
     * @param vector
     * @param px
     * @param pz
     * @param rmin
     * @param rmax
     * @return
     */
    public static Vector3 getRoundPosition(MapInfo mapInfo, Vector vector, double px, double pz, float rmin, float rmax) {
        if (rmin < 0.1) {
            rmin = 0.1f;
        }

        float vr = RandomUtils.randomFloatValue(rmin, rmax);

        double cx = px + vector.getDir_x() * MoveUtil.getV12XD(vr, vector.getAtan());
        double cz = pz + vector.getDir_z() * MoveUtil.getV12ZD(vr, vector.getAtan());

        if (!mapInfo.getNavMap().isPointInBlocks(cx, cz) && mapInfo.getNavMap().isPointInPaths(cx, cz)) {
            return Vector3.getVector3(cx, cz);
        }
        return null;
    }

}
