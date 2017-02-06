package net.sz;

/**
 *
 * @author troy-pc
 */
public class ATest {

    public static void main(String[] args) {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 0);
        Point p3 = new Point(3, 1);
        Point p4 = new Point(0, 1);

        Point p5 = new Point(5, 1.1);

        double len51 = Math.pow(p5.x - p1.x, 2) + Math.pow(p5.y - p1.y, 2);
        double len52 = Math.pow(p5.x - p2.x, 2) + Math.pow(p5.y - p2.y, 2);
        double len53 = Math.pow(p5.x - p3.x, 2) + Math.pow(p5.y - p3.y, 2);
        double len54 = Math.pow(p5.x - p4.x, 2) + Math.pow(p5.y - p4.y, 2);

        double r1 = Math.acos((len51 + len52 - 9) / (2 * Math.pow(len51, 0.5) * Math.pow(len52, 0.5)));
        double r2 = Math.acos((len52 + len53 - 1) / (2 * Math.pow(len52, 0.5) * Math.pow(len53, 0.5)));
        double r3 = Math.acos((len53 + len54 - 9) / (2 * Math.pow(len53, 0.5) * Math.pow(len54, 0.5)));
        double r4 = Math.acos((len54 + len51 - 1) / (2 * Math.pow(len54, 0.5) * Math.pow(len51, 0.5)));

        System.out.println(r1 + r2 + r3 + r4);
    }
}

class Point {

    double x;
    double y;

    public Point(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }
}
