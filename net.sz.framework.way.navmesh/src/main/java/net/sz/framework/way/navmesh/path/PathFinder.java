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
import net.sz.framework.way.navmesh.util.BinaryHeap;
import net.sz.framework.way.navmesh.util.Tracker;

/**
 * Finds a path through PathBlockingObstacles.
 *
 * Some code concepts from: http://www.policyalmanac.org/games/aStarTutorial.htm
 * Thanks to Jono and Nate (Nathan Sweet) from JavaGaming.org for their clever
 * help.
 *
 * @author Keith Woodward
 *
 * @version 20 December 2008
 */
public class PathFinder implements Serializable {

    public KNode startNode;
    public KNode endNode;
    public BinaryHeap<KNode> openList;

    // Tracker is used in conjunction with the KNodes to detect if the Nodes are in the open or closed state.
    Tracker tracker = new Tracker();

    // for debugging only:
    public boolean debug = false;
    public Vector3 startPointDebug;
    public Vector3 endPointDebug;
    public ArrayList<KNode> startNodeTempReachableNodesDebug = new ArrayList<>();
    public ArrayList<KNode> endNodeTempReachableNodesDebug = new ArrayList<>();

    public PathFinder() {
        openList = new BinaryHeap<>();
        startNode = new KNode();
        endNode = new KNode();
    }

    public PathData calc(Vector3 start, Vector3 end, double maxTempNodeConnectionDist, NodeConnector nodeConnector, List obstacles) {
        return calc(start, end, maxTempNodeConnectionDist, Double.MAX_VALUE, nodeConnector, obstacles);
    }

    /**
     * @param start
     * @param end
     * @param maxTempNodeConnectionDist Maximum connection distance from start
     * to obstacles and end to obstacles. The smaller the distance, the faster
     * the algorithm.
     * @param maxSearchDistStartToEnd Maximum distance from start to end. Any
     * paths with a longer distance won't be returned. The smaller the value the
     * faster the algorithm.
     * @param nodeConnector
     * @param obstacles
     * @return
     */
    public PathData calc(Vector3 start, Vector3 end, double maxTempNodeConnectionDist, double maxSearchDistStartToEnd, NodeConnector nodeConnector, List obstacles) {
        synchronized (this.openList) {

            if (tempReachableNodesExist(obstacles)) {
                return null;
            }
            double startToEndDist = start.distance(end);
            if (startToEndDist > maxSearchDistStartToEnd) {
                // no point doing anything since startToEndDist is greater than maxSearchDistStartToEnd.
                PathData pathData = new PathData(PathData.Result.ERROR1);
                return pathData;
            }
            startNode.clearForReuse();
            startNode.setPoint(start);
            // Set startNode gCost to zero
            startNode.calcGCost();
            KNode currentNode = startNode;
            endNode.clearForReuse();
            endNode.setPoint(end);

            // Check for straight line path between start and end.
            // Note that this assumes start and end are not both contained in the same polygon.
            boolean intersection = false;
            for (Object obstacle : obstacles) {
                KPolygon innerPolygon = ((PathBlockingObstacle) obstacle).getInnerPolygon();
                // Test if polygon intersects the line from start to end
                if (innerPolygon.intersectionPossible(start, end) && innerPolygon.intersectsLine(start, end)) {
                    intersection = true;
                    break;
                }
            }
            if (intersection == false) {
                // No intersections, so the straight-line path is fine!
                endNode.setParent(currentNode);
                PathData pathData = this.makePathData();
                clearTempReachableNodes();
                tracker.incrementCounter();
                return pathData;
            }
            {
                // Connect the startNode to its reachable nodes and vice versa
                ArrayList<KNode> reachableNodes = nodeConnector.makeReachableNodesFor(startNode, maxTempNodeConnectionDist, obstacles);
                if (reachableNodes.isEmpty()) {
                    // path from start node is not possible since there are no connections to it.
                    PathData pathData = new PathData(PathData.Result.ERROR2);
                    clearTempReachableNodes();
                    tracker.incrementCounter();
                    return pathData;
                }
                startNode.getTempConnectedNodes().addAll(reachableNodes);
                for (KNode node : reachableNodes) {
                    node.getTempConnectedNodes().add(startNode);
                }

                // Connect the endNode to its reachable nodes and vice versa
                reachableNodes = nodeConnector.makeReachableNodesFor(endNode, maxTempNodeConnectionDist, obstacles);
                if (reachableNodes.isEmpty()) {
                    // path to end node is not possible since there are no connections to it.
                    PathData pathData = new PathData(PathData.Result.ERROR3);
                    clearTempReachableNodes();
                    tracker.incrementCounter();
                    return pathData;
                }
                endNode.getTempConnectedNodes().addAll(reachableNodes);
                for (KNode node : reachableNodes) {
                    node.getTempConnectedNodes().add(endNode);
                }
            }

            // Here we start the A* algorithm!
            openList.makeEmpty();
            while (true) {
                // put the current node in the closedSet and take it out of the openList.
                currentNode.setPathFinderStatus(KNode.CLOSED, tracker);
                if (openList.isEmpty() == false) {
                    openList.deleteMin();
                }
                // add reachable nodes to the openList if they're not already there.
                ArrayList<KNode> reachableNodes = currentNode.getConnectedNodes();
                for (int i = 0; i < reachableNodes.size(); i++) {
                    KNode reachableNode = reachableNodes.get(i);
                    if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED) {
                        reachableNode.setParent(currentNode);
                        reachableNode.calcHCost(endNode);
                        reachableNode.calcGCost();
                        reachableNode.calcFCost();
                        if (reachableNode.getFCost() <= maxSearchDistStartToEnd) {
                            openList.add(reachableNode);
                            reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
                        }
                    } else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN) {
                        if (reachableNode.getGCost() == KNode.G_COST_NOT_CALCULATED_FLAG) {
                            continue;
                        }
                        double currentGCost = reachableNode.getGCost();
                        double newGCost = currentNode.getGCost() + currentNode.getPoint().distance(reachableNode.getPoint());
                        if (newGCost < currentGCost) {
                            reachableNode.setParent(currentNode);
                            reachableNode.setGCost(newGCost);	//reachableNode.calcGCost();
                            reachableNode.calcFCost();
                            // Since the g-cost of the node has changed,
                            // must re-sort the list to reflect this.
                            int index = openList.indexOf(reachableNode);
                            openList.percolateUp(index);
                        }
                    }
                }
                ArrayList<KNode> tempReachableNodes = currentNode.getTempConnectedNodes();
                for (int i = 0; i < tempReachableNodes.size(); i++) {
                    KNode reachableNode = tempReachableNodes.get(i);
                    if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED) {
                        reachableNode.setParent(currentNode);
                        reachableNode.calcHCost(endNode);
                        reachableNode.calcGCost();
                        reachableNode.calcFCost();
                        if (reachableNode.getFCost() <= maxSearchDistStartToEnd) {
                            openList.add(reachableNode);
                            reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
                        }
                    } else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN) {
                        if (reachableNode.getGCost() == KNode.G_COST_NOT_CALCULATED_FLAG) {
                            continue;
                        }
                        double currentGCost = reachableNode.getGCost();
                        double newGCost = currentNode.getGCost() + currentNode.getPoint().distance(reachableNode.getPoint());
                        if (newGCost < currentGCost) {
                            reachableNode.setParent(currentNode);
                            reachableNode.setGCost(newGCost);	//reachableNode.calcGCost();
                            reachableNode.calcFCost();
                            // Since the g-cost of the node has changed,
                            // must re-sort the list to reflect this.
                            int index = openList.indexOf(reachableNode);
                            openList.percolateUp(index);
                        }
                    }
                }
                if (openList.size() == 0) {
                    //System.out.println(this.getClass().getSimpleName()+": openList.size() == 0, returning");
                    PathData pathData = new PathData(PathData.Result.ERROR4);
                    clearTempReachableNodes();
                    tracker.incrementCounter();
                    return pathData;
                }

                currentNode = openList.peekMin();
                if (currentNode == endNode) {
                    //System.out.println(this.getClass().getSimpleName()+": currentNode == endNode, returning");
                    break;
                }
            }
            PathData pathData = makePathData();
            clearTempReachableNodes();
            tracker.incrementCounter();
            return pathData;
        }
    }

    public PathData calcInside(Vector3 start, Vector3 end, double maxTempNodeConnectionDist, double maxSearchDistStartToEnd, NodeConnector nodeConnector, List obstacles) {
        if (tempReachableNodesExist(obstacles)) {
            return null;
        }
        double startToEndDist = start.distance(end);
        if (startToEndDist > maxSearchDistStartToEnd) {
            // no point doing anything since startToEndDist is greater than maxSearchDistStartToEnd.
            PathData pathData = new PathData(PathData.Result.ERROR1);
            return pathData;
        }

        startNode.clearForReuse();
        startNode.setPoint(start);
        // Set startNode gCost to zero
        startNode.calcGCost();
        KNode currentNode = startNode;
        endNode.clearForReuse();
        endNode.setPoint(end);

        // Check for straight line path between start and end.
        // Note that this assumes start and end are not both contained in the same polygon.
        boolean intersection = false;
        ObstacleLoop:
//		for (int i = 0; i < obstacles.size(); i++){
//			KPolygon innerPolygon = ((PathBlockingObstacle)obstacles.get(i)).getInnerPolygon();
//			// Test if polygon intersects the line from start to end
//			if (innerPolygon.intersectionPossible(start, end) && innerPolygon.intersectsLine(start, end)){
//				intersection = true;
//				break ObstacleLoop;
//			}
//		}
//		if (intersection == true){
//			// No intersections, so the straight-line path is fine!
//			endNode.setParent(currentNode);
//			PathData pathData = this.makePathData();
//			clearTempReachableNodes();
//			tracker.incrementCounter();
//			return pathData;
//		}
        {
            // Connect the startNode to its reachable nodes and vice versa
            ArrayList<KNode> reachableNodes = nodeConnector.makeReachableNodesFor(startNode, maxTempNodeConnectionDist, obstacles);
            if (reachableNodes.isEmpty()) {
                // path from start node is not possible since there are no connections to it.
                PathData pathData = new PathData(PathData.Result.ERROR2);
                clearTempReachableNodes();
                tracker.incrementCounter();
                return pathData;
            }
            startNode.getTempConnectedNodes().addAll(reachableNodes);
            for (int i = 0; i < reachableNodes.size(); i++) {
                KNode node = reachableNodes.get(i);
                node.getTempConnectedNodes().add(startNode);
            }

            // Connect the endNode to its reachable nodes and vice versa
            reachableNodes = nodeConnector.makeReachableNodesFor(endNode, maxTempNodeConnectionDist, obstacles);
            if (reachableNodes.isEmpty()) {
                // path to end node is not possible since there are no connections to it.
                PathData pathData = new PathData(PathData.Result.ERROR3);
                clearTempReachableNodes();
                tracker.incrementCounter();
                return pathData;
            }
            endNode.getTempConnectedNodes().addAll(reachableNodes);
            for (int i = 0; i < reachableNodes.size(); i++) {
                KNode node = reachableNodes.get(i);
                node.getTempConnectedNodes().add(endNode);
            }
        }

        // Here we start the A* algorithm!
        openList.makeEmpty();
        while (true) {
            // put the current node in the closedSet and take it out of the openList.
            currentNode.setPathFinderStatus(KNode.CLOSED, tracker);
            if (openList.isEmpty() == false) {
                openList.deleteMin();
            }
            // add reachable nodes to the openList if they're not already there.
            ArrayList<KNode> reachableNodes = currentNode.getConnectedNodes();
            for (int i = 0; i < reachableNodes.size(); i++) {
                KNode reachableNode = reachableNodes.get(i);
                if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED) {
                    reachableNode.setParent(currentNode);
                    reachableNode.calcHCost(endNode);
                    reachableNode.calcGCost();
                    reachableNode.calcFCost();
                    if (reachableNode.getFCost() <= maxSearchDistStartToEnd) {
                        openList.add(reachableNode);
                        reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
                    }
                } else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN) {
                    if (reachableNode.getGCost() == KNode.G_COST_NOT_CALCULATED_FLAG) {
                        continue;
                    }
                    double currentGCost = reachableNode.getGCost();
                    double newGCost = currentNode.getGCost() + currentNode.getPoint().distance(reachableNode.getPoint());
                    if (newGCost < currentGCost) {
                        reachableNode.setParent(currentNode);
                        reachableNode.setGCost(newGCost);	//reachableNode.calcGCost();
                        reachableNode.calcFCost();
                        // Since the g-cost of the node has changed,
                        // must re-sort the list to reflect this.
                        int index = openList.indexOf(reachableNode);
                        openList.percolateUp(index);
                    }
                }
            }
            ArrayList<KNode> tempReachableNodes = currentNode.getTempConnectedNodes();
            for (int i = 0; i < tempReachableNodes.size(); i++) {
                KNode reachableNode = tempReachableNodes.get(i);
                if (reachableNode.getPathFinderStatus(tracker) == KNode.UNPROCESSED) {
                    reachableNode.setParent(currentNode);
                    reachableNode.calcHCost(endNode);
                    reachableNode.calcGCost();
                    reachableNode.calcFCost();
                    if (reachableNode.getFCost() <= maxSearchDistStartToEnd) {
                        openList.add(reachableNode);
                        reachableNode.setPathFinderStatus(KNode.OPEN, tracker);
                    }
                } else if (reachableNode.getPathFinderStatus(tracker) == KNode.OPEN) {
                    if (reachableNode.getGCost() == KNode.G_COST_NOT_CALCULATED_FLAG) {
                        continue;
                    }
                    double currentGCost = reachableNode.getGCost();
                    double newGCost = currentNode.getGCost() + currentNode.getPoint().distance(reachableNode.getPoint());
                    if (newGCost < currentGCost) {
                        reachableNode.setParent(currentNode);
                        reachableNode.setGCost(newGCost);	//reachableNode.calcGCost();
                        reachableNode.calcFCost();
                        // Since the g-cost of the node has changed,
                        // must re-sort the list to reflect this.
                        int index = openList.indexOf(reachableNode);
                        openList.percolateUp(index);
                    }
                }
            }
            if (openList.size() == 0) {
                //System.out.println(this.getClass().getSimpleName()+": openList.size() == 0, returning");
                PathData pathData = new PathData(PathData.Result.ERROR4);
                clearTempReachableNodes();
                tracker.incrementCounter();
                return pathData;
            }

            currentNode = openList.peekMin();
            if (currentNode == endNode) {
                //System.out.println(this.getClass().getSimpleName()+": currentNode == endNode, returning");
                break;
            }
        }
        PathData pathData = makePathData();
        clearTempReachableNodes();
        tracker.incrementCounter();
        return pathData;
    }

    protected void clearTempReachableNodes() {
        if (debug) {
            startPointDebug = startNode.getPoint().copy();
            endPointDebug = endNode.getPoint().copy();
            startNodeTempReachableNodesDebug.clear();
            endNodeTempReachableNodesDebug.clear();
            startNodeTempReachableNodesDebug.addAll(startNode.getTempConnectedNodes());
            endNodeTempReachableNodesDebug.addAll(endNode.getTempConnectedNodes());
        }

        // Erase all nodes' tempConnectedNodes
        if (startNode != null) {
            startNode.clearTempConnectedNodes();
        }
        if (endNode != null) {
            endNode.clearTempConnectedNodes();
        }
    }

    protected PathData makePathData() {
        KNode currentNode = getEndNode();
        int i = 0;
        HashSet<KNode> nodes = new HashSet<>();
        ArrayList<Vector3> points = new ArrayList<>();
        while (true) {
            nodes.add(currentNode);
            points.add(currentNode.getPoint());
            KNode parentNode = currentNode.getParent();
            if (parentNode == null || nodes.contains(parentNode)) {
                break;
            }
            currentNode = parentNode;
        }
        Collections.reverse(points);
        PathData pathData = new PathData(points, nodes);
        nodes.clear();
        points.clear();
        return pathData;
    }

    public boolean pathExists() {
        if (getEndNode() != null && getEndNode().getParent() != null) {
            return true;
        }
        return false;
    }

    public KNode getEndNode() {
        return endNode;
    }

    public KNode getStartNode() {
        return startNode;
    }

    // used only for assertion checks
    protected boolean tempReachableNodesExist(List obstacles) {
        for (int i = 0; i < obstacles.size(); i++) {
            PathBlockingObstacle obst = (PathBlockingObstacle) obstacles.get(i);
            for (int j = 0; j < obst.getNodes().size(); j++) {
                KNodeOfObstacle node = obst.getNodes().get(j);
                if (node.getTempConnectedNodes().size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
