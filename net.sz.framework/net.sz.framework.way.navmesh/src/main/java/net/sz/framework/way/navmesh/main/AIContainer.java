package net.sz.framework.way.navmesh.main;

//package net.sz.game.engine.navmesh.main;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.function.Predicate;
//import net.sz.game.engine.navmesh.Vector3;
//import net.sz.game.engine.navmesh.path.NavMap;
//import net.sz.game.engine.navmesh.path.PathData;
//
//
///**
// *
// * @author wzyi
// * @QQ 156320312
// * @Te 18202020823
// */
//public abstract class AIContainer {
//
//    protected static Logger LOGGER = Logger.getLogger(AIContainer.class);
//
//    protected final Vector3 direction = new Vector3();
//
//    protected NavMap navMap;
//    protected long lastMoveTime;
//    protected PathData pathData;
//    protected Vector3 currentPoint;
//    protected Vector3 targetPoint;
//    protected float speedX;
//    protected float speedY;
//    protected float moveAngle;
//    protected long totalDamage;
//    //ai总执行
//    protected int count;
//
//    //离开出生点最大距离
//    protected float backDistance = 5;
//    //巡逻半径
//    protected float patrolRadius = 3;
//    //警戒半径
//    protected float guardRadius = 4;
//    //巡逻休息最大时间：毫秒
//    protected int patrolIdleMinTime = 5000;
//    //巡逻休息最大时间：毫秒
//    protected int patrolIdleMaxTime = 20000;
//    //追击停止距离
//    protected float pursurStopDistance = 1;
//    //持续追击概率
//    protected int keepPursurOdds = 5000;
//    //攻击频率
//    protected int attackFrequency = 2000;
//    //复活时间配置
//    protected int reviveTime = 2000;
//    //死亡倒地持续时间
//    protected int dieTime;
//    //出生动作播放时间
//    protected int bornTime;
//    //出生动作播放时间
//    protected int movieTime;
//
//    //怪物死亡复活修正时间：由于场景人数
//    protected int reviveModTime;
//
//    public float getPatrolRadius() {
//        return patrolRadius;
//    }
//
//    public void setPatrolRadius(float patrolRadius) {
//        this.patrolRadius = patrolRadius;
//    }
//
//    public float getBackDistance() {
//        return backDistance;
//    }
//
//    public boolean moveTo(Vector3 target, long time, float stopDistance) {
//        try {
//            pathData = getNavMap().path(owner.getRealPosition(), target);
//            if (pathData != null && !pathData.points.isEmpty()) {
//                if (stopDistance > 0 && pathData.points.size() > 1) {
//                    targetPoint = Vector3.getStopPoint(pathData.points.get(pathData.points.size() - 2), pathData.points.get(pathData.points.size() - 1), stopDistance);
//                } else {
//                    targetPoint = pathData.points.get(pathData.points.size() - 1);
//                }
//                lastMoveTime = time;
//                return true;
//            }
//        } catch (Exception e) {
//            LOGGER.error(null, e);
//        }
//        return false;
//    }
//
//    public boolean moveByPaths(long time, PersonBaseStatus status) {
//        float speed = 1;
//
//        Vector3 currentTargetPoint = null;
//        double secondsLeft = (time - lastMoveTime) / 1000f;
//        Iterator<Vector3> it = pathData.points.iterator();
//        while (it.hasNext()) {
//            currentTargetPoint = it.next();
//            Vector3 oldPos = new Vector3();
//            oldPos.x = getCurrentPoint().x;
//            oldPos.z = getCurrentPoint().z;
//            double distUntilTargetReached = Vector3.distance(currentTargetPoint.x, currentTargetPoint.z, getCurrentPoint().x, getCurrentPoint().z);
//            double timeUntilTargetReached = distUntilTargetReached / speed;
//            if (timeUntilTargetReached <= 0) {
//                it.remove();
//                continue;
//            }
//            double xCoordToWorkOutAngle = currentTargetPoint.x - getCurrentPoint().x;
//            double yCoordToWorkOutAngle = currentTargetPoint.z - getCurrentPoint().z;
//            if (xCoordToWorkOutAngle != 0 || yCoordToWorkOutAngle != 0) {
//                double dirAngle = Vector3.findAngle(0, 0, xCoordToWorkOutAngle, yCoordToWorkOutAngle);//(float)Math.atan(yCoordToWorkOutAngle/xCoordToWorkOutAngle);
//                moveAngle = (float) dirAngle;
//                direction.y = Vector3.toUnityDegrees(dirAngle);
//                speedX = (float) Math.cos(moveAngle) * speed;
//                speedY = (float) Math.sin(moveAngle) * speed;
//            } else {
//                speedX = 0f;
//                speedY = 0f;
//            }
//            if (secondsLeft >= timeUntilTargetReached) {
//                getCurrentPoint().x = currentTargetPoint.x;
//                getCurrentPoint().z = currentTargetPoint.z;
//                speedX = 0f;
//                speedY = 0f;
//                secondsLeft -= timeUntilTargetReached;
//                it.remove();
//            } else {
//                getCurrentPoint().x = (float) (oldPos.x + secondsLeft * speedX);
//                getCurrentPoint().z = (float) (oldPos.z + secondsLeft * speedY);
//                secondsLeft = 0;
//                break;
//            }
//        }
//        lastMoveTime = time;
//        MapThreadManager.getInstance().syncPerson(getNavMap(), owner, getCurrentPoint(), targetPoint, direction, status, time, false);
////        LOGGER.warn("同步怪物{} 当前位置位置{}, 目标位置{}", getOwner().getName(), getCurrentPoint(), targetPoint);
//        return pathEnd();
//    }
//
//    public double getDistance(Vector3 target) {
//        return Vector3.distance(owner.getRealPosition(), target);
//    }
//
//    public NavMap getNavMap() {
//        if (navMap == null) {
//            navMap = owner.getMapinfos().getNavMap();
//        }
//        return navMap;
//    }
//
//    public int getAttackFrequency() {
//        return attackFrequency;
//    }
//
//    public int getDieTime() {
//        return dieTime;
//    }
//
//    /**
//     * @return the reviveTime
//     */
//    public int getReviveTime() {
//        return reviveTime + reviveModTime;
//    }
//
//    /**
//     * @return the bornTime
//     */
//    public int getBornTime() {
//        return bornTime;
//    }
//
//    /**
//     * @return the movieTime
//     */
//    public int getMovieTime() {
//        if (movieTime < 1000) {
//            movieTime = 1000;
//        }
//        return movieTime;
//    }
//
//    /**
//     * @param movieTime the movieTime to set
//     */
//    public void setMovieTime(int movieTime) {
//        this.movieTime = movieTime;
//    }
//
//    /**
//     * @return the pathData
//     */
//    public PathData getPathData() {
//        return pathData;
//    }
//
//    public boolean pathEnd() {
//        return pathData == null || pathData.getPoints().isEmpty();
//    }
//
//    /**
//     * @return the currentPoint
//     */
//    public Vector3 getCurrentPoint() {
//        if (currentPoint == null) {
//            currentPoint = new Vector3(owner.getRealPosition());
//        }
//        return currentPoint;
//    }
//
//    /**
//     * @return the direction
//     */
//    public Vector3 getDirection() {
//        return direction;
//    }
//
//    /**
//     * @return the targetPoint
//     */
//    public Vector3 getTargetPoint() {
//        return targetPoint;
//    }
//
//    /**
//     * @return the pursurStopDistance
//     */
//    public float getPursurStopDistance() {
//        return pursurStopDistance;
//    }
//
//    /**
//     * @return the guardRadius
//     */
//    public float getGuardRadius() {
//        return guardRadius;
//    }
//
//    /**
//     * @param guardRadius the guardRadius to set
//     */
//    public void setGuardRadius(float guardRadius) {
//        this.guardRadius = guardRadius;
//    }
//}
