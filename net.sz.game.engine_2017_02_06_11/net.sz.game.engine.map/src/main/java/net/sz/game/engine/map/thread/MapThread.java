package net.sz.game.engine.map.thread;

import net.sz.game.engine.thread.ThreadRunnable;
import net.sz.game.engine.thread.ThreadType;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapThread extends ThreadRunnable {

    private static final Logger log = Logger.getLogger(MapThread.class);

    protected long mapId;
    protected int mapModelId;
    protected int maplinId;
    protected int mapThreadNext;

    public MapThread(long mapId, int mapModelId, int MaplinId, int mapThreadNext, String name) {
        super(ThreadType.User, MapThreadExcutor.THREAD_GROUP, name, 1);
        this.mapId = mapId;
        this.mapModelId = mapModelId;
        this.maplinId = MaplinId;
        this.mapThreadNext = mapThreadNext;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public int getMapModelId() {
        return mapModelId;
    }

    public void setMapModelId(int mapModelId) {
        this.mapModelId = mapModelId;
    }

    public int getMaplinId() {
        return maplinId;
    }

    public void setMaplinId(int MaplinId) {
        this.maplinId = MaplinId;
    }

}
