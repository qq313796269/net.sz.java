package net.sz.framework.way.astar;

import net.sz.framework.utils.MoveUtil;

import net.sz.framework.szlog.SzLogger;

/**
 * 寻路用的点位
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Point {

    private static final SzLogger log = SzLogger.getLogger();
    private int x;
    private int y;
    /// <summary>
    /// 代价总和
    /// </summary>
    private int F;
    private Point Next;
    private int g;
    private int h;
    // 0：→， 1：↗， 2：↑， 3：↖， 4：←， 5：↙， 6：↓， 7：↘
    private int dir;

    public Point() {
    }

    public Point(Point p) {
        this(p.x, p.y, p.dir);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y) {
        this.x = MoveUtil.seat(x);
        this.y = MoveUtil.seat(y);
    }

    public Point(int x, int y, int dir) {
        this.y = y;
        this.x = x;
        this.dir = dir;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public Point getNext() {
        return Next;
    }

    public void setNext(Point Next) {
        this.Next = Next;
    }

    /**
     * 从起点到当前点的代价
     *
     * @return
     */
    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
        this.F = this.g + this.h;
    }

    /**
     * 从终点到当前点的代价
     *
     * @return
     */
    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
        this.F = this.g + this.h;
    }

    public int getF() {
        return F;
    }

    public void setF(int F) {
        this.F = F;
    }

    public String getKey() {
        return this.x + "-" + this.y;
    }

    /**
     * 获取真实坐标信息,是以坐标格子信息的格子中心点实际坐标
     *
     * @return
     */
    public Position getPosition() {
        Position position = new Position(getPosition_X(), getPosition_Z());
        return position;
    }

    /**
     * 获取真实坐标信息,是以坐标格子信息的格子中心点实际坐标
     *
     * @return
     */
    public float getPosition_X() {
        //四舍五入的保留两位小时
        return (float) MoveUtil.position(x);
    }

    /**
     * 获取真实坐标信息,是以坐标格子信息的格子中心点实际坐标
     *
     * @return
     */
    public float getPosition_Z() {
        //四舍五入的保留两位小时
        return (float) MoveUtil.position(y);
    }

    @Override

    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.y;
        hash = 67 * hash + this.x;
        hash = 67 * hash + this.dir;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (this.y != other.y) {
            return false;
        }
        if (this.x != other.x) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{" + "x:" + x + ", y:" + y + '}';
    }

}
