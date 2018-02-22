package net.sz.framework.map.timer;

import net.sz.framework.map.MapInfo;
import net.sz.framework.map.manager.AbsMapManager;
import net.sz.framework.thread.timer.TimerTask;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapTimerTask extends TimerTask {

    private static final long serialVersionUID = -2468526730972775709L;

    protected final MapInfo mapInfo;
    protected final String mapKey;

    public MapTimerTask(MapInfo mapInfo, int intervalTime) {
        super(intervalTime);
        this.mapInfo = mapInfo;
        this.mapKey = AbsMapManager.getMapSkey(mapInfo);
    }

}
