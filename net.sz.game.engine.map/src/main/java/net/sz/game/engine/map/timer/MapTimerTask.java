package net.sz.game.engine.map.timer;

import net.sz.game.engine.map.MapInfo;
import net.sz.game.engine.map.manager.AbsMapManager;
import net.sz.game.engine.thread.TimerTaskModel;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapTimerTask extends TimerTaskModel {

    private static final long serialVersionUID = -2468526730972775709L;

    protected final MapInfo mapInfo;
    protected final String mapKey;

    public MapTimerTask(MapInfo mapInfo, int intervalTime) {
        super(intervalTime);
        this.mapInfo = mapInfo;
        this.mapKey = AbsMapManager.getMapSkey(mapInfo);
    }

}
