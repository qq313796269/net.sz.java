/**
 * 特别鸣谢修仙项目组负责人吴章义
 */
package net.sz.game.engine.navmesh.main;

import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.navmesh.path.NavMap;
import net.sz.game.engine.navmesh.path.PathData;
import net.sz.game.engine.utils.MoveUtil;

public class MovePlayer {

    protected Vector3 pos = new Vector3();
    protected Vector3 target = new Vector3();
    protected Vector3 targetAdjusted = new Vector3();
    protected PathData pathData;
    protected float speed;
    protected float speedX;
    protected float speedZ;
    protected float moveAngle;
    protected Vector3 currentTargetPoint = null;
    protected NavMap map;

    public MovePlayer(NavMap map) {
        pathData = new PathData();
        speed = 200;
        this.map = map;
    }

    public void path() {
        long time = System.currentTimeMillis();
        pathData = map.path(pos, targetAdjusted);
        time = System.currentTimeMillis() - time;
        if (time > 2) {
            System.out.println("寻路耗时：" + time);
        }
    }

    public void update(double seconds) {
        pos = map.getNearestPointInPaths(pos);
        targetAdjusted = map.getNearestPointInPaths(target);
        if (speed == 0) {
            return;
        }
        double secondsLeft = seconds;
        for (int i = 0; i < pathData.points.size(); i++) {
            currentTargetPoint = pathData.points.get(i);
            Vector3 oldPos = new Vector3();
            oldPos.x = pos.x;
            oldPos.z = pos.z;
            double distUntilTargetReached = MoveUtil.distance(currentTargetPoint.x, currentTargetPoint.z, pos.x, pos.z);
            double timeUntilTargetReached = distUntilTargetReached / speed;
            if (timeUntilTargetReached < 0) {
                return;
            }
            double xCoordToWorkOutAngle = currentTargetPoint.x - pos.x;
            double yCoordToWorkOutAngle = currentTargetPoint.z - pos.z;
            if (xCoordToWorkOutAngle != 0 || yCoordToWorkOutAngle != 0) {
                double dirAngle = Vector3.findAngle(0, 0, xCoordToWorkOutAngle, yCoordToWorkOutAngle);//(float)Math.atan(yCoordToWorkOutAngle/xCoordToWorkOutAngle);
                moveAngle = (float) dirAngle;
                speedX = (float) Math.cos(moveAngle) * speed;
                speedZ = (float) Math.sin(moveAngle) * speed;
            } else {
                speedX = 0f;
                speedZ = 0f;
            }
            if (secondsLeft >= timeUntilTargetReached) {
                pos.x = currentTargetPoint.x;
                pos.z = currentTargetPoint.z;
                speedX = 0f;
                speedZ = 0f;
                secondsLeft -= timeUntilTargetReached;
                if (i < 0) {
                    break;
                }
                pathData.points.remove(i);
                i--;
            } else {
                //s = t(u + v)/2
                pos.x = (float) (oldPos.x + secondsLeft * speedX);
                pos.z = (float) (oldPos.z + secondsLeft * speedZ);
                secondsLeft = 0;
                break;
            }
        }
    }

    /**
     * @return the pos
     */
    public Vector3 getPos() {
        return pos;
    }

    /**
     * @return the targetAdjusted
     */
    public Vector3 getTargetAdjusted() {
        return targetAdjusted;
    }

    /**
     * @return the target
     */
    public Vector3 getTarget() {
        return target;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Vector3 target) {
        this.target = target;
    }

    /**
     * @param targetAdjusted the targetAdjusted to set
     */
    public void setTargetAdjusted(Vector3 targetAdjusted) {
        this.targetAdjusted = targetAdjusted;
    }

    /**
     * @return the map
     */
    public NavMap getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(NavMap map) {
        this.map = map;
    }
}
