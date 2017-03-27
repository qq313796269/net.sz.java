package net.sz.game.engine.map.timer;

import java.util.ArrayList;
import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.map.thread.MapThread;
import net.sz.game.engine.thread.TimerTaskModel;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapThreadTimerTask extends TimerTaskModel {

    private static SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 3134083867566457672L;

    protected MapThread mapThread;
    protected ArrayList<Person> playerList = new ArrayList<>();

    public MapThreadTimerTask(MapThread mapThread, int intervalTime) {
        super(intervalTime);
        this.mapThread = mapThread;
    }

}
