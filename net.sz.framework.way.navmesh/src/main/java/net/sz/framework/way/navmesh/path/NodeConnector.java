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

import java.io.Serializable;
import java.util.*;
import net.sz.framework.way.navmesh.KPolygon;
import net.sz.framework.way.navmesh.Vector3;

/**
 *
 * @author woodwardk
 */
public class NodeConnector<T extends PathBlockingObstacle> implements Serializable, Cloneable {
    // This list is cleared after each method call rather than created anew, to avoid creating new lists all the time.

    public NodeConnector() {
    }

    @Override
    protected NodeConnector clone() throws CloneNotSupportedException {
        return (NodeConnector) super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public void reConnectNode(KNodeOfObstacle node, PathBlockingObstacle obstacle, double maxConnectionDistance, List<T> obstacles) {
//		ct.setEnabled(false);
//		ct.click("startNode.clearConnectedNodes();");
        node.clearConnectedNodes();
        // Test to see if it's ok to ignore this startNode since it's
        // concave (inward-pointing) or it's contained by an obstacle.
        if (node.isConcave() || node.getContained() == KNodeOfObstacle.TRUE_VALUE) {
//			ct.lastClick();
            return;
        }
        reConnectNodeAfterChecks(node, obstacle, maxConnectionDistance, obstacles);
    }

    protected void reConnectNodeAfterChecks(KNodeOfObstacle node, PathBlockingObstacle obstacle, double maxConnectionDistance, List<T> obstaclesToIntersect) {
        // make new connections between newObstacle and every other obstacle.
        // To optimise the line-obstacle intersection testing, we'll order the obstacle list
        // by their distance to the startNode, smallest first.
        // These closer obstacles are more likely to intersect any lines from the
        // startNode to the far away obstacle nodes.
        // Dump the obstacles in a new list, along with their distance to the startNode.
//		ct.click("obstAndDists.clear");
        ArrayList<ObstAndDist> obstAndDists = new ArrayList<>();
//		ct.click("obstAndDists.add");
        Vector3 p = node.getPoint();
        for (int n = 0; n < obstaclesToIntersect.size(); n++) {
            PathBlockingObstacle obst = obstaclesToIntersect.get(n);
            double dist = node.getPoint().distance(obst.getInnerPolygon().getCenter()) - obst.getInnerPolygon().getRadius();
            obstAndDists.add(new ObstAndDist(obst, dist));
        }
        // Sort the list.
//		ct.click("sort");
        Collections.sort(obstAndDists);

//		ct.click("contained check");
        if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE) {
            // Calculate if the startNode is contained and cache the result.
            // This speeds up this method and the 'makeReachableNodesFor' method significantly.
            for (int i = 0; i < obstAndDists.size(); i++) {
                PathBlockingObstacle obst = obstAndDists.get(i).getObst();
                if (obst == obstacle) {
                    continue;
                }
                //if (obstAndDists.get(i).getDist() > obst.getInnerPolygon().getRadius()){
                if (obstAndDists.get(i).getDist() > 0) {
                    // Break this checking loop since all of the rest of the obstacles
                    // must be too far away to possibly overlap any points in testOb1's polygon.
                    //System.out.println("NodeConnector: breaking at i == "+i+" out of obstAndDists.size() == "+obstAndDists.size());
                    break;
                }
                KPolygon poly = obst.getInnerPolygon();
                if (poly.contains(node.getPoint())) {
                    node.setContained(KNodeOfObstacle.TRUE_VALUE);
                    break;
                }
            }
            if (node.getContained() == KNodeOfObstacle.TRUE_VALUE) {
                return;
            } else {
                node.setContained(KNodeOfObstacle.FALSE_VALUE);
            }
        }
//		ct.click("connect");
        // Test the startNode for straight lines to nodes in other
        // polygons (including testOb1 itself).
        for (int k = 0; k < obstaclesToIntersect.size(); k++) {
            PathBlockingObstacle testOb2 = obstaclesToIntersect.get(k);
            ArrayList<KNodeOfObstacle> testOb2Nodes = testOb2.getNodes();
            //float halfNodeToNode2Dist = startNode.getPoint().distance(testOb2.getInnerPolygon().getCenter()) + polygon.getRadius() + testOb2.getInnerPolygon().getRadius();
            NodeLoop:
            for (int m = 0; m < testOb2Nodes.size(); m++) {
                // Don't test a 'line' from the exact same points in the same polygon.
                KNodeOfObstacle node2 = testOb2Nodes.get(m);
                // Test to see if it's ok to ignore this startNode since it's
                // concave (inward-pointing) or it's contained by an obstacle.
                if (node2 == node
                        || node2.isConcave()
                        || node2.getContained() == KNodeOfObstacle.TRUE_VALUE) {
                    continue;
                }
                Vector3 p2 = node2.getPoint();
                double nodeToNode2Dist = p.distance(p2);
                if (nodeToNode2Dist > maxConnectionDistance) {
                    continue;
                }

                // Only connect the nodes if the connection will be useful.
                if (isConnectionPossibleAndUseful(node, node.getPointNum(), obstacle.getNodes(), node2, m, testOb2Nodes) == false) {
                    continue;
                }

                if (testOb2.getInnerPolygon().intersectsLine(node.getPoint(), node2.getPoint())) {
                    continue NodeLoop;
                }

                // Need to test if line from startNode to node2 intersects any obstacles
                // Also test if any startNode is contained in any obstacle.
                ObstacleLoop:
                for (int n = 0; n < obstAndDists.size(); n++) {
                    if (obstAndDists.get(n).getDist() > nodeToNode2Dist) {
                        // Break this checking loop since all of the rest of the obstacles
                        // must be too far away to possibly overlap the line from startNode to node2
                        //System.out.println("NodeConnector: breaking at i == "+n+" out of obstAndDists.size() == "+obstAndDists.size());
                        break;
                    }
                    PathBlockingObstacle testOb3 = obstAndDists.get(n).getObst();
                    KPolygon innerPolygon = testOb3.getInnerPolygon();
                    if (testOb3 == testOb2) {
                        continue;
                    }
                    if (innerPolygon.intersectionPossible(node.getPoint(), node2.getPoint()) == false) {
                        continue;
                    }
                    // Check that startNode is not inside testOb3
                    if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE && innerPolygon.contains(node.getPoint())) {
                        continue NodeLoop;
                    }
                    if (innerPolygon.intersectsLine(node.getPoint(), node2.getPoint())) {
                        continue NodeLoop;
                    }
                }
                if (node.getConnectedNodes().contains(node2)) {
                    continue;
                }
                node.getConnectedNodes().add(node2);
                if (node2.getConnectedNodes().contains(node)) {
                    continue;
                }
                node2.getConnectedNodes().add(node);
            }
        }
    }

    public void addObstacle(T obst, ArrayList<T> obstacles, double maxConnectionDistance) {
        if (!obstacles.contains(obst)) {
            return;
        }
        resetObstacleNodes(obst);
        KPolygon poly = obst.getInnerPolygon();
        ArrayList<T> nearByObstacles = obstacles;
        // Any nodes that may be contained need to be marked as so.
        for (T nearByObstacle : nearByObstacles) {
            if (nearByObstacle == obst) {
                continue;
            }
            for (KNodeOfObstacle node : nearByObstacle.getNodes()) {
                if (poly.getCenter().distanceSq(node.getPoint()) <= poly.getRadiusSq()) {
                    boolean contained = poly.contains(node.getPoint());
                    if (contained) {
                        node.setContained(KNodeOfObstacle.TRUE_VALUE);
                        node.clearConnectedNodes();
                    }
                }
            }
        }

        // check if the new obstacle obstructs any startNode connections, and if it does, delete them.
        KPolygon polygon = obst.getInnerPolygon();
        for (int i = 0; i < nearByObstacles.size(); i++) {
            PathBlockingObstacle testOb1 = nearByObstacles.get(i);
            if (testOb1 == obst) {
                continue;
            }
            for (int j = 0; j < testOb1.getNodes().size(); j++) {
                KNodeOfObstacle node = testOb1.getNodes().get(j);
                ArrayList<KNode> reachableNodes = node.getConnectedNodes();
                for (int k = 0; k < reachableNodes.size(); k++) {
                    KNode node2 = reachableNodes.get(k);
                    if (polygon.intersectionPossible(node.getPoint(), node2.getPoint()) && polygon.intersectsLine(node.getPoint(), node2.getPoint())) {
                        // delete startNode's reachable KNode.
                        reachableNodes.remove(k);
                        // delete node2's reachable KNode too.
                        int index = node2.getConnectedNodes().indexOf(node);
//                        if (index > -1) {
                            node2.getConnectedNodes().remove(index);
//                        }
                        k--;
                        continue;
                    }
                }
            }
        }
        // connect the obstacle's nodes with all nearby nodes.
        for (KNodeOfObstacle node : obst.getNodes()) {
            reConnectNode(node, obst, maxConnectionDistance, obstacles);
        }
        //long endTime = System.nanoTime();
        //System.out.println(this.getClass().getSimpleName()+".addObstacle running time = "+((endTime - startTime)/1000000000f));
    }

    public void removeObstacle(T obst, double maxConnectionDistance, List<T> obstacles) {
        for (KNodeOfObstacle node : obst.getNodes()) {
            node.clearConnectedNodes();
            node.setContained(KNodeOfObstacle.UNKNOWN_VALUE);
            reConnectNodeAfterChecks(node, obst, maxConnectionDistance, obstacles);
        }
    }

    /**
     * Removes the nodes in removedObstacle from any nodes that it is connected
     * to. Note that this method doesn't connect nodes that were obstructed by
     * this obstacle, that needs to be done by calling reConnectNode on all of
     * the nodes around the removedObstacle.
     *
     * @param removedObstacle
     */
    public void clearConnectionsToRemovedObstacleNodes(T removedObstacle) {
        // Delete all startNode connections between the removed obstacle and every other obstacle.
        for (int j = 0; j < removedObstacle.getNodes().size(); j++) {
            KNodeOfObstacle node = removedObstacle.getNodes().get(j);
            node.clearConnectedNodes();
        }
    }

    public void reConnectNodesAroundRemovedObstacle(T removedObst, PathBlockingObstacle obstacle, ArrayList<KNodeOfObstacle> nodeList, ArrayList<T> obstacles, double maxConnectionDistance) {
        KPolygon poly = removedObst.getInnerPolygon();
        ArrayList<KNodeOfObstacle> otherNodesToConnect = new ArrayList<>();
        double maxConnectionDistanceSq = maxConnectionDistance * maxConnectionDistance;
        // Starting from the last startNode in the list, test to see if the removed
        // obstacle was between that startNode and another startNode. Put all of the
        // nodes that could now be connected in a list, then call reConnectNode.
        // Then remove the last startNode in the list to avoid doubling up.
        while (nodeList.size() > 0) {
            KNodeOfObstacle currentNode = nodeList.get(nodeList.size() - 1);
            Vector3 p1 = currentNode.getPoint();
            otherNodesToConnect.clear();
            for (int i = 0; i < nodeList.size() - 1; i++) {
                KNodeOfObstacle otherNode = nodeList.get(i);
                Vector3 p2 = otherNode.getPoint();
                if (p1.distanceSq(p2) < maxConnectionDistanceSq) {
                    if (poly.intersectionPossible(p1, p2) == true) {
                        // it's possible that the nodes might be connected since the obstacle was removed.
                        otherNodesToConnect.add(otherNode);
                    }
                }
            }
            reConnectNode(currentNode, obstacle, otherNodesToConnect, obstacles);
            nodeList.remove(nodeList.size() - 1);
        }
    }

    public void reConnectNode(KNodeOfObstacle node, PathBlockingObstacle obstacle, ArrayList<KNodeOfObstacle> otherNodes, ArrayList<T> obstaclesToIntersect) {
        //long startTime = System.nanoTime();
        // Test to see if it's ok to ignore this startNode since it's
        // concave (inward-pointing) or it's contained by an obstacle.
        if (node.isConcave() || node.getContained() == KNodeOfObstacle.TRUE_VALUE) {
            return;
        }

        // make new connections between newObstacle and every other obstacle.
        // To optimise the line-obstacle intersection testing, we'll order the obstacle list
        // by their distance to the startNode, smallest first.
        // These closer obstacles are more likely to intersect any lines from the
        // startNode to the far away obstacle nodes.
        // Dump the obstacles in a new list, along with their distance to the startNode.
        ArrayList<ObstAndDist> obstAndDists = new ArrayList<>(obstaclesToIntersect.size());
        for (int n = 0; n < obstaclesToIntersect.size(); n++) {
            PathBlockingObstacle obst = obstaclesToIntersect.get(n);
            double dist = node.getPoint().distance(obst.getInnerPolygon().getCenter()) - obst.getInnerPolygon().getRadius();
            obstAndDists.add(new ObstAndDist(obst, dist));
        }
        // Sort the list.
        Collections.sort(obstAndDists);

        if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE) {
            // Calculate if the startNode is contained and cache the result.
            // This speeds up this method and the 'makeReachableNodesFor' method significantly.
            for (int i = 0; i < obstAndDists.size(); i++) {
                PathBlockingObstacle obst = obstAndDists.get(i).getObst();
                if (obst == obstacle) {
                    continue;
                }
                //if (obstAndDists.get(i).getDist() > obst.getInnerPolygon().getRadius()){
                if (obstAndDists.get(i).getDist() > 0) {
                    // Break this checking loop since all of the rest of the obstacles
                    // must be too far away to possibly overlap any points in testOb1's polygon.
                    //System.out.println("NodeConnector: breaking at i == "+i+" out of obstAndDists.size() == "+obstAndDists.size());
                    break;
                }
                KPolygon poly = obst.getInnerPolygon();
                if (poly.contains(node.getPoint())) {
                    node.setContained(KNodeOfObstacle.TRUE_VALUE);
                    break;
                }
            }
            if (node.getContained() == KNodeOfObstacle.TRUE_VALUE) {
                return;
            } else {
                node.setContained(KNodeOfObstacle.FALSE_VALUE);
            }
        }
        Vector3 p = node.getPoint();

        // Test the startNode for a straight line to otherNodes.
        NodeLoop:
        for (int m = 0; m < otherNodes.size(); m++) {
            KNodeOfObstacle node2 = otherNodes.get(m);
            // Test to see if it's ok to ignore this startNode since it's
            // concave (inward-pointing) or it's contained by an obstacle.
            if (node2 == node
                    || node2.isConcave()
                    || node2.getContained() == KNodeOfObstacle.TRUE_VALUE) {
                continue;
            }

            // Only connect the nodes if the connection will be useful.
            if (isConnectionPossibleAndUseful(node, obstacle, node2) == false) {
                continue;
            }

            if (obstacle.getInnerPolygon().intersectsLine(node.getPoint(), node2.getPoint())) {
                continue NodeLoop;
            }
            // Need to test if line from startNode to node2 intersects any obstacles
            // Also test if any startNode is contained in any obstacle.
            double nodeToNode2Dist = node.getPoint().distance(node2.getPoint());
            ObstacleLoop:
            for (int n = 0; n < obstAndDists.size(); n++) {
                if (obstAndDists.get(n).getDist() > nodeToNode2Dist) {
                    // Break this checking loop since all of the rest of the obstacles
                    // must be too far away to possibly overlap the line from startNode to node2
                    //System.out.println("NodeConnector: breaking at i == "+n+" out of obstAndDists.size() == "+obstAndDists.size());
                    break;
                }
                PathBlockingObstacle testOb3 = obstAndDists.get(n).getObst();
                KPolygon innerPolygon = testOb3.getInnerPolygon();
                if (testOb3 == obstacle) {
                    continue;
                }
                if (innerPolygon.intersectionPossible(node.getPoint(), node2.getPoint()) == false) {
                    continue;
                }
                // Check that startNode is not inside testOb3
                if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE && innerPolygon.contains(node.getPoint())) {
                    continue NodeLoop;
                }
                if (innerPolygon.intersectsLine(node.getPoint(), node2.getPoint())) {
                    continue NodeLoop;
                }
            }
            if (node.getConnectedNodes().contains(node2) == false) {
                node.getConnectedNodes().add(node2);
            }
            if (node2.getConnectedNodes().contains(node) == false) {
                node2.getConnectedNodes().add(node);
            }
        }

        //long endTime = System.nanoTime();
        //System.out.println(this.getClass().getSimpleName()+".addObstacle running time = "+((endTime - startTime)/1000000000f));
    }

    public void reConnectNode(KNodeOfObstacle node, PathBlockingObstacle obstacle, KNodeOfObstacle node2, ArrayList<T> obstaclesToIntersect) {
        // Test to see if it's ok to ignore this startNode since it's
        // concave (inward-pointing) or it's contained by an obstacle.
        if (node.isConcave() || node.getContained() == KNodeOfObstacle.TRUE_VALUE) {
            return;
        }

        if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE) {
            // Calculate if the startNode is contained and cache the result.
            // This speeds up this method and the 'makeReachableNodesFor' method significantly.
            Vector3 point = node.getPoint();
            for (int i = 0; i < obstaclesToIntersect.size(); i++) {
                PathBlockingObstacle obst = obstaclesToIntersect.get(i);
                if (obst == obstacle) {
                    continue;
                }
                KPolygon poly = obst.getInnerPolygon();
                if (point.distance(poly.getCenter()) > poly.getRadius()) {
                    continue;
                }
                if (poly.contains(point)) {
                    node.setContained(KNodeOfObstacle.TRUE_VALUE);
                    break;
                }
            }
            if (node.getContained() == KNodeOfObstacle.TRUE_VALUE) {
                return;
            } else {
                node.setContained(KNodeOfObstacle.FALSE_VALUE);
            }
        }

        // Test startNode for a straight line to node2.
        // Test to see if it's ok to ignore this startNode since it's
        // concave (inward-pointing) or it's contained by an obstacle.
        if (node2 == node
                || node2.isConcave()
                || node2.getContained() == KNodeOfObstacle.TRUE_VALUE) {
            //startNode.getConnectedNodes().contains(node2)
            return;
        }
        // Only connect the nodes if the connection will be useful.
        if (isConnectionPossibleAndUseful(node, obstacle, node2) == false) {
            return;
        }
        Vector3 midPoint = node.getPoint().midPoint(node2.getPoint());
        double nodeToNode2Dist = node.getPoint().distance(node2.getPoint());
        double halfNodeToNode2Dist = nodeToNode2Dist / 2f;
        // Need to test if line from startNode to node2 intersects any obstacles
        // Also test if any startNode is contained in any obstacle.
        boolean intersection = false;
        ObstacleLoop:
        for (int n = 0; n < obstaclesToIntersect.size(); n++) {
            PathBlockingObstacle testOb3 = obstaclesToIntersect.get(n);
            KPolygon innerPolygon = testOb3.getInnerPolygon();
            if (midPoint.distance(innerPolygon.getCenter()) > innerPolygon.getRadius() + halfNodeToNode2Dist) {
                continue;
            }
            if (innerPolygon.intersectionPossible(node.getPoint(), node2.getPoint()) == false) {
                continue;
            }
            // Check that startNode is not inside testOb3
            if (node.getContained() == KNodeOfObstacle.UNKNOWN_VALUE && innerPolygon.contains(node.getPoint())) {
                intersection = true;
                break;
            }
            if (innerPolygon.intersectsLine(node.getPoint(), node2.getPoint())) {
                intersection = true;
                break ObstacleLoop;
            }
        }
        if (intersection == false) {
            if (node.getConnectedNodes().contains(node2) == false) {
                node.getConnectedNodes().add(node2);
            }
            if (node2.getConnectedNodes().contains(node) == false) {
                node2.getConnectedNodes().add(node);
            }
        }
    }

    protected int getXIndicator(Vector3 p, KPolygon poly) {
        int xIndicator;
        double relX = poly.getCenter().x - p.getX();
        if (relX - poly.getRadius() > 0) {
            xIndicator = 1;
        } else if (relX + poly.getRadius() < 0) {
            xIndicator = -1;
        } else {
            xIndicator = 0;
        }
        return xIndicator;
    }

    protected int getYIndicator(Vector3 p, KPolygon poly) {
        int yIndicator;
        double relY = poly.getCenter().z - p.getZ();
        if (relY - poly.getRadius() > 0) {
            yIndicator = 1;
        } else if (relY + poly.getRadius() < 0) {
            yIndicator = -1;
        } else {
            yIndicator = 0;
        }
        return yIndicator;
    }

    public ArrayList<KNode> makeReachableNodesFor(KNode node, double maxConnectionDistance, List<T> obstaclesToIntersect) {
//		codeTimer.click("clear");
        // To optimise the line-obstacle intersection testing, order the obstacle list
        // by their distance to the startNode, smallest first.
        // These closer obstacles are more likely to intersect any lines from the
        // startNode to the far away obstacle nodes.
        // Dump the obstacles in a new list, along with their distance to the startNode.
        ArrayList<ObstDistAndQuad> obstDistAndQuads = new ArrayList<>();
//		codeTimer.click("add");
        // add the obstacles to obstDistAndQuads
        Vector3 p = node.getPoint();
        for (int n = 0; n < obstaclesToIntersect.size(); n++) {
            KPolygon poly = obstaclesToIntersect.get(n).getInnerPolygon();
            double distEyeToCenterLessRadius = p.distance(poly.getCenter()) - poly.getRadius();
            // Note that distCenterToEyeLessCircBound can be negative.
            double distEyeToCenterLessRadiusSqSigned = distEyeToCenterLessRadius * distEyeToCenterLessRadius;
            if (distEyeToCenterLessRadius < 0) {
                // to preserve the sign of the original number:
                distEyeToCenterLessRadiusSqSigned *= -1;
            }
            int xIndicator = getXIndicator(p, poly);
            int yIndicator = getYIndicator(p, poly);
            ObstDistAndQuad obstDistAndQuad = new ObstDistAndQuad(obstaclesToIntersect.get(n), distEyeToCenterLessRadiusSqSigned, xIndicator, yIndicator);
            obstDistAndQuads.add(obstDistAndQuad);
        }
//		codeTimer.click("sort");
        // Sort the list.
        Collections.sort(obstDistAndQuads);
//		codeTimer.click("connect");

        double maxConnectionDistanceSq = maxConnectionDistance * maxConnectionDistance;

        // Test for straight lines between the startNode and nodes of obstacles that don't intersect any obstacles.
        ArrayList<KNode> reachableNodes = new ArrayList<>();
        for (int k = 0; k < obstDistAndQuads.size(); k++) {
            ObstDistAndQuad obstDistAndQuad = obstDistAndQuads.get(k);
            PathBlockingObstacle testOb2 = obstDistAndQuad.getObst();
            ArrayList<KNodeOfObstacle> testOb2Nodes = testOb2.getNodes();
            //KNodeOfObstacle node2Minus = testOb2Nodes.get(testOb2Nodes.size()-1);
            //KNodeOfObstacle node2Plus = null;
            NodeLoop:
            for (int m = 0; m < testOb2Nodes.size(); m++) {
                KNodeOfObstacle node2 = testOb2Nodes.get(m);
                // Ignore this startNode since it's concave (inward-pointing)
                if (node2.isConcave() || node2.getContained() == KNodeOfObstacle.TRUE_VALUE) {
                    continue;
                }

                if (isConnectionPossibleAndUseful(node, node2, m, testOb2Nodes) == false) {
                    continue;
                }
                Vector3 p2 = node2.getPoint();
//				KNodeOfObstacle node2Minus = testOb2Nodes.get(m-1 < 0 ? testOb2Nodes.size()-1 : m-1);
//				KNodeOfObstacle node2Plus = testOb2Nodes.get(m+1 >= testOb2Nodes.size() ? 0 : m+1);
//				KPoint p2Minus = node2Minus.getPoint();
//				KPoint p2 = node2.getPoint();
//				KPoint p2Plus = node2Plus.getPoint();
//				int p2MinusToP2RCCW = p.relCCW(p2Minus, p2);
//				int p2ToP2PlusRCCW = p.relCCW(p2, p2Plus);
//				if (p2MinusToP2RCCW * p2ToP2PlusRCCW == 1){
//					// Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus (or it is clockwise to both lines) then the connection betwen them will not be useful.
//					continue;
//				}
                // Need to test if line from startNode to node2 intersects any
                // obstacles (including testOb2 itself).
                // Should check closest obst first, and obst whose points were found to be reachable
                double nodeToNode2DistSq = p.distanceSq(p2);
                if (nodeToNode2DistSq > maxConnectionDistanceSq) {
                    continue;
                }

                if (testOb2.getInnerPolygon().intersectsLine(p, p2)) {
                    continue NodeLoop;
                }
                ObstacleLoop:
                for (int n = 0; n < obstDistAndQuads.size(); n++) {
                    ObstDistAndQuad obstDistAndQuad2 = obstDistAndQuads.get(n);
                    PathBlockingObstacle testOb3 = obstDistAndQuad2.getObst();
                    KPolygon innerPolygon = testOb3.getInnerPolygon();
                    if (testOb3 == testOb2) {
                        continue;
                    }
                    // Test if testOb3.getInnerPolygon() intersects the line from startNode to node2
                    if (obstDistAndQuads.get(n).getDistNodeToCenterLessRadiusSqSigned() > nodeToNode2DistSq) {
                        // Break this checking loop since all of the rest of the obstacles
                        // must be too far away to possibly overlap the line from startNode to node2
                        break;
                    }
                    if (obstDistAndQuad.getXIndicator() * obstDistAndQuad2.getXIndicator() == -1 || obstDistAndQuad.getYIndicator() * obstDistAndQuad2.getYIndicator() == -1) {
                        continue;
                    }
                    if (innerPolygon.intersectionPossible(p, p2) == false) {
                        continue;
                    }
                    // Check that node2 is not inside testOb3
                    if (node2.getContained() == KNodeOfObstacle.UNKNOWN_VALUE && innerPolygon.contains(p2)) {
                        continue NodeLoop;
                    }
                    if (innerPolygon.intersectsLine(p, p2)) {
                        continue NodeLoop;
                    }
                }
                if (!reachableNodes.contains(node2)) {
                    reachableNodes.add(node2);
                }
            }
        }
        return reachableNodes;
    }

    public ArrayList<KNode> makeInsideReachableNodesFor(KNode node, double maxConnectionDistance, List<T> obstaclesToIntersect) {
//		codeTimer.click("clear");
        // To optimise the line-obstacle intersection testing, order the obstacle list
        // by their distance to the startNode, smallest first.
        // These closer obstacles are more likely to intersect any lines from the
        // startNode to the far away obstacle nodes.
        // Dump the obstacles in a new list, along with their distance to the startNode.
        ArrayList<ObstDistAndQuad> obstDistAndQuads = new ArrayList<>();
//		codeTimer.click("add");
        // add the obstacles to obstDistAndQuads
        Vector3 p = node.getPoint();
        for (int n = 0; n < obstaclesToIntersect.size(); n++) {
            KPolygon poly = obstaclesToIntersect.get(n).getInnerPolygon();
            double distEyeToCenterLessRadius = p.distance(poly.getCenter()) - poly.getRadius();
            // Note that distCenterToEyeLessCircBound can be negative.
            double distEyeToCenterLessRadiusSqSigned = distEyeToCenterLessRadius * distEyeToCenterLessRadius;
            if (distEyeToCenterLessRadius < 0) {
                // to preserve the sign of the original number:
                distEyeToCenterLessRadiusSqSigned *= -1;
            }
            int xIndicator = getXIndicator(p, poly);
            int yIndicator = getYIndicator(p, poly);
            ObstDistAndQuad obstDistAndQuad = new ObstDistAndQuad(obstaclesToIntersect.get(n), distEyeToCenterLessRadiusSqSigned, xIndicator, yIndicator);
            obstDistAndQuads.add(obstDistAndQuad);
        }
//		codeTimer.click("sort");
        // Sort the list.
        Collections.sort(obstDistAndQuads);
//		codeTimer.click("connect");

        double maxConnectionDistanceSq = maxConnectionDistance * maxConnectionDistance;

        // Test for straight lines between the startNode and nodes of obstacles that don't intersect any obstacles.
        ArrayList<KNode> reachableNodes = new ArrayList<>();
        for (int k = 0; k < obstDistAndQuads.size(); k++) {
            ObstDistAndQuad obstDistAndQuad = obstDistAndQuads.get(k);
            PathBlockingObstacle testOb2 = obstDistAndQuad.getObst();
            ArrayList<KNodeOfObstacle> testOb2Nodes = testOb2.getNodes();
            //KNodeOfObstacle node2Minus = testOb2Nodes.get(testOb2Nodes.size()-1);
            //KNodeOfObstacle node2Plus = null;
            NodeLoop:
            for (int m = 0; m < testOb2Nodes.size(); m++) {
                KNodeOfObstacle node2 = testOb2Nodes.get(m);
                // Ignore this startNode since it's concave (inward-pointing)
                if (node2.isConcave() || node2.getContained() == KNodeOfObstacle.TRUE_VALUE) {
                    continue;
                }

                if (isConnectionPossibleAndUseful(node, node2, m, testOb2Nodes) == false) {
                    continue;
                }
                Vector3 p2 = node2.getPoint();
//				KNodeOfObstacle node2Minus = testOb2Nodes.get(m-1 < 0 ? testOb2Nodes.size()-1 : m-1);
//				KNodeOfObstacle node2Plus = testOb2Nodes.get(m+1 >= testOb2Nodes.size() ? 0 : m+1);
//				KPoint p2Minus = node2Minus.getPoint();
//				KPoint p2 = node2.getPoint();
//				KPoint p2Plus = node2Plus.getPoint();
//				int p2MinusToP2RCCW = p.relCCW(p2Minus, p2);
//				int p2ToP2PlusRCCW = p.relCCW(p2, p2Plus);
//				if (p2MinusToP2RCCW * p2ToP2PlusRCCW == 1){
//					// Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus (or it is clockwise to both lines) then the connection betwen them will not be useful.
//					continue;
//				}
                // Need to test if line from startNode to node2 intersects any
                // obstacles (including testOb2 itself).
                // Should check closest obst first, and obst whose points were found to be reachable
                double nodeToNode2DistSq = p.distanceSq(p2);
                if (nodeToNode2DistSq > maxConnectionDistanceSq) {
                    continue;
                }

                if (!testOb2.getInnerPolygon().intersectsLine(p, p2)) {
                    continue NodeLoop;
                }
                ObstacleLoop:
                for (int n = 0; n < obstDistAndQuads.size(); n++) {
                    ObstDistAndQuad obstDistAndQuad2 = obstDistAndQuads.get(n);
                    PathBlockingObstacle testOb3 = obstDistAndQuad2.getObst();
                    KPolygon innerPolygon = testOb3.getInnerPolygon();
                    if (testOb3 == testOb2) {
                        continue;
                    }
                    // Test if testOb3.getInnerPolygon() intersects the line from startNode to node2
                    if (obstDistAndQuads.get(n).getDistNodeToCenterLessRadiusSqSigned() < nodeToNode2DistSq) {
                        // Break this checking loop since all of the rest of the obstacles
                        // must be too far away to possibly overlap the line from startNode to node2
                        break;
                    }
                    if (obstDistAndQuad.getXIndicator() * obstDistAndQuad2.getXIndicator() == -1 || obstDistAndQuad.getYIndicator() * obstDistAndQuad2.getYIndicator() == -1) {
                        continue;
                    }
                    if (innerPolygon.intersectionPossible(p, p2) == false) {
                        continue;
                    }
                    // Check that node2 is not inside testOb3
                    if (node2.getContained() == KNodeOfObstacle.UNKNOWN_VALUE && innerPolygon.contains(p2)) {
                        continue NodeLoop;
                    }
                    if (innerPolygon.intersectsLine(p, p2)) {
                        continue NodeLoop;
                    }
                }
                if (!reachableNodes.contains(node2)) {
                    reachableNodes.add(node2);
                }
            }
        }

        return reachableNodes;
    }

    /**
     * Only connect the nodes if the connection will be useful Consider the
     * following square obstacle ABCD and a player at point P that might be
     * trying to get to Q, R or S. In this example, P would be 'startNode' and
     * A, B, C or D could be 'node2'.
     *
     *                                  *P *Q A____________B | | | | | obstacle | *R | | | | D____________C
     *
     *               *S
     *
     * P should not be connected with B since that connection will not be useful
     * for making a path to Q, R or S. P should not be connected with D since
     * that connection intersects the obstacle and it will not be useful for
     * making a path to Q, R or S. P should be connected with A since that
     * connection will be useful for making a path to R. P should be connected
     * with C since that connection will be useful for making a path to S.
     *
     * @param startNode
     * @param node2
     * @param node2PointNum
     * @param node2List
     * @return true if the connection should be attempted.
     */
//	protected boolean isConnectionPossibleAndUseful(KNode startNode, KNodeOfObstacle node2, int node2PointNum, ArrayList<KNodeOfObstacle> node2List){
//		return true;
//	}
//	collinear(double x1, double y1, double x2, double y2, double x3, double y3){
//		double collinearityTest = x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2);
//		double              ccw = (y3-y1) * x2 - (x3-x1) * y2;
    double smallAmount = 0.0001;

    protected boolean isConnectionPossibleAndUseful(KNode node, KNodeOfObstacle node2, int node2PointNum, ArrayList<KNodeOfObstacle> node2List) {
        Vector3 p = node.getPoint();
        Vector3 p2 = node2.getPoint();
        // test if startNode is in the reject region of node2's obstacle
        {
            // Only connect the nodes if the connection will be useful.
            // See the comment in the method makeReachableNodes for a full explanation.
            KNode node2Minus = node2List.get(node2PointNum - 1 < 0 ? node2List.size() - 1 : node2PointNum - 1);
            KNode node2Plus = node2List.get(node2PointNum + 1 >= node2List.size() ? 0 : node2PointNum + 1);
            Vector3 p2Minus = node2Minus.getPoint();
            Vector3 p2Plus = node2Plus.getPoint();

            //double p2MinusToP2RCCW = p.c(p2Minus, p2);
            double p2LessP2MinusX = p2.x - p2Minus.x;
            double p2LessP2MinusY = p2.z - p2Minus.z;
            double pLessP2MinusX = p.x - p2Minus.x;
            double pLessP2MinusY = p.z - p2Minus.z;
            double p2MinusToP2RCCW = pLessP2MinusY * p2LessP2MinusX - pLessP2MinusX * p2LessP2MinusY;

            //double p2ToP2PlusRCCW = p.c(p2, p2Plus);
            double pLessP2X = p.x - p2.x;
            double pLessP2Y = p.z - p2.z;
            double p2PlusLessP2X = p2Plus.x - p2.x;
            double p2PlusLessP2Y = p2Plus.z - p2.z;
            double p2ToP2PlusRCCW = pLessP2Y * p2PlusLessP2X - pLessP2X * p2PlusLessP2Y;

            if (p2MinusToP2RCCW * p2ToP2PlusRCCW > 0) {
//				System.out.println(this.getClass().getSimpleName()+": p2MinusToP2RCCWInt * p2ToP2PlusRCCWInt == 1");
//				System.out.println(this.getClass().getSimpleName()+": p2MinusToP2RCCW == "+p2MinusToP2RCCW);
//				System.out.println(this.getClass().getSimpleName()+": p2ToP2PlusRCCW == "+p2ToP2PlusRCCW);
//				System.out.println(this.getClass().getSimpleName()+": p2Minus == "+p2Minus);
//				System.out.println(this.getClass().getSimpleName()+": p2 == "+p2);
//				System.out.println(this.getClass().getSimpleName()+": p2Plus == "+p2Plus);
//				System.out.println(this.getClass().getSimpleName()+": p == "+p);

                // To avoid floating point error problems we should only return false
                // if p is well away from the lines. If it's close, then return
                // true just to be safe. Returning false when the connection is
                // actually useful is a much bigger problem than returning true
                // and sacrificing some performance.
//				double p2MinusToP2LineDistSq = p.ptLineDistSq(p2Minus, p2);
//				if (p2MinusToP2LineDistSq < smallAmount){
//					return true;
//				}
                {
                    double dotprod = pLessP2MinusX * p2LessP2MinusX + pLessP2MinusY * p2LessP2MinusY;
                    // dotprod is the length of the px,py vector
                    // projected on the x1,y1=>x2,y2 vector times the
                    // length of the x1,y1=>x2,y2 vector
                    double projlenSq = dotprod * dotprod / (p2LessP2MinusX * p2LessP2MinusX + p2LessP2MinusY * p2LessP2MinusY);
                    // Distance to line is now the length of the relative point
                    // vector minus the length of its projection onto the line
                    double p2MinusToP2LineDistSq = pLessP2MinusX * pLessP2MinusX + pLessP2MinusY * pLessP2MinusY - projlenSq;
                    if (p2MinusToP2LineDistSq < smallAmount) {
                        return true;
                    }
                }

//				double p2ToP2PlusLineDistSq = p.ptLineDistSq(p2, p2Plus);
//				if (p2ToP2PlusLineDistSq < smallAmount){
//					return true;
//				}
                {
                    double dotprod = pLessP2X * p2PlusLessP2X + pLessP2Y * p2PlusLessP2Y;
                    // dotprod is the length of the px,py vector
                    // projected on the x1,y1=>x2,y2 vector times the
                    // length of the x1,y1=>x2,y2 vector
                    double projlenSq = dotprod * dotprod / (p2PlusLessP2X * p2PlusLessP2X + p2PlusLessP2Y * p2PlusLessP2Y);
                    // Distance to line is now the length of the relative point
                    // vector minus the length of its projection onto the line
                    double p2ToP2PlusLineDistSq = pLessP2X * pLessP2X + pLessP2Y * pLessP2Y - projlenSq;
                    if (p2ToP2PlusLineDistSq < smallAmount) {
                        return true;
                    }
                }
                // Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus
                // (or it is clockwise to both lines) then the connection betwen
                // them will not be useful so return .
                return false;
            }
        }
        return true;
    }

    protected boolean isConnectionPossibleAndUseful(KNodeOfObstacle node, PathBlockingObstacle obstacle, KNodeOfObstacle node2) {
        return isConnectionPossibleAndUseful(node, node.getPointNum(), obstacle.getNodes(), node2, node2.getPointNum(), obstacle.getNodes());
    }

    protected boolean isConnectionPossibleAndUseful(KNodeOfObstacle node, int nodePointNum, ArrayList<KNodeOfObstacle> nodeList, KNodeOfObstacle node2, int node2PointNum, ArrayList<KNodeOfObstacle> node2List) {
        Vector3 p = node.getPoint();
        Vector3 p2 = node2.getPoint();
        // test if startNode is in the reject region of node2's obstacle
        {
            // Only connect the nodes if the connection will be useful.
            // See the comment in the method makeReachableNodes for a full explanation.
            KNode node2Minus = node2List.get(node2PointNum - 1 < 0 ? node2List.size() - 1 : node2PointNum - 1);
            KNode node2Plus = node2List.get(node2PointNum + 1 >= node2List.size() ? 0 : node2PointNum + 1);
            Vector3 p2Minus = node2Minus.getPoint();
            Vector3 p2Plus = node2Plus.getPoint();

            double p2MinusToP2RCCW = p.relCCWDouble(p2Minus, p2);
            double p2ToP2PlusRCCW = p.relCCWDouble(p2, p2Plus);
            if (p2MinusToP2RCCW * p2ToP2PlusRCCW > 0) {
                // To avoid floating point error problems we should only return false
                // if p is well away from the lines. If it's close, then return
                // true just to be safe. Returning false when the connection is
                // actually useful is a much bigger problem than returning true
                // and sacrificing some performance.
                double p2MinusToP2LineDistSq = p.ptLineDistSq(p2Minus, p2);
                if (p2MinusToP2LineDistSq < smallAmount) {
                    return true;
                }
                double p2ToP2PlusLineDistSq = p.ptLineDistSq(p2, p2Plus);
                if (p2ToP2PlusLineDistSq < smallAmount) {
                    return true;
                }
                // Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus
                // (or it is clockwise to both lines) then the connection betwen
                // them will not be useful so return .
                return false;
            }
        }
        // test if node2 is in the reject region of node1's obstacle
        {
            // Only connect the nodes if the connection will be useful.
            // See the comment in the method makeReachableNodes for a full explanation.
            KNode nodeMinus = nodeList.get(nodePointNum - 1 < 0 ? nodeList.size() - 1 : nodePointNum - 1);
            KNode nodePlus = nodeList.get(nodePointNum + 1 >= nodeList.size() ? 0 : nodePointNum + 1);
            Vector3 pMinus = nodeMinus.getPoint();
            //KPoint p2 = node2.getPoint();
            Vector3 pPlus = nodePlus.getPoint();
            double pMinusToPRCCW = p2.relCCWDouble(pMinus, p);
            double pToPPlusRCCW = p2.relCCWDouble(p, pPlus);
            if (pMinusToPRCCW * pToPPlusRCCW > 0) {
                // To avoid floating point error problems we should only return false
                // if p is well away from the lines. If it's close, then return
                // true just to be safe. Returning false when the connection is
                // actually useful is a much bigger problem than returning true
                // and sacrificing some performance.
                double pMinusToPLineDistSq = p2.ptLineDistSq(pMinus, p);
                if (pMinusToPLineDistSq < smallAmount) {
                    return true;
                }
                double pToPPlusLineDistSq = p2.ptLineDistSq(p, pPlus);
                if (pToPPlusLineDistSq < smallAmount) {
                    return true;
                }
                // Since p is anti-clockwise to both lines p2MinusToP2 and p2ToP2Plus
                // (or it is clockwise to both lines) then the connection betwen
                // them will not be useful so return .
                return false;
            }
        }
        return true;
    }

    public void resetObstacleNodes(ArrayList<T> obstacles) {
        for (int i = 0; i < obstacles.size(); i++) {
            resetObstacleNodes(obstacles.get(i));
        }
    }

    public void resetObstacleNodes(T obstacle) {
        for (int j = 0; j < obstacle.getNodes().size(); j++) {
            KNodeOfObstacle node = obstacle.getNodes().get(j);
            node.getConnectedNodes().clear();
            node.getTempConnectedNodes().clear();
            node.resetContainedToUnknown();
        }
    }

}
