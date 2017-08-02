package net.sz.framework.map.thread;

import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.map.run.RunTimerTask;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.struct.thread.ThreadType;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapPersonThread extends SzQueueThread {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 1928153076994880003L;

    /*处理地图寻路设置，又服务器寻路路径移动，怪物，npc，玩家*/
    private RunTimerTask personRunTimerTask;
    /* 线程控制的玩家 */
    private ConcurrentHashMap<Long, Person> personMap = new ConcurrentHashMap<>();

    /**
     *
     * @param name
     */
    public MapPersonThread(String name) {
        super(ThreadType.User, MapThreadExcutor.THREAD_GROUP, name, 1);
    }

    public MapPersonThread(String name, int threadCount) {
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

    public ConcurrentHashMap<Long, Person> getPersonMap() {
        return personMap;
    }

    public void setPersonMap(ConcurrentHashMap<Long, Person> personMap) {
        this.personMap = personMap;
    }

}
