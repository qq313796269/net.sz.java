package net.sz.game.engine.map.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.util.NodeEnty2;
import net.sz.game.engine.map.MapArea;
import net.sz.game.engine.map.MapInfo;
import net.sz.game.engine.map.MapObject;
import net.sz.game.engine.map.thread.MapThread;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.thread.TaskEvent;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.TimerTaskEvent;
import net.sz.game.engine.utils.MoveUtil;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class AbsMapManager {

    private static final Logger log = Logger.getLogger(AbsMapManager.class);

    protected static final ConcurrentHashMap<String, MapInfo> mapInfos = new ConcurrentHashMap<>();
    /**
     * 只会记录副本和战场
     */
    protected static final ConcurrentHashMap<Long, MapInfo> mapInfo1s = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, MapThread> MAPTHREAD_MAP = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log.error(getMapSkey(0, 0, 0));
    }

    public static ConcurrentHashMap<String, MapInfo> getMapInfos() {
        return mapInfos;
    }

    /**
     * 只会记录副本和战场
     */
    public static ConcurrentHashMap<Long, MapInfo> getMapInfo1s() {
        return mapInfo1s;
    }

    public static ConcurrentHashMap<String, MapThread> getMAPTHREAD_MAP() {
        return MAPTHREAD_MAP;
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
        return String.format("%s_%s_%s", mapId, mapModelId, lineId);
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

    public static MapInfo removeMap(long mapId) {
        MapInfo get = mapInfo1s.get(mapId);
        removeMap(get);
        return get;
    }

    public static MapInfo removeMap(String mapKey) {
        MapInfo get = mapInfos.get(mapKey);
        removeMap(get);
        return get;
    }

    public static void removeMap(MapInfo mapInfo) {
        // 销毁副本
        ConcurrentHashMap<Integer, NodeEnty2<Long, Object>> mapthreadMap = mapInfo.getMapthreadMap();
        String mapSkey = getMapSkey(mapInfo);
        for (Map.Entry<Integer, NodeEnty2<Long, Object>> entry : mapthreadMap.entrySet()) {
            NodeEnty2<Long, Object> nodeEnty2 = entry.getValue();

            Long tid = nodeEnty2.getKey();

            AbsMapManager.getMAPTHREAD_MAP().get(mapSkey + tid);

            ThreadPool.remove(tid);
            log.error("removeZoneServer " + mapSkey + tid);
        }
        log.error("销毁 战场 或者 副本，当前地图线程总数量:" + AbsMapManager.getMAPTHREAD_MAP().size());
        mapInfo1s.remove(mapInfo.getMapId());
        mapInfos.remove(mapSkey);
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
        return mapInfo1s.get(mapId);
    }

    public static MapInfo getMapInfo(String mapSkey) {
        return mapInfos.get(mapSkey);
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
        return a1 * 1000 + a2;
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
        MapInfo mapInfo = mapInfos.get(mapSkey);
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
        MapInfo mapInfo = mapInfos.get(mapSkey);
        MapArea mapArea = null;
        if (mapInfo != null) {
            mapArea = mapInfo.getAreaOrIn(areaId);
        }
        return mapArea;
    }

    public static void addTask(MapObject mapObject, TaskEvent taskModel) {
        MapInfo map = AbsMapManager.getMapInfo(mapObject);
        map.addTask(taskModel);
//        ThreadPool.addTask(mapObject.getMapThread(), taskModel);

    }

    public static void addTimerTask(MapObject mapObject, TimerTaskEvent timerEvent) {
        MapInfo map = AbsMapManager.getMapInfo(mapObject);
        map.addTimerTask(timerEvent);
//        ThreadPool.addTimerTask(mapObject.getMapThread(), timerEvent);
    }

}
