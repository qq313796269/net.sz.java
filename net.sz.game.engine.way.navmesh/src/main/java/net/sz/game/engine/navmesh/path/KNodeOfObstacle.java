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
package net.sz.game.engine.navmesh.path;

import java.util.*;
import net.sz.game.engine.navmesh.KPolygon;
import net.sz.game.engine.navmesh.Vector3;

/**
 *
 * @author Keith Woodward
 */
public class KNodeOfObstacle extends KNode {

    public PathBlockingObstacle obstacle;
    public int pointNum;
    public boolean concave;
    // contained == TRUE_VALUE when this node is inside another obstacle's innerPolygon.
    // contained == FALSE_VALUE when this node is not inside any other obstacle's innerPolygon.
    // contained == UNKNOWN_VALUE when this node has not had the calcContained method called on it.
    public int contained;
    public static final int FALSE_VALUE = 0;
    public static final int TRUE_VALUE = 1;
    public static final int UNKNOWN_VALUE = 2;

    public KNodeOfObstacle() {
    }

    public KNodeOfObstacle(PathBlockingObstacle obstacle, int pointNum) {
        super(obstacle.getOuterPolygon().getPoint(pointNum).copy());
        this.obstacle = obstacle;
        this.pointNum = pointNum;
        calcConcave();
        contained = UNKNOWN_VALUE;
    }

    public void clearForReuse() {
        super.clearForReuse();
        obstacle = null;
        pointNum = -1;
        concave = false;
        contained = UNKNOWN_VALUE;
    }

    public PathBlockingObstacle getObstacle() {
        return obstacle;
    }

    public void setObstacle(PathBlockingObstacleImpl obstacle) {
        this.obstacle = obstacle;
    }

    public void calcConcave() {
        KPolygon polygon = obstacle.getOuterPolygon();
        int pointBeforeNum = (pointNum - 1 < 0 ? polygon.getPoints().size() - 1 : pointNum - 1);
        int pointAfterNum = (pointNum + 1 >= polygon.getPoints().size() ? 0 : pointNum + 1);
        Vector3 pointBefore = polygon.getPoint(pointBeforeNum);
        Vector3 pointAfter = polygon.getPoint(pointAfterNum);
        if (polygon.isCounterClockWise()) {
            if (point.relCCW(pointBefore, pointAfter) > 0) {
                concave = true;
            } else {
                concave = false;
            }

        } else {
            if (point.relCCW(pointBefore, pointAfter) < 0) {
                concave = true;
            } else {
                concave = false;
            }
        }
    }

    public boolean isConcave() {
        return concave;
    }

    public void calcContained(ArrayList<? extends PathBlockingObstacle> allObstacles) {
        contained = FALSE_VALUE;
        ObstacleLoop:
        for (int n = 0; n < allObstacles.size(); n++) {
            PathBlockingObstacle testOb3 = allObstacles.get(n);
            // Skip the obstacle if it contains this node
            if (testOb3 == this.getObstacle()) {
                continue;
            }
            // Test if testOb3.getInnerPolygon() can possibly contain this point
            KPolygon innerPolygon = testOb3.getInnerPolygon();
            if (innerPolygon.getCenter().distance(getPoint()) > innerPolygon.getRadius()) {
                continue ObstacleLoop;
            }
            // Check that this point is not inside testOb3
            if (innerPolygon.contains(getPoint())) {
                contained = TRUE_VALUE;
                break ObstacleLoop;
            }
        }
    }

    public void resetContainedToUnknown() {
        contained = UNKNOWN_VALUE;
    }

    public int getContained() {
        return contained;
    }

    public void setContained(int containedStatus) {
        this.contained = containedStatus;
    }

    public int getPointNum() {
        return pointNum;
    }
}
