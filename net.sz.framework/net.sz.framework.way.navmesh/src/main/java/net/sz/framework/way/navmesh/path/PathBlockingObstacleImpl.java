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
package net.sz.framework.way.navmesh.path;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.Serializable;
import net.sz.framework.way.navmesh.PolygonBufferer;
import java.util.*;
import net.sz.framework.way.navmesh.KPolygon;
import net.sz.framework.way.navmesh.Vector3;

/**
 *
 * @author Keith Woodward
 */
public final class PathBlockingObstacleImpl implements Serializable, PathBlockingObstacle, Cloneable {

    public static float BUFFER_AMOUNT = 3f;
    public static int NUM_POINTS_IN_A_QUADRANT = 0;

    public KPolygon outerPolygon;
    public KPolygon innerPolygon;
    public ArrayList<KNodeOfObstacle> nodes;
    private ArrayList<Vector3> randomPoints = new ArrayList();
    private int randomNum;

    @Override
    protected PathBlockingObstacleImpl clone() throws CloneNotSupportedException {
        PathBlockingObstacleImpl pathBlockingObstacleImpl = (PathBlockingObstacleImpl) super.clone();
        if (pathBlockingObstacleImpl.outerPolygon != null) {
            pathBlockingObstacleImpl.outerPolygon = this.outerPolygon.clone();
        }

        if (pathBlockingObstacleImpl.innerPolygon != null) {
            pathBlockingObstacleImpl.innerPolygon = this.innerPolygon.clone();
        }

        pathBlockingObstacleImpl.randomPoints = new ArrayList<>();
        for (Vector3 node : randomPoints) {
            pathBlockingObstacleImpl.randomPoints.add(node.clone());
        }

        pathBlockingObstacleImpl.nodes = new ArrayList<>();
        for (KNodeOfObstacle node : nodes) {
            pathBlockingObstacleImpl.nodes.add(node.clone());
        }

        return pathBlockingObstacleImpl;
    }

    public PathBlockingObstacleImpl() {
    }

    public PathBlockingObstacleImpl(KPolygon outerPolygon, KPolygon innerPolygon) {
        this.outerPolygon = outerPolygon;
        this.innerPolygon = innerPolygon;
        resetNodes();
    }

    /**
     * @param points
     */
    public void addRandomPoints(Coordinate[] points) {
        for (Coordinate point : points) {
            Vector3 p = new Vector3(point.x, getOuterPolygon().getY(), point.y);
            this.randomPoints.add(p);
        }
        randomNum = this.randomPoints.size();
    }

    public void resetNodes() {
        if (nodes == null) {
            nodes = new ArrayList<>();
            for (int i = 0; i < this.outerPolygon.getPoints().size(); i++) {
                nodes.add(new KNodeOfObstacle(this, i));
            }
        } else if (nodes.size() != getOuterPolygon().getPoints().size()) {
            nodes.clear();
            for (int i = 0; i < this.outerPolygon.getPoints().size(); i++) {
                nodes.add(new KNodeOfObstacle(this, i));
            }
        } else {
            for (int j = 0; j < nodes.size(); j++) {
                KNodeOfObstacle node = nodes.get(j);
                Vector3 outerPolygonPoint = getOuterPolygon().getPoint(j);
                node.getPoint().x = outerPolygonPoint.x;
                node.getPoint().z = outerPolygonPoint.z;
            }
        }
    }

    public static PathBlockingObstacleImpl createObstacleFromOuterPolygon(KPolygon outerPolygon) {
        PolygonBufferer polygonBufferer = new PolygonBufferer();
        KPolygon innerPolygon = polygonBufferer.buffer(outerPolygon, 0, NUM_POINTS_IN_A_QUADRANT);
        if (innerPolygon == null) {
            // there was an error so return null;
            return null;
        }
        PathBlockingObstacleImpl pathBlockingObstacleImpl = new PathBlockingObstacleImpl(outerPolygon, innerPolygon);
        return pathBlockingObstacleImpl;
    }

    public static PathBlockingObstacleImpl createObstacleFromInnerPolygon(KPolygon innerPolygon) {
        PolygonBufferer polygonBufferer = new PolygonBufferer();
        KPolygon outerPolygon = polygonBufferer.buffer(innerPolygon, BUFFER_AMOUNT, NUM_POINTS_IN_A_QUADRANT);
        if (outerPolygon == null) {
            // there was an error so return null;
            return null;
        }
        PathBlockingObstacleImpl pathBlockingObstacleImpl = new PathBlockingObstacleImpl(outerPolygon, innerPolygon);
        return pathBlockingObstacleImpl;
    }

    @Override
    public ArrayList<KNodeOfObstacle> getNodes() {
        return nodes;
    }

    @Override
    public KPolygon getOuterPolygon() {
        return outerPolygon;
    }

    @Override
    public KPolygon getInnerPolygon() {
        return innerPolygon;
    }

    @Override
    public KPolygon getPolygon() {
        return this.getInnerPolygon();
    }

    public void setOuterPolygon(KPolygon outerPolygon) {
        this.outerPolygon = outerPolygon;
    }

    public void setInnerPolygon(KPolygon innerPolygon) {
        this.innerPolygon = innerPolygon;
    }

    /**
     * @return the randomNum
     */
    public int getRandomNum() {
        return randomNum;
    }

    /**
     * @return the randomPoints
     */
    public ArrayList<Vector3> getRandomPoints() {
        return randomPoints;
    }
}
