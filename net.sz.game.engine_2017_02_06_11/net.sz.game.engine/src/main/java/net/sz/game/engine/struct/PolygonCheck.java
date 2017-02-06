package net.sz.test;

import net.sz.game.engine.utils.BitUtil;

/**
 * 任意多边形，
 *
 */
public class PolygonCheck {

    public static void main(String[] args) {
        double px = 2.901;
        double py = 3.001;
        PolygonCheck polygonCheck = new PolygonCheck(4);
        polygonCheck.add(2, 2);
        polygonCheck.add(3, 3);
        polygonCheck.add(3, 5);
        polygonCheck.add(1, 4);
        System.out.println(polygonCheck.isInPolygon(px, py));
    }

    /*多边形的顶点*/
    double[] pointXs;
    double[] pointZs;
    /*当前已经添加的坐标点*/
    int pointCount = 0;

    /**
     *
     * @param size 多边形的顶点数
     */
    public PolygonCheck(int size) {
        pointXs = new double[size];
        pointZs = new double[size];
    }

    /**
     *
     * @param x 坐标点
     * @param z 坐标点
     */
    public void add(double x, double z) {
        add(pointCount, x, z);
        pointCount++;
    }

    /**
     *
     * @param index 当前索引
     * @param x 坐标点
     * @param z 坐标点
     */
    public void add(int index, double x, double z) {
        if (0 <= index && index < pointXs.length) {
            pointXs[index] = BitUtil.getDouble2(x);
            pointZs[index] = BitUtil.getDouble2(z);
        } else {
            throw new UnsupportedOperationException("index out of");
        }
    }

    /**
     * 判断点是否在多边形内 <br>
     * ----------原理---------- <br>
     * 注意到如果从P作水平向左的射线的话，如果P在多边形内部，那么这条射线与多边形的交点必为奇数，<br>
     * 如果P在多边形外部，则交点个数必为偶数(0也在内)。<br>
     *
     * @param x 要判断的点
     * @param z 要判断的点
     * @return
     */
    public boolean isInPolygon(double x, double z) {
        boolean inside = false;
        double p1x = 0, p1z = 0, p2x = 0, p2z = 0;

        for (int i = 0, j = pointCount - 1; i < pointCount; j = i, i++) {
            /*第一个点和最后一个点作为第一条线，之后是第一个点和第二个点作为第二条线，之后是第二个点与第三个点，第三个点与第四个点...*/
            p1x = pointXs[i];
            p1z = pointZs[i];

            p2x = pointXs[j];
            p2z = pointZs[j];

            if (z < p2z) {/*p2在射线之上*/
                if (p1z <= z) {/*p1正好在射线中或者射线下方*/
                    if ((z - p1z) * (p2x - p1x) >= (x - p1x) * (p2z - p1z))/*斜率判断,在P1和P2之间且在P1P2右侧*/ {
                        /*射线与多边形交点为奇数时则在多边形之内，若为偶数个交点时则在多边形之外。
                        由于inside初始值为false，即交点数为零。所以当有第一个交点时，则必为奇数，则在内部，此时为inside=(!inside)
                        所以当有第二个交点时，则必为偶数，则在外部，此时为inside=(!inside)*/
                        inside = (!inside);
                    }
                }
            } else if (z < p1z) {
                /*p2正好在射线中或者在射线下方，p1在射线上*/
                if ((z - p1z) * (p2x - p1x) <= (x - p1x) * (p2z - p1z))/*斜率判断,在P1和P2之间且在P1P2右侧*/ {
                    inside = (!inside);
                }
            }
        }
        return inside;
    }
}
