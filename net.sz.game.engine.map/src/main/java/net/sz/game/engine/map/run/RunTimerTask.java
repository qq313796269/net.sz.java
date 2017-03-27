package net.sz.game.engine.map.run;

import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.map.MapInfo;
import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.map.thread.MapThread;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.navmesh.path.PathData;
import net.sz.game.engine.thread.TimerTaskModel;
import net.sz.game.engine.util.ConcurrentHashSet;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class RunTimerTask extends TimerTaskModel {

    private static SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -2840856134900737793L;

    protected MapThread mapThread;

    public abstract void findWay(Person person, double endx, double endz, float theModelVr, long cooldown, long theid, boolean tellSelf, boolean changeDir, Runnable callback);

    public abstract void findWay(Person person, double endx, double endz, float theModelVr, long cooldown, long theid, Double vr, boolean tellSelf, boolean changeDir, Runnable callback);

    public abstract void findWay(Person person, Vector3 vend, float theModelVr, long cooldown, long theid, Double vr, boolean tellSelf, boolean changeDir, Runnable callback);

    public abstract void findWay(Person person, PathData path, long cooldown, Vector3 vend, float theModelVr, long theid, Double vr, boolean tellSelf, boolean changeDir, Runnable callback);

    /**
     * 目前运动
     */
    protected ConcurrentHashMap<Long, PersonRunInfo> runs = new ConcurrentHashMap<>(); // 主线程执行,线程安全的
    /**
     * 地图如果划分若干线程的话
     */
    protected ConcurrentHashMap<Long, ConcurrentHashSet<Long>> rhreadRuns = new ConcurrentHashMap<>(); // 主线程执行,线程安全的

    public RunTimerTask(int intervalTime) {
        super(intervalTime);
    }

    /**
     *
     * @param personId
     * @return
     */
    public abstract PersonRunInfo getRunInfo(long personId);

    /**
     *
     * @param personRunInfo
     * @param tellSelf
     * @param changeDir
     */
    public abstract void addRunInfo(PersonRunInfo personRunInfo, boolean tellSelf, boolean changeDir);

    /**
     * 非立刻终止的
     *
     * @param personId
     */
    public abstract void stopRunInfo(long personId);

    /**
     *
     * @param personId
     */
    public abstract void removeRunInfo(long personId);

    /**
     *
     * @param personId
     * @param isEnd 是否是正常移动结束
     * @param sendMsg 是否发送通知
     */
    public abstract void removeRunInfo(long personId, boolean isEnd, boolean sendMsg);

    /**
     *
     * @param personId
     * @return
     */
    public abstract boolean checkHave(long personId);

}
