package net.sz.framework.map.timer;

import java.util.ArrayList;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.map.thread.MapPersonThread;
import net.sz.framework.szthread.TimerTaskModel;

import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapThreadTimerTask extends TimerTaskModel {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 3134083867566457672L;

    protected MapPersonThread mapThread;
    protected ArrayList<Person> playerList = new ArrayList<>();

    public MapThreadTimerTask(MapPersonThread mapThread, int intervalTime) {
        super(intervalTime);
        this.mapThread = mapThread;
    }

}
