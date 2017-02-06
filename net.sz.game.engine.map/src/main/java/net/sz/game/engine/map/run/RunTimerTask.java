package net.sz.game.engine.map.run;

import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.map.MapInfo;
import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.navmesh.path.PathData;
import net.sz.game.engine.thread.TimerTaskEvent;
import net.sz.game.engine.util.ConcurrentHashSet;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class RunTimerTask extends TimerTaskEvent {

    private static final Logger log = Logger.getLogger(RunTimerTask.class);
    private static final long serialVersionUID = -2840856134900737793L;

    protected MapInfo mapInfo;
    protected int mapThreadNext;

    public abstract void findWay(Person person, double endx, double endz, long cooldown, long theid, boolean tellSelf, boolean changeDir);

    public abstract void findWay(Person person, double endx, double endz, long cooldown, long theid, Double vr, boolean tellSelf, boolean changeDir);

    public abstract void findWay(Person person, Vector3 vend, long cooldown, long theid, Double vr, boolean tellSelf, boolean changeDir);

    public abstract void findWay(Person person, PathData path, long cooldown, Vector3 vend, long theid, Double vr, boolean tellSelf, boolean changeDir);

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
     * @param needSendStopMsg 是否发送通知
     */
    public abstract void removeRunInfo(long personId, boolean needSendStopMsg);

    /**
     *
     * @param personId
     * @return
     */
    public abstract boolean checkHave(long personId);

}
