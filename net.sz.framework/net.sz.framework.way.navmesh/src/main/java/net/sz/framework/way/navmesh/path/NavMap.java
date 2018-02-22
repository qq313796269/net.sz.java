package net.sz.framework.way.navmesh.path;

import com.alibaba.fastjson.JSON;
import com.vividsolutions.jts.shape.random.RandomPointsBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;
import net.sz.framework.way.navmesh.KPolygon;
import net.sz.framework.way.navmesh.PolygonConverter;
import net.sz.framework.way.navmesh.Vector3;

/**
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 */
public class NavMap implements Serializable, Cloneable {

    private static final SzLogger log = SzLogger.getLogger();

    private NodeConnector blockNodeConnector = new NodeConnector();
    private ArrayList<PathBlockingObstacleImpl> blockStationaryObstacles = new ArrayList<>();

    private NodeConnector pathNodeConnector = new NodeConnector();
    private ArrayList<PathBlockingObstacleImpl> pathStationaryObstacles = new ArrayList<>();

    private NodeConnector safeNodeConnector = new NodeConnector();
    private ArrayList<PathBlockingObstacleImpl> safeStationaryObstacles = new ArrayList<>();

    private float maxDistanceBetweenObstacles;
    private PolygonConverter polygonConverter = new PolygonConverter();
    private PathFinder pathFinder = new PathFinder();

    private final float scale;
    private final float width;
    private final float height;
    private final int mapID;
    private final float startX;
    private final float startZ;
    private final float endX;
    private final float endZ;
    private final String filePath;
    private final boolean editor;

    transient static final ConcurrentHashMap<String, NavMeshData> navMeshDataMap = new ConcurrentHashMap<>();

    @Override
    public NavMap clone() {
        try {
            NavMap navMap = (NavMap) super.clone();

            navMap.blockNodeConnector = this.blockNodeConnector.clone();
            navMap.pathNodeConnector = this.pathNodeConnector.clone();
            navMap.safeNodeConnector = this.safeNodeConnector.clone();

            navMap.blockStationaryObstacles = new ArrayList<>();
            for (PathBlockingObstacleImpl blockStationaryObstacle : blockStationaryObstacles) {
                navMap.blockStationaryObstacles.add(blockStationaryObstacle.clone());
            }

            navMap.pathStationaryObstacles = new ArrayList<>();
            for (PathBlockingObstacleImpl blockStationaryObstacle : pathStationaryObstacles) {
                navMap.pathStationaryObstacles.add(blockStationaryObstacle.clone());
            }

            navMap.safeStationaryObstacles = new ArrayList<>();
            for (PathBlockingObstacleImpl blockStationaryObstacle : safeStationaryObstacles) {
                navMap.safeStationaryObstacles.add(blockStationaryObstacle.clone());
            }

            return navMap;
//            return new NavMap(this.filePath, editor);
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static NavMeshData readTxtFile(String path, String fileName) {
        return readTxtFile(path + File.separatorChar + fileName);
    }

    public static NavMeshData readTxtFile(String filePath) {
        String encoding = "GBK";
        File file = new File(filePath);
        NavMeshData navMeshData;
        navMeshData = navMeshDataMap.get(file.getName());
        if (navMeshData != null) {
            return navMeshData;
        }
        if ((file.isFile() && file.exists())) { //判断文件是否存在
            try {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                read.close();
                navMeshData = JSON.parseObject(lineTxt, NavMeshData.class);
                navMeshDataMap.put(file.getName(), navMeshData);
                return navMeshData;
            } catch (Exception e) {
                throw new UnsupportedOperationException("文件{}配置有误,找不到指定的文件 ->" + filePath, e);
            }
        } else {
            throw new UnsupportedOperationException("文件{}配置有误,找不到指定的文件 ->" + filePath);
        }
    }

    public NavMap(String filePath) {
        this(filePath, false);
    }

    /**
     *
     * @param filePath
     * @param editor true/会被缩放
     */
    public NavMap(String filePath, boolean editor) {
        this.filePath = filePath;
        this.editor = editor;
        NavMeshData data = readTxtFile(filePath);
        if (data == null) {
            throw new UnsupportedOperationException("地图数据加载错误");
        }
        this.width = Math.abs(data.getEndX() - data.getStartX());
        this.height = Math.abs(data.getEndZ() - data.getStartZ());
        this.startX = data.getStartX();
        this.startZ = data.getStartZ();
        this.endX = data.getEndX();
        this.endZ = data.getEndZ();
        if (this.editor) {
            scale = 850 / height;
        } else {
            scale = 1;
        }
        this.mapID = data.getMapID();
        if (mapID < 1) {
            throw new UnsupportedOperationException("地图ID错误");
        }
        maxDistanceBetweenObstacles = Math.max(width, height) * scale;

        try {
            createPolygons(blockNodeConnector, blockStationaryObstacles, data.getBlockTriangles(), data.getBlockVertices(), true, this.editor);
            createPolygons(pathNodeConnector, pathStationaryObstacles, data.getPathTriangles(), data.getPathVertices(), false, this.editor);
            createPolygons(safeNodeConnector, safeStationaryObstacles, data.getSafeTriangles(), data.getSafeVertices(), false, this.editor);
        } catch (Exception e) {
            throw e;
        }

    }

    public final void createPolygons(NodeConnector nodeConnector, ArrayList<PathBlockingObstacleImpl> stationaryObstacles, int[] triangles, Vector3[] vertices, boolean bufferOuter, boolean editor) {
        if (triangles == null || vertices == null) {
            return;
        }
        ArrayList<Vector3> list = new ArrayList<>(3);
        if (editor) {
            for (Vector3 li : vertices) {
                li.scale(scale);
            }
        }
        RandomPointsBuilder rpb = new RandomPointsBuilder();
        for (int i = 0; i < triangles.length; i += 3) {
            if (triangles.length <= i + 2) {
                break;
            }
            list.clear();
            list.add(vertices[triangles[i]]);
            list.add(vertices[triangles[i + 1]]);
            list.add(vertices[triangles[i + 2]]);
            com.vividsolutions.jts.geom.Polygon jtsPolygon = getPolygon(list);
            if (jtsPolygon == null) {
                continue;
            }
            KPolygon poly = polygonConverter.makeKPolygonFromExterior(jtsPolygon);
            if (poly == null) {
                continue;
            }
            KPolygon copy = poly.copy();
            PathBlockingObstacleImpl obst = null;
            if (bufferOuter) {
                obst = PathBlockingObstacleImpl.createObstacleFromInnerPolygon(copy);
            } else {
                obst = PathBlockingObstacleImpl.createObstacleFromOuterPolygon(copy);
            }
            if (obst == null) {
                continue;
            }
            if (editor) {
                rpb.setNumPoints((int) ((Math.sqrt(jtsPolygon.getArea() / scale / scale) + 1) * 5));
            } else {
                rpb.setNumPoints((int) ((Math.sqrt(jtsPolygon.getArea()) + 1) * 5));
            }
            rpb.setExtent(jtsPolygon);
            obst.addRandomPoints(rpb.getGeometry().getCoordinates());
            stationaryObstacles.add(obst);
            nodeConnector.addObstacle(obst, stationaryObstacles, maxDistanceBetweenObstacles);
        }
    }

    /**
     * 添加一个阻挡
     *
     * @param vertices
     * @return
     */
    public final PathBlockingObstacleImpl addBlock(ArrayList<Vector3> vertices) {
        return addBlock(vertices, true, false);
    }

    /**
     * 添加一个阻挡
     *
     * @param vertices
     * @param bufferOuter
     * @param editor
     * @return
     */
    public final PathBlockingObstacleImpl addBlock(ArrayList<Vector3> vertices, boolean bufferOuter, boolean editor) {

        if (vertices == null || vertices.isEmpty()) {
            return null;
        }

        if (editor) {
            for (Vector3 li : vertices) {
                li.scale(scale);
            }
        }

        RandomPointsBuilder rpb = new RandomPointsBuilder();
        com.vividsolutions.jts.geom.Polygon jtsPolygon = getPolygon(vertices);
        if (jtsPolygon == null) {
            return null;
        }

        KPolygon poly = polygonConverter.makeKPolygonFromExterior(jtsPolygon);
        if (poly == null) {
            return null;
        }

        KPolygon copy = poly.copy();
        PathBlockingObstacleImpl obst = null;

        if (bufferOuter) {
            obst = PathBlockingObstacleImpl.createObstacleFromInnerPolygon(copy);
        } else {
            obst = PathBlockingObstacleImpl.createObstacleFromOuterPolygon(copy);
        }

        if (obst == null) {
            return null;
        }

        if (editor) {
            rpb.setNumPoints((int) ((Math.sqrt(jtsPolygon.getArea() / scale / scale) + 1) * 5));
        } else {
            rpb.setNumPoints((int) ((Math.sqrt(jtsPolygon.getArea()) + 1) * 5));
        }

        rpb.setExtent(jtsPolygon);
        obst.addRandomPoints(rpb.getGeometry().getCoordinates());

        this.blockStationaryObstacles.add(obst);
        this.blockNodeConnector.addObstacle(obst, this.blockStationaryObstacles, maxDistanceBetweenObstacles);

        return obst;
    }

    /**
     * 删除一个阻挡
     *
     * @param obst
     */
    public final void removeBlock(PathBlockingObstacleImpl obst) {
        if (obst != null) {
            this.blockStationaryObstacles.remove(obst);
            this.blockNodeConnector.removeObstacle(obst, maxDistanceBetweenObstacles, blockStationaryObstacles);
        }
    }

    /**
     * 获取矩形
     *
     * @param position 当前位置
     * @param distance 距离
     * @param sourceDirection 当前方向，注意是unity的方向
     * @param width
     * @param height
     * @return
     */
    public final KPolygon getKPolygon(Vector3 position, double distance, Vector3 sourceDirection, float width, float height) {
        Vector3 source = position.unityTranslate(sourceDirection, 0, distance);
        Vector3 corner_1 = source.unityTranslate(sourceDirection, -90, width / 2);
        Vector3 corner_2 = source.unityTranslate(sourceDirection, 90, width / 2);
        Vector3 corner_3 = corner_2.unityTranslate(sourceDirection, 0, height);
        Vector3 corner_4 = corner_1.unityTranslate(sourceDirection, 0, height);
        List<Vector3> sectors = new ArrayList<>(4);
        sectors.add(corner_1);
        sectors.add(corner_4);
        sectors.add(corner_3);
        sectors.add(corner_2);
        return getKPolygon(sectors);
    }

    /**
     * 根据当前位置获取扇形
     *
     * @param position
     * @param sourceDirection
     * @param distance
     * @param radius
     * @param degrees
     * @return
     */
    public final KPolygon getKPolygon(Vector3 position, Vector3 sourceDirection, float distance, float radius, float degrees) {
        Vector3 source = position.unityTranslate(sourceDirection, 0, distance);
        Vector3 forward_l = position.unityTranslate(sourceDirection, -degrees / 2, radius);
        Vector3 forward_r = position.unityTranslate(sourceDirection, degrees / 2, radius);
        List<Vector3> sectors = new ArrayList<>(4);
        sectors.add(source);
        sectors.add(forward_l);
        int size = (int) (degrees / 10) / 2 - 1;
        for (int i = -size; i <= size; i++) {
            Vector3 forward = position.unityTranslate(sourceDirection, i * 10, radius);
            sectors.add(forward);
        }
        sectors.add(forward_r);
        return getKPolygon(sectors);
    }

    /**
     * 根据半径获取一个多边形
     *
     * @param center
     * @param radius
     * @param vertexCount
     * @return
     */
    public final KPolygon getKPolygon(Vector3 center, float radius, int vertexCount) {
        if (vertexCount < 3) {
            vertexCount = 3;
        }
        List<Vector3> sectors = new ArrayList<>(vertexCount);
        double degrees = 360d / vertexCount;
        Random random = new Random(TimeUtil.currentTimeMillis());
        double randomDegrees = random.nextFloat() * 360;
        for (int i = 0; i < vertexCount; i++) {
            Vector3 source = center.translateCopy(i * degrees + randomDegrees, radius);
            sectors.add(source);
        }
        return getKPolygon(sectors);
    }

    public final KPolygon getKPolygon(List<Vector3> list) {
        com.vividsolutions.jts.geom.Polygon jtsPolygon = getPolygon(list);
        if (jtsPolygon == null) {
            return null;
        }
        KPolygon poly = polygonConverter.makeKPolygonFromExterior(jtsPolygon);
        return poly;
    }

    public final com.vividsolutions.jts.geom.Polygon getPolygon(List<Vector3> pos) {
        KPolygon poly = new KPolygon(pos);
        com.vividsolutions.jts.geom.Polygon jtsPolygon = polygonConverter.makeJTSPolygonFrom(poly);
        return jtsPolygon;
    }

    public PathData path(Vector3 start, Vector3 end) {
        PathData data;
        synchronized (pathFinder) {
            data = pathFinder.calc(start, end, this.maxDistanceBetweenObstacles, getBlockNodeConnector(), getBlockStationaryObstacles());
        }
        return data;
    }

    public Vector3 getPointInPaths(double x, double z) {
        Vector3 movedPoint = new Vector3(x, z);
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            if (obst.getInnerPolygon().contains(movedPoint)) {
                KPolygon poly = obst.getInnerPolygon();
                if (poly != null) {
                    movedPoint.y = poly.getY();
                    return movedPoint;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param center
     * @param radius
     * @param minDisToCenter
     * @return
     */
    public Vector3 getRandomPointInPaths(Vector3 center, float radius, float minDisToCenter) {
        List<PathBlockingObstacleImpl> list = new ArrayList<>();
        float dis2 = radius * radius + minDisToCenter;
        double dis = 0;
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            if (obst.getInnerPolygon().contains(center)) {
                list.add(obst);
            } else {
                for (Vector3 point : obst.getInnerPolygon().getPoints()) {
                    dis = point.distanceSqlt2D(center);
                    if (dis <= dis2 && dis >= minDisToCenter) {
                        list.add(obst);
                        break;
                    }
                }
            }
        }
        ArrayList<Vector3> targets = new ArrayList<>();
        for (PathBlockingObstacleImpl pb : list) {
            for (Vector3 p : pb.getRandomPoints()) {
                if (p.distanceSqlt2D(center) <= dis2) {
                    targets.add(p);
                }
            }
        }
        Vector3 point = random(targets);
        if (point == null) {
            return center;
        }
        return point;
    }

    /**
     * 从集合中随机一个元素
     *
     * @param <T>
     * @param collection
     * @return
     */
    protected <T> T random(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        int t = (int) (collection.size() * Math.random());
        int i = 0;
        for (Iterator<T> item = collection.iterator(); i <= t && item.hasNext();) {
            T next = item.next();
            if (i == t) {
                return next;
            }
            i++;
        }
        return null;
    }

    public boolean isPointInPaths(Vector3 movedPoint) {
        return isPointInPaths(movedPoint.x, movedPoint.z);
    }

    public boolean isPointInPaths(double x, double z) {
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            if (obst.getInnerPolygon().contains(x, z)) {
                KPolygon poly = obst.getInnerPolygon();
                if (poly != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPointInBlocks(Vector3 movedPoint) {
        return isPointInBlocks(movedPoint.x, movedPoint.z);
    }

    public boolean isPointInBlocks(double x, double z) {
        for (PathBlockingObstacleImpl obst : getBlockStationaryObstacles()) {
            if (obst.getInnerPolygon().contains(x, z)) {
                KPolygon poly = obst.getInnerPolygon();
                if (poly != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isPointInSafes(Vector3 movedPoint) {
        return isPointInSafes(movedPoint.x, movedPoint.z);
    }

    public boolean isPointInSafes(double x, double z) {
        if (!safeStationaryObstacles.isEmpty()) {
            for (PathBlockingObstacleImpl obst : safeStationaryObstacles) {
                if (obst.getInnerPolygon().contains(x, z)) {
                    KPolygon poly = obst.getInnerPolygon();
                    if (poly != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void amendPoint(Vector3 movedPoint) {
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            if (obst.getInnerPolygon().contains(movedPoint)) {
                KPolygon poly = obst.getInnerPolygon();
                if (poly != null) {
                    movedPoint.y = poly.getY();
                    break;
                }
            }
        }
    }

    public KPolygon getPolygonInPaths(Vector3 movedPoint) {
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            if (obst.getInnerPolygon().contains(movedPoint)) {
                KPolygon poly = obst.getInnerPolygon();
                if (poly != null) {
                    return poly;
                }
            }
        }
        return null;
    }

    public Vector3 getNearestPointInPaths(Vector3 point) {
        Vector3 movedPoint = point.copy();
        boolean targetIsInsideObstacle = false;
        int count = 0;
        while (true) {
            for (PathBlockingObstacleImpl obst : getBlockStationaryObstacles()) {
                if (obst.getOuterPolygon().contains(movedPoint)) {
                    targetIsInsideObstacle = true;
                    KPolygon poly = obst.getOuterPolygon();
                    Vector3 p = poly.getBoundaryPointClosestTo(movedPoint);
                    if (p != null) {
                        movedPoint.x = p.x;
                        movedPoint.z = p.z;
                    }
                }
            }
            count++;
            if (targetIsInsideObstacle == false || count >= 3) {
                break;
            }
        }
        return movedPoint;
    }

    /**
     * @return the blockNodeConnector
     */
    public NodeConnector getBlockNodeConnector() {
        return blockNodeConnector;
    }

    /**
     * @return the blockStationaryObstacles
     */
    public ArrayList<PathBlockingObstacleImpl> getBlockStationaryObstacles() {
        return blockStationaryObstacles;
    }

    /**
     * @return the pathNodeConnector
     */
    public NodeConnector getPathNodeConnector() {
        return pathNodeConnector;
    }

    /**
     * @return the pathStationaryObstacles
     */
    public ArrayList<PathBlockingObstacleImpl> getPathStationaryObstacles() {
        return pathStationaryObstacles;
    }

    /**
     * @return the maxConnectionDistanceBetweenObstacles
     */
    public float getMaxConnectionDistanceBetweenObstacles() {
        return maxDistanceBetweenObstacles;
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @return the mapID
     */
    public int getMapID() {
        return mapID;
    }

    /**
     * @return the startX
     */
    public float getStartX() {
        return startX;
    }

    /**
     * @return the startZ
     */
    public float getStartZ() {
        return startZ;
    }

    /**
     * @return the endX
     */
    public float getEndX() {
        return endX;
    }

    /**
     * @return the endZ
     */
    public float getEndZ() {
        return endZ;
    }
}
