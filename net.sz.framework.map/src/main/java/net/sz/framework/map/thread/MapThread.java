package net.sz.framework.map.thread;

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
public class MapThread extends SzQueueThread {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -6320887807708168829L;

    public MapThread(String name, int threadCount) {
        super(ThreadType.User, MapThreadExcutor.THREAD_GROUP, name, threadCount);
    }

}
