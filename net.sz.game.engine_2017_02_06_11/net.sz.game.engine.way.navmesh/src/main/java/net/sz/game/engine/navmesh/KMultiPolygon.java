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
/**
 * 特别鸣谢修仙项目组负责人吴章义
 */
package net.sz.game.engine.navmesh;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 *
 * @author Keith
 */
public class KMultiPolygon implements Shape, PolygonHolder {

    public ArrayList<KPolygon> polygons;

    public KMultiPolygon() {
    }

    public KMultiPolygon(KPolygon polygon) {
        this.polygons = new ArrayList<>();
        polygons.add(polygon);
    }

    public KMultiPolygon(ArrayList<KPolygon> polygons) {
        this.polygons = polygons;
    }

    public KMultiPolygon(KPolygon... polygonArray) {
        polygons = new ArrayList<>();
        for (int i = 0; i < polygonArray.length; i++) {
            polygons.add(polygonArray[i]);
        }
    }

    public KMultiPolygon copy() {
        ArrayList<KPolygon> newPolygons = new ArrayList<>();
        for (int i = 0; i < polygons.size(); i++) {
            newPolygons.add(polygons.get(i).copy());
        }
        return new KMultiPolygon(newPolygons);
    }

    public void translate(double x, double y) {
        for (int i = 0; i < polygons.size(); i++) {
            KPolygon polygon = polygons.get(i);
            polygon.translate(x, y);
        }
    }

    public void translate(Vector3 translation) {
        translate(translation.x, translation.z);
    }

    public void translateTo(double x, double y) {
        Vector3 center = this.getExteriorPolygon().getCenter();
        double xIncrement = x - center.x;
        double yIncrement = y - center.z;
        for (int i = 0; i < polygons.size(); i++) {
            KPolygon polygon = polygons.get(i);
            polygon.translate(xIncrement, yIncrement);
        }
    }

    public void translateTo(Vector3 newCentre) {
        translateTo(newCentre.x, newCentre.z);
    }

    public void translateToOrigin() {
        translateTo(0, 0);
    }

    public void rotate(double angle) {
        Vector3 center = this.getExteriorPolygon().getCenter();
        rotate(angle, center.x, center.z);
    }

    public void rotate(double angle, Vector3 axle) {
        rotate(angle, axle.x, axle.z);
    }

    public void rotate(double angle, double x, double y) {
        for (int i = 0; i < polygons.size(); i++) {
            KPolygon polygon = polygons.get(i);
            polygon.rotate(angle, x, y);
        }
    }

    public void scale(double xMultiplier, double yMultiplier, double x, double y) {
        for (int i = 0; i < polygons.size(); i++) {
            KPolygon polygon = polygons.get(i);
            polygon.scale(xMultiplier, yMultiplier, x, y);
        }
    }

    public void scale(double multiplierX, double multiplierY) {
        Vector3 center = getExteriorPolygon().getCenter();
        scale(multiplierX, multiplierY, center.x, center.z);
    }

    public void scale(double multiplierX, double multiplierY, Vector3 p) {
        scale(multiplierX, multiplierY, p.x, p.z);
    }

    public void scale(double multiplier) {
        Vector3 center = getExteriorPolygon().getCenter();
        scale(multiplier, multiplier, center.x, center.z);
    }

    public void scale(double multiplier, Vector3 p) {
        scale(multiplier, multiplier, p.x, p.z);
    }

    public KPolygon getExteriorPolygon() {
        if (polygons.size() > 0) {
            return polygons.get(0);
        }
        return null;
    }

    public KPolygon getPolygon(int index) {
        return polygons.get(index);

    }

    public ArrayList<KPolygon> getPolygons() {
        return polygons;
    }

    public ArrayList<KPolygon> getInteriorPolygonsCopy() {
        ArrayList<KPolygon> internalPolygons = new ArrayList<>();
        for (int i = 1; i < polygons.size(); i++) {
            internalPolygons.add(polygons.get(i));
        }
        return internalPolygons;
    }

    /**
     * Needed by PolygonHolder.
     *
     * @return This KPolygon.
     */
    public KPolygon getPolygon() {
        return getExteriorPolygon();
    }

    // Note: The following methods are neded to implement java.awt.geom.Shape.
    public Rectangle2D.Double getBounds2D() {
        return getExteriorPolygon().getBounds2D();
    }

    public Rectangle getBounds() {
        return getExteriorPolygon().getBounds();
    }

    public boolean intersects(double x, double y, double w, double h) {
        return getExteriorPolygon().intersects(x, y, w, h);
    }

    public boolean intersects(Rectangle2D r) {
        return this.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    public boolean contains(Vector3 p) {
        return contains(p.x, p.z);
    }

    public boolean contains(double x, double y) {
        if (getExteriorPolygon().contains(x, y) == true) {
            int crossings = 0;
            for (int h = 1; h < polygons.size(); h++) {
                List<Vector3> points = polygons.get(h).getPoints();
                Vector3 pointIBefore = (points.size() != 0 ? points.get(points.size() - 1) : null);
                for (int i = 0; i < points.size(); i++) {
                    Vector3 pointI = points.get(i);
                    if (((pointIBefore.z <= y && y < pointI.z)
                            || (pointI.z <= y && y < pointIBefore.z))
                            && x < ((pointI.x - pointIBefore.x) / (pointI.z - pointIBefore.z) * (y - pointIBefore.z) + pointIBefore.x)) {
                        crossings++;
                    }
                    pointIBefore = pointI;
                }
            }
            return (crossings % 2 != 1);
        } else {
            return false;
        }
    }

    public boolean contains(double x, double y, double w, double h) {
        return getExteriorPolygon().contains(x, y, w, h);
    }

    public boolean contains(Rectangle2D r) {
        return this.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return new KMultiPolygonIterator(this, at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new KMultiPolygonIterator(this, at);
    }

    public class KMultiPolygonIterator implements PathIterator {

        int type = PathIterator.SEG_MOVETO;
        int index = 0;
        int polygonNum = 0;
        KPolygon currentPolygon;
        Vector3 currentPoint;
        KMultiPolygon multiPolygon;
        AffineTransform affine;

        double[] singlePointSetDouble = new double[2];

        KMultiPolygonIterator(KMultiPolygon kPolygon) {
            this(kPolygon, null);
        }

        KMultiPolygonIterator(KMultiPolygon kPolygon, AffineTransform at) {
            this.multiPolygon = kPolygon;
            this.affine = at;
            currentPolygon = multiPolygon.getPolygon(polygonNum);
            currentPoint = currentPolygon.getPoint(0);
        }

        public int getWindingRule() {
            return PathIterator.WIND_EVEN_ODD;
        }

        public boolean isDone() {
            if (polygonNum >= polygons.size()) {
                return true;
            }
            return false;
        }

        public void next() {
            index++;
            if (index == currentPolygon.getPoints().size() + 1) {
                polygonNum++;
                index = 0;
            }
        }

        public void assignPointAndType() {
            currentPolygon = multiPolygon.getPolygon(polygonNum);
            if (index == 0) {
                currentPoint = currentPolygon.getPoint(0);
                type = PathIterator.SEG_MOVETO;
            } else if (index == currentPolygon.getPoints().size()) {
                type = PathIterator.SEG_CLOSE;
            } else {
                currentPoint = currentPolygon.getPoint(index);
                type = PathIterator.SEG_LINETO;
            }
        }

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

//	public static void main(String[] args){
//		KPolygon poly1 = KPolygon.createRegularPolygon(10, 100);
//		KPolygon poly2 = KPolygon.createRegularPolygon(10, 50);
//		poly1.translate(10, 10);
//		poly2.translate(10, 10);
//		KMultiPolygon multiPoly = new KMultiPolygon(poly1, poly2);
//		Shape shape = (new PolygonConverter()).makePath2DFrom(multiPoly);
//		{
//			System.out.println("For shape:");
//			PathIterator pathIterator = shape.getPathIterator(null);
//			double[] coords = new double[6];
//			while (pathIterator.isDone() == false){
//				int type = pathIterator.currentSegment(coords);
//				if (type == PathIterator.SEG_MOVETO){
//					System.out.println(": SEG_MOVETO, "+coords[0]+", "+coords[1]);
//					pathIterator.next();
//				}else if (type == PathIterator.SEG_LINETO){
//					System.out.println(": SEG_LINETO, "+coords[0]+", "+coords[1]);
//					pathIterator.next();
//				}else if (type == PathIterator.SEG_CLOSE){
//					System.out.println(": SEG_CLOSE, "+coords[0]+", "+coords[1]);
//					pathIterator.next();
//				}
//			}
//		}
//		{
//			System.out.println("For multiPoly:");
//			PathIterator pathIterator = multiPoly.getPathIterator(null);
//			double[] coords = new double[6];
//			while (pathIterator.isDone() == false){
//				int type = pathIterator.currentSegment(coords);
//				if (type == PathIterator.SEG_MOVETO){
//					System.out.println(": SEG_MOVETO, "+coords[0]+", "+coords[1]);
//					pathIterator.next();
//				}else if (type == PathIterator.SEG_LINETO){
//					System.out.println(": SEG_LINETO, "+coords[0]+", "+coords[1]);
//					pathIterator.next();
//				}else if (type == PathIterator.SEG_CLOSE){
//					System.out.println(": SEG_CLOSE, "+coords[0]+", "+coords[1]);
//					pathIterator.next();
//				}
//			}
//		}
//	}
//	Path2D.Double path = new Path2D.Double();
//				path.setWindingRule(Path2D.WIND_EVEN_ODD);
//				{
//					double x = 0;
//					double z = 0;
//					double d = 400;
//					path.moveTo(x, z);
//					path.lineTo(x+d, z+0);
//					path.lineTo(x+d, z+d);
//					path.lineTo(x+0, z+d);
//					path.closePath();
//				}
//				{
//					double x = 50;
//					double z = 50;
//					double d = 300;
//					path.moveTo(x, z);
//					path.lineTo(x+d, z+0);
//					path.lineTo(x+d, z+d);
//					path.lineTo(x+0, z+d);
//					path.closePath();
//				}
//				{
//					double x = 100;
//					double z = 100;
//					double d = 200;
//					path.moveTo(x, z);
//					path.lineTo(x+d, z+0);
//					path.lineTo(x+d, z+d);
//					path.lineTo(x+0, z+d);
//					path.closePath();
//				}
//				//PolygonConverter pc = new PolygonConverter();
//				if (path.contains(lastMousePointInWorldCoords.x, lastMousePointInWorldCoords.z)){
//					g.setColor(Color.MAGENTA);
//				}else{
//					g.setColor(Color.BLACK);
//				}
//
//				g.draw(path);
//	public KMultiPolygon parentMultiPolygon;
//	public KPolygon polygon;
//	// level 0 has the top polygon.
//	// level 1 has holes within the first polygon.
//	// level 2 has polygons within the level 1 holes.
//	// level 3 has holes within the level 2 polygons.
//	// etc
//	public int level;
//	public ArrayList<KMultiPolygon> innerPolygons;
//
//
//	public KMultiPolygon(){
//	}
//	public KMultiPolygon(KPolygon polygon){
//		this(null, polygon, 0, new ArrayList<KMultiPolygon>());
//	}
//	public KMultiPolygon(KMultiPolygon parentMultiPolygon, KPolygon polygon, int level){
//		this(parentMultiPolygon, polygon, level, new ArrayList<KMultiPolygon>());
//	}
//	public KMultiPolygon(KMultiPolygon parentMultiPolygon, KPolygon polygon, int level, ArrayList<KMultiPolygon> innerPolygons){
//		this.parentMultiPolygon = parentMultiPolygon;
//		this.level = level;
//		this.polygon = polygon;
//		this.innerPolygons = innerPolygons;
//	}
//
//
//	public boolean isHole(){
//		// if level is odd, it is a hole.
//		return (level % 2 == 1);
//	}
//
//	public ArrayList<KMultiPolygon> getInnerPolygons() {
//		return innerPolygons;
//	}
//
//	public void setInnerPolygons(ArrayList<KMultiPolygon> innerPolygons) {
//		this.innerPolygons = innerPolygons;
//	}
//
//	public int getLevel() {
//		return level;
//	}
//
//	public void setLevel(int level) {
//		this.level = level;
//	}
//
//	public KMultiPolygon getParentMultiPolygon() {
//		return parentMultiPolygon;
//	}
//
//	public void setParentMultiPolygon(KMultiPolygon parentMultiPolygon) {
//		this.parentMultiPolygon = parentMultiPolygon;
//	}
//
//	public KPolygon getPolygon() {
//		return polygon;
//	}
//
//	public void setPolygon(KPolygon polygon) {
//		this.polygon = polygon;
//	}
}
