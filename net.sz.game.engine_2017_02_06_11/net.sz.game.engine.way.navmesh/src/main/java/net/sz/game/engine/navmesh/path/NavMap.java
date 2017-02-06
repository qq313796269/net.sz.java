/**
 * 特别鸣谢修仙项目组负责人吴章义
 */
package net.sz.game.engine.navmesh.path;

import com.alibaba.fastjson.JSON;
import com.vividsolutions.jts.shape.random.RandomPointsBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.navmesh.KPolygon;
import net.sz.game.engine.navmesh.PolygonConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 */
public class NavMap {

    private static final Logger log = Logger.getLogger(NavMap.class);

    final String readTxtFile(String filePath) {
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                read.close();
                return lineTxt;
            } else {
                throw new UnsupportedOperationException("文件{}配置有误,找不到指定的文件 ->" + filePath);
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("文件{}配置有误,找不到指定的文件 ->" + filePath);
        }
    }

    private final NodeConnector blockNodeConnector = new NodeConnector();
    private final ArrayList<PathBlockingObstacleImpl> blockStationaryObstacles = new ArrayList<>();
    private final NodeConnector pathNodeConnector = new NodeConnector();
    private final ArrayList<PathBlockingObstacleImpl> pathStationaryObstacles = new ArrayList<>();
    private final float maxDistanceBetweenObstacles;
    private final PolygonConverter polygonConverter = new PolygonConverter();
    private final PathFinder pathFinder = new PathFinder();

    private final float scale;
    private final float width;
    private final float height;
    private final String filePath;
    private final int mapID;
    private final float startX;
    private final float startZ;
    private final float endX;
    private final float endZ;

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
        String navMesh = readTxtFile(filePath);
        try {
            NavMeshData data = JSON.parseObject(navMesh, NavMeshData.class);
            if (data == null) {
                throw new UnsupportedOperationException("地图数据加载错误");
            }
            this.width = Math.abs(data.getEndX() - data.getStartX());
            this.height = Math.abs(data.getEndZ() - data.getStartZ());
            this.startX = data.getStartX();
            this.startZ = data.getStartZ();
            this.endX = data.getEndX();
            this.endZ = data.getEndZ();
            if (editor) {
                /*测试是代码缩放*/
                scale = 850 / height;
            } else {
                scale = 1;
            }
            this.mapID = data.getMapID();
            if (mapID < 1) {
                throw new UnsupportedOperationException("地图ID错误");
            }
            maxDistanceBetweenObstacles = Math.max(width, height) * scale;

            createPolygons(blockNodeConnector, blockStationaryObstacles, data.getBlockTriangles(), data.getBlockVertices(), editor);
            createPolygons(pathNodeConnector, pathStationaryObstacles, data.getPathTriangles(), data.getPathVertices(), editor);
        } catch (Exception e) {
            throw e;
        }
    }

    public NavMap clone() {
        try {
            return new NavMap(filePath);
        } catch (Exception ex) {
            return null;
        }
    }

    public final void createPolygons(NodeConnector nodeConnector, ArrayList<PathBlockingObstacleImpl> stationaryObstacles, int[] triangles, Vector3[] vertices, boolean editor) {
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
            PathBlockingObstacleImpl obst = PathBlockingObstacleImpl.createObstacleFromInnerPolygon(copy);
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
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            KPolygon poly = obst.getInnerPolygon();
            if (poly != null) {
                if (poly.contains(x, z)) {
                    Vector3 vector3 = new Vector3(x, z);
                    vector3.y = poly.getY();
                    return vector3;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param center
     * @param radius 半径
     * @param amend 修正
     * @return
     */
    public Vector3 getRandomPointInPaths(Vector3 center, double radius, double amend) {

        return getRandomPointInPaths(center.getX(), center.getZ(), radius, amend);
    }

    /**
     *
     * @param x
     * @param z
     * @param radius 半径
     * @param amend 修正
     * @return
     */
    public Vector3 getRandomPointInPaths(double x, double z, double radius, double amend) {
        List<PathBlockingObstacleImpl> list = new ArrayList<>();
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            double dis = obst.getInnerPolygon().getCenter().distance(x, z);
            if (obst.getInnerPolygon().contains(x, z)) {
                list.add(obst);
            } else {
                if (dis <= radius + amend) {
                    list.add(obst);
                }
            }
        }
        PathBlockingObstacleImpl rk = random(list);
        if (rk == null) {
            return null;
        }
        return rk.getRandomPoint();
    }

    /**
     * 按随机点数一个随机点
     *
     * @param collection
     * @return
     */
    protected PathBlockingObstacleImpl random(Collection<PathBlockingObstacleImpl> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        int total = 0;
        for (PathBlockingObstacleImpl b : collection) {
            total += b.getRandomNum();
        }
        int t = (int) (total * Math.random());
        int i = 0;
        for (Iterator<PathBlockingObstacleImpl> item = collection.iterator(); i <= t && item.hasNext();) {
            PathBlockingObstacleImpl next = item.next();
            i += next.getRandomNum();
            if (i >= t) {
                return next;
            }
        }
        return null;
    }

    public boolean isBlock(double x, double z) {
        return !isPointInPaths(x, z);
    }

    public boolean isBlock(Vector3 vector3) {
        return !isPointInPaths(vector3.getX(), vector3.getZ());
    }

    /**
     * 是否是寻路层可以点
     *
     * @param movedPoint
     * @return
     */
    public boolean isPointInPaths(Vector3 movedPoint) {
        return isPointInPaths(movedPoint.getX(), movedPoint.getZ());
    }

    /**
     * 是否是寻路层可以点
     *
     * @param x
     * @param z
     * @return
     */
    public boolean isPointInPaths(double x, double z) {
        for (PathBlockingObstacleImpl obst : getPathStationaryObstacles()) {
            KPolygon poly = obst.getInnerPolygon();
            if (poly != null) {
                if (poly.contains(x, z)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 修改，获取当前坐标点高度
     *
     * @param movedPoint
     */
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

    /**
     * 获取最近的获得最近的能用的坐标点
     *
     * @param point
     * @return
     */
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
     * 获取最近的获得最近的能用的坐标点
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Vector3 getNearestPointInPaths(double x, double y, double z) {
        Vector3 movedPoint = new Vector3(x, y, z);
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
