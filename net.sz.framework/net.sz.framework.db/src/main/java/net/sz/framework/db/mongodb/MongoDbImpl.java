package net.sz.framework.db.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sz.framework.db.Dao;
import net.sz.framework.db.mongodb.struct.MongoDbBaseModel;
import net.sz.framework.db.struct.SqlColumn;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.ConvertTypeUtil;
import net.sz.framework.utils.StringUtil;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <T>
 */
public class MongoDbImpl<T extends MongoDbBaseModel> extends Dao<T> {

    private static final SzLogger log = SzLogger.getLogger();

    private MongoClient mongoClient;

    private MongoDatabase mongoDatabase;

    public MongoDbImpl(String hostName, int port, String databaseName) {

        MongoClientOptions options = new MongoClientOptions.Builder()
                .connectionsPerHost(100)/*最大连接数*/
                .minConnectionsPerHost(20)/*最小连接数*/
                .maxWaitTime(200)/*最大等待可用连接的时间*/
                .maxConnectionIdleTime(0)/*连接的最大闲置时间*/
                .maxConnectionLifeTime(0)/*连接的最大生存时间*/
                .connectTimeout(200)/*连接超时时间*/
                .build();

        ServerAddress serverAddress = new ServerAddress(hostName, port);

        // 连接到 mongodb 服务
        mongoClient = new MongoClient(serverAddress, options);
        this.setDatabase(databaseName);
//        mongoClient = new MongoClient(new MongoClientURI("mongodb://kwiner:test123@127.0.0.1/test?authMechanism=MONGODB-CR&maxPoolSize=500"));
    }

    /**
     * 关闭驱动日志打印
     * <br>
     * driver判断了如果log4j存在就用log4j的配置，否则用Java自带的日志
     * <br>
     * 关闭系统日志 JULLogger
     */
    public static void offLog() {
        Logger log = Logger.getLogger("org.mongodb.driver");
        log.setLevel(Level.OFF);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * 获取数据库
     *
     * @param databaseName
     */
    public void setDatabase(String databaseName) {
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    @Override
    public void close() {
        this.mongoClient.close();
    }

    /**
     * 获取表
     *
     * @param objModel
     * @return
     */
    public MongoCollection<Document> getCollection(T objModel) {
        String tableName = getTableName(objModel);
        return getCollection(tableName);
    }

    /**
     * 获取表
     *
     * @param collectionName
     * @return
     */
    public MongoCollection<Document> getCollection(String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        return collection;
    }

    /**
     * 创建索引，
     *
     * @param docs
     * @param databaseName
     * @param ss 1(升序);-1(降序)：
     */
    public void setDatabase(MongoCollection<Document> docs, String databaseName, int ss) {
        docs.createIndex(new Document(databaseName, -1).append("background", true));
    }

    /**
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public Document getDocument(T obj) throws Exception {
        List<SqlColumn> columns = this.getColumns(obj.getClass());
        return getDocument(columns, obj);
    }

    /**
     *
     * @param columns
     * @param obj
     * @return
     * @throws Exception
     */
    public Document getDocument(List<SqlColumn> columns, T obj) throws Exception {
        Document d = new Document();
        getDocument(d, columns, obj);
        return d;
    }

    /**
     *
     * @param d
     * @param columns
     * @param obj
     * @throws Exception
     */
    public void getDocument(Document d, List<SqlColumn> columns, T obj) throws Exception {
        for (int i = 0; i < columns.size(); i++) {
            SqlColumn sqlColumn = columns.get(i);
            d.append(sqlColumn.getColumnName(), sqlColumn.getGetMethod().invoke(obj));
        }
    }

    /**
     * 单形式插入
     *
     * @param os
     * @return
     * @throws Exception
     */
    public int insert(T... os) throws Exception {
        if (os != null && os.length > 0) {
            for (int i = 0; i < os.length; i++) {
                T objModel = os[i];
                MongoCollection<Document> collection = this.getCollection(objModel);
                Document document = getDocument(objModel);
                collection.insertOne(document);
            }
            return os.length;
        }
        return 0;
    }

    /**
     * 批量模式插入
     *
     * @param constCount
     * @param os
     * @return
     * @throws Exception
     */
    @Override
    public int inserts(int constCount, T... os) throws Exception {
        HashMap<String, ArrayList<T>> objMap = new HashMap<>();
        for (T obj : os) {
            //获取表名
            String tableName = getTableName(obj);
            ArrayList<T> get = objMap.get(tableName);
            if (get == null) {
                get = new ArrayList<>();
                objMap.put(tableName, get);
            }
            get.add(obj);
        }

        for (Map.Entry<String, ArrayList<T>> entry : objMap.entrySet()) {
            ArrayList<T> values = entry.getValue();
            T objfirst = values.get(0);
            //得到对象的类
            Class<?> clazz = objfirst.getClass();
            //获取表名
            String tableName = getTableName(objfirst);
            //拿到表的所有要创建的字段名
            List<SqlColumn> columns = getColumns(clazz);

            MongoCollection<Document> collection = getCollection(tableName);

            List<Document> docs = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                Document document = getDocument(columns, values.get(i));
                docs.add(document);
            }
            collection.insertMany(docs);
        }
        return 0;
    }

    /**
     * 批量模式插入
     *
     * @param os
     * @return
     * @throws Exception
     */
    @Override
    public int inserts(T... os) throws Exception {
        return this.inserts(1000, os); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 批量模式插入
     *
     * @param os
     * @return
     * @throws Exception
     */
    @Override
    public int insertList(List<T> os) throws Exception {
        return this.inserts(os.toArray(TS));
    }

    /**
     * 获取一个
     *
     * @param obj
     * @return
     * @throws Exception
     */
    @Override
    public T getObject(T obj) throws Exception {
        String tableName = this.getTableName(obj);
        List<SqlColumn> columns = this.getColumns(obj.getClass());
        MongoCollection<Document> collection = getCollection(tableName);
        Document first = collection.find().first();
        T newInstance = (T) obj.getClass().newInstance();
        getDataBaseModel(newInstance, columns, first);
        return newInstance;
    }

    /**
     *
     * @param obj
     * @param sqlWhere josn 格式的 query 条件
     * @param objs
     * @return
     * @throws Exception
     */
    @Override
    public T getObjectByWhere(T obj, String sqlWhere, Object... objs) throws Exception {
        String tableName = this.getTableName(obj);
        List<SqlColumn> columns = this.getColumns(obj.getClass());
        MongoCollection<Document> collection = getCollection(tableName);
        Document first = collection.find(Document.parse(sqlWhere)).first();
        T newInstance = (T) obj.getClass().newInstance();
        getDataBaseModel(newInstance, columns, first);
        return newInstance;
    }

    @Override
    @Deprecated
    public T getObject(Connection con, T obj, Object... objs) throws Exception {
        throw new UnsupportedOperationException("getObject(Connection con, T obj, Object... objs) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public T getObject(T obj, Object... objs) throws Exception {
        throw new UnsupportedOperationException("getObject(T obj, Object... objs) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public T getObjectByWhere(Connection con, T obj, String sqlWhereString, Object... objs) throws Exception {
        throw new UnsupportedOperationException("getObjectByWhere(Connection con, T obj, String sqlWhereString, Object... objs) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public T getObjectBySql(Connection con, T obj, String sqlString, Object... objs) throws Exception {
        throw new UnsupportedOperationException("getObjectBySql(Connection con, T obj, String sqlString, Object... objs) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public T getObjectBySql(T obj, String sqlString, Object... objs) throws Exception {
        throw new UnsupportedOperationException("getObjectBySql(T obj, String sqlString, Object... objs) do not Operation by mongodb");
    }

    /**
     *
     * @param obj
     * @param columns
     * @param document
     * @throws java.lang.Exception
     */
    public void getDataBaseModel(T obj, List<SqlColumn> columns, Document document) throws Exception {
        Object idObject = document.get("_id");
        obj._setId((ObjectId) idObject);
        for (int i = 0; i < columns.size(); i++) {
            SqlColumn sqlColumn = columns.get(i);
            Object colVaule = document.get(sqlColumn.getColumnName());
            colVaule = ConvertTypeUtil.changeType(colVaule, sqlColumn.getColumnClassType());
            sqlColumn.getSetMethod().invoke(obj, colVaule);
        }
    }

    /**
     *
     * @param obj
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public List<T> getList(T obj) throws Exception {
        return getListByWhere(obj, null);
    }

    @Override
    @Deprecated
    public List<T> getList(Connection con, T obj) throws Exception {
        throw new UnsupportedOperationException("getList(Connection con, T obj) do not Operation by mongodb");
    }

    /**
     *
     * @param obj
     * @param sqlString
     * @param strs
     * @return
     * @throws Exception
     */
    @Override
    @Deprecated
    public List<T> getListBySql(T obj, String sqlString, Object... strs) throws Exception {
        throw new UnsupportedOperationException("getListBySql(T obj, String sqlString, Object... strs) do not Operation by mongodb");
    }

    /**
     *
     * @param con
     * @param obj
     * @param sqlString
     * @param objs
     * @return
     * @throws Exception
     */
    @Override
    @Deprecated
    public List<T> getListBySql(Connection con, T obj, String sqlString, Object... objs) throws Exception {
        throw new UnsupportedOperationException("getListBySql(Connection con, T obj, String sqlString, Object... objs) do not Operation by mongodb");
    }

    /**
     *
     * @param obj
     * @param whereSqlString json 格式的 query 字符串
     * @param strs
     * @return
     * @throws Exception
     */
    @Override
    public List<T> getListByWhere(T obj, String whereSqlString, Object... strs) throws Exception {
        String tableName = this.getTableName(obj);
        List<SqlColumn> columns = this.getColumns(obj.getClass());
        MongoCollection<Document> collection = getCollection(tableName);
        FindIterable<Document> find;

        if (StringUtil.isNullOrEmpty(whereSqlString)) {
            find = collection.find();
        } else {
            find = collection.find(Document.parse(whereSqlString));
        }

        List<T> rets = new ArrayList<>();
        if (find != null) {
            for (Document document : find) {
                T newInstance = (T) obj.getClass().newInstance();
                getDataBaseModel(newInstance, columns, document);
                rets.add(newInstance);
            }
        }
        return rets;
    }

    @Override
    @Deprecated
    public List<T> getListByWhere(Connection con, T obj, String whereSqlString, Object... objs) throws Exception {
        throw new UnsupportedOperationException("getListByWhere(Connection con, T obj, String whereSqlString, Object... objs) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int update(T... objs) throws Exception {
        throw new UnsupportedOperationException("update(T... objs) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int update(int constCount, T... objs) throws Exception {
        throw new UnsupportedOperationException("update(int constCount, T... objs) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int updateList(List<T> os) throws Exception {
        throw new UnsupportedOperationException("updateList(List<T> os) do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int updateList(int constCount, List<T> os) throws Exception {
        throw new UnsupportedOperationException("updateList(int constCount, List<T> os) do not Operation by mongodb");
    }

    /**
     * 只更新符合条件的第一条文档
     *
     * @param sqlWhere
     * @param obj
     * @return
     * @throws Exception
     */
    public int updateOneByWhere(String sqlWhere, T obj) throws Exception {
        String tableName = this.getTableName(obj);
        MongoCollection<Document> collection = getCollection(tableName);
        /*3.0 之后，可以用 replaceOne  new UpdateOptions().bypassDocumentValidation(true).upsert(true))*/
        UpdateResult updateOne = collection.replaceOne(Document.parse(sqlWhere), getDocument(obj));
        return (int) updateOne.getModifiedCount();
    }

    /**
     * 更新符合条件的所有的文档
     *
     * @param sqlWhere
     * @param obj
     * @return
     * @throws Exception
     */
    public int updateManyByWhere(String sqlWhere, T obj) throws Exception {
        String tableName = this.getTableName(obj);
        MongoCollection<Document> collection = getCollection(tableName);
        /*因为3.0之后 操作原因*/
        UpdateResult updateMany = collection.updateMany(Document.parse(sqlWhere), new Document("$set", getDocument(obj)));
        return (int) updateMany.getModifiedCount();
    }

    @Override
    @Deprecated
    public int delete(Connection con, T obj, Object... objs) throws Exception {
        return super.delete(con, obj, objs); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int deletes(Connection con, T... objs) throws Exception {
        return super.deletes(con, objs); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int deletes(T... objs) throws Exception {
        return super.deletes(objs); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int deleteList(List<T> objs) throws Exception {
        return super.deleteList(objs); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int delete(T obj, Object... objs) throws Exception {
        return super.delete(obj, objs); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int deleteByWhere(Connection con, T obj, String sqlWhere, Object... objs) throws Exception {
        return super.deleteByWhere(con, obj, sqlWhere, objs); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int deleteByWhere(T obj, String sqlWhere, Object... objs) throws Exception {
        return super.deleteByWhere(obj, sqlWhere, objs); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int delete(T obj) throws Exception {
        return super.delete(obj); //To change body of generated methods, choose Tools | Templates.
    }

    public int deleteOne(T obj, String sqlwhere) {
        String tableName = this.getTableName(obj);
        MongoCollection<Document> collection = getCollection(tableName);
        DeleteResult deleteOne = collection.deleteOne(Document.parse(sqlwhere));
        return (int) deleteOne.getDeletedCount();
    }

    public int deleteMany(T obj, String sqlwhere) {
        String tableName = this.getTableName(obj);
        MongoCollection<Document> collection = getCollection(tableName);
        DeleteResult deleteOne = collection.deleteMany(Document.parse(sqlwhere));
        return (int) deleteOne.getDeletedCount();
    }

    @Override
    @Deprecated
    public Connection getConnection() throws Exception {
        return null; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    protected Connection getConnection(String dbnameString) throws Exception {
        return null;
    }

    @Override
    @Deprecated
    protected String getConnectionDriverName() {
        throw new UnsupportedOperationException(this.getDbName() + " getConnectionDriverName do not Operation by mongodb");
    }

    @Override
    @Deprecated
    protected String getConnectionString(String dbnameString) {
        throw new UnsupportedOperationException(this.getDbName() + " getConnectionString do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public void createTable(Connection con, Class<?> clazz) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public void createTable(Connection con, T obj) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public void createTable(Class<?> clazz) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public void createTable(T obj) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public void createTables(List<T> objs) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    protected void createTable(Connection con, String tableName, List<SqlColumn> columns, List<List<String>> columnKeys) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createTable do not Operation by mongodb"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    protected boolean existsTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " existsTable do not Operation by mongodb"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    protected boolean existsColumn(Connection con, String tableName, String columnName) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " existsColumn do not Operation by mongodb"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Deprecated
    public int createDatabase(String database) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createDatabase do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int createDatabase() throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " createDatabase do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int update(Connection con, int constCount, T... objs) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " update do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int update(Connection con, T... objs) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " update do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int executeUpdate(Connection con, String sql, Object... objs) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " executeUpdate do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public int executeUpdate(String sql, Object... objs) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " executeUpdate do not Operation by mongodb");
    }

    @Override
    @Deprecated
    protected boolean existsColumn(String tableName, String columnName) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " existsColumn do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public boolean existsTable(Connection con, T t, boolean isCloumn) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " existsTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public boolean existsTable(T t, boolean isCloumn) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " existsTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public boolean existsTable(T t) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " existsTable do not Operation by mongodb");
    }

    @Override
    @Deprecated
    public boolean existsTable(String tableName) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " existsTable do not Operation by mongodb");
    }

    /**
     * 删除当前数据库
     *
     * @return
     * @throws Exception
     */
    public int dropDatabase() throws Exception {
        this.mongoDatabase.drop();
        return 1;
    }

    /**
     * 删除指定的数据库
     *
     * @param database
     * @return
     * @throws Exception
     */
    @Override
    public int dropDatabase(String database) throws Exception {
        this.mongoClient.dropDatabase(database);
        return 1;
    }

    @Override
    public int dropTable(String tableName) throws Exception {
        MongoCollection<Document> collection = getCollection(tableName);
        collection.drop();
        return 1;
    }

    @Override
    public int dropTable(T obj) throws Exception {
        String tableName = this.getTableName(obj);
        return dropTable(tableName);
    }

}
