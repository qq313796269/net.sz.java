package net.sz;

import java.io.Serializable;
import net.sz.framework.db.sqlite.SqliteDaoImpl;
import net.sz.framework.db.struct.AttColumn;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.db.thread.CUDThread;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.LongId0;
import net.sz.framework.utils.RandomUtils;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TopTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws Exception {

        SqliteDaoImpl sdi = new SqliteDaoImpl("/home/toplist.db");

        CUDThread cudt = new CUDThread(sdi, "top-list-thread");
        /*设置异步操作的缓冲容量*/
        cudt.setMaxTaskCount(500000);
        /*设置单次写入的数据量*/
        cudt.setGetTaskMax(3000);

        /*id生成器*/
        LongId0 longId0 = new LongId0();

        int points = 1200;

        TopList topList = new TopList();
//        for (int i = 1; i <= points; i++) {
//            topList.setDataTableName(getTableName(i));
//            /*创建表*/
//            sdi.createTable(topList);
//        }


        /*模拟5万个玩家*/
        for (int i = 1; i <= 50000; i++) {
            long id = longId0.getId();
            /*模拟关卡*/
            int j = 1;
            for (; j <= points; j++) {

                TopList clone = (TopList) topList.clone();
                clone.setDataTableName(getTableName(j));
                clone.setPid(id);
                clone.setTime(System.currentTimeMillis());
                clone.setPoint(j);
                clone.setStar(3);
                /*随机积分*/
                clone.setIntegral(RandomUtils.random(20000, 400000));

                cudt.insert_Sync(clone);
            }
            log.info("总共写入数据量：" + (i * (j - 1)));
            Thread.sleep(1000);
        }
    }

    /**
     * 获取表名
     *
     * @param point
     * @return
     */
    static String getTableName(int point) {
        int tableId = 0;
        if (point < 300) {
            /*第一段前300关存入15张表*/
            tableId = 1000 + point % 15;
        } else if (point < 800) {
            /*第二段后500关卡存入5张表*/
            tableId = 2000 + point % 5;
        } else {
            /*800关卡以后的数据存入剩余10张表*/
            tableId = 3000 + point % 10;
        }

        return TopList.class.getSimpleName().toLowerCase() + tableId;
    }

}

class TopList extends DataBaseModel implements Serializable, Cloneable {

    /*玩家id*/
    @AttColumn(index = true)
    private long pid;
    /*关卡*/
    @AttColumn(index = true)
    private int point;
    /*积分*/
    @AttColumn(index = true)
    private int integral;
    /*星级*/
    private int star;
    /*通关时间*/
    private long time;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    @Override
    public Object clone() {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

}
