package net.sz.framework.way.astar;

import java.io.Serializable;
import java.util.Random;
import net.sz.framework.utils.MoveUtil;

/**
 * 方向
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Vector3 implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    public static final float GAILV = 0.7071068f;

    private float x;
    // private float y;
    private float z;

    public Vector3() {
    }

    public Vector3(float x, float z) {
        this.x = x;
        this.z = z;
    }

    public Vector3(float x, float z, byte dir) {
        this.x = x;
        this.z = z;
    }

    public Vector3(Vector3 vector3) {
        this.x = vector3.getX();
        this.z = vector3.getZ();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Float.floatToIntBits(this.x);
        hash = 29 * hash + Float.floatToIntBits(this.z);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector3 other = (Vector3) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        return Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }

    @Override
    public String toString() {
        return "x=" + x + ", z=" + z;
    }

}
