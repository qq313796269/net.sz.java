/**
 * 特别鸣谢修仙项目组负责人吴章义
 */
package net.sz.game.engine.navmesh.path;

import net.sz.game.engine.navmesh.Vector3;

/**
 * 寻路数据
 *
 * @author wzyi
 * @QQ 156320312
 * @Te 18202020823
 */
public class NavMeshData {

    private int[] blockTriangles;
    private Vector3[] blockVertices;

    private int[] pathTriangles;
    private Vector3[] pathVertices;

    private float startX;
    private float startZ;
    private float endX;
    private float endZ;
    private int mapID;

    public NavMeshData() {
    }

    /**
     * @return the blockTriangles
     */
    public int[] getBlockTriangles() {
        return blockTriangles;
    }

    /**
     * @param blockTriangles the blockTriangles to set
     */
    public void setBlockTriangles(int[] blockTriangles) {
        this.blockTriangles = blockTriangles;
    }

    /**
     * @return the blockVertices
     */
    public Vector3[] getBlockVertices() {
        return blockVertices;
    }

    /**
     * @param blockVertices the blockVertices to set
     */
    public void setBlockVertices(Vector3[] blockVertices) {
        this.blockVertices = blockVertices;
    }

    /**
     * @return the pathTriangles
     */
    public int[] getPathTriangles() {
        return pathTriangles;
    }

    /**
     * @param pathTriangles the pathTriangles to set
     */
    public void setPathTriangles(int[] pathTriangles) {
        this.pathTriangles = pathTriangles;
    }

    /**
     * @return the pathVertices
     */
    public Vector3[] getPathVertices() {
        return pathVertices;
    }

    /**
     * @param pathVertices the pathVertices to set
     */
    public void setPathVertices(Vector3[] pathVertices) {
        this.pathVertices = pathVertices;
    }

    /**
     * @return the mapID
     */
    public int getMapID() {
        return mapID;
    }

    /**
     * @param mapID the mapID to set
     */
    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    /**
     * @return the startX
     */
    public float getStartX() {
        return startX;
    }

    /**
     * @param startX the startX to set
     */
    public void setStartX(float startX) {
        this.startX = startX;
    }

    /**
     * @return the startZ
     */
    public float getStartZ() {
        return startZ;
    }

    /**
     * @param startZ the startZ to set
     */
    public void setStartZ(float startZ) {
        this.startZ = startZ;
    }

    /**
     * @return the endX
     */
    public float getEndX() {
        return endX;
    }

    /**
     * @param endX the endX to set
     */
    public void setEndX(float endX) {
        this.endX = endX;
    }

    /**
     * @return the endZ
     */
    public float getEndZ() {
        return endZ;
    }

    /**
     * @param endZ the endZ to set
     */
    public void setEndZ(float endZ) {
        this.endZ = endZ;
    }

}
