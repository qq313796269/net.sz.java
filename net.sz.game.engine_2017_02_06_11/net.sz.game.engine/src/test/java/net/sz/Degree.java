package net.sz;

import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Degree {

    private static final Logger log = Logger.getLogger(Degree.class);
    private double x;
    private double y;

    public Degree(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double[] getDegree(Degree a, Degree b) {
        double k = Math.abs((b.y - a.y) / (b.x - a.x));
        return new double[]{Math.toDegrees(Math.atan(k)), 360 - Math.toDegrees(Math.atan(k))};
    }

    public static void main(String[] args) {
        Degree a = new Degree(0, 3);
        Degree b = new Degree(3, 0);
        double[] degree = Degree.getDegree(a, b);
        System.out.println(Arrays.toString(degree));
    }
}
