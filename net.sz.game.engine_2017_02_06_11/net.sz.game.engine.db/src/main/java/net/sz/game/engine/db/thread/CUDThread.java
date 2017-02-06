package net.sz.game.engine.db.thread;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.game.engine.db.Dao;
import net.sz.game.engine.db.IDaoRun;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.ThreadRunnable;
import net.sz.game.engine.thread.ThreadType;
import net.sz.game.engine.thread.TimerTaskEvent;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CUDThread extends ThreadRunnable {

    private static final Logger log = Logger.getLogger(CUDThread.class);

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("DB THREAD　GROUP");

    private DbObjects insertDbObjects;
    private DbObjects updateDbObjects;
    private DbObjects deleteDbObjects;

    private DbRuns insertDbRuns;
    private DbRuns updateDbRuns;
    private DbRuns deleteDbRuns;

    private Dao dao;

    public Dao getDao() {
        return dao;
    }

    public CUDThread(Dao dao, String name, int threadCount) {
        super(ThreadType.Sys, THREAD_GROUP, name, threadCount);
        this.dao = dao;
        insertDbObjects = new DbObjects();
        updateDbObjects = new DbObjects();
        deleteDbObjects = new DbObjects();

        insertDbRuns = new DbRuns();
        updateDbRuns = new DbRuns();
        deleteDbRuns = new DbRuns();
        this.addTimerTask(new DbTimerEvent(200));
        ThreadPool.getThreadMap().put(tid, this);
    }

    public int size() {
        int size = 0;

        size += this.insertDbObjects.objs.size();
        size += size;
        size += this.deleteDbObjects.objs.size();
        size += this.updateDbObjects.objs.size();

        return size;
    }

    public void insert(Object object) throws Exception {
        this.dao.insert(object);
    }

    public void update(Object object) throws Exception {
        this.dao.update(object);
    }

    public void delete(Object object) throws Exception {
        this.dao.delete(object);
    }

    public void insert(Connection con, Object object) throws Exception {
        this.dao.insert(con, object);
    }

    public void update(Connection con, Object object) throws Exception {
        this.dao.update(con, object);
    }

    public void delete(Connection con, Object object) throws Exception {
        this.dao.delete(con, object);
    }

    public void insert_Sync(Object object) {
        insertDbObjects.objs.add(object);
    }

    public void update_Sync(Object object) {
        updateDbObjects.objs.add(object);
    }

    public void delete_Sync(Object object) {
        deleteDbObjects.objs.add(object);
    }

    public void dao_Run(IDaoRun run) throws Exception {
        run.run(this.dao);
    }

    public void insert_run_Sync(IDaoRun run) {
        insertDbRuns.objs.add(run);
    }

    public void update_run_Sync(IDaoRun run) {
        updateDbRuns.objs.add(run);
    }

    public void delete_run_Sync(IDaoRun run) {
        deleteDbRuns.objs.add(run);
    }

    final class DbTimerEvent extends TimerTaskEvent {

        public DbTimerEvent(int intervalTime) {
            super(intervalTime);
        }

        @Override
        public void run() {
            {
                ArrayList<Object> objects = CUDThread.this.deleteDbObjects.getObjects();
                if (!objects.isEmpty()) {
                    try {
                        int deleteList = CUDThread.this.dao.deleteList(objects);
                        log.debug("删除数据影响行数：" + deleteList);
                    } catch (Exception ex) {
                        log.error("异步写入数据库错误 删除对象", ex);
                    }
                }
                CUDThread.this.deleteDbObjects.values.clear();
            }
            {
                ArrayList<Object> objects = CUDThread.this.insertDbObjects.getObjects();
                if (!objects.isEmpty()) {
                    try {
                        int insertList = CUDThread.this.dao.insertList(2000, objects);
                        log.debug("新增数据插入影响行数：" + insertList);
                    } catch (Exception ex) {
                        log.error("异步写入数据库错误 插入对象", ex);
                    }
                }
                CUDThread.this.insertDbObjects.values.clear();
            }
            {
                ArrayList<Object> objects = CUDThread.this.updateDbObjects.getObjects();
                if (!objects.isEmpty()) {
                    try {
                        int updateList = CUDThread.this.dao.updateList(2000, objects);
                        log.debug("更新数据插入影响行数：" + updateList);
                    } catch (Exception ex) {
                        log.error("异步写入数据库错误 更新对象", ex);
                    }
                }
                CUDThread.this.updateDbObjects.values.clear();
            }
        }

    }

    final class DbRuns {

        public DbRuns() {
        }

        ConcurrentLinkedQueue<IDaoRun> objs = new ConcurrentLinkedQueue<>();

        ArrayList<IDaoRun> values = new ArrayList<>();

        int getSize() {
            return values.size() + objs.size();
        }

        ArrayList<IDaoRun> getObjects() {
            for (int i = 0; i < 1000; i++) {
                IDaoRun peek = objs.poll();
                if (peek != null) {
                    values.add(peek);
                } else {
                    if (objs.isEmpty()) {
                        //已经没有数据了
                        break;
                    }
                }
            }
            return values;
        }
    }

    final class DbObjects {

        public DbObjects() {
        }

        ConcurrentLinkedQueue<Object> objs = new ConcurrentLinkedQueue<>();

        ArrayList<Object> values = new ArrayList<>();

        int getSize() {
            return values.size() + objs.size();
        }

        ArrayList<Object> getObjects() {
            for (int i = 0; i < 1000; i++) {
                Object peek = objs.poll();
                if (peek != null) {
                    values.add(peek);
                } else {
                    if (objs.isEmpty()) {
                        //已经没有数据了
                        break;
                    }
                }
            }
            return values;
        }
    }
}
