package net.sz.framework.utils;

import net.sz.framework.struct.PolygonCheck;
import net.sz.framework.struct.Vector;

/**
 * 移动和工具辅助函数
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MoveUtil {

    /**
     * 一个格子的大小
     */
    public static int Const_Area_Width = 5;

    /**
     * 一个格子的大小
     */
    public static int Const_Area_Height = 5;

    /**
     * 同步区域 格子数
     */
    public static int Const_Round_Width = 2;

    /**
     * 同步区域 格子数
     */
    public static int Const_Round_Height = 2;

    /**
     * 同步区域 格子数
     */
    public static int Const_Round_Width_Max = 5;

    /**
     * 同步区域 格子数
     */
    public static int Const_Round_Height_Max = 5;

    /**
     * 阻挡格子，二维阻挡表
     */
    public static float Const_Block_Area = 0.5f;

    /**
     * 二维阻挡表 半个格子
     */
    public static float Const_Block_Area_2 = Const_Block_Area / 2;

    /**
     * 标准速度是 3000
     */
    static public float Const_SPEED = 3000;
    /**
     * 每一秒钟的偏移量 3.0f
     */
    static public float Const_VR = 3.0f;
//    /**
//     * 每一次移动的距离 0.3f
//     */
//    static public float SPEED100ms = 0.3f; //ConstArea;
//
//    /**
//     * 标准1秒钟3米的移动速度，
//     */
//    static public int GRID = (int) (Const_VR / SPEED100ms) + (Const_VR % SPEED100ms == 0 ? 0 : 1);
//    /**
//     * 检测范围
//     */
//    static public float SPEED100_2ms = 0.15f; //SPEED100ms / 2;

    /**
     * 从0开始
     *
     * @param _p
     * @return
     */
    static public int seat(double _p) {
        return seat(_p, Const_Block_Area);
    }

    /**
     * 从0开始的
     *
     * @param _p
     * @param _r
     * @return
     */
    static public int seat(double _p, double _r) {
        //四舍五入取整数 二维数组的下标需要 -1
        return ((int) (_p / _r)) + (_p % _r > 0 ? 1 : 0) - 1;
    }

    /**
     * 从0开始
     *
     * @param _p
     * @return
     */
    static public double position(int _p) {
        return position(_p, Const_Block_Area);
    }

    /**
     * 从0开始的
     *
     * @param _p
     * @param _r
     * @return
     */
    static public double position(int _p, double _r) {
        //四舍五入取整数 二维数组的下标需要 -1
        return _p * _r + _r / 2;
    }

    // <editor-fold desc="获取方向 static public byte getVector8(double x1, double z1, double x2, double z2)">
    /**
     * 获取方向
     * <br>
     * 根据特点，0方向是y轴正方向，顺时针移动
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    static public byte getVector8(double x1, double z1, double x2, double z2) {
        Vector v12Vector = getV12Vector(x1, z1, x2, z2);
        double aTan360 = v12Vector.getAtan360();
        byte vector = 0;
        if (22.5 < aTan360 && aTan360 <= 67.5) {
            /* ↘ */
            vector = 3;
        } else if (aTan360 <= 112.5) {
            /* ↓ */
            vector = 4;
        } else if (aTan360 <= 157.5) {
            /* ↙ */
            vector = 5;
        } else if (aTan360 <= 202.5) {
            /* ← */
            vector = 6;
        } else if (aTan360 <= 247.5) {
            /* ↖ */
            vector = 7;
        } else if (aTan360 <= 292.5) {
            /* ↑ */
            vector = 0;
        } else if (aTan360 <= 337.5) {
            /* ↗ */
            vector = 1;
        } else {
            /* → */
            vector = 2;
        }
        return vector;
    }
    // </editor-fold>

    // <editor-fold desc="位移时的z轴 static float getV8Z(int vector, double offset)">
    public static double getV8Z(double offset, double sin) {
        double sinr = offset * Math.sin(Math.toRadians(sin));
        return BitUtil.getDouble4(sinr);
    }
    // </editor-fold>

    // <editor-fold desc="位移时的X轴 static float getV8X(int vector, double offset)">
    /**
     *
     * @param offset 位移量
     * @param cos 角度
     * @return
     */
    public static double getV8X(double offset, double cos) {
        double sinr = offset * Math.cos(Math.toRadians(cos));
        return BitUtil.getDouble4(sinr);
    }
    //</editor-fold>

    /**
     * 获取两个坐标点的朝向(x1z1坐标点根据x2z2坐标点获得朝向)
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static Vector getV12Vector(double x1, double z1, double x2, double z2) {
        Vector vector = new Vector();
        getV12Vector(vector, x1, z1, x2, z2);
        return vector;
    }

    /**
     * 获取两个坐标点的朝向
     *
     * @param vector
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     */
    public static void getV12Vector(Vector vector, double x1, double z1, double x2, double z2) {
//        log.error("切换坐标点", new Exception());
        vector.setAtan(getATan(x1, z1, x2, z2));
        vector.setDir(_getVector12(vector.getAtan(), x1, z1, x2, z2));
        vector.setDir_x(getVector12_x(x1, x2));
        vector.setDir_z(getVector12_z(z1, z2));
        vector.setAtan360(getATan360ByaTan(vector.getAtan(), vector.getDir(), vector.getDir_x(), vector.getDir_z()));
    }

    /**
     * 获取两个坐标点的朝向
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double getATan360(double x1, double z1, double x2, double z2) {
        double aTan = getATan(x1, z1, x2, z2);
        byte _getVector12 = _getVector12(aTan, x1, z1, x2, z2);
        byte vector12_x = getVector12_x(x1, x2);
        byte vector12_z = getVector12_z(z1, z2);
        return getATan360ByaTan(aTan, _getVector12, vector12_x, vector12_z);
    }

    /**
     * 朝向是有修正，在修正下真实朝向，有正负区分
     *
     * @param z1
     * @param z2
     * @return
     */
    static public byte getVector12_z(double z1, double z2) {
        byte vector = 1;
        if (z1 > z2) {
            /*表示z方向递减*/
            vector = -1;
        }
        return vector;
    }

    /**
     * 朝向是有修正，在修正下真实朝向，有正负区分
     *
     * @param x1
     * @param x2
     * @return
     */
    static public byte getVector12_x(double x1, double x2) {
        byte vector = 1;
        if (x1 > x2) {
            /*表示x方向递减*/
            vector = -1;
        }
        return vector;
    }

    /**
     * 位移是z轴
     *
     * @param offset
     * @param sin
     * @return
     */
    public static double getV12ZD(double offset, double sin) {
        offset = Math.abs(offset);
        /* 三角函数计算器 */
        double sinr = (offset * Math.sin(Math.toRadians(sin)));
        /* 拿到保留4位小数计算器 */
        return BitUtil.getDouble4(sinr);
    }

    /**
     * 位移时的X轴
     *
     * @param offset
     * @param cos
     * @return
     */
    public static double getV12XD(double offset, double cos) {
        offset = Math.abs(offset);
        /* 三角函数计算器 */
        double cosr = (offset * Math.cos(Math.toRadians(cos)));
        /* 拿到保留4位小数计算器 */
        return BitUtil.getDouble4(cosr);
    }

    //<editor-fold desc="获取角度 public static int getV12ATan(double x1, double y1, double x2, double y2)">
    public static double getATan(double x1, double z1, double x2, double z2) {
        //正切（tan）等于对边比邻边；tanA=a/b
        double a = 0;
        if (x1 == x2) {
            //x坐标相同的情况表示正上或者正下方移动
            a = 90;
        } else if (z1 != z2) {
            //三角函数的角度计算
            double ta = Math.abs(z1 - z2) / Math.abs(x1 - x2);
            double atan = Math.atan(ta);
            a = BitUtil.getDouble4(Math.toDegrees(atan));
        }
        return a;
    }
    //</editor-fold>

    /**
     *
     * @param atan360 360° 角度，
     * @return
     */
    public static Vector getVectorBy360Atan(double atan360) {
        Vector vector = new Vector();
        getVectorBy360Atan(vector, atan360);
        return vector;
    }

    /**
     *
     * @param vector
     * @param atan360 360° 角度，
     */
    public static void getVectorBy360Atan(Vector vector, double atan360) {
        vector.setAtan360(atan360);
        setAtan360(vector);
    }

    /**
     * 根据360度算出各种朝向问题
     *
     * @param vector
     */
    public static void setAtan360(Vector vector) {
        double atan360 = vector.getAtan360();
        if (0 <= atan360 && atan360 <= 15) {
            vector.setDir(0);
            vector.setDir_x(1);
            vector.setDir_z(1);
            vector.setAtan(90 - atan360);
        } else if (15 < atan360 && atan360 <= 45) {
            vector.setDir(1);
            vector.setDir_x(1);
            vector.setDir_z(1);
            vector.setAtan(90 - atan360);
        } else if (45 < atan360 && atan360 <= 75) {
            vector.setDir(2);
            vector.setDir_x(1);
            vector.setDir_z(1);
            vector.setAtan(90 - atan360);
        } else if (75 < atan360 && atan360 <= 90) {
            vector.setDir(3);
            vector.setDir_x(1);
            vector.setDir_z(1);
            vector.setAtan(90 - atan360);
        } else if (90 < atan360 && atan360 <= 105) {
            vector.setDir(3);
            vector.setDir_x(1);
            vector.setDir_z(-1);
            vector.setAtan(atan360 - 90);
        } else if (105 < atan360 && atan360 <= 135) {
            vector.setDir(4);
            vector.setDir_x(1);
            vector.setDir_z(-1);
            vector.setAtan(atan360 - 90);
        } else if (135 < atan360 && atan360 <= 165) {
            vector.setDir(5);
            vector.setDir_x(1);
            vector.setDir_z(-1);
            vector.setAtan(atan360 - 90);
        } else if (165 < atan360 && atan360 <= 180) {
            vector.setDir(6);
            vector.setDir_x(1);
            vector.setDir_z(-1);
            vector.setAtan(atan360 - 90);
        } else if (180 < atan360 && atan360 <= 195) {
            vector.setDir(6);
            vector.setDir_x(-1);
            vector.setDir_z(-1);
            vector.setAtan(270 - atan360);
        } else if (195 < atan360 && atan360 <= 225) {
            vector.setDir(7);
            vector.setDir_x(-1);
            vector.setDir_z(-1);
            vector.setAtan(270 - atan360);
        } else if (225 < atan360 && atan360 <= 255) {
            vector.setDir(8);
            vector.setDir_x(-1);
            vector.setDir_z(-1);
            vector.setAtan(270 - atan360);
        } else if (255 < atan360 && atan360 <= 270) {
            vector.setDir(9);
            vector.setDir_x(-1);
            vector.setDir_z(1);
            vector.setAtan(270 - atan360);
        } else if (270 < atan360 && atan360 <= 285) {
            vector.setDir(9);
            vector.setDir_x(-1);
            vector.setDir_z(1);
            vector.setAtan(atan360 - 270);
        } else if (285 < atan360 && atan360 <= 315) {
            vector.setDir(10);
            vector.setDir_x(-1);
            vector.setDir_z(1);
            vector.setAtan(atan360 - 270);
        } else if (315 < atan360 && atan360 <= 345) {
            vector.setDir(11);
            vector.setDir_x(-1);
            vector.setDir_z(1);
            vector.setAtan(atan360 - 270);
        } else if (345 < atan360) {
            vector.setDir(0);
            vector.setDir_x(-1);
            vector.setDir_z(1);
            vector.setAtan(atan360 - 270);
        }
    }

    /**
     * 当前角度加上修正角度，并且换算360
     *
     * @param atan360
     * @param tmptan
     * @return
     */
    public static double getATan360(double atan360, double tmptan) {
        atan360 += tmptan;
        if (atan360 < 0) {
            atan360 = 360 + atan360;
        }
        return BitUtil.getDouble4(atan360 % 360);
    }

    /**
     * 获取方向
     * <br>
     * 根据特点，0方向是y轴正方向，顺时针移动
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    static private byte _getVector12(double atan, double x1, double y1, double x2, double y2) {
        byte vector = 0;
        if (0 <= atan && atan <= 15) {
            if (x1 > x2) {
                vector = 9;
            } else {
                vector = 3;
            }
        } else if (15 < atan && atan <= 45) {
            if (x1 < x2) {
                if (y1 < y2) {
                    vector = 2;
                } else {
                    vector = 4;
                }
            } else if (y1 < y2) {
                vector = 10;
            } else if (y1 > y2) {
                vector = 8;
            }
        } else if (45 < atan && atan <= 75) {
            if (x1 < x2) {
                if (y1 < y2) {
                    vector = 1;
                } else {
                    vector = 5;
                }
            } else if (y1 < y2) {
                vector = 11;
            } else if (y1 > y2) {
                vector = 7;
            }
        } else if (y1 > y2) {
            vector = 6;
        } else {
            vector = 0;
        }

        return vector;
    }

    /**
     * 根据0-90朝向角度，计算360°
     *
     * @param aTan 0 - 90 度
     * @param vector12
     * @param vx
     * @param vz
     * @return
     */
    public static double getATan360ByaTan(double aTan, int vector12, int vx, int vz) {
        switch (vector12) {
            case 0:
            case 1:
            case 2:
            case 3:
                if (vector12 == 0 && vx < 0) {
                    aTan = 360 - aTan;
                } else if (vector12 == 3 && vz < 0) {
                    aTan = 90 + aTan;
                } else {
                    aTan = 90 - aTan;
                }
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                if (vx > 0) {
                    aTan = 90 + aTan;
                } else {
                    aTan = 270 - aTan;
                }
                break;
            case 9:
            case 10:
            case 11:
                if (vz > 0) {
                    aTan = 270 + aTan;
                } else {
                    aTan = 270 - aTan;
                }
                break;
        }
        return aTan;
    }

    /**
     * 计算两点距离
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double distance(double x1, double z1, double x2, double z2) {
        x1 -= x2;
        z1 -= z2;
        return Math.sqrt(x1 * x1 + z1 * z1);
    }

    /**
     * 未开平方根的距离
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double distanceSq(double x1, double z1, double x2, double z2) {
        x1 -= x2;
        z1 -= z2;
        return (x1 * x1 + z1 * z1);
    }

    // <editor-fold desc="当前坐标点位中心点的等角（等边）三角形，当前朝向位A点顶点延伸 static public PolygonCheck getTriangle(Vector vector, double x, double z, double vr)">
    /**
     * 当前坐标点位中心点的等角（等边）三角形，当前朝向位A点顶点延伸
     *
     * @param vector
     * @param x
     * @param z
     * @param vr 中心点偏移位置
     * @param vr_width 三角形，中心点距离顶点距离
     * @return
     */
    static public PolygonCheck getTriangle(Vector vector, double x, double z, double vr, double vr_width) {

        if (vr != 0) {
            /* 根据三角函数计算出 中心点 偏移量 */
            double v12_V_X = 0;
            double v12_V_Y = 0;
            if (vr < 0) {
                /* 传入负数的时候方向刚好是相反方向运动 */
                v12_V_X = -1 * vector.getDir_x() * getV12XD(vr, vector.getAtan());
                v12_V_Y = -1 * vector.getDir_z() * getV12ZD(vr, vector.getAtan());
            } else {
                /* 正前方移动 */
                v12_V_X = vector.getDir_x() * getV12XD(vr, vector.getAtan());
                v12_V_Y = vector.getDir_z() * getV12ZD(vr, vector.getAtan());
            }
            x += v12_V_X;
            z += v12_V_Y;
        }

        Vector bVector = getVectorBy360Atan(getATan360(vector.getAtan360(), 120));
        Vector cVector = getVectorBy360Atan(getATan360(vector.getAtan360(), 240));

        double ax = x + (vector.getDir_x() * getV12XD(vr, vector.getAtan()));
        double az = z + (vector.getDir_z() * getV12ZD(vr, vector.getAtan()));

        double bx = x + (bVector.getDir_x() * getV12XD(vr, bVector.getAtan()));
        double bz = z + (bVector.getDir_z() * getV12ZD(vr, bVector.getAtan()));

        double cx = x + (cVector.getDir_x() * getV12XD(vr, cVector.getAtan()));
        double cz = z + (cVector.getDir_z() * getV12ZD(vr, cVector.getAtan()));

        PolygonCheck polygonCheck = new PolygonCheck(3);
        polygonCheck.add(ax, az);
        polygonCheck.add(bx, bz);
        polygonCheck.add(cx, cz);
        return polygonCheck;
    }
// </editor-fold>

    // <editor-fold desc="90°朝向矩形，以传入的坐标点为AB边中心点距离 static public PolygonCheck getRectangle(Vector vector, double x, double y, double vr, double vr_width, double vr_hight)">
    /**
     * 90°朝向矩形，以传入的坐标点为AB边中心点距离
     *
     * @param vector 当前朝向
     * @param x 当前坐标点
     * @param z 当前坐标点
     * @param offset 原点偏移量，AB编中心点90°偏移量 偏移，正前方（正数）或者正后方（负数）米数
     * @param vr_width 偏移量，矩形的宽度，左右各偏移0.2m直线是0.4m
     * @param vr_hight 偏移量高，矩形的长度
     * @return
     */
    static public PolygonCheck getRectangle(Vector vector, double x, double z, double offset, double vr_width, double vr_hight) {
        //宽度修正
        vr_width = vr_width / 2;

        Vector aVector = getVectorBy360Atan(getATan360(vector.getAtan360(), -90));
        Vector bVector = getVectorBy360Atan(getATan360(vector.getAtan360(), 90));

        if (offset != 0) {
            /* 根据三角函数计算出 中心点 偏移量 */
            double v12_V_X = 0;
            double v12_V_Y = 0;
            if (offset < 0) {
                /* 传入负数的时候方向刚好是相反方向运动 */
                v12_V_X = -1 * vector.getDir_x() * getV12XD(offset, vector.getAtan());
                v12_V_Y = -1 * vector.getDir_z() * getV12ZD(offset, vector.getAtan());
            } else {
                /* 正前方移动 */
                v12_V_X = vector.getDir_x() * getV12XD(offset, vector.getAtan());
                v12_V_Y = vector.getDir_z() * getV12ZD(offset, vector.getAtan());
            }
            x += v12_V_X;
            z += v12_V_Y;
        }

        /* 根据三角函数计算出 A 点偏移量 */
        double v12_A_X = aVector.getDir_x() * getV12XD(vr_width, aVector.getAtan());
        double v12_A_Y = aVector.getDir_z() * getV12ZD(vr_width, aVector.getAtan());
        /* 由于在计算12方向位移函数里面已经计算偏移量是正负值 */
        double A_X = x + v12_A_X;
        double A_Y = z + v12_A_Y;

        /* 根据三角函数计算出 B 点偏移量 */
        double v12_B_X = bVector.getDir_x() * getV12XD(vr_width, bVector.getAtan());
        double v12_B_Y = bVector.getDir_z() * getV12ZD(vr_width, bVector.getAtan());
        /* 由于在计算12方向位移函数里面已经计算偏移量是正负值 */
        double B_X = x + v12_B_X;
        double B_Y = z + v12_B_Y;

        /* 根据三角函数计算出 C 或者 D 点偏移量 */
        double v12_CD_X = vector.getDir_x() * getV12XD(vr_hight, vector.getAtan());
        double v12_CD_Y = vector.getDir_z() * getV12ZD(vr_hight, vector.getAtan());

        /* C 点应该是 B 点的垂直方向也就是原来玩家的移动方向 由于在计算12方向位移函数里面已经计算偏移量是正负值*/
        double C_X = B_X + v12_CD_X;
        double C_Y = B_Y + v12_CD_Y;
        /* D 点应该是 A 点的垂直方向也就是原来玩家的移动方向 由于在计算12方向位移函数里面已经计算偏移量是正负值*/
        double D_X = A_X + v12_CD_X;
        double D_Y = A_Y + v12_CD_Y;

        PolygonCheck polygonCheck = new PolygonCheck(4);
        polygonCheck.add(A_X, A_Y);
        polygonCheck.add(B_X, B_Y);
        polygonCheck.add(C_X, C_Y);
        polygonCheck.add(D_X, D_Y);
        return polygonCheck;
    }
    //</editor-fold>

    public static void main(String[] args) {
        double x1 = 110.11, z1 = 46.52, x2 = 114.90, z2 = 36.35;
        System.out.println(distance(x1, z1, x2, z2));
        Vector v12Vector = getV12Vector(x1, z1, x2, z2);
        System.out.println(v12Vector);
        for (int i = 0; i < 11; i++) {
            PolygonCheck rectangle = getRectangle(v12Vector, x1, z1, i, 3, 1);
            System.out.println(rectangle.contains(x2, z2));
//            System.out.println(rectangle.toString());
        }
    }
}
