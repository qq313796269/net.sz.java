package net.sz.framework.db.thread;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.framework.db.Dao;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.db.IDaoRun;
import net.sz.framework.struct.thread.BaseThreadModel;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CUDThread extends BaseThreadModel {

    private static final SzLogger log = SzLogger.getLogger();

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("DB-THREAD-GROUP");
    private static final long serialVersionUID = 6734580741605186941L;

    private DbObjects insertDbObjects;
    private DbObjects updateDbObjects;
    private DbObjects deleteDbObjects;

    private DbRuns insertDbRuns;
    private DbRuns updateDbRuns;
    private DbRuns deleteDbRuns;

    private Dao dao;

    private int getTaskMax = 200;

    public Dao getDao() {
        return dao;
    }

    public CUDThread(Dao dao, String name) {
        super(THREAD_GROUP, name);
        this.dao = dao;
        insertDbObjects = new DbObjects();
        updateDbObjects = new DbObjects();
        deleteDbObjects = new DbObjects();

        insertDbRuns = new DbRuns();
        updateDbRuns = new DbRuns();
        deleteDbRuns = new DbRuns();
        this.setMaxTaskCount(50000);
        this.start();
    }

    public int getGetTaskMax() {
        return getTaskMax;
    }

    public void setGetTaskMax(int getTaskMax) {
        this.getTaskMax = getTaskMax;
    }

    public int size() {
        int size = 0;

        size += this.insertDbObjects.size();
        size += this.deleteDbObjects.size();
        size += this.updateDbObjects.size();

        size += this.insertDbRuns.size();
        size += this.updateDbRuns.size();
        size += this.deleteDbRuns.size();

        return size;
    }

    /**
     * 插入数据对象
     *
     * @param object
     * @throws Exception
     */
    public void insert(DataBaseModel object) throws Exception {
        this.dao.inserts(object);
    }

    /**
     * 更新数据对象
     *
     * @param object
     * @throws Exception
     */
    public void update(DataBaseModel object) throws Exception {
        this.dao.update(object);
    }

    /**
     * 删除数据对象
     *
     * @param object
     * @throws Exception
     */
    public void delete(DataBaseModel object) throws Exception {
        this.dao.delete(object);
    }

    /**
     * 插入数据对象
     *
     * @param con
     * @param object
     * @throws Exception
     */
    public void insert(Connection con, DataBaseModel object) throws Exception {
        this.dao.inserts(con, object);
    }

    /**
     * 更新数据对象
     *
     * @param con
     * @param object
     * @throws Exception
     */
    public void update(Connection con, DataBaseModel object) throws Exception {
        this.dao.update(con, object);
    }

    /**
     * 删除数据对象
     *
     * @param con
     * @param object
     * @throws Exception
     */
    public void delete(Connection con, DataBaseModel object) throws Exception {
        this.dao.delete(con, object);
    }

    /**
     * 异步写入数据对象
     *
     * @param object
     */
    public void insert_Sync(DataBaseModel object) {
        insertDbObjects.add(object);
    }

    /**
     * 异步更新数据对象
     *
     * @param object
     */
    public void update_Sync(DataBaseModel object) {
        updateDbObjects.objs.add(object);
    }

    /**
     * 异步删除数据对象
     *
     * @param object
     */
    public void delete_Sync(DataBaseModel object) {
        deleteDbObjects.add(object);
    }

    public void dao_Run(IDaoRun run) throws Exception {
        run.run(this.dao);
    }

    public void insert_run_Sync(IDaoRun run) {
        insertDbRuns.add(run);
    }

    public void update_run_Sync(IDaoRun run) {
        updateDbRuns.add(run);
    }

    public void delete_run_Sync(IDaoRun run) {
        deleteDbRuns.add(run);
    }

    @Override
    public void run() {
        while (true) {
            try {

                while (size() < 1) {
                    synchronized (this) {
                        this.wait(2);
                    }
                }

                {
                    ArrayList<DataBaseModel> objects = CUDThread.this.deleteDbObjects.getObjects();
                    if (!objects.isEmpty()) {
                        try {
                            long currentTimeMillis = TimeUtil.currentTimeMillis();
                            int deleteList = CUDThread.this.dao.deleteList(objects);
                            if (log.isDebugEnabled()) {
                                log.debug("删除数据影响行数：" + deleteList + " 耗时：" + (TimeUtil.currentTimeMillis() - currentTimeMillis));
                            }
                        } catch (Throwable ex) {
                            log.error("异步写入数据库错误 删除对象", ex);
                        }
                    }
                    CUDThread.this.deleteDbObjects.values.clear();
                }
                {
                    ArrayList<DataBaseModel> objects = CUDThread.this.insertDbObjects.getObjects();
                    if (!objects.isEmpty()) {
                        try {
                            long currentTimeMillis = TimeUtil.currentTimeMillis();
                            int insertList = CUDThread.this.dao.insertList(2000, objects);
                            if (log.isDebugEnabled()) {
                                log.debug("新增数据插入影响行数：" + insertList + " 耗时：" + (TimeUtil.currentTimeMillis() - currentTimeMillis));
                            }
                        } catch (Throwable ex) {
                            log.error("异步写入数据库错误 插入对象", ex);
                        }
                    }
                    CUDThread.this.insertDbObjects.values.clear();
                }
                {
                    ArrayList<DataBaseModel> objects = CUDThread.this.updateDbObjects.getObjects();
                    if (!objects.isEmpty()) {
                        try {
                            long currentTimeMillis = TimeUtil.currentTimeMillis();
                            int updateList = CUDThread.this.dao.updateList(2000, objects);
                            if (log.isDebugEnabled()) {
                                log.debug("更新数据插入影响行数：" + updateList + " 耗时：" + (TimeUtil.currentTimeMillis() - currentTimeMillis));
                            }
                        } catch (Throwable ex) {
                            log.error("异步写入数据库错误 更新对象", ex);
                        }
                    }
                    CUDThread.this.updateDbObjects.values.clear();
                }
            } catch (Throwable ex) {
                log.error("数据库错误", ex);
            }
            if (log.isDebugEnabled()) {
                log.debug("当前待处理剩余数量：" + size());
            }
        }
    }

    final class DbRuns {

        public DbRuns() {
        }

        ConcurrentLinkedQueue<IDaoRun> objs = new ConcurrentLinkedQueue<>();

        boolean add(IDaoRun run) {
            if (size() > getMaxTaskCount()) {
                throw new UnsupportedOperationException("积压过多的任务：" + getMaxTaskCount());
            }
            return objs.add(run);
        }

        int size() {
            return values.size() + objs.size();
        }

        ArrayList<IDaoRun> values = new ArrayList<>();

        ArrayList<IDaoRun> getObjects() {
            for (int i = 0; i < CUDThread.this.getGetTaskMax(); i++) {
                IDaoRun peek = objs.poll();
                if (peek != null) {
                    values.add(peek);
                } else if (objs.isEmpty()) {
                    //已经没有数据了
                    break;
                }
            }
            return values;
        }
    }

    final class DbObjects {

        public DbObjects() {
        }

        ConcurrentLinkedQueue<DataBaseModel> objs = new ConcurrentLinkedQueue<>();

        boolean add(DataBaseModel object) {
            if (size() > getMaxTaskCount()) {
                throw new UnsupportedOperationException("积压过多的任务：" + getMaxTaskCount());
            }
            return objs.add(object);
        }

        int size() {
            return values.size() + objs.size();
        }

        ArrayList<DataBaseModel> values = new ArrayList<>();

        ArrayList<DataBaseModel> getObjects() {
            for (int i = 0; i < CUDThread.this.getGetTaskMax(); i++) {
                DataBaseModel peek = objs.poll();
                if (peek != null) {
                    values.add(peek);
                } else if (objs.isEmpty()) {
                    //已经没有数据了
                    break;
                }
            }
            return values;
        }
    }
}
