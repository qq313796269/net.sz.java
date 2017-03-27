package net.sz.game.engine.map.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.map.MapInfo;
import net.sz.game.engine.map.manager.AbsMapManager;
import net.sz.game.engine.map.run.RunTimerTask;
import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.thread.SzThread;
import net.sz.game.engine.thread.ThreadType;
import net.sz.game.engine.thread.TimerTaskModel;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapThread extends SzThread {

    private static SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 1928153076994880003L;

    /*处理地图寻路设置，又服务器寻路路径移动，怪物，npc，玩家*/
    private RunTimerTask personRunTimerTask;
    /* 线程控制的玩家 */
    private ConcurrentHashMap<Long, Person> personMap = new ConcurrentHashMap<>();

    /**
     *
     * @param name
     */
    public MapThread(String name) {
        super(ThreadType.User, MapThreadExcutor.THREAD_GROUP, name, 1);
    }

    public MapThread(String name, int threadCount) {
        super(ThreadType.User, MapThreadExcutor.THREAD_GROUP, name, threadCount);
    }

    public int personSize() {
        return personMap.size();
    }

    public RunTimerTask getPersonRunTimerTask() {
        return personRunTimerTask;
    }

    public void setPersonRunTimerTask(RunTimerTask personRunTimerTask) {
        this.personRunTimerTask = personRunTimerTask;
    }

//    /**
//     *
//     */
//    @Override
//    public void timerRun() {
//        HashMap<String, ConcurrentHashMap<Long, TimerTaskEvent>> hashMap = new HashMap<>(mapInfoTimerTaskMap);
//        for (Map.Entry<String, ConcurrentHashMap<Long, TimerTaskEvent>> entry : hashMap.entrySet()) {
//            timerRun(entry.getValue());
//        }
//    }
//    /**
//     * boolean romveTimerTask(String mapkey, long taskId)
//     *
//     * @param taskId
//     * @return
//     * @deprecated
//     */
//    @Override
//    @Deprecated
//    public boolean romveTimerTask(long taskId) {
//        throw new UnsupportedOperationException("Unsupported Operation");
//    }
//    /**
//     * boolean romveTimerTask(String mapkey, TimerTaskEvent runnable)
//     *
//     * @param runnable
//     * @return
//     * @deprecated
//     */
//    @Override
//    @Deprecated
//    public boolean romveTimerTask(TimerTaskEvent runnable) {
//        throw new UnsupportedOperationException("Unsupported Operation");
//    }
//    /**
//     *
//     * @param mapkey
//     * @param taskId
//     * @return
//     */
//    public boolean romveTimerTask(String mapkey, long taskId) {
//        ConcurrentHashMap<Long, TimerTaskEvent> get = this.mapInfoTimerTaskMap.get(mapkey);
//        if (get != null && get.remove(taskId) != null) {
//            return true;
//        }
//        return false;
//    }
//    /**
//     *
//     * @param mapkey
//     * @param runnable
//     * @return
//     */
//    public boolean romveTimerTask(String mapkey, TimerTaskEvent runnable) {
//        return romveTimerTask(mapkey, runnable.getTaskEventId());
//    }
//    /**
//     * void addTimerTask(String mapkey, TimerTaskEvent runnable)
//     *
//     * @param runnable
//     * @deprecated
//     */
//    @Override
//    @Deprecated
//    public void addTimerTask(TimerTaskEvent runnable) {
//        throw new UnsupportedOperationException("Unsupported Operation");
//    }
//    /**
//     *
//     * @param mapkey
//     * @param runnable
//     */
//    public void addTimerTask(String mapkey, TimerTaskEvent runnable) {
//        ConcurrentHashMap<Long, TimerTaskEvent> get = this.mapInfoTimerTaskMap.get(mapkey);
//        if (get != null) {
//            get.put(runnable.getTaskEventId(), runnable);
//        } else {
//            throw new UnsupportedOperationException("无法找到地图");
//        }
//    }
    public ConcurrentHashMap<Long, Person> getPersonMap() {
        return personMap;
    }

    public void setPersonMap(ConcurrentHashMap<Long, Person> personMap) {
        this.personMap = personMap;
    }

}
