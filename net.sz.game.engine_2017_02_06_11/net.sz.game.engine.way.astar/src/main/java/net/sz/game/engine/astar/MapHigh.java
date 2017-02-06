package net.sz.game.engine.astar;

import net.sz.game.engine.utils.MoveUtil;
import org.apache.log4j.Logger;

/**
 * 地图阻挡，u3d的阻挡是左下角开始，右上角结束，
 * <br>
 * 我们的阻挡是左上角开始右下角结束，地图和阻挡看上去是上下翻转
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapHigh implements Cloneable {

    public static void main(String[] args) {

        MapHigh mapBlock = new MapHigh(82, 2, 2);
        mapBlock.getMapBlock()[0][0] = 1;
        MapHigh clone = mapBlock.clone();
        clone.getMapBlock()[0][1] = 1;
        System.out.println(clone.getMapBlock()[0][1]);
    }

    private static final Logger log = Logger.getLogger(MapHigh.class);
    //地图模版
    private int mapid;
    //宽度
    private int width;
    //高度
    private int high;

    // 地图信息 1为阻挡 2为安全区
    private int[][] mapBlock;

    public MapHigh(int mapid, int width, int high) {
        this.mapid = mapid;
        this.width = width;
        this.high = high;
        this.mapBlock = new int[width][high];
    }

    public int getMapid() {
        return mapid;
    }

    public void setMapid(int mapid) {
        this.mapid = mapid;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int[][] getMapBlock() {
        return mapBlock;
    }

    public void setMapBlock(int[][] mapBlock) {
        this.mapBlock = mapBlock;
    }

    /**
     * 是否是阻挡点
     *
     * @param p
     * @return
     */
    public boolean isBlock(Position p) {
        return isBlock(p.seatX(), p.seatY());
    }

    /**
     * 是否是阻挡点
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isBlock(int x, int y) {
        return checkConst(mapBlock, x, y, Move.ConstBlock);
    }

    /**
     * 是否是阻挡点
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isBlock(float x, float y) {
        return checkConst(mapBlock, MoveUtil.seat(x), MoveUtil.seat(y), Move.ConstBlock);
    }

    /**
     * 是否是阻挡点
     *
     * @param p
     * @return
     */
    public boolean isSafe(Position p) {
        return isSafe(p.seatX(), p.seatY());
    }

    /**
     * 是否是安全区
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isSafe(int x, int y) {
        return checkConst(mapBlock, x, y, Move.ConstSafe);
    }

    /**
     *
     * @param rs
     * @param x
     * @param y
     * @param _const
     * @return
     */
    public static boolean checkConst(int[][] rs, int x, int y, int _const) {
        if (x <= 0 || y <= 0 || y >= rs.length || x >= rs[0].length) {
            return true;
        }
        return rs[y][x] == _const;
    }

    @Override
    public MapHigh clone() {
        try {
            MapHigh mapBlock = (MapHigh) super.clone();
            mapBlock.mapBlock = new int[this.width][this.high];
            for (int i = 0; i < this.high; i++) {
                for (int j = 0; j < this.width; j++) {
                    mapBlock.mapBlock[i][j] = this.mapBlock[i][j];
                }
            }
            return mapBlock;
        } catch (Exception e) {
        }
        return null;
    }

}
