/*
 * Copyright (c) 2008, Keith Woodward
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of Keith Woodward nor the names
 *    of its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.sz.framework.way.navmesh;

import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A cool polygon class that's got some pretty useful geometry methods. Can be
 * drawn and filled by Java2D's java.awt.Graphics object. Note that the polygon
 * can be convex or concave but it should not have intersecting sides.
 *
 * Some code is from here:
 * http://www.cs.princeton.edu/introcs/35purple/Polygon.java.html
 *
 * and Joseph O'Rourke: http://exaflop.org/docs/cgafaq/cga2.html
 *
 * Another good source Paul Bourke:
 * http://local.wasp.uwa.edu.au/~pbourke/geometry/
 *
 * @author Keith Woodward
 */
public final class KPolygon implements Serializable, Cloneable, PolygonHolder, Shape {

    private ArrayList<Vector3> points;
    public Vector3 center;
    //移动的方向
    public Vector3 translation;
    //移动的次数
    public int translationCount;
    public double area;
    public double radius;
    private double y = -1;
    public double radiusSq;
    public boolean counterClockWise;
    // for use with straightedge.geom.util.Tracker and straightedge.geom.util.TileArray
    public int trackerID = -1;
    public long trackerCounter = -1;
    public boolean trackerAddedStatus = false;

    public KPolygon() {

    }

    @Override
    public KPolygon clone() throws CloneNotSupportedException {
        KPolygon kPolygon = (KPolygon) super.clone();
        if (kPolygon.center != null) {
            kPolygon.center = this.center.clone();
        }
        if (kPolygon.translation != null) {
            kPolygon.translation = this.translation.clone();
        }
        kPolygon.points = new ArrayList<>();
        for (Vector3 point : points) {
            kPolygon.points.add(point.clone());
        }
        return kPolygon;
    }

    public boolean touches(KPolygon target) {
        int count = 0;
        for (Vector3 point : points) {
            for (Vector3 t : target.getPoints()) {
                if (point.equals(t)) {
                    count++;
                }
            }
        }
        return count >= 2;
    }

    public boolean union(KPolygon target) {
        Vector3 add = null;
        ArrayList<Integer> indexs = new ArrayList<>();
        for (Vector3 t : target.getPoints()) {
            boolean eq = false;
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).equals(t)) {
                    eq = true;
                    indexs.add(i);
                }
            }
            if (!eq) {
                add = t;
            }
        }
        if (indexs.size() == 2) {
            int max = Math.max(indexs.get(0), indexs.get(1));
            int min = Math.min(indexs.get(0), indexs.get(1));
            if (Math.abs(max - min) == 1) {
                this.points.add(max, add);
            } else {
                this.points.add(add);
            }
            calcAll();
            return true;
        }
        return false;
    }

    public KPolygon(java.util.List<Vector3> pointsList, boolean copyPoints) {
        if (pointsList.size() < 3) {
            throw new RuntimeException("Minimum of 3 points needed. pointsList.size() == " + pointsList.size());
        }
        this.points = new ArrayList<>(pointsList.size());
        for (int i = 0; i < pointsList.size(); i++) {
            Vector3 existingPoint = pointsList.get(i);
            if (copyPoints) {
                points.add(new Vector3(existingPoint));
            } else {
                points.add(existingPoint);
            }
        }
        calcAll();
    }

    public KPolygon(java.util.List<Vector3> pointsList) {
        this(pointsList, true);
    }

    public KPolygon(Vector3[] pointsArray, boolean copyPoints) {
        if (pointsArray.length < 3) {
            throw new RuntimeException("Minimum of 3 points needed. pointsArray.length == " + pointsArray.length);
        }
        this.points = new ArrayList<>(pointsArray.length);
        for (int i = 0; i < pointsArray.length; i++) {
            Vector3 existingPoint = pointsArray[i];
            if (copyPoints) {
                points.add(new Vector3(existingPoint));
            } else {
                points.add(existingPoint);
            }
        }
        calcAll();
    }

    public KPolygon(Vector3... pointsArray) {
        this(pointsArray, true);
    }

    public KPolygon(KPolygon polygon) {
        points = new ArrayList<>(polygon.getPoints().size());
        for (int i = 0; i < polygon.getPoints().size(); i++) {
            Vector3 existingPoint = polygon.getPoints().get(i);
            points.add(new Vector3(existingPoint));
        }
        area = polygon.getArea();
        counterClockWise = polygon.isCounterClockWise();
        radius = polygon.getRadius();
        radiusSq = polygon.getRadiusSq();
        center = new Vector3(polygon.getCenter());
    }

    public static KPolygon createRect(double x, double z, double x2, double z2) {
        // make x and z the bottom left point.
        if (x2 < x) {
            double t = x;
            x = x2;
            x2 = t;
        }
        // make x2 and z2 the top right point.
        if (z2 < z) {
            double t = z;
            z = z2;
            z2 = t;
        }
        ArrayList<Vector3> pointList = new ArrayList<>();
        pointList.add(new Vector3(x, z));
        pointList.add(new Vector3(x2, z));
        pointList.add(new Vector3(x2, z2));
        pointList.add(new Vector3(x, z2));
        return new KPolygon(pointList, false);
    }

    public static KPolygon createRect(Vector3 p, Vector3 p2) {
        return createRect(p.x, p.z, p2.x, p2.z);
    }

    public static KPolygon createRect(Vector3 botLeftPoint, double width, double height) {
        return createRect(botLeftPoint.x, botLeftPoint.z, botLeftPoint.x + width, botLeftPoint.z + height);
    }

    public static KPolygon createRectOblique(double x, double z, double x2, double z2, double width) {
        ArrayList<Vector3> pointList = new ArrayList<>();
        double r = width / 2f;
        double xOffset = 0;
        double yOffset = 0;
        double xDiff = x2 - x;
        double yDiff = z2 - z;
        if (xDiff == 0) {
            xOffset = r;
            yOffset = 0;
        } else if (yDiff == 0) {
            xOffset = 0;
            yOffset = r;
        } else {
            double gradient = (yDiff) / (xDiff);
            xOffset = (r * gradient / (Math.sqrt(1 + gradient * gradient)));
            yOffset = -xOffset / gradient;
        }
        //System.out.println(this.getClass().getSimpleName() + ": xOffset == "+xOffset+", yOffset == "+yOffset);
        pointList.add(new Vector3(x - xOffset, z - yOffset));
        pointList.add(new Vector3(x + xOffset, z + yOffset));
        pointList.add(new Vector3(x2 + xOffset, z2 + yOffset));
        pointList.add(new Vector3(x2 - xOffset, z2 - yOffset));
        return new KPolygon(pointList, false);
    }

    public static KPolygon createRectOblique(Vector3 p1, Vector3 p2, double width) {
        return createRectOblique(p1.x, p1.z, p2.x, p2.z, width);
    }

    public static KPolygon createRegularPolygon(int numPoints, double distFromCenterToPoints) {
        if (numPoints < 3) {
            throw new IllegalArgumentException("numPoints must be 3 or more, it can not be " + numPoints + ".");
        }
        ArrayList<Vector3> pointList = new ArrayList<>();
        double angleIncrement = Math.PI * 2f / (numPoints);
        double radius = distFromCenterToPoints;
        double currentAngle = 0;
        for (int k = 0; k < numPoints; k++) {
            double x = radius * Math.cos(currentAngle);
            double z = radius * Math.sin(currentAngle);
            pointList.add(new Vector3(x, z));
            currentAngle += angleIncrement;
        }
        KPolygon createdPolygon = new KPolygon(pointList, false);
        return createdPolygon;
    }

    public java.util.List<Vector3> getPoints() {
        return points;
    }

    // Gives point of intersection with line specified, where intersectoin point
    // returned is the one closest to (x1, z1).
    // null is returned if there is no intersection.
    public Vector3 getClosestIntersectionToFirstFromSecond(double x1, double z1, double x2, double z2) {
        Vector3 closestIntersectionPoint = null;
        double closestIntersectionDistanceSq = Double.MAX_VALUE;
        int nextI;
        for (int i = 0; i < points.size(); i++) {
            nextI = (i + 1 == points.size() ? 0 : i + 1);
            if (Vector3.linesIntersect(x1, z1, x2, z2, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)) {
                Vector3 currentIntersectionPoint = Vector3.getLineLineIntersection(x1, z1, x2, z2, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z);
                if (currentIntersectionPoint == null) {
                    continue;
                }
                double currentIntersectionDistanceSq = currentIntersectionPoint.distanceSq(x1, z1);
                if (currentIntersectionDistanceSq < closestIntersectionDistanceSq) {
                    closestIntersectionPoint = currentIntersectionPoint;
                    closestIntersectionDistanceSq = currentIntersectionDistanceSq;
                }
            }
        }
        return closestIntersectionPoint;
    }

    public Vector3 getClosestIntersectionToFirstFromSecond(Vector3 first, Vector3 second) {
        return getClosestIntersectionToFirstFromSecond(first.x, first.z, second.x, second.z);
    }

    public Vector3 getBoundaryPointClosestTo(Vector3 p) {
        return getBoundaryPointClosestTo(p.x, p.z);
    }

    public double getY() {
        if (y != -1) {
            return y;
        }
        if (points.size() < 1) {
            return 0;
        }
        for (Vector3 point : points) {
            y += point.y;
        }
        y = y / points.size();
        return y;
    }

    public Vector3 getBoundaryPointClosestTo(double x, double z) {
        double closestDistanceSq = Double.MAX_VALUE;
        int closestIndex = -1;
        int closestNextIndex = -1;

        int nextI;
        for (int i = 0; i < points.size(); i++) {
            nextI = (i + 1 == points.size() ? 0 : i + 1);
            Vector3 p = this.getPoints().get(i);
            Vector3 pNext = this.getPoints().get(nextI);
            double ptSegDistSq = Vector3.ptSegDistSq(p.x, p.z, pNext.x, pNext.z, x, z);
            if (ptSegDistSq < closestDistanceSq) {
                closestDistanceSq = ptSegDistSq;
                closestIndex = i;
                closestNextIndex = nextI;
            }
        }
        Vector3 p = this.getPoints().get(closestIndex);
        Vector3 pNext = this.getPoints().get(closestNextIndex);
        return Vector3.getClosestPointOnSegment(p.x, p.z, pNext.x, pNext.z, x, z);
    }

    public boolean contains(KPolygon foreign) {
        if (intersectsPerimeter(foreign)) {
            return false;
        }
        if (contains(foreign.getPoints().get(0)) == false) {
            return false;
        }
        return true;
    }

    public boolean contains(Vector3 p) {
        return contains(p.x, p.z);
    }

    // Source code from: http://exaflop.org/docs/cgafaq/cga2.html
    //Subject 2.03: How do I find if a point lies within a polygon?
    //The definitive reference is "Point in Polyon Strategies" by Eric Haines [Gems IV] pp. 24-46. The code in the Sedgewick book Algorithms (2nd Edition, p.354) is incorrect.
    //The essence of the ray-crossing method is as follows. Think of standing inside a field with a fence representing the polygon. Then walk north. If you have to jump the fence you know you are now outside the poly. If you have to cross again you know you are now inside again; i.e., if you were inside the field to start with, the total number of fence jumps you would make will be odd, whereas if you were ouside the jumps will be even.
    //The code below is from Wm. Randolph Franklin <wrf@ecse.rpi.edu> with some minor modifications for speed. It returns 1 for strictly interior points, 0 for strictly exterior, and 0 or 1 for points on the boundary. The boundary behavior is complex but determined; | in particular, for a partition of a region into polygons, each point | is "in" exactly one polygon. See the references below for more detail
    //The code may be further accelerated, at some loss in clarity, by avoiding the central computation when the inequality can be deduced, and by replacing the division by a multiplication for those processors with slow divides.
    //References:
    //[Gems IV] pp. 24-46
    //[O'Rourke] pp. 233-238
    //[Glassner:RayTracing]
    public boolean contains(double x, double z) {
        Vector3 pointIBefore = (points.size() != 0 ? points.get(points.size() - 1) : null);
        int crossings = 0;
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            if (((pointIBefore.z <= z && z < pointI.z)
                    || (pointI.z <= z && z < pointIBefore.z))
                    && x < ((pointI.x - pointIBefore.x) / (pointI.z - pointIBefore.z) * (z - pointIBefore.z) + pointIBefore.x)) {
                crossings++;
            }
            pointIBefore = pointI;
        }
        return (crossings % 2 != 0);
    }

    public Vector3 getPoint(int i) {
        return getPoints().get(i);
    }

    public void calcArea() {
        double signedArea = getAndCalcSignedArea();
        if (signedArea < 0) {
            counterClockWise = false;
        } else {
            counterClockWise = true;
        }
        area = Math.abs(signedArea);
    }

    public double getAndCalcSignedArea() {
        double totalArea = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            totalArea += ((points.get(i).x - points.get(i + 1).x) * (points.get(i + 1).z + (points.get(i).z - points.get(i + 1).z) / 2));
        }
        // need to do points[point.length-1] and points[0].
        totalArea += ((points.get(points.size() - 1).x - points.get(0).x) * (points.get(0).z + (points.get(points.size() - 1).z - points.get(0).z) / 2));
        return totalArea;
    }

    public double[] getBoundsArray() {
        return getBoundsArray(new double[4]);
    }

    public double[] getBoundsArray(double[] bounds) {
        double leftX = Double.MAX_VALUE;
        double botY = Double.MAX_VALUE;
        double rightX = -Double.MAX_VALUE;
        double topY = -Double.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x < leftX) {
                leftX = points.get(i).x;
            }
            if (points.get(i).x > rightX) {
                rightX = points.get(i).x;
            }
            if (points.get(i).z < botY) {
                botY = points.get(i).z;
            }
            if (points.get(i).z > topY) {
                topY = points.get(i).z;
            }
        }
        bounds[0] = leftX;
        bounds[1] = botY;
        bounds[2] = rightX;
        bounds[3] = topY;
        return bounds;
    }

//    public AABB getAABB() {
//        double leftX = Double.MAX_VALUE;
//        double botY = Double.MAX_VALUE;
//        double rightX = -Double.MAX_VALUE;
//        double topY = -Double.MAX_VALUE;
//
//        for (int i = 0; i < points.size(); i++) {
//            if (points.get(i).x < leftX) {
//                leftX = points.get(i).x;
//            }
//            if (points.get(i).x > rightX) {
//                rightX = points.get(i).x;
//            }
//            if (points.get(i).z < botY) {
//                botY = points.get(i).z;
//            }
//            if (points.get(i).z > topY) {
//                topY = points.get(i).z;
//            }
//        }
//        AABB aabb = new AABB(leftX, botY, rightX, topY);
//        return aabb;
//    }
    public boolean intersectsPerimeter(KPolygon foreign) {
        Vector3 pointIBefore = (points.size() != 0 ? points.get(points.size() - 1) : null);
        Vector3 pointJBefore = (foreign.points.size() != 0 ? foreign.points.get(foreign.points.size() - 1) : null);
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            //int nextI = (i+1 >= points.size() ? 0 : i+1);
            for (int j = 0; j < foreign.points.size(); j++) {
                //int nextJ = (j+1 >= foreign.points.size() ? 0 : j+1);
                Vector3 pointJ = foreign.points.get(j);
                //if (KPoint.linesIntersect(points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z, foreign.points.get(j).x, foreign.points.get(j).z, foreign.points.get(nextJ).x, foreign.points.get(nextJ).z)){
                // The below linesIntersect could be sped up slightly since many things are recalc'ed over and over again.
                if (Vector3.linesIntersect(pointI, pointIBefore, pointJ, pointJBefore)) {
                    return true;
                }
                pointJBefore = pointJ;
            }
            pointIBefore = pointI;
        }
        return false;
    }

    public boolean intersects(KPolygon foreign) {
        if (intersectsPerimeter(foreign)) {
            return true;
        }
//        if (contains(foreign.getPoint(0)) || foreign.contains(getPoint(0))) {
//            return true;
//        }
        return false;
    }

    public boolean intersectionPossible(KPolygon poly) {
        return intersectionPossible(this, poly);
    }

    public static boolean intersectionPossible(KPolygon poly, KPolygon poly2) {
        double sumRadiusSq = poly.getRadius() + poly2.getRadius();
        sumRadiusSq *= sumRadiusSq;
        if (poly.getCenter().distanceSq(poly2.getCenter()) > sumRadiusSq) {
            //if (center.distance(foreign.getCenter()) > radius + foreign.getRadius()){
            return false;
        }
        return true;
    }

    public boolean intersectionPossible(Vector3 p1, Vector3 p2) {
        return intersectionPossible(p1.x, p1.z, p2.x, p2.z);
    }

    public boolean intersectionPossible(double x1, double z1, double x2, double z2) {
        if (center.ptSegDistSq(x1, z1, x2, z2) > radiusSq) {
            return false;
        }
        return true;
    }

    public boolean intersectsLine(Vector3 p1, Vector3 p2) {
        return intersectsLine(p1.x, p1.z, p2.x, p2.z);
    }

    public boolean intersectsLine(double x1, double z1, double x2, double z2) {

        // Sometimes this method fails if the 'lines'
        // start and end on the same point, so here we check for that.
        if (x1 == x2 && z1 == z2) {
            return false;
        }
        double ax = x2 - x1;
        double ay = z2 - z1;
        Vector3 pointIBefore = points.get(points.size() - 1);
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            double x3 = pointIBefore.x;
            double z3 = pointIBefore.z;
            double x4 = pointI.x;
            double y4 = pointI.z;

            double bx = x3 - x4;
            double by = z3 - y4;
            double cx = x1 - x3;
            double cy = z1 - z3;

            double alphaNumerator = by * cx - bx * cy;
            double commonDenominator = ay * bx - ax * by;
            if (commonDenominator > 0) {
                if (alphaNumerator < 0 || alphaNumerator > commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            } else if (commonDenominator < 0) {
                if (alphaNumerator > 0 || alphaNumerator < commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            }
            double betaNumerator = ax * cy - ay * cx;
            if (commonDenominator > 0) {
                if (betaNumerator < 0 || betaNumerator > commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            } else if (commonDenominator < 0) {
                if (betaNumerator > 0 || betaNumerator < commonDenominator) {
                    pointIBefore = pointI;
                    continue;
                }
            }
            if (commonDenominator == 0) {
                // This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
                // The lines are parallel.
                // Check if they're collinear.
                double collinearityTestForP3 = x1 * (z2 - z3) + x2 * (z3 - z1) + x3 * (z1 - z2);	// see http://mathworld.wolfram.com/Collinear.html
                // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
                if (collinearityTestForP3 == 0) {
                    // The lines are collinear. Now check if they overlap.
                    if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4
                            || x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4
                            || x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2) {
                        if (z1 >= z3 && z1 <= y4 || z1 <= z3 && z1 >= y4
                                || z2 >= z3 && z2 <= y4 || z2 <= z3 && z2 >= y4
                                || z3 >= z1 && z3 <= z2 || z3 <= z1 && z3 >= z2) {
                            return true;
                        }
                    }
                }
                pointIBefore = pointI;
                continue;
            }
            return true;
        }
        return false;
    }

    public void calcCenter() {
        if (center == null) {
            center = new Vector3();
        }
        if (getArea() == 0) {
            center.x = points.get(0).x;
            center.z = points.get(0).z;
            return;
        }
        double cx = 0.0f;
        double cz = 0.0f;
        Vector3 pointIBefore = (!points.isEmpty() ? points.get(points.size() - 1) : null);
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            double multiplier = (pointIBefore.z * pointI.x - pointIBefore.x * pointI.z);
            cx += (pointIBefore.x + pointI.x) * multiplier;
            cz += (pointIBefore.z + pointI.z) * multiplier;
            pointIBefore = pointI;
        }
        cx /= (6 * getArea());
        cz /= (6 * getArea());
        if (counterClockWise == true) {
            cx *= -1;
            cz *= -1;
        }
        center.x = cx;
        center.z = cz;
    }

    public void calcRadius() {
        if (center == null) {
            calcCenter();
        }
        double maxRadiusSq = -1;
        int furthestPointIndex = 0;
        for (int i = 0; i < points.size(); i++) {
            double currentRadiusSq = (center.distanceSq(points.get(i)));
            if (currentRadiusSq > maxRadiusSq) {
                maxRadiusSq = currentRadiusSq;
                furthestPointIndex = i;
            }
        }
        radius = (center.distance(points.get(furthestPointIndex)));
        radiusSq = radius * radius;
    }

    public void calcAll() {
        this.calcArea();
        this.calcCenter();
        this.calcRadius();
    }

    public double getArea() {
        return area;
    }

    public Vector3 getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public double getRadiusSq() {
        return radiusSq;
    }

    public double getPerimeter() {
        double perimeter = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            perimeter += points.get(i).distance(points.get(i + 1));
        }
        perimeter += points.get(points.size()).distance(points.get(0));
        return perimeter;
    }

    public void rotate(double angle) {
        rotate(angle, center.x, center.z);
    }

    public void rotate(double angle, Vector3 axle) {
        rotate(angle, axle.x, axle.z);
    }

    public void rotate(double angle, double x, double z) {
        for (int i = 0; i < points.size(); i++) {
            Vector3 p = points.get(i);
            p.rotate(angle, x, z);
        }
        // rotate the center if it's not equal to the axle.
        if (x != center.x || z != center.z) {
            center.rotate(angle, x, z);
        }
    }

    public void translate(double x, double z) {
        for (int i = 0; i < points.size(); i++) {
            points.get(i).x += x;
            points.get(i).z += z;
        }
        center.x += x;
        center.z += z;
    }

    public boolean move() {
        if (translation != null && translationCount > 0) {
            translate(translation);
            translationCount--;
            if (translationCount <= 0) {
                translation = null;
            }
            return false;
        }
        return true;
    }

    public void translate(Vector3 translation) {
        translate(translation.x, translation.z);
    }

    public void translateTo(double x, double z) {
        double xIncrement = x - center.x;
        double yIncrement = z - center.z;
        center.x = x;
        center.z = z;
        for (int i = 0; i < points.size(); i++) {
            points.get(i).x += xIncrement;
            points.get(i).z += yIncrement;
        }
    }

    public void translateTo(Vector3 newCentre) {
        translateTo(newCentre.x, newCentre.z);
    }

    public void translateToOrigin() {
        translateTo(0, 0);
    }

    public void scale(double xMultiplier, double zMultiplier, double x, double z) {
        double incX;
        double incZ;
        for (int i = 0; i < points.size(); i++) {
            incX = points.get(i).x - x;
            incZ = points.get(i).z - z;
            incX *= xMultiplier;
            incZ *= zMultiplier;
            points.get(i).x = x + incX;
            points.get(i).z = z + incZ;
        }
        incX = center.x - x;
        incZ = center.z - z;
        incX *= xMultiplier;
        incZ *= zMultiplier;
        center.x = x + incX;
        center.z = z + incZ;
        this.calcArea();
        this.calcRadius();
    }

    public void scale(double multiplierX, double multiplierZ) {
        scale(multiplierX, multiplierZ, getCenter().x, getCenter().z);
    }

    public void scale(double multiplierX, double multiplierZ, Vector3 p) {
        scale(multiplierX, multiplierZ, p.x, p.z);
    }

    public void scale(double multiplier) {
        scale(multiplier, multiplier, getCenter().x, getCenter().z);
    }

    public void scale(double multiplier, Vector3 p) {
        scale(multiplier, multiplier, p.x, p.z);
    }

    public Vector3 getBoundaryPointFromCenterToward(Vector3 endPoint) {
        double distToExtendOutTo = 3 * getRadius();
        double xCoord = getCenter().x;
        double yCoord = getCenter().z;
        double xDiff = endPoint.x - getCenter().x;
        double yDiff = endPoint.z - getCenter().z;
        if (xDiff == 0 && yDiff == 0) {
            yCoord += distToExtendOutTo;
        } else if (xDiff == 0) {
            yCoord += distToExtendOutTo * Math.signum(yDiff);
        } else if (yDiff == 0) {
            xCoord += distToExtendOutTo * Math.signum(xDiff);
        } else {
            xCoord += distToExtendOutTo * Math.abs(xDiff / (xDiff + yDiff)) * Math.signum(xDiff);
            yCoord += distToExtendOutTo * Math.abs(yDiff / (xDiff + yDiff)) * Math.signum(yDiff);
        }
        Vector3 boundaryPoint = getClosestIntersectionToFirstFromSecond(getCenter().x, getCenter().z, xCoord, yCoord);
        return boundaryPoint;
    }

    public boolean isCounterClockWise() {
        return counterClockWise;
    }

    public void reversePointOrder() {
        counterClockWise = !counterClockWise;
        ArrayList<Vector3> tempPoints = new ArrayList<>(points.size());
        for (int i = points.size() - 1; i >= 0; i--) {
            tempPoints.add(points.get(i));
        }
        points.clear();
        points.addAll(tempPoints);
    }

    public boolean isValidNoLineIntersections() {
        return isValidNoLineIntersections(points);
    }

    public static boolean isValidNoLineIntersections(java.util.List<Vector3> points) {
        for (int i = 0; i < points.size(); i++) {
            int iPlus = (i + 1 >= points.size() ? 0 : i + 1);
            for (int j = i + 2; j < points.size(); j++) {
                int jPlus = (j + 1 >= points.size() ? 0 : j + 1);
                if (i == jPlus) {
                    continue;
                }
                if (Vector3.linesIntersect(points.get(i), points.get(iPlus), points.get(j), points.get(jPlus))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidNoConsecutiveEqualPoints() {
        return isValidNoConsecutiveEqualPoints(points);
    }

    public static boolean isValidNoConsecutiveEqualPoints(java.util.List<Vector3> points) {
        Vector3 pointIBefore = (!points.isEmpty() ? points.get(points.size() - 1) : null);
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            if (pointI.x == pointIBefore.x && pointI.z == pointIBefore.z) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidNoEqualPoints() {
        return isValidNoEqualPoints(points);
    }

    public static boolean isValidNoEqualPoints(java.util.List<Vector3> points) {
        for (int i = 0; i < points.size(); i++) {
            Vector3 pointI = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                Vector3 pointJ = points.get(j);
                if (pointI.x == pointJ.x && pointI.z == pointJ.z) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean printOffendingIntersectingLines(java.util.List<Vector3> points) {
        boolean linesIntersect = false;
        for (int i = 0; i < points.size(); i++) {
            int iPlus = (i + 1 >= points.size() ? 0 : i + 1);
            for (int j = i + 2; j < points.size(); j++) {
                int jPlus = (j + 1 >= points.size() ? 0 : j + 1);
                if (i == jPlus) {
                    continue;
                }
                if (Vector3.linesIntersect(points.get(i), points.get(iPlus), points.get(j), points.get(jPlus))) {
                    System.out.println(KPolygon.class.getSimpleName() + ": the line between points.get(" + i + ") & points.get(" + iPlus + ") intersects with the line between points.get(" + j + ") & points.get(" + jPlus + ")");
                    System.out.println(KPolygon.class.getSimpleName() + ": the line between points.get(" + i + ") == " + points.get(i));
                    System.out.println(KPolygon.class.getSimpleName() + ": the line between points.get(" + iPlus + ") == " + points.get(iPlus));
                    System.out.println(KPolygon.class.getSimpleName() + ": the line between points.get(" + j + ") == " + points.get(j));
                    System.out.println(KPolygon.class.getSimpleName() + ": the line between points.get(" + jPlus + ") == " + points.get(jPlus));
                    linesIntersect = true;
                }
            }
        }
        return linesIntersect;
    }

    public KPolygon copy() {
        KPolygon polygon = new KPolygon(this);
        return polygon;
    }

    /**
     * Needed by PolygonHolder.
     *
     * @return This KPolygon.
     */
    public KPolygon getPolygon() {
        return this;
    }

    public int getNextIndex(int i) {
        int iPlus = i + 1;
        return (iPlus >= points.size() ? 0 : iPlus);
    }

    public int getPrevIndex(int i) {
        int iMinus = i - 1;
        return (iMinus < 0 ? points.size() - 1 : iMinus);
    }

    public Vector3 getNextPoint(int i) {
        return points.get(getNextIndex(i));
    }

    public Vector3 getPrevPoint(int i) {
        return points.get(getPrevIndex(i));
    }

    public String toString() {
        String str = getClass().getName() + "@" + Integer.toHexString(hashCode());
        if (getCenter() != null) {
            str += ", center == " + getCenter().toString();
        }
        str += ", area == " + area;
        str += ", radius == " + radius;
        if (points != null) {
            //str += ", points == " + points.toString();
            str += ", points.size() == " + points.size() + ":\n";
            for (int i = 0; i < points.size(); i++) {
                Vector3 p = points.get(i);
                str += "  i == " + i + ", " + p + "\n";
            }
        }
        return str;
    }

    // Note: The following methods are neded to implement java.awt.geom.Shape.
    @Override
    public Rectangle2D.Double getBounds2D() {
        double[] bounds = getBoundsArray();
        return new Rectangle2D.Double(bounds[0], bounds[1], bounds[2], bounds[3]);
    }

    public Rectangle getBounds() {
        double[] bounds = getBoundsArray();
        return new Rectangle((int) (bounds[0]), (int) (bounds[1]), (int) Math.ceil(bounds[2]), (int) Math.ceil(bounds[3]));
    }

    /**
     * Unlike Shape.intersects, this method is exact. Note that this method
     * should really be called overlaps(x,z,w,h) since it doesn't just test for
     * line-line intersection.
     *
     * @param x
     * @param z
     * @param w
     * @param h
     * @return Returns true if the given rectangle overlaps this polygon.
     */
    public boolean intersects(double x, double z, double w, double h) {
        if (x + w < center.x - radius
                || x > center.x + radius
                || z + h < center.z - radius
                || z > center.z + radius) {
            return false;
        }
        for (int i = 0; i < points.size(); i++) {
            int nextI = (i + 1 >= points.size() ? 0 : i + 1);
            if (Vector3.linesIntersect(x, z, x + w, z, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x, z, x, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x, z + h, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x + w, z, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)) {
                return true;
            }
        }
        double px = points.get(0).x;
        double py = points.get(0).z;
        if (px > x && px < x + w && py > z && py < z + h) {
            return true;
        }
        if (contains(x, z) == true) {
            return true;
        }
        return false;
    }

    public boolean intersects(Rectangle2D r) {
        return this.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * Unlike Shape.contains, this method is exact.
     *
     * @param x
     * @param z
     * @param w
     * @param h
     * @return Returns true if the given rectangle wholly fits inside this
     * polygon with no perimeter intersections.
     */
    public boolean contains(double x, double z, double w, double h) {
        if (x + w < center.x - radius
                || x > center.x + radius
                || z + h < center.z - radius
                || z > center.z + radius) {
            return false;
        }
        for (int i = 0; i < points.size(); i++) {
            int nextI = (i + 1 >= points.size() ? 0 : i + 1);
            if (Vector3.linesIntersect(x, z, x + w, z, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x, z, x, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x, z + h, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)
                    || Vector3.linesIntersect(x + w, z, x + w, z + h, points.get(i).x, points.get(i).z, points.get(nextI).x, points.get(nextI).z)) {
                return false;
            }
        }
        double px = points.get(0).x;
        double py = points.get(0).z;
        if (px > x && px < x + w && py > z && py < z + h) {
            return false;
        }
        return contains(x, z) == true;
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return this.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new KPolygonIterator(this, at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new KPolygonIterator(this, at);
    }

    public class KPolygonIterator implements PathIterator {

        int type = PathIterator.SEG_MOVETO;
        int index = 0;
        KPolygon polygon;
        Vector3 currentPoint;
        AffineTransform affine;

        double[] singlePointSetDouble = new double[2];

        KPolygonIterator(KPolygon kPolygon) {
            this(kPolygon, null);
        }

        KPolygonIterator(KPolygon kPolygon, AffineTransform at) {
            this.polygon = kPolygon;
            this.affine = at;
            currentPoint = polygon.getPoint(0);
        }

        public int getWindingRule() {
            return PathIterator.WIND_EVEN_ODD;
        }

        @Override
        public boolean isDone() {
            if (index == polygon.points.size() + 1) {
                return true;
            }
            return false;
        }

        @Override
        public void next() {
            index++;
        }

        public void assignPointAndType() {
            if (index == 0) {
                currentPoint = polygon.getPoint(0);
                type = PathIterator.SEG_MOVETO;
            } else if (index == polygon.points.size()) {
                type = PathIterator.SEG_CLOSE;
            } else {
                currentPoint = polygon.getPoint(index);
                type = PathIterator.SEG_LINETO;
            }
        }

        @Override
        public int currentSegment(float[] coords) {
            assignPointAndType();
            if (type != PathIterator.SEG_CLOSE) {
                if (affine != null) {
                    float[] singlePointSetFloat = new float[2];
                    singlePointSetFloat[0] = (float) currentPoint.x;
                    singlePointSetFloat[1] = (float) currentPoint.z;
                    affine.transform(singlePointSetFloat, 0, coords, 0, 1);
                } else {
                    coords[0] = (float) currentPoint.x;
                    coords[1] = (float) currentPoint.z;
                }
            }
            return type;
        }

        @Override
        public int currentSegment(double[] coords) {
            assignPointAndType();
            if (type != PathIterator.SEG_CLOSE) {
                if (affine != null) {
                    singlePointSetDouble[0] = currentPoint.x;
                    singlePointSetDouble[1] = currentPoint.z;
                    affine.transform(singlePointSetDouble, 0, coords, 0, 1);
                } else {
                    coords[0] = currentPoint.x;
                    coords[1] = currentPoint.z;
                }
            }
            return type;
        }
    }

}
