/**
 * 特别鸣谢修仙项目组负责人吴章义
 */
package net.sz.game.engine.navmesh;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.util.Random;
import net.sz.game.engine.utils.MoveUtil;

/**
 *
 * @author Keith Woodward
 */
public class Vector3 implements Serializable {

    static public final Vector3 ZERO = new Vector3();
    static private final Random random = new Random();
    static public final float GAILV = 0.7071068f;

    static public final double radiansToDegrees = 180f / Math.PI;

    static public final double degreesToRadians = Math.PI / 180;

    public final static double TWO_PI = Math.PI * 2;

    public double x;
    public double y;
    public double z;

    public Vector3() {
    }

    /**
     * 三角函数的角度
     *
     * @param degree
     */
    public Vector3(double degree) {
        degree = 90 - degree;
        x = (float) Math.cos(degreesToRadians * degree);
        z = (float) Math.sin(degreesToRadians * degree);
    }

    public Vector3(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 old) {
        this.x = old.x;
        this.y = old.y;
        this.z = old.z;
    }

    public void copyVector3(Vector3 old) {
        this.x = old.x;
        this.y = old.y;
        this.z = old.z;
    }

    public static Vector3 newRandomVector3() {
        float x = random.nextFloat();
        float z = random.nextFloat();
        if (random.nextBoolean()) {
            x = 0 - x;
        }
        if (random.nextBoolean()) {
            z = 0 - z;
        }
        return new Vector3(x, z);
    }

    public void copy(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void copy(Vector3 old) {
        this.x = old.x;
        this.y = old.y;
        this.z = old.z;
    }

    public int seatX() {
        return MoveUtil.seat(x);
    }

    public int seatY() {
        return MoveUtil.seat(z);
    }

    public double getX() {
        return x;
    }

    public float xF() {
        return (float) x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public float yF() {
        return (float) y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public float zF() {
        return (float) z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setCoords(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public void setCoords(Vector3 p) {
        this.x = p.x;
        this.z = p.z;
    }

    public void translate(Vector3 pointIncrement) {
        translate(pointIncrement.x, pointIncrement.z);
    }

    /**
     *
     * @param xIncrement
     * @param zIncrement
     */
    public void translate(double xIncrement, double zIncrement) {
        this.x += xIncrement;
        this.z += zIncrement;
    }

    /**
     * 拷贝
     *
     * @param pointIncrement
     * @return
     */
    public Vector3 translateCopy(Vector3 pointIncrement) {
        Vector3 p = this.copy();
        p.translate(pointIncrement.x, pointIncrement.z);
        return p;
    }

    public Vector3 translateCopy(double xIncrement, double zIncrement) {
        Vector3 p = this.copy();
        p.translate(xIncrement, zIncrement);
        return p;
    }

    public void rotate(double angle, Vector3 center) {
        rotate(angle, center.x, center.z);
    }

    public void rotate(double angle, double xCenter, double zCenter) {
        double currentAngle;
        double distance;
        currentAngle = Math.atan2(z - zCenter, x - xCenter);
        currentAngle += angle;
        distance = Vector3.distance(x, z, xCenter, zCenter);
        x = xCenter + (distance * Math.cos(currentAngle));
        z = zCenter + (distance * Math.sin(currentAngle));
    }

    /**
     *
     * @param degrees unity角度
     * @param distance
     * @return
     */
    public Vector3 unityTranslate(double degrees, double distance) {
        Vector3 p = this.copy();
        double angle = unityDegreesToAngle(degrees);
        p.x = x + (distance * Math.cos(angle));
        p.z = z + (distance * Math.sin(angle));
        return p;
    }

    public Vector3 unityTranslate(Vector3 sourceDirection, double degrees, double distance) {
        return unityTranslate(sourceDirection.y + degrees, distance);
    }

    public boolean isInSector(Vector3 sourceDirection, Vector3 target, float radius, float degrees) {
        if (this.equals(target)) {
            return true;
        }
        double dis = this.distanceSq(target);
        Vector3 vt = target.sub(this);
        vt.y = 0;
        if (vt.isZero()) {
            return true;
        }
        double targetAngle = angle(vt, new Vector3(sourceDirection.y));
        return (dis <= radius * radius && targetAngle <= degrees / 2 && targetAngle >= -(degrees / 2));
    }

//    public boolean isINRect(List<Vector3> corners) {
//        if (corners.size()<4) {
//            return false;
//        }
//        double v0x = corners.get(0).x;
//        double v0z = corners.get(0).z;
//        double v1x = corners.get(1).x;
//        double v1z = corners.get(1).z;
//        double v2x = corners.get(2).x;
//        double v2z = corners.get(2).z;
//        double v3x = corners.get(3).x;
//        double v3z = corners.get(3).z;
//        return multiply(x, z, v0x, v0z, v1x, v1z) * multiply(x, z, v3x, v3z, v2x, v2z) <= 0 && multiply(x, z, v3x, v3z, v0x, v0z) * multiply(x, z, v2x, v2z, v1x, v1z) <= 0;
//    }
//
//    private static double multiply(double p1x, double p1z, double p2x, double p2z, double p0x, double p0z) {
//        return ((p1x - p0x) * (p2z - p0z) - (p2x - p0x) * (p1z - p0z));
//    }
    public Vector3 sub(final Vector3 a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z);
    }

    public Vector3 sub(double x, double y, double z) {
        Vector3 copy = this.copy();
        copy.x -= x;
        copy.y -= y;
        copy.z -= z;
        return copy;
    }

    public double distance2(Vector3 point) {
        final double a = point.x - x;
        final double b = point.y - y;
        final double c = point.z - z;
        return a * a + b * b + c * c;
    }

    public Vector3 rotateCopy(double angle, Vector3 center) {
        Vector3 p = this.copy();
        p.rotate(angle, center.x, center.z);
        return p;
    }

    public Vector3 rotateCopy(double angle, double xCenter, double zCenter) {
        Vector3 p = this.copy();
        double currentAngle = Math.atan2(p.z - zCenter, p.x - xCenter);
        currentAngle += angle;
        double distance = Vector3.distance(p.x, p.z, xCenter, zCenter);
        p.x = xCenter + (distance * Math.cos(currentAngle));
        p.z = zCenter + (distance * Math.sin(currentAngle));
        return p;
    }

    public boolean equals(Vector3 p) {
        if (Math.abs(x - p.x) < 0.01 && Math.abs(y - p.y) < 0.01 && Math.abs(z - p.z) < 0.01) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param scale
     */
    public void scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    /**
     * 计算两点距离
     *
     * @param p
     * @return
     */
    public double distance(Vector3 p) {
        return distance(this.x, this.z, p.x, p.z);
    }

    /**
     * 计算两点距离
     *
     * @param x2
     * @param z2
     * @return
     */
    public double distance(double x2, double z2) {
        return distance(this.x, this.z, x2, z2);
    }

    /**
     * 计算两点距离
     *
     * @param p
     * @param p2
     * @return
     */
    public static double distance(Vector3 p, Vector3 p2) {
        return distance(p.x, p.z, p2.x, p2.z);
    }

    /**
     * 计算两点距离
     *
     * @param p
     * @param x2
     * @param z2
     * @return
     */
    public static double distance(Vector3 p, double x2, double z2) {
        return distance(p.x, p.z, x2, z2);
    }

    /**
     * 计算两点距离
     *
     * @param x1
     * @param z1
     * @param p2
     * @return
     */
    public static double distance(double x1, double z1, Vector3 p2) {
        return distance(x1, z1, p2.x, p2.z);
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
     * @param p
     * @return
     */
    public double distanceSq(Vector3 p) {
        return distanceSq(this.x, this.z, p.x, p.z);
    }

    /**
     * 未开平方根的距离
     *
     * @param x2
     * @param z2
     * @return
     */
    public double distanceSq(double x2, double z2) {
        return distanceSq(this.x, this.z, x2, z2);
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

    public static boolean collinear(double x1, double z1, double x2, double z2, double x3, double y3) {
        double collinearityTest = x1 * (z2 - y3) + x2 * (y3 - z1) + x3 * (z1 - z2);	// see http://mathworld.wolfram.com/Collinear.html
        if (collinearityTest == 0) {
            return true;
        }
        return false;
    }

    public static boolean collinear(Vector3 p1, Vector3 p2, Vector3 p3) {
        return collinear(p1.x, p1.z, p2.x, p2.z, p3.x, p3.z);
    }

    public boolean collinear(Vector3 p1, Vector3 p2) {
        return collinear(x, z, p1.x, p1.z, p2.x, p2.z);
    }

    public boolean collinear(double x1, double z1, double x2, double z2) {
        return collinear(x, z, x1, z1, x2, z2);
    }

    public static boolean linesIntersect(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4) {
        return linesIntersect(p1.x, p1.z, p2.x, p2.z, p3.x, p3.z, p4.x, p4.z);
    }

    public static boolean linesIntersect(double x1, double z1, double x2, double z2,
            double x3, double y3, double x4, double y4) {
        // Return false if either of the lines have zero length
        if (x1 == x2 && z1 == z2
                || x3 == x4 && y3 == y4) {
            return false;
        }
        // Fastest method, based on Franklin Antonio's "Faster Line Segment Intersection" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
        double ax = x2 - x1;
        double ay = z2 - z1;
        double bx = x3 - x4;
        double by = y3 - y4;
        double cx = x1 - x3;
        double cy = z1 - y3;

        double alphaNumerator = by * cx - bx * cy;
        double commonDenominator = ay * bx - ax * by;
        if (commonDenominator > 0) {
            if (alphaNumerator < 0 || alphaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (alphaNumerator > 0 || alphaNumerator < commonDenominator) {
                return false;
            }
        }
        double betaNumerator = ax * cy - ay * cx;
        if (commonDenominator > 0) {
            if (betaNumerator < 0 || betaNumerator > commonDenominator) {
                return false;
            }
        } else if (commonDenominator < 0) {
            if (betaNumerator > 0 || betaNumerator < commonDenominator) {
                return false;
            }
        }
        // if commonDenominator == 0 then the lines are parallel.
        if (commonDenominator == 0) {
            // This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
            // The lines are parallel.
            // Check if they're collinear.
            double collinearityTestForP3 = x1 * (z2 - y3) + x2 * (y3 - z1) + x3 * (z1 - z2);	// see http://mathworld.wolfram.com/Collinear.html
            // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
            if (collinearityTestForP3 == 0) {
                // The lines are collinear. Now check if they overlap.
                if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4
                        || x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4
                        || x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
                    if (z1 >= y3 && z1 <= y4 || z1 <= y3 && z1 >= y4
                            || z2 >= y3 && z2 <= y4 || z2 <= y3 && z2 >= y4
                            || y3 >= z1 && y3 <= z2 || y3 <= z1 && y3 >= z2) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;

    }

    public static Vector3 getLineLineIntersection(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4) {
        return getLineLineIntersection(p1.x, p1.z, p2.x, p2.z, p3.x, p3.z, p4.x, p4.z);
    }

    public static Vector3 getLineLineIntersection(double x1, double z1, double x2, double z2, double x3, double y3, double x4, double y4) {
        double det1And2 = det(x1, z1, x2, z2);
        double det3And4 = det(x3, y3, x4, y4);
        double x1LessX2 = x1 - x2;
        double y1LessY2 = z1 - z2;
        double x3LessX4 = x3 - x4;
        double y3LessY4 = y3 - y4;
        double det1Less2And3Less4 = det(x1LessX2, y1LessY2, x3LessX4, y3LessY4);
        if (det1Less2And3Less4 == 0) {
            return null;
        }
        double x = (det(det1And2, x1LessX2,
                det3And4, x3LessX4)
                / det1Less2And3Less4);
        double z = (det(det1And2, y1LessY2,
                det3And4, y3LessY4)
                / det1Less2And3Less4);
        return new Vector3(x, z);
    }

    protected static double det(double a, double b, double c, double d) {
        return a * d - b * c;
    }

    /**
     * Returns a positive double if (x, z) is counter-clockwise to (x2, z2)
     * relative to the origin in the cartesian coordinate space (positive x-axis
     * extends right, positive z-axis extends up). Returns a negative double if
     * (x, z) is clockwise to (x2, z2) relative to the origin. Returns a 0.0 if
     * (x, z), (x2, z2) and the origin are collinear.
     *
     * Alternatively, a value of 1 indicates that the shortest angle from (x,z)
     * to (x2, z2) is in the direction that takes the positive X axis towards
     * the positive Y axis.
     *
     * Note that this method gives different results to
     * java.awt.geom.Line2D.relativeCCW() since Java2D uses a different
     * coordinate system (positive x-axis extends right, positive z-axis extends
     * down).
     *
     *
     * @param x
     * @param y
     * @param x2
     * @param z2
     * @return
     */
    public static double ccwDouble(double x2LessX1, double y2LessY1,
            double pxLessX1, double pyLessY1) {
        double ccw = pyLessY1 * x2LessX1 - pxLessX1 * y2LessY1;
        return ccw;
    }

    public static double ccwDoubleExtra(double x2LessX1, double y2LessY1,
            double pxLessX1, double pyLessY1) {
        double ccw = pyLessY1 * x2LessX1 - pxLessX1 * y2LessY1;
        if (ccw == 0.0) {
            // The point is colinear, classify based on which side of
            // the segment the point falls on.  We can calculate a
            // relative value using the projection of px,py onto the
            // segment - a negative value indicates the point projects
            // outside of the segment in the direction of the particular
            // endpoint used as the origin for the projection.
            ccw = pxLessX1 * x2LessX1 + pyLessY1 * y2LessY1;
            if (ccw > 0.0) {
                // Reverse the projection to be relative to the original x2,z2
                // x2 and z2 are simply negated.
                // px and py need to have (x2 - x1) or (z2 - z1) subtracted
                //    from them (based on the original values)
                // Since we really want to get a positive answer when the
                //    point is "beyond (x2,z2)", then we want to calculate
                //    the inverse anyway - thus we leave x2 & z2 negated.
                pxLessX1 -= x2LessX1;
                pyLessY1 -= y2LessY1;
                ccw = pxLessX1 * x2LessX1 + pyLessY1 * y2LessY1;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return ccw;
    }

//	/**
//	 * Returns a positive double if (x, z) is counter-clockwise to (x2, z2) relative to the origin
//	 * in the cartesian coordinate space (positive x-axis extends right, positive z-axis extends up).
//	 * Returns a negative double if (x, z) is clockwise to (x2, z2) relative to the origin.
//	 * Returns a 0.0 if (x, z), (x2, z2) and the origin are collinear.
//	 *
//	 * Alternatively, a value of 1 indicates that the shortest angle from (x,z) to (x2, z2)
//	 * is in the direction that takes the positive X axis towards the positive Y axis.
//	 *
//	 * Note that this method gives different results to java.awt.geom.Line2D.relativeCCW() since Java2D
//	 * uses a different coordinate system (positive x-axis extends right, positive z-axis extends down).
//
//	 *
//	 * @param x
//	 * @param z
//	 * @param x2
//	 * @param z2
//	 * @return
//	 */
    public double ccwDouble(double x2, double z2) {
        return ccwDouble(x, z, x2, z2);
    }

    public double ccwDouble(Vector3 p) {
        return ccwDouble(x, z, p.x, p.z);
    }

    public static int ccw(double x, double z,
            double x2, double z2) {
        double ccw = ccwDouble(x, z, x2, z2);
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }

    public double ccw(double x2, double z2) {
        return ccw(x, z, x2, z2);
    }

    public double ccw(Vector3 p) {
        return ccw(x, z, p.x, p.z);
    }

    public static double relCCWDouble(double x1, double z1,
            double x2, double z2,
            double px, double py) {
        x2 -= x1;
        z2 -= z1;
        px -= x1;
        py -= z1;
        double ccw = py * x2 - px * z2;
        return ccw;
    }

    public double relCCWDouble(double x1, double z1, double x2, double z2) {
        return relCCWDouble(x1, z1, x2, z2, x, z);
    }

    public double relCCWDouble(Vector3 p1, Vector3 p2) {
        return relCCWDouble(p1.x, p1.z, p2.x, p2.z, x, z);
    }

    /**
     * Returns a positive double if (px, py) is counter-clockwise to (x2, z2)
     * relative to (x1, z1). in the cartesian coordinate space (positive x-axis
     * extends right, positive z-axis extends up). Returns a negative double if
     * (px, py) is clockwise to (x2, z2) relative to (x1, z1). Returns a 0.0 if
     * (px, py), (x1, z1) and (x2, z2) are collinear. Note that this method
     * gives different results to java.awt.geom.Line2D.relativeCCW() since
     * Java2D uses a different coordinate system (positive x-axis extends right,
     * positive z-axis extends down).
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @param px
     * @param py
     * @return
     */
    public static int relCCW(double x1, double z1,
            double x2, double z2,
            double px, double py) {
        double ccw = relCCWDouble(x1, z1, x2, z2, px, py);
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }

    public int relCCW(double x1, double z1, double x2, double z2) {
        return relCCW(x1, z1, x2, z2, x, z);
    }

    public int relCCW(Vector3 p1, Vector3 p2) {
        return relCCW(p1.x, p1.z, p2.x, p2.z, x, z);
    }

    public static double relCCWDoubleExtra(double x1, double z1,
            double x2, double z2,
            double px, double py) {
        x2 -= x1;
        z2 -= z1;
        px -= x1;
        py -= z1;
        double ccw = py * x2 - px * z2;
        if (ccw == 0.0) {
            // The point is colinear, classify based on which side of
            // the segment the point falls on.  We can calculate a
            // relative value using the projection of px,py onto the
            // segment - a negative value indicates the point projects
            // outside of the segment in the direction of the particular
            // endpoint used as the origin for the projection.
            ccw = px * x2 + py * z2;
            if (ccw > 0.0) {
                // Reverse the projection to be relative to the original x2,z2
                // x2 and z2 are simply negated.
                // px and py need to have (x2 - x1) or (z2 - z1) subtracted
                //    from them (based on the original values)
                // Since we really want to get a positive answer when the
                //    point is "beyond (x2,z2)", then we want to calculate
                //    the inverse anyway - thus we leave x2 & z2 negated.
                px -= x2;
                py -= z2;
                ccw = px * x2 + py * z2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return ccw;
    }

    @Override
    public String toString() {
        return "{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    /**
     * 获取方向
     *
     * @param ox
     * @param oy
     * @return
     */
    public double findSignedAngle(double ox, double oy) {
        return findSignedAngle(this.x, this.z, ox, oy);
    }

    /**
     * 获取方向
     *
     * @param dest
     * @return
     */
    public double findSignedAngle(Vector3 dest) {
        return findSignedAngle(this, dest);
    }

    /**
     * 获取方向
     *
     * @param start
     * @param dest
     * @return
     */
    public static double findSignedAngle(Vector3 start, Vector3 dest) {
        return findSignedAngle(start.x, start.z, dest.x, dest.z);
    }

    /**
     * 获取方向
     *
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     * @return
     */
    public static double findSignedAngle(double x1, double z1, double x2, double z2) {
        double x = x2 - x1;
        double z = z2 - z1;
        double angle = (Math.atan2(z, x));
        return angle;
    }

    /**
     * 根据两点组成的直线在xz平面的角度，顺时针
     *
     * @param start
     * @param end
     * @return
     */
    public static double toUnityDegrees(Vector3 start, Vector3 end) {
        return Vector3.toUnityDegrees(findAngle(start, end));
    }

    /**
     * java弧度转unity角度
     *
     * @param angle
     * @return
     */
    public static double toUnityDegrees(double angle) {
        return 90 - Math.toDegrees(angle);
    }

    public static double unityDegreesToAngle(double degrees) {
        return ((90 - degrees) % 360) * Math.PI / 180;
    }

    public static double dot(float x1, float y1, float z1, float x2, float y2, float z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    /**
     * 返回两个向量（从原点到两点）间的夹角
     *
     * @param v1
     * @param v2
     * @return Degrees
     */
    public static double angle(Vector3 v1, Vector3 v2) {
        if (v1.isZero()) {
            throw new IllegalArgumentException("v1为零向量");
        }
        if (v2.isZero()) {
            throw new IllegalArgumentException("v2为零向量");
        }
        return ((Math.acos(v1.dot(v2) / (v1.length() * v2.length()))) * radiansToDegrees);
    }

    @JSONField(serialize = false)
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public double dot(final Vector3 vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    public double findAngle(double ox, double oy) {
        return findAngle(this.x, this.z, ox, oy);
    }

    public double findAngle(Vector3 dest) {
        return findAngle(this, dest);
    }

    public double findAngleFromOrigin() {
        double angle = findSignedAngleFromOrigin();
        if (angle < 0) {
            angle += TWO_PI;
        }
        return angle;
    }

    public double findSignedAngleFromOrigin() {
        return Math.atan2(z, x);
    }

    public static double findAngle(Vector3 start, Vector3 dest) {
        return findAngle(start.x, start.z, dest.x, dest.z);
    }

    public static double findAngle(double x1, double z1, double x2, double z2) {
        double angle = findSignedAngle(x1, z1, x2, z2);
        if (angle < 0) {
            angle += TWO_PI;
        }
        return angle;
    }

    public double findSignedRelativeAngle(double x1, double z1, double x2, double z2) {
        return findSignedRelativeAngle(this.x, this.z, x1, z1, x2, z2);
    }

    public double findSignedRelativeAngle(Vector3 start, Vector3 end) {
        return findSignedRelativeAngle(this, start, end);
    }

    public static double findSignedRelativeAngle(Vector3 point, Vector3 start, Vector3 end) {
        return findSignedRelativeAngle(point.x, point.z, start.x, start.z, end.x, end.z);
    }

    public static double findSignedRelativeAngle(double x, double z, double x1, double z1, double x2, double z2) {
        double lineAngle = findAngle(x1, z1, x2, z2);
        double pointAngle = findAngle(x1, z1, x, z);
        if (pointAngle < lineAngle) {
            pointAngle += TWO_PI;
        }
        double relativePointAngle = pointAngle - lineAngle;
        if (relativePointAngle > Math.PI) {
            relativePointAngle -= TWO_PI;
        }
        if (relativePointAngle <= Math.PI && relativePointAngle >= -Math.PI) {
            return relativePointAngle;
        }
        return 0;

    }

    public double findRelativeAngle(double x1, double z1, double x2, double z2) {
        return findRelativeAngle(this.x, this.z, x1, z1, x2, z2);
    }

    public double findRelativeAngle(Vector3 start, Vector3 end) {
        return findRelativeAngle(this, start, end);
    }

    public static double findRelativeAngle(Vector3 point, Vector3 start, Vector3 end) {
        return findRelativeAngle(point.x, point.z, start.x, start.z, end.x, end.z);
    }

    public static double findRelativeAngle(double x, double z, double x1, double z1, double x2, double z2) {
        double relativePointAngle = findSignedRelativeAngle(x, z, x1, z1, x2, z2);
        if (relativePointAngle < -Math.PI) {
            relativePointAngle += TWO_PI;
        }
        if (relativePointAngle <= 2 * Math.PI && relativePointAngle >= 0) {
            return relativePointAngle;
        }
        return 0;
    }

    public Vector3 midPoint(Vector3 p) {
        return midPoint(x, z, p.x, p.z);
    }

    public static Vector3 midPoint(Vector3 p, Vector3 p2) {
        return midPoint(p.x, p.z, p2.x, p2.z);
    }

    public static Vector3 midPoint(double x, double z, double x2, double z2) {
        return new Vector3((x + x2) / 2f, (z + z2) / 2f);
    }

    public static Vector3 getStopPoint(Vector3 up, Vector3 end, double distance) {
        if (distance >= up.distance(end)) {
            return up;
        }
        return end.createPointFromAngle(end.findAngle(up), distance);
    }

    public Vector3 createPointFromAngle(double angle, double distance) {
        return createPointFromAngle(this, angle, distance);
    }

    public static Vector3 createPointFromAngle(Vector3 source, double angle, double distance) {
        Vector3 p = new Vector3();
        double xDist = Math.cos(angle) * distance;
        double yDist = Math.sin(angle) * distance;
        p.x = (source.x + xDist);
        p.z = (source.z + yDist);
        p.y = source.y;
        return p;
    }

    public Vector3 createPointToward(Vector3 p, double distance) {
        return createPointToward(x, z, p.x, p.z, distance);
    }

    public Vector3 createPointToward(double x2, double z2, double distance) {
        return createPointToward(x, z, x2, z2, distance);
    }

    public static Vector3 createPointToward(double x, double z, double x2, double z2, double distance) {
        Vector3 p = new Vector3();
        double xDiff = (x2 - x);
        double yDiff = (z2 - z);
        double ptDist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        double distOnPtDist = distance / ptDist;
        double xDist = xDiff * distOnPtDist;
        double yDist = yDiff * distOnPtDist;
        p.x = (x + xDist);
        p.z = (z + yDist);
        return p;
    }

    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    public double ptLineDist(double x1, double z1, double x2, double z2) {
        return ptLineDist(x1, z1, x2, z2, x, z);
    }

    public double ptLineDist(Vector3 start, Vector3 end) {
        return ptLineDist(start.x, start.z, end.x, end.z, x, z);
    }

    public static double ptLineDist(Vector3 start, Vector3 end, Vector3 p) {
        return ptLineDist(start.x, start.z, end.x, end.z, p.x, p.z);
    }

    public static double ptLineDist(double x1, double z1, double x2, double z2, double px, double py) {
        return Math.sqrt(ptLineDistSq(x1, z1, x2, z2, px, py));
    }

    public double ptLineDistSq(double x1, double z1, double x2, double z2) {
        return ptLineDistSq(x1, z1, x2, z2, x, z);
    }

    public double ptLineDistSq(Vector3 start, Vector3 end) {
        return ptLineDistSq(start.x, start.z, end.x, end.z, x, z);
    }

    public static double ptLineDistSq(Vector3 start, Vector3 end, Vector3 p) {
        return ptLineDistSq(start.x, start.z, end.x, end.z, p.x, p.z);
    }

    public static double ptLineDistSq(double x1, double z1, double x2, double z2, double px, double py) {
        //from: Line2D.Float.ptLineDistSq(x1, z1, x2, z2, px, py);
        // Adjust vectors relative to x1,z1
        // x2,z2 becomes relative vector from x1,z1 to end of segment
        x2 -= x1;
        z2 -= z1;
        // px,py becomes relative vector from x1,z1 to test point
        px -= x1;
        py -= z1;
        double dotprod = px * x2 + py * z2;
        // dotprod is the length of the px,py vector
        // projected on the x1,z1=>x2,z2 vector times the
        // length of the x1,z1=>x2,z2 vector
        double projlenSq = dotprod * dotprod / (x2 * x2 + z2 * z2);
        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        double lenSq = px * px + py * py - projlenSq;
        if (lenSq < 0) {
            lenSq = 0;
        }
        return lenSq;
    }

    public double ptSegDist(double x1, double z1, double x2, double z2) {
        return ptSegDist(x1, z1, x2, z2, x, z);
    }

    public double ptSegDist(Vector3 start, Vector3 end) {
        return ptSegDist(start.x, start.z, end.x, end.z, x, z);
    }

    public static double ptSegDist(Vector3 start, Vector3 end, Vector3 p) {
        return ptSegDist(start.x, start.z, end.x, end.z, p.x, p.z);
    }

    public static double ptSegDist(double x1, double z1, double x2, double z2, double px, double py) {
        return Math.sqrt(ptSegDistSq(x1, z1, x2, z2, px, py));
    }

    public double ptSegDistSq(double x1, double z1, double x2, double z2) {
        return ptSegDistSq(x1, z1, x2, z2, x, z);
    }

    public double ptSegDistSq(Vector3 start, Vector3 end) {
        return ptSegDistSq(start.x, start.z, end.x, end.z, x, z);
    }

    public static double ptSegDistSq(Vector3 start, Vector3 end, Vector3 p) {
        return ptSegDistSq(start.x, start.z, end.x, end.z, p.x, p.z);
    }

    public static double ptSegDistSq(double x1, double z1, double x2, double z2, double px, double py) {
        //from: Line2D.Float.ptSegDistSq(x1, z1, x2, z2, px, py);
        // Adjust vectors relative to x1,z1
        // x2,z2 becomes relative vector from x1,z1 to end of segment
        x2 -= x1;
        z2 -= z1;
        // px,py becomes relative vector from x1,z1 to test point
        px -= x1;
        py -= z1;
        double dotprod = px * x2 + py * z2;
        double projlenSq;
        if (dotprod <= 0.0) {
            // px,py is on the side of x1,z1 away from x2,z2
            // distance to segment is length of px,py vector
            // "length of its (clipped) projection" is now 0.0
            projlenSq = 0.0;
        } else {
            // switch to backwards vectors relative to x2,z2
            // x2,z2 are already the negative of x1,z1=>x2,z2
            // to get px,py to be the negative of px,py=>x2,z2
            // the dot product of two negated vectors is the same
            // as the dot product of the two normal vectors
            px = x2 - px;
            py = z2 - py;
            dotprod = px * x2 + py * z2;
            if (dotprod <= 0.0) {
                // px,py is on the side of x2,z2 away from x1,z1
                // distance to segment is length of (backwards) px,py vector
                // "length of its (clipped) projection" is now 0.0
                projlenSq = 0.0;
            } else {
                // px,py is between x1,z1 and x2,z2
                // dotprod is the length of the px,py vector
                // projected on the x2,z2=>x1,z1 vector times the
                // length of the x2,z2=>x1,z1 vector
                projlenSq = dotprod * dotprod / (x2 * x2 + z2 * z2);
            }
        }
        // Distance to line is now the length of the relative point
        // vector minus the length of its projection onto the line
        // (which is zero if the projection falls outside the range
        //  of the line segment).
        double lenSq = px * px + py * py - projlenSq;
        if (lenSq < 0) {
            lenSq = 0;
        }
        return lenSq;
    }

    public static Vector3 getClosestPointOnSegment(double x1, double z1, double x2, double z2, double px, double py) {
        Vector3 closestPoint = new Vector3();
        double x2LessX1 = x2 - x1;
        double y2LessY1 = z2 - z1;
        double lNum = x2LessX1 * x2LessX1 + y2LessY1 * y2LessY1;
        double rNum = ((px - x1) * x2LessX1 + (py - z1) * y2LessY1) / lNum;
//		double lNum = (x2 - x1)*(x2 - x1) + (z2 - z1)*(z2 - z1);
//		double rNum = ((px - x1)*(x2 - x1) + (py - z1)*(z2 - z1)) / lNum;
        if (rNum <= 0) {
            closestPoint.x = x1;
            closestPoint.z = z1;
        } else if (rNum >= 1) {
            closestPoint.x = x2;
            closestPoint.z = z2;
        } else {
            closestPoint.x = (x1 + rNum * x2LessX1);
            closestPoint.z = (z1 + rNum * y2LessY1);
        }
        return closestPoint;
    }

    public Vector3 getClosestPointOnSegment(double x1, double z1, double x2, double z2) {
        return getClosestPointOnSegment(x1, z1, x2, z2, x, z);
    }

    public Vector3 getClosestPointOnSegment(Vector3 p1, Vector3 p2) {
        return getClosestPointOnSegment(p1.x, p1.z, p2.x, p2.z, x, z);
    }

}
