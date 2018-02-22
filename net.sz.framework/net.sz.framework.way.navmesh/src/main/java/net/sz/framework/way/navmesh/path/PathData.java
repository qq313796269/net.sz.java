/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sz.framework.way.navmesh.path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import net.sz.framework.way.navmesh.Vector3;

/**
 *
 * @author Keith
 */
public class PathData implements Serializable, Cloneable {

    public enum Result {
        NO_RESULT {
            public String getMessage() {
                return "No results.";
            }

            public boolean isError() {
                return true;
            }
        },
        SUCCESS {
            public String getMessage() {
                return "Success, path found.";
            }

            public boolean isError() {
                return false;
            }
        },
        ERROR1 {
            public String getMessage() {
                return "Error, startToEndDist is greater than maxSearchDistStartToEnd.";
            }

            public boolean isError() {
                return true;
            }
        },
        ERROR2 {
            public String getMessage() {
                return "Error, start node can not be connected to obstacle nodes or end node. Increase maxTempNodeConnectionDist or check that start node is not inside an obstacle.";
            }

            public boolean isError() {
                return true;
            }
        },
        ERROR3 {
            public String getMessage() {
                return "Error, end node can not be connected to obstacle nodes or start node. Increase maxTempNodeConnectionDist or check that end node is not inside an obstacle.";
            }

            public boolean isError() {
                return true;
            }
        },
        ERROR4 {
            public String getMessage() {
                return "Error, no path found. Could be due to obstacles fencing in the start or end node, or because maxTempNodeConnectionDist or maxSearchDistStartToEnd are not large enough.";
            }

            public boolean isError() {
                return true;
            }
        };

        // Do arithmetic op represented by this constant
        public abstract boolean isError();

        public abstract String getMessage();

        public String toString() {
            return getMessage();
        }
    }

    Result result;
    //从起始点到终点的
    public ArrayList<Vector3> points;
    //从终点到起始点的
    public HashSet<KNode> nodes;

    public PathData() {
        reset();
    }

    public PathData(Result result) {
        initLists();
        if (result.isError() == false) {
            throw new IllegalArgumentException("This constructor can only be used for error results. result.isError() == " + result.isError());
        }
        this.result = result;
    }

    public PathData(ArrayList<Vector3> points, HashSet<KNode> nodes) {
        setSuccess(points, nodes);
    }

    public void reset() {
        this.result = Result.NO_RESULT;
        initLists();
    }

    public void initLists() {
        points = new ArrayList<>();
        nodes = new HashSet<>();
    }

    public void setError(Result result) {
        if (result.isError() == false) {
            throw new IllegalArgumentException("Result must be an error. result.isError() == " + result.isError());
        }
        this.result = result;
        initLists();
    }

    public void setSuccess(ArrayList<Vector3> points, HashSet<KNode> nodes) {
        result = Result.SUCCESS;
        initLists();
        this.points.addAll(points);
        this.nodes.addAll(nodes);
    }

    public boolean isError() {
        return result.isError();
    }

    public Result getResult() {
        return result;
    }

    public HashSet<KNode> getNodes() {
        return nodes;
    }

    public ArrayList<Vector3> getPoints() {
        return points;
    }

    @Override
    public PathData clone() {
        try {
            PathData clone = (PathData) super.clone();
            clone.nodes = new HashSet<>(nodes);
            clone.points = new ArrayList<>(points);
            return clone;
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

}
