package net.sz.framework.way.astar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sz.framework.utils.MoveUtil;

import net.sz.framework.szlog.SzLogger;

/**
 * 移动辅助
 * <br>
 * x轴是正方向，为0 ，顺时针依次
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Move {

    private static final SzLogger log = SzLogger.getLogger();
    private static final NumberFormat ddf1 = NumberFormat.getNumberInstance();

    /**
     * 非阻挡，默认配置.0
     */
    public static int ConstUnBlock = 0;

    /**
     * 阻挡点配置,默认是1
     */
    public static int ConstBlock = 1;

    /**
     * 安全区域配置,默认是2
     */
    public static int ConstSafe = 2;

    // <editor-fold desc="寻路相关模块">
    //从开启列表查找F值最小的节点
    private static Point getMinFFromOpenList(HashMap<String, Point> Open_List) {
        Point Pmin = null;
        for (Map.Entry<String, Point> entry : Open_List.entrySet()) {
            String key = entry.getKey();
            Point item = entry.getValue();
            if (Pmin == null || Pmin.getF() > item.getF()) {
                Pmin = item;
            }
        }
        return Pmin;
    }

    //判断关闭列表是否包含一个坐标的点
    private static boolean checkInCloseList(String key, HashMap<String, Point> Close_List) {
        return Close_List.containsKey(key);
    }

    //从关闭列表返回对应坐标的点
    private static Point getPointFromCloseList(String key, HashMap<String, Point> Close_List) {
        return Close_List.get(key);
    }

    //判断开启列表是否包含一个坐标的点
    private static boolean checkInOpenList(String key, HashMap<String, Point> Open_List) {
        return Open_List.containsKey(key);
    }
    //从开启列表返回对应坐标的点

    private static Point getPointFromOpenList(String key, HashMap<String, Point> Open_List) {
        return Open_List.get(key);
    }

    //计算某个点的G值
    private static int getG(Point p) {
        if (p.getNext() == null) {
            return 0;
        }
        if (p.getX() == p.getNext().getX() || p.getY() == p.getNext().getY()) {
            return p.getNext().getG() + 10;
        } else {
            return p.getNext().getG() + 14;
        }
    }

    //计算某个点的H值
    private static int getH(Point p, Point pb) {
        return Math.abs(p.getX() - pb.getX()) + Math.abs(p.getY() - pb.getY());
    }

    // <editor-fold desc="检查当前节点附近的节点 private static void checkP8(Point p0, byte[][] map, Point pa, Point pb, HashMap<String, Point> Open_List, HashMap<String, Point> Close_List)">
    /**
     * 检查当前节点附近的节点
     *
     * @param p0
     * @param map
     * @param pa
     * @param pb
     * @param Open_List
     * @param Close_List
     */
    private static void checkP8(Point p0, int[][] map, Point pa, Point pb, HashMap<String, Point> Open_List, HashMap<String, Point> Close_List) {
        //这里的循环其实就是8方向判断
        for (int xt = p0.getX() - 1; xt <= p0.getX() + 1; xt++) {
            for (int yt = p0.getY() - 1; yt <= p0.getY() + 1; yt++) {
                //排除超过边界和等于自身的点
                if (xt >= 0 && xt < map.length && yt >= 0 && yt < map[0].length && !(xt == p0.getX() && yt == p0.getY())) {
                    String key = xt + "-" + yt;
                    //排除障碍点和关闭列表中的点
                    if (map[yt][xt] != ConstBlock && !checkInCloseList(key, Close_List)) {
                        Point pt = getPointFromOpenList(key, Open_List);
                        if (pt != null) {
                            //如果节点在开启列表中更新带价值
                            int G_new = 0;
                            if (p0.getX() == pt.getX() || p0.getY() == pt.getY()) {
                                G_new = p0.getG() + 10;
                            } else {
                                G_new = p0.getG() + 14;
                            }
                            if (G_new < pt.getG()) {
                                //Open_List.Remove(pt);
                                pt.setNext(p0);
                                pt.setG(G_new);
                                //Open_List.Add(pt);
                            }
                        } else {
                            //不在开启列表中,如果不存在创建添加到开启列表中
                            pt = new Point();
                            pt.setX(xt);
                            pt.setY(yt);
                            pt.setNext(p0);
                            pt.setG(getG(pt));
                            pt.setH(getH(pt, pb));
                            Open_List.put(pt.getKey(), pt);
                        }
                    }
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="寻路路径  static public Point findWay(byte[][] r, int sx, int sz, int ex, int ez)">
    /**
     *
     * @param r
     * @param sx
     * @param sz
     * @param ex
     * @param ez
     * @return
     */
    static public ArrayList<Point> findWay(int[][] r, float sx, float sz, float ex, float ez) {
        return findWay(r, new Point(sx, sz), new Point(ex, ez));
    }

    /**
     * 寻路路径
     *
     * @param r
     * @param sx
     * @param sz
     * @param ex
     * @param ez
     * @return
     */
    static public ArrayList<Point> findWay(int[][] r, int sx, int sz, int ex, int ez) {
        return findWay(r, new Point(sx, sz), new Point(ex, ez));
    }

    /**
     * 寻路路径
     *
     * @param r
     * @param pa
     * @param pb
     * @return
     */
    static public ArrayList<Point> findWay(int[][] r, Point pa, Point pb) {
        //如果点超出范围，或者是阻挡点
        if (0 < pb.getX() && pb.getX() < r[0].length
                && 0 < pa.getX() && pa.getX() < r[0].length
                && 0 < pb.getY() && pb.getY() < r.length
                && 0 < pa.getY() && pa.getY() < r.length
                && !checkBlocking(r, pa)
                && !checkBlocking(r, pb)) {
            String key = pb.getX() + "-" + pb.getY();
            //开启列表
            HashMap<String, Point> Open_List = new HashMap<>();
            //关闭列表
            HashMap<String, Point> Close_List = new HashMap<>();

            Open_List.put(pa.getKey(), pa);
            while (!(checkInOpenList(key, Open_List) || Open_List.isEmpty())) {
                Point p0 = getMinFFromOpenList(Open_List);
                if (p0 == null) {
                    return null;
                }
                Open_List.remove(p0.getKey());
                Close_List.put(p0.getKey(), p0);
                checkP8(p0, r, pa, pb, Open_List, Close_List);
            }
            Point p = getPointFromOpenList(key, Open_List);
            return reverse(p);
        }
        return null;
    }
    // </editor-fold>

    static ArrayList<Point> reverse(Point point) {
        ArrayList<Point> points = new ArrayList<>();
        Point current = point;
        while (current != null) {
            points.add(new Point(current));
            current = current.getNext();
        }
        //反转
        Collections.reverse(points);
//        return points;
        ArrayList<Point> rets = new ArrayList<>();

        //当前方向
        int vc = -1;
        //下一个方向
        int tmpvc = -1;
        int forcunt = points.size();
        for (int i = 0; i < forcunt; i++) {
            //当是第一个点位或者最后一二点位无论如何都要发送
            Point next = points.get(i);
            if (i < forcunt - 1) {
                //当中间点位和下一个点位方向不一样的时候就要发
                Point next1 = points.get(i + 1);
                //当前点位和下一个点位如果转向
                tmpvc = MoveUtil.getVector8(next.getX(), next.getY(), next1.getX(), next1.getY());
            }
            if (i == 0 || (i == forcunt - 1) || (i > 0 && vc != tmpvc)) {
                next.setDir(vc);
                rets.add(next);
            }
            vc = tmpvc;
        }
        return rets;
    }

    public static boolean checkBlocking(int[][] mapBlock, Point point) {
        return MapBlock.checkConst(mapBlock, point.getX(), point.getY(), ConstBlock);
    }
    // </editor-fold>

    // <editor-fold desc="打印地图阻挡信息 public static void printMap(byte[][] r)">
    /**
     * 打印地图阻挡信息
     *
     * @param r
     */
    public static void printMap(int[][] r) {
        for (int y = 0; y < r.length; y++)//Y轴
        {
            for (int x = 0; x < r[0].length; x++)//X轴
            {
                System.out.print(r[y][x]);
            }
            System.out.print("\n");
        }
    }
    // </editor-fold>

    // <editor-fold desc="打印路径 static public void printWay(byte[][] r, Point way)">
    /**
     * 打印路径
     *
     * @param r
     * @param way
     */
    static public void printWay(int[][] r, ArrayList<Point> way) {
        for (int y = 0; y < r.length; y++)//Y轴
        {
            for (int x = 0; x < r[0].length; x++)//X轴
            {
                if (check(x, y, way)) {
                    System.out.print('X');
                } else {
                    System.out.print(r[y][x]);
                }
            }
            System.out.print("\n");
        }
    }

    static boolean check(int x, int y, ArrayList<Point> way) {
        if (way != null) {
            for (Point f : way) {
                if (f.getX() == x && f.getY() == y) {
                    return true;
                }
            }
        }
        return false;
    }
    // </editor-fold>

}
