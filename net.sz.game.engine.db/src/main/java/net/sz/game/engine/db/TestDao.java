package net.sz.game.engine.db;

import java.util.ArrayList;

import net.sz.game.engine.szlog.SzLogger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 反射自动查询和封装的类
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TestDao {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws Exception {
//        System.in.read();
        Dao mysqlps;
//        mysqlps = new MysqlDaoImpl("192.168.2.220:3306", "test", "root", "1qaz2wsx", Dao.DriverName.MysqlDriver56Left, false, false);
        mysqlps = new SqliteDaoImpl(null, "/home/sqlitedata/test.db3", "", "", false, false);

        mysqlps.createTable(Person.class);
        int forCount = 200000;
        List<Object> ps = new ArrayList<>();
        for (int i = 0; i < forCount; i++) {
            int id = (i + 1);
            Person p1 = new Person(id, "xxxx", 10, "家住Apache基金组织");
            ps.add(p1);
        }
        for (int j = 0; j < 1; j++) {

            long bigen;
            long end;
            List<Person> listBySql;
            bigen = System.currentTimeMillis();
            listBySql = mysqlps.getListBySql(Person.class, "select _id,dName from Person");
            end = System.currentTimeMillis();
            log.error("执行选择：" + listBySql.size() + " 条数据 耗时：" + (end - bigen));

            bigen = System.currentTimeMillis();
            listBySql = mysqlps.getList(Person.class);
            end = System.currentTimeMillis();
            log.error("执行全字段选择：" + listBySql.size() + " 条数据 耗时：" + (end - bigen));

            try {
                mysqlps.delete(Person.class);
                bigen = System.currentTimeMillis();
                int insertList = mysqlps.insertList(10000, ps);
                end = System.currentTimeMillis();
                log.error("执行插入：" + insertList + " 条数据 耗时：" + (end - bigen));

                for (Object p : ps) {
                    ((Person) p).setAge(5);
                }

                bigen = System.currentTimeMillis();
                int updateList = mysqlps.updateList(10000, ps);
                end = System.currentTimeMillis();
                log.error("执行更新：" + updateList + " 条数据 耗时：" + (end - bigen));
            } catch (Exception e) {
                throw e;
            }
//            System.in.read();
        }
    }
}

class BaseTest {

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
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
@Table(name = "Person")
class Person extends BaseTest {

    private static SzLogger log = SzLogger.getLogger();

    @Id
    @Column(name = "_id")
    private int id;

    @Column(name = "dName", length = 655)
    private String name;

    private int age;
    private Short age1;
    private Byte age2;
    private String address;

    @Column(nullable = false)
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
