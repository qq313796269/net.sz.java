package net.sz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sz.framework.db.Dao;
import net.sz.framework.db.sqlite.SqliteDaoImpl;
import net.sz.framework.db.struct.AttColumn;
import net.sz.framework.db.struct.AttTable;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 * 反射自动查询和封装的类
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestDao {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws Exception {
//        System.in.read();
        Dao mysqlps;
//        mysqlps = new MysqlDaoImpl("192.168.2.220:3306", "test", "root", "1qaz2wsx", Dao.DriverName.MysqlDriver56Left, false, false);
        mysqlps = new SqliteDaoImpl("/home/sqlitedata/test.db3", true);

        mysqlps.createTable(new Person());
        int forCount = 200000;
        List<DataBaseModel> ps = new ArrayList<>();
        for (int i = 0; i < forCount; i++) {
            int id = (i + 1);
            Person p1 = new Person(id, "xxxx", 10, "家住Apache基金组织");
            ps.add(p1);
        }
        for (int j = 0; j < 1; j++) {

            long bigen;
            long end;
            List<DataBaseModel> listBySql;
            bigen = TimeUtil.currentTimeMillis();
            listBySql = mysqlps.getListBySql(new Person(), "select _id,dName from Person");
            end = TimeUtil.currentTimeMillis();
            log.error("执行选择：" + listBySql.size() + " 条数据 耗时：" + (end - bigen));

            bigen = TimeUtil.currentTimeMillis();
            listBySql = mysqlps.getList(new Person());
            end = TimeUtil.currentTimeMillis();
            log.error("执行全字段选择：" + listBySql.size() + " 条数据 耗时：" + (end - bigen));

            try {
                mysqlps.delete(new Person());
                bigen = TimeUtil.currentTimeMillis();
                int insertList = mysqlps.insertList(10000, ps);
                end = TimeUtil.currentTimeMillis();
                log.error("执行插入：" + insertList + " 条数据 耗时：" + (end - bigen));

                for (Object p : ps) {
                    ((Person) p).setAge(5);
                }

                bigen = TimeUtil.currentTimeMillis();
                int updateList = mysqlps.updateList(10000, ps);
                end = TimeUtil.currentTimeMillis();
                log.error("执行更新：" + updateList + " 条数据 耗时：" + (end - bigen));
            } catch (Exception e) {
                throw e;
            }
//            System.in.read();
        }
    }
}

class BaseTest extends DataBaseModel {

    private int dd;

    public int getDd() {
        return dd;
    }

    public void setDd(int dd) {
        this.dd = dd;
    }

}

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
@AttTable(name = "Person")
class Person extends BaseTest {

    private static final SzLogger log = SzLogger.getLogger();

    @AttColumn(key = true, name = "_id")
    private int id;

    @AttColumn(name = "dName", length = 655)
    private String name;

    private int age;
    private Short age1;
    private Byte age2;
    private String address;
    @AttColumn(unique = true)
    private String address2;

    @AttColumn(index = true)
    private String address3;

    @AttColumn(nullable = false)
    private Map<String, String> map = new HashMap<>();

    public Person() {
        // TODO Auto-generated constructor stub
    }

    public Person(int id, String name, int age, String address) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Short getAge1() {
        return age1;
    }

    public void setAge1(Short age1) {
        this.age1 = age1;
    }

    public Byte getAge2() {
        return age2;
    }

    public void setAge2(Byte age2) {
        this.age2 = age2;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", name=" + name + ", age=" + age + ", age1=" + age1 + ", age2=" + age2 + ", address=" + address + ", map=" + map + '}';
    }

}
