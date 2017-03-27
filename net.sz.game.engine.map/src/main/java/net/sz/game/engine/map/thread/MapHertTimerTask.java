package net.sz.game.engine.map.thread;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.map.MapInfo;
import net.sz.game.engine.map.manager.AbsMapManager;
import net.sz.game.engine.thread.TimerTaskModel;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class MapHertTimerTask extends TimerTaskModel {

    private static SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -4590064142759244242L;

    public MapHertTimerTask() {
        super(8);
    }

    @Override
    public void run() {
        ArrayList< MapInfo> mapInfo_Map = new ArrayList<>(AbsMapManager.getMapInfo_Map().values());
        for (int i = 0; i < mapInfo_Map.size(); i++) {
            MapInfo mapInfo = mapInfo_Map.get(i);
            MapThreadExcutor.MapExcutor.timerRun(mapInfo.getTimerTaskMap());
        }
    }

}
