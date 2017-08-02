package net.sz.framework.way.astar;

import net.sz.framework.utils.MoveUtil;
import net.sz.framework.utils.XmlUtil;

import net.sz.framework.szlog.SzLogger;

/**
 * 地图阻挡，u3d的阻挡是左下角开始，右上角结束，
 * <br>
 * 我们的阻挡是左上角开始右下角结束，地图和阻挡看上去是上下翻转
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapBlock implements Cloneable {

    public static void main(String[] args) throws Exception {
        String string = "C:\\Users\\troy_worker\\Desktop\\311.block.xml";
        MapBlockConfig mapBlockConfig = XmlUtil.readerSimpleXmlToFile(string, MapBlockConfig.class);
        MapBlock buildMapBlock = mapBlockConfig.buildMapBlock();
        if (log.isDebugEnabled()) {
            log.debug(buildMapBlock.getMapid());
        }
    }

    private static final SzLogger log = SzLogger.getLogger();
    //地图模版
    private int mapid;
    //宽度
    private int width;
    //高度
    private int high;

    // 地图信息 1为阻挡 2为安全区
    private int[][] mapBlock;

    public MapBlock(int mapid, int width, int high) {
        this.mapid = mapid;
        this.width = width;
        this.high = high;

        this.mapBlock = new int[high][width];
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

    public boolean isBlock(Point point) {
        return isBlock(point.getX(), point.getY());
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
    public MapBlock clone() {
        try {
            MapBlock mapBlock = (MapBlock) super.clone();
            mapBlock.mapBlock = new int[this.width][this.high];
            for (int i = 0; i < this.high; i++) {
                for (int j = 0; j < this.width; j++) {
                    mapBlock.mapBlock[i][j] = this.mapBlock[i][j];
                }
            }
            return mapBlock;
        } catch (Exception e) {
            log.error("创建阻挡信息副本出错", e);
        }
        return null;
    }

}
