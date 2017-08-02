package net.sz;

import java.sql.Connection;
import net.sz.framework.db.struct.AttColumn;
import net.sz.framework.db.Dao;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.db.mysql.MysqlDaoImpl;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.IntegerSSId;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test {

    private static SzLogger log = SzLogger.getLogger();
    static IntegerSSId ids = new IntegerSSId();
    static Dao mysqlps = null;
    static Connection connection;

    static {
        try {
            mysqlps = new MysqlDaoImpl("192.168.2.220:3306", "local_loginsr_10001_100_801", "root", "1qaz2wsx", Dao.DriverName.MysqlDriver56Left, true, true);
//            mysqlps = new SqliteDaoImpl("/home/sqlitedata/test.db3", false, false);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {
        mysqlps.createTable(new TT());

        while (true) {
            Thread thread1 = new Thread(() -> {
                add();
            });

            Thread thread2 = new Thread(() -> {
                add();
            });

            Thread thread3 = new Thread(() -> {
                add();
            });

            thread1.start();
            thread2.start();
            thread3.start();

            thread1.join();
            thread2.join();
            thread3.join();
        }

    }

    public static void add() {
        try {

            TT[] us = new TT[500];

            for (int i = 0; i < us.length; i++) {
                TT userinfo = new TT();
                int id = ids.getId();
                userinfo.setId1(id);
                userinfo.setId2(id);
                userinfo.setId3(id);
                userinfo.setId4(id);
                us[i] = userinfo;
            }
            mysqlps.inserts(us);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    static class TT extends DataBaseModel {

        @AttColumn(key = true)
        private long id1;
        @AttColumn(key = true)
        private long id2;
        @AttColumn(index = true)
        private long id3;
        @AttColumn(index = true, unique = true)
        private long id4;

        public long getId1() {
            return id1;
        }

        public void setId1(long id1) {
            this.id1 = id1;
        }

        public long getId2() {
            return id2;
        }

        public void setId2(long id2) {
            this.id2 = id2;
        }

        public long getId3() {
            return id3;
        }

        public void setId3(long id3) {
            this.id3 = id3;
        }

        public long getId4() {
            return id4;
        }

        public void setId4(long id4) {
            this.id4 = id4;
        }

    }

}
