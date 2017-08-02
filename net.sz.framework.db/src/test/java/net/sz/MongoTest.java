package net.sz;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sz.framework.db.mongodb.MongoDbImpl;
import net.sz.framework.db.mongodb.struct.MongoDbBaseModel;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.szlog.SzLogger;
import org.bson.BsonDocument;
import org.bson.Document;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MongoTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {

        MongoDbImpl.offLog();

        try {
            // 连接到 mongodb 服务
            MongoDbImpl mongoClient = new MongoDbImpl("192.168.1.220", 27017, "mycol");
            MModel base = new MModel();
            mongoClient.setClassType(base);
            mongoClient.dropDatabase();
//            System.out.println("准备就绪，请敲击回车");
//            System.in.read();

            inster(mongoClient);
            inster(mongoClient);
//            insters(mongoClient);
            find(mongoClient);
//            find(mongoClient);
//            find(mongoClient);
            reader(mongoClient);
            update(mongoClient);
            delete(mongoClient);
            reader(mongoClient);
//            reader(mongoClient);
//            reader(mongoClient);

        } catch (Exception e) {
            log.error(e.getClass().getName(), e);
        }
    }

    static void insters(MongoDbImpl<MongoDbBaseModel> mdi) {

        long currentTimeMillis;
        currentTimeMillis = System.currentTimeMillis();

        /* 连接到数据库                                        相当于表名 */
        MongoCollection<Document> collection = mdi.getCollection("mycol");

        ArrayList<Document> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Document document = new Document().
                    append("description", "database" + i).
                    append("likes", 100 + i).
                    append("by", "Fly" + i);
            list.add(document);
        }
        collection.insertMany(list);
        System.out.println("10000条文档插入成功：" + (System.currentTimeMillis() - currentTimeMillis));

    }

    static void inster(MongoDbImpl<MongoDbBaseModel> mdi) throws Exception {
        long currentTimeMillis;
        currentTimeMillis = System.currentTimeMillis();
        MModel mm = new MModel();
        mm.setAge(2);
        mm.setId(1);
        mm.setName("Troy.Chen");
        mdi.insert(mm);
        System.out.println("文档插入成功：" + (System.currentTimeMillis() - currentTimeMillis));
    }

    static void delete(MongoDbImpl<MongoDbBaseModel> mdi) throws Exception {
        System.out.println("删除：" + mdi.deleteOne(new MModel(), "{}"));
    }

    static void update(MongoDbImpl mdi) throws Exception {
        MModel mm = new MModel();
        mm.setAge(2);
        mm.setId(1);
        mm.setName("Troy.Chen");
        mdi.updateOneByWhere("{'name':'name'}", mm);
    }

    static void find(MongoDbImpl<MongoDbBaseModel> mdi) {
        long currentTimeMillis;
        currentTimeMillis = System.currentTimeMillis();
        /* 连接到数据库                                        相当于表名 */
        MongoCollection<Document> collection = mdi.getCollection("mycol");
//        Document append = new Document().append("likes", 8273);//new Document().append("$ne", null)
//        System.out.println(append.toJson());
//Document append = new Document();
        Document parse = Document.parse("{'likes':8273}");
        parse = Document.parse("{'likes':8273}");
//BsonDocument
        /*这里只是创建了类似 datareader 的地方*/
        FindIterable<Document> find = collection.find(parse);
//        System.out.println("文档查询创建完成：" + (System.currentTimeMillis() - currentTimeMillis));
//        currentTimeMillis = System.currentTimeMillis();
        /*这里才是读取数据的地方*/
        MongoCursor<Document> iterator = find.iterator();
        System.out.println("文档查询读取完成：" + (System.currentTimeMillis() - currentTimeMillis));
        while (iterator.hasNext()) {
            Document next = iterator.next();
            System.out.println(next.toJson());
        }
    }

    static void reader(MongoDbImpl<MongoDbBaseModel> mdi) throws Exception {
        long currentTimeMillis;
        currentTimeMillis = System.currentTimeMillis();
        MModel base = new MModel();
        List<MongoDbBaseModel> find = mdi.getList(base);
        System.out.println("文档查询读取完成：" + (System.currentTimeMillis() - currentTimeMillis));
        for (MongoDbBaseModel dataBaseModel : find) {
            System.out.println(dataBaseModel.toString());
        }
    }

    public static class MModel extends MongoDbBaseModel {

        private int id;
        private String name;
        private int age;

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

    }
}
