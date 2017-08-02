package net.sz.framework.way.astar;

import java.io.Serializable;
import net.sz.framework.utils.MoveUtil;

import net.sz.framework.szlog.SzLogger;

/**
 * 程序使用的坐标位置表示
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Position implements Serializable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -3638649152105184342L;

    //我们程序的阻挡和客户端阻挡是上下翻转的，所有发送同步朝向给客户端的时候也是上下翻转
    //方向
    private int vector;
    //和客户端一致的方向
    private int clientVector;

    private float x;
    private float z;
//    private float z;

    public Position() {
    }

    public Position(Position source) {
        this.x = source.x;
        this.z = source.z;
//        this.z = source.z;
    }

    public Position(float x, float y) {
        this.x = x;
        this.z = y;
    }

//    public Position(float x, float z, float z) {
//        this.x = x;
//        this.z = z;
//        this.z = z;
//    }
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
//        new Exception().printStackTrace();
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
//        new Exception().printStackTrace();
    }

//    public float getZ() {
//        return z;
//    }
//
//    public void setZ(float z) {
//        this.z = z;
//    }
    public int getVector() {
        return vector;
    }

    public void setVector(int vector) {
        this.vector = vector;
    }

    public int getClientVector() {
        return clientVector;
    }

    public void setClientVector(int clientVector) {
        this.clientVector = clientVector;
    }

    /**
     * 根据实际坐标点，阻挡格子
     *
     * @return
     */
    public Point seat() {
        return new Point(seatX(), seatY());
    }

    public int seatX() {
        return MoveUtil.seat(x);
    }

    public int seatY() {
        return MoveUtil.seat(z);
    }

    public boolean equal(Position pos) {
        return this.x == pos.getX() && this.z == pos.getZ();
    }

    @Override
    public String toString() {
        return "[x:" + x + ",z:" + z + ",seatX:" + seatX() + ",seatY:" + seatY() + "]";
    }

    /**
     * 计算2点之间距离
     *
     * @param p1
     * @param p2
     * @return
     */
    public static double distance(Position p1, Position p2) {
        return distance(p1.x, p1.z, p2.x, p2.z);
    }

    /**
     * 计算2点之间距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance(float x1, float y1, float x2, float y2) {
        float gridx = x1 - x2;
        float gridy = y1 - y2;
        return Math.sqrt(gridx * gridx + gridy * gridy);
    }

}
