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

/**
 * Used to help sort the obstacle lists.
 *
 * @author Keith
 */
public class ObstDistAndQuad implements Comparable {

    public PathBlockingObstacle obst;
    public double distNodeToCenterLessRadiusSqSigned;
    public int xIndicator;
    public int yIndicator;

    public ObstDistAndQuad(PathBlockingObstacle obst, double distNodeToCenterLessRadiusSqSigned, int xIndicator, int yIndicator) {
        this.obst = obst;
        this.distNodeToCenterLessRadiusSqSigned = distNodeToCenterLessRadiusSqSigned;
        this.xIndicator = xIndicator;
        this.yIndicator = yIndicator;
    }

    @Override
    public int compareTo(Object ob) {
        if (!(ob instanceof ObstDistAndQuad)) {
            return 0;
        }
        double obDist = ((ObstDistAndQuad) ob).getDistNodeToCenterLessRadiusSqSigned();
        if (distNodeToCenterLessRadiusSqSigned > obDist) {
            return 1;
        } else if (distNodeToCenterLessRadiusSqSigned < obDist) {
            return -1;
        } else {
            return 0;
        }
    }

    public double getDistNodeToCenterLessRadiusSqSigned() {
        return distNodeToCenterLessRadiusSqSigned;
    }

    public PathBlockingObstacle getObst() {
        return obst;
    }

    public int getXIndicator() {
        return xIndicator;
    }

    public int getYIndicator() {
        return yIndicator;
    }

}
