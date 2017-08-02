package net.sz.framework.map.thread;

import net.sz.framework.map.MapInfo;
import net.sz.framework.map.MapObject;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.TaskModel;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class MapTask extends TaskModel {

    private static final SzLogger log = SzLogger.getLogger();

    protected MapInfo tmpMapInfo;
    protected MapObject tmpMapObject;

    public MapTask(MapInfo tmpMapInfo, MapObject tmpPerson) {
        this.tmpMapInfo = tmpMapInfo;
        this.tmpMapObject = tmpPerson;
    }

}
