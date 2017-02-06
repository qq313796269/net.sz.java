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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

/**
 *
 * @author Keith
 */
public class PolygonBufferer {

    public PolygonConverter polygonConverter = new PolygonConverter();
    private final static float MIN = 0.05f;

    public PolygonBufferer() {
    }

    public KPolygon buffer(KPolygon originalPolygon, double bufferAmount, int numPointsInAQuadrant) {
//        return originalPolygon.copy();
        if (bufferAmount == 0) {
            return originalPolygon.copy();
        }
        com.vividsolutions.jts.geom.Polygon jtsPolygon = polygonConverter.makeJTSPolygonFrom(originalPolygon);
        com.vividsolutions.jts.geom.Polygon bufferedJTSPolygon = null;
        jtsPolygon.setSRID(numPointsInAQuadrant);
        Geometry bufferedGeometry = jtsPolygon.buffer(bufferAmount, numPointsInAQuadrant);
        if (bufferedGeometry instanceof com.vividsolutions.jts.geom.Polygon) {
            bufferedJTSPolygon = (com.vividsolutions.jts.geom.Polygon) bufferedGeometry;
        } else if (bufferedGeometry instanceof com.vividsolutions.jts.geom.MultiPolygon) {
            MultiPolygon multiPolygon = (com.vividsolutions.jts.geom.MultiPolygon) bufferedGeometry;
            // use the first polygon
            bufferedJTSPolygon = (com.vividsolutions.jts.geom.Polygon) multiPolygon.getGeometryN(0);
        } else {
            System.err.println(this.getClass().getSimpleName() + ": JTS didn't make a proper polygon, this might be because the outerPolygon is too small, so that after it's shrunk, it disappears or makes more than one Polygon.");
            return null;
        }
        KPolygon bufferedPolygon = polygonConverter.makeKPolygonFromExterior(bufferedJTSPolygon);
        if (bufferedPolygon == null) {
            System.out.println(this.getClass().getSimpleName() + ": bufferedPolygon == null");
            System.out.println(this.getClass().getSimpleName() + ": bufferedGeometry.getClass().getSimpleName() == " + bufferedGeometry.getClass().getSimpleName());
            System.out.println(this.getClass().getSimpleName() + ": bufferedJTSPolygon.getCoordinates().length == " + bufferedJTSPolygon.getCoordinates().length);
            for (int j = 0; j < bufferedJTSPolygon.getCoordinates().length; j++) {
                System.out.println(this.getClass().getSimpleName() + ": " + bufferedJTSPolygon.getCoordinates()[j].x + ", " + bufferedJTSPolygon.getCoordinates()[j].y);
            }
            System.out.println(this.getClass().getSimpleName() + ": originalPolygon.toString() == " + originalPolygon.toString());
            return null;
        }
        if (bufferedPolygon.isCounterClockWise() != originalPolygon.isCounterClockWise()) {
            bufferedPolygon.reversePointOrder();
        }
        for (Vector3 point : bufferedPolygon.getPoints()) {
            for (Vector3 ori : originalPolygon.getPoints()) {
                if (Math.abs(point.x - ori.x) < MIN && Math.abs(point.z - ori.z) < MIN) {
                    point.y = ori.y;
                    break;
                }
            }
        }
        return bufferedPolygon;
    }
}
