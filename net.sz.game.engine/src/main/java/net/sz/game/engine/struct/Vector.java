package net.sz.game.engine.struct;

import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * 表示朝向，位移量
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Vector implements Serializable {

    private static final Logger log = Logger.getLogger(Vector.class);
    private static final long serialVersionUID = -8252572890329345857L;

    /*表示当前朝向修正值 0 - 11 包含*/
    private int dir;
    /*表示未修正的x方向正负位移量 只能是1或者-1*/
    private int dir_x;
    /*表示未修正的y方向正负位移量 只能是1或者-1*/
    private int dir_y;
    /*表示未修正的z方向正负位移量 只能是1或者-1*/
    private int dir_z;
    /*在x轴方向位移 偏移量 >=0 */
    @Deprecated
    private double vrx;
    /*在z轴方向的位移 偏移量 >=0*/
    @Deprecated
    private double vrz;
    /*角 a 度数 0 - 90 包含*/
    private double atan;
    /*角 a 度数 0 ~ 360° 不包含 360*/
    private double atan360;

    public Vector() {
    }

    public Vector(Vector vector) {
        this.dir = vector.dir;
        this.dir_x = vector.dir_x;
        this.dir_y = vector.dir_y;
        this.dir_z = vector.dir_z;
        this.atan = vector.atan;
        this.atan360 = vector.atan360;
        this.vrx = vector.vrx;
        this.vrz = vector.vrz;
    }

    public void copyVector(Vector vector) {
        this.dir = vector.dir;
        this.dir_x = vector.dir_x;
        this.dir_y = vector.dir_y;
        this.dir_z = vector.dir_z;
        this.atan = vector.atan;
        this.atan360 = vector.atan360;
        this.vrx = vector.vrx;
        this.vrz = vector.vrz;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getDir_x() {
        return dir_x;
    }

    public void setDir_x(int dir_x) {
        this.dir_x = dir_x;
    }

    public int getDir_y() {
        return dir_y;
    }

    public void setDir_y(int dir_y) {
        this.dir_y = dir_y;
    }

    public int getDir_z() {
        return dir_z;
    }

    public void setDir_z(int dir_z) {
        this.dir_z = dir_z;
    }

    public double getAtan() {
        return atan;
    }

    public void setAtan(double atan) {
        this.atan = atan;
    }

    public double getAtan360() {
        return atan360;
    }

    public void setAtan360(double atan360) {
        this.atan360 = atan360;
    }

    @Deprecated
    public double getVrx() {
        return vrx;
    }

    @Deprecated
    public void setVrx(double vrx) {
        this.vrx = vrx;
    }

    @Deprecated
    public double getVrz() {
        return vrz;
    }

    @Deprecated
    public void setVrz(double vrz) {
        this.vrz = vrz;
    }

    @Override
    public String toString() {
        return "dir=" + dir + ", dir_x=" + dir_x + ", dir_z=" + dir_z + ", atan=" + atan + ", atan360=" + atan360;
    }

}
