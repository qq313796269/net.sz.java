package net.sz.game.engine.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import net.sz.game.engine.utils.StringUtil;
import net.sz.game.engine.utils.ZipUtil;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class Dao {

    private static final Logger log = Logger.getLogger(Dao.class);
    /**
     * 存储所有类型解析
     */
    protected static final ConcurrentHashMap<String, List<SqlColumn>> sqlColumnMap = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, String> insertSqlMap = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, String> selectSqlMap = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, String> selectWhereSqlMap = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, String> updateSqlMap = new ConcurrentHashMap<>();
    protected static final ConcurrentHashMap<String, List<SqlColumn>> updateColumnMap = new ConcurrentHashMap<>();

    public enum DriverName {
        /**
         * ，mysql 5.6 以下版本
         */
        MysqlDriver56Left("com.mysql.jdbc.Driver"),
        /**
         * ，mysql 5.6 以上版本
         */
        MysqlDriver56Right("com.mysql.cj.jdbc.Driver"),
        /**
         * ，sqlite 数据库
         */
        SqliteDriver("org.sqlite.JDBC"),;
        private String driverString;

        DriverName(String driver) {
            this.driverString = driver;
        }

        public String getDriver() {
            return driverString;
        }
    }

    protected ComboPooledDataSource bds = null;
    protected DriverName driver = null;

    /**
     * 数据库连接
     */
    protected String dbUrl;
    /**
     * 数据库名字
     */
    protected String dbName;
    /**
     * 数据库用户
     */
    protected String dbUser;
    /**
     * 数据库密码
     */
    protected String dbPwd;
    /**
     * 是否显示sql语句
     */
    protected boolean showSql;
    /**
     * 是否显示sql语句
     */
    protected boolean c3p0;

    protected boolean startend = false;

    public Dao() {
    }

    //<editor-fold defaultstate="collapsed" desc="构造函数 public Dao(String dbUrl, String dbName, String dbUser, String dbPwd, boolean showSql)">
    /**
     * 构造函数
     *
     * @param dbUrl
     * @param dbName
     * @param driverName
     * @param dbUser
     * @param dbPwd
     * @param c3p0
     * @param showSql
     * @throws java.lang.Exception
     */
    public Dao(String dbUrl, String dbName, DriverName driverName, String dbUser, String dbPwd, boolean c3p0, boolean showSql) throws Exception {
        this.dbUrl = dbUrl;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPwd = dbPwd;
        this.showSql = showSql;
        this.driver = driverName;
        this.c3p0 = c3p0;

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取数据库的连接 protected abstract Connection getConnection()">
    /**
     * 获取数据库的连接
     *
     * @return
     * @throws java.lang.Exception
     */
    public Connection getConnection() throws Exception {
        if (this.c3p0 && this.bds == null) {
            this.bds = new ComboPooledDataSource();
            this.bds.setUser(this.dbUser);
            this.bds.setPassword(this.dbPwd);
            this.bds.setJdbcUrl(getConnectionString(this.dbName));
            this.bds.setDriverClass(getConnectionDriverName());
            /*初始化连接池中的连接数*/
            this.bds.setInitialPoolSize(20);
            /*当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。默认值: 3*/
            this.bds.setAcquireIncrement(5);
            /*连接池中保留的最大连接数*/
            this.bds.setMaxPoolSize(800);
            /*连接池中保留的最小连接数*/
            this.bds.setMinPoolSize(10);
            /*最大空闲时间，60秒内未使用则连接被丢弃。若为0则永不丢弃。*/
            this.bds.setMaxIdleTime(60);
            /*配置连接的生存时间*/
            this.bds.setMaxConnectionAge(20);
            /*这个配置主要是为了减轻连接池的负载*/
            this.bds.setMaxIdleTimeExcessConnections(50);
            /*获取连接失败后该数据源将申明已断开并永久关闭*/
            this.bds.setBreakAfterAcquireFailure(false);
            /*当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出SQLException,如设为0则无限期等待。单位毫秒。*/
            this.bds.setCheckoutTimeout(10 * 1000);
            /*每60秒检查所有连接池中的空闲连接*/
            this.bds.setIdleConnectionTestPeriod(60);
            /*缓慢的JDBC操作通过帮助进程完成。扩展这些操作可以有效的提升性能通过多线程实现多个操作同时被执行*/
            this.bds.setNumHelperThreads(3);
            /*连接关闭时默认将所有未提交的操作回滚*/
            this.bds.setAutoCommitOnClose(false);
            /*防止连接丢失*/
            this.bds.setTestConnectionOnCheckin(true);
            this.bds.setTestConnectionOnCheckout(true);
        }
        if (c3p0 && this.bds != null) {
            return this.bds.getConnection();
        }
        throw new UnsupportedOperationException("连接池尚未初始化");
    }
    //</editor-fold>

    /**
     * 检查是否可以处理的类型，创建表的时候<br>
     * 枚举，接口，匿名类，注解类，静态类，抽象类，忽略
     *
     * @param oClass
     * @return
     */
    public boolean checkClazz(Class<?> oClass) {
        if (oClass.isEnum()/*枚举类型*/
                || Modifier.isStrict(oClass.getModifiers())/*静态类*/
                || Modifier.isAbstract(oClass.getModifiers())/*抽象类*/
                || oClass.isInterface()/*这个是接口*/
                || oClass.isAnonymousClass()/*这个是匿名类*/
                || oClass.isAnnotation()/*这个是注解类*/
                || oClass.getAnnotation(Transient.class) != null/*他是一个忽略的*/) {
            return false;
        }
        return true;
    }

    /**
     * 指定远程连接地址，动态指定数据库名字 ps 用于创建数据库
     *
     * @param dbnameString
     * @return
     * @throws Exception
     */
    protected abstract Connection getConnection(String dbnameString) throws Exception;

    protected String getConnectionDriverName() {
        return this.driver.driverString;
    }

    /**
     *
     * @param dbnameString
     * @return
     */
    protected abstract String getConnectionString(String dbnameString);

    /**
     * 关闭数据库连接
     *
     * @throws java.lang.Exception
     */
    public abstract void close() throws Exception;

    //<editor-fold defaultstate="collapsed" desc="获取表名 protected String getTableName(Object o)">
    /**
     * 获取表名
     *
     * @param oClass
     * @return
     */
    public String getTableName(Class<?> oClass) {
        if (!checkClazz(oClass)) {
            throw new UnsupportedOperationException("无法处理的类型，请先调用 checkClazz() 方法检查类型");
        }
        //判断指定类型的注释是否存在于此元素上
        Table table = oClass.getAnnotation(Table.class);//拿到对应的表格注解类型
//        boolean isHaveTable = oClass.isAnnotationPresent(Table.class);
        if (table == null || StringUtil.isNullOrEmpty(table.name())) {
            return oClass.getSimpleName();//不存在就不需要获取其表名
        } else {
            return table.name();//返回注解中的值，也就是表名
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="设置字段值，插入数据库，支持sql注入攻击  protected void setStmtParams(PreparedStatement stmt, SqlColumn sqlColumn, Integer nums, Object value)">
    /**
     * 设置字段值，插入数据库，支持sql注入攻击
     *
     * @param stmt
     * @param sqlColumn
     * @param nums
     * @param value
     * @throws java.lang.Exception
     */
    protected void setStmtParams(PreparedStatement stmt, SqlColumn sqlColumn, Integer nums, Object value) throws Exception {
        switch (sqlColumn.getClassType().getName().toLowerCase()) {
            case "int":
            case "java.lang.integer":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setInt(nums, (Integer) value);
                }

                break;
            case "string":
            case "java.lang.string":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = "";
                    }
                }
                stmt.setString(nums, (String) value);
                break;
            case "double":
            case "java.lang.double":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0.0;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setDouble(nums, (Double) value);
                }
                break;
            case "float":
            case "java.lang.float":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0.0;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setFloat(nums, (Float) value);
                }
                break;
            case "long":
            case "java.lang.long":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0.0;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setLong(nums, (Long) value);
                }
                break;
            case "bigdecimal":
            case "java.math.bigdecimal":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = BigDecimal.valueOf(0);
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setBigDecimal(nums, (BigDecimal) value);
                }
                break;
            case "biginteger":
            case "java.math.biginteger":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0l;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setLong(nums, (Long) value);
                }
                break;
            case "byte":
            case "java.lang.byte":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0.0;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setByte(nums, (Byte) value);
                }
                break;
            case "short":
            case "java.lang.short":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0.0;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setShort(nums, (Short) value);
                }
                break;
            case "boolean":
            case "java.lang.boolean":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = false;
                    }
                }
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setBoolean(nums, (Boolean) value);
                }
                break;
            case "date":
            case "java.lang.date":
                if (value == null) {
                    if (!sqlColumn.isColumnNullAble()) {
                        value = 0.0;
                    }
                }
                stmt.setDate(nums, (Date) value);
                break;
            default: {
                if (value == null) {
                    stmt.setObject(nums, null);
                } else {
                    stmt.setBytes(nums, ZipUtil.zipObject(value));
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="设置字段值，插入数据库，支持sql注入攻击  protected void setStmtParams(PreparedStatement stmt, SqlColumn sqlColumn, Integer nums, Object value)">
    /**
     * 设置字段值，插入数据库，支持sql注入攻击
     *
     * @param stmt
     * @param nums
     * @param value
     * @throws java.lang.Exception
     */
    protected void setStmtParams(PreparedStatement stmt, Integer nums, Object value) throws Exception {
        if (value == null) {
            stmt.setObject(nums, null);
            return;
        }
        switch (value.getClass().getName().toLowerCase()) {
            case "int":
            case "java.lang.integer":
                stmt.setInt(nums, (Integer) value);
                break;
            case "string":
            case "java.lang.string":
                stmt.setString(nums, (String) value);
                break;
            case "double":
            case "java.lang.double":
                stmt.setDouble(nums, (Double) value);
                break;
            case "float":
            case "java.lang.float":
                stmt.setFloat(nums, (Float) value);
                break;
            case "long":
            case "java.lang.long":
                stmt.setLong(nums, (Long) value);
                break;
            case "bigdecimal":
            case "java.math.bigdecimal":
                stmt.setBigDecimal(nums, (BigDecimal) value);
                break;
            case "biginteger":
            case "java.math.biginteger":
                stmt.setLong(nums, (Long) value);
                break;
            case "byte":
            case "java.lang.byte":
                stmt.setByte(nums, (Byte) value);
                break;
            case "boolean":
            case "java.lang.boolean":
                stmt.setBoolean(nums, (Boolean) value);
                break;
            case "short":
            case "java.lang.short":
                stmt.setShort(nums, (Short) value);
                break;
            case "date":
            case "java.lang.date":
                stmt.setDate(nums, (Date) value);
                break;
            default: {
                stmt.setBytes(nums, ZipUtil.zipObject(value));
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="反射获取字段信息 过滤 transient 字段 protected Map<String, SqlColumn> getColumns(Object o)">
    /**
     * 反射获取字段信息 过滤 transient 字段
     *
     * @param clazz
     * @return
     */
    protected List<SqlColumn> getColumns(Class<?> clazz) {
        if (!checkClazz(clazz)) {
            throw new UnsupportedOperationException("无法处理的类型，请先调用 checkClazz() 方法检查类型");
        }
        List<SqlColumn> columns = sqlColumnMap.get(clazz.getName());
        if (columns != null) {
            return columns;
        }

        columns = new ArrayList<>();

        Boolean ispakey = getColumns(columns, clazz);

        if (!ispakey) {
            throw new UnsupportedOperationException("实体类不允许没有主键字段：" + clazz.getName());
        }

        if (columns.isEmpty()) {
            throw new UnsupportedOperationException("实体模型未有任何字段：" + clazz.getName());
        }

        sqlColumnMap.put(clazz.getName(), columns);

        return columns;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取属性，递归查找包括父类 protected boolean getColumns(List<SqlColumn> columns, Class<?> clazz)">
    /**
     * 获取属性，递归查找包括父类
     *
     * @param columns
     * @param clazz
     * @return
     */
    protected boolean getColumns(List<SqlColumn> columns, Class<?> clazz) {
        if (clazz == null || !checkClazz(clazz)) {
            return false;
        }
        boolean ispakey = false;
        /* 获取对象中所有的属性 */
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getMethods();
        //遍历所有属性
        for (Field field : fields) {
            //忽略字段，静态字段，最终字段，不会书写到数据库
            if (Modifier.isTransient(field.getModifiers())
                    || Modifier.isStatic(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())
                    || field.getAnnotation(Transient.class) != null) {
                log.error("类：" + clazz.getName() + " 字段：" + field.getName() + " is transient or static or final;");
                continue;
            }

            SqlColumn sqlColumn = new SqlColumn();
            sqlColumn.setColumnName(field.getName());
            sqlColumn.setFieldName(field.getName());
            field.setAccessible(true);
            sqlColumn.setField(field);

            for (Method method : methods) {
                String methodName = method.getName();//获取每一个方法名
                if (methodName.equalsIgnoreCase("get" + sqlColumn.getFieldName()) || methodName.equalsIgnoreCase("is" + sqlColumn.getFieldName())) {
                    method.setAccessible(true);
                    sqlColumn.setGetMethod(method);
                } else if (methodName.equalsIgnoreCase("set" + sqlColumn.getFieldName())) {
                    method.setAccessible(true);
                    sqlColumn.setSetMethod(method);
                }
            }

            if (sqlColumn.getGetMethod() == null || sqlColumn.getSetMethod() == null) {
                throw new UnsupportedOperationException("类：" + clazz.getName() + " 字段：" + field.getName() + " 没有 set or get Method");
            }
            //如果属性上有对应的列注解类型则获取这个注解类型
            Column column = field.getAnnotation(Column.class);

            if (column != null) {
                if (column.name() != null && !column.name().trim().isEmpty()) {
                    sqlColumn.setColumnName(column.name().trim());
                }

                if (column.length() > 0) {
                    sqlColumn.setColunmLength(column.length());
                }

                sqlColumn.setColumnNullAble(column.nullable());

                if (column.columnDefinition() != null) {
                    sqlColumn.setColumnDefinition(column.columnDefinition());
                }
            }

            //拿到对应属性的类型，然后根据对应的类型去声明字段类型
            Class<?> type = field.getType();

            sqlColumn.setClassType(type);

            String columnvalue = null;
            String toLowerCase = type.getName().toLowerCase();
            Lob lob = field.getAnnotation(Lob.class);
            if (lob != null) {
                toLowerCase = lob.getClass().getName().toLowerCase();
            }
            switch (toLowerCase) {
                case "int":
                case "java.lang.integer":
                    columnvalue = "int(4)";
                    break;
                case "string":
                case "java.lang.string":
                    if (sqlColumn.getColunmLength() < 1000) {
                        columnvalue = "varchar(" + sqlColumn.getColunmLength() + ")";
                    } else if (sqlColumn.getColunmLength() < 10000) {
                        columnvalue = "text";
                    } else {
                        columnvalue = "longtext";
                    }
                    break;
                case "double":
                case "java.lang.double":
                    columnvalue = "double";
                    break;
                case "float":
                case "java.lang.float":
                    columnvalue = "float";
                    break;
                case "byte":
                case "java.lang.byte":
                    columnvalue = "tinyint(2)";
                    break;
                case "boolean":
                case "java.lang.boolean":
                    columnvalue = "tinyint(1)";
                    break;
                case "long":
                case "java.lang.long":
                case "biginteger":
                case "java.math.biginteger":
                    columnvalue = "bigint";
                    break;
                case "bigdecimal":
                case "java.math.bigdecimal":
                    columnvalue = "decimal";
                    break;
                case "short":
                case "java.lang.short":
                    columnvalue = "tinyint(2)";
                    break;
                default:
                    columnvalue = "longblob";
                    break;
            }
            if (columnvalue != null) {
                //如果属性上有对应的主键ID注解类型则获取这个注解类型
                Id tpid = field.getAnnotation(Id.class);
                if (tpid != null) {
                    ispakey = true;
                    sqlColumn.setColumnkey(true);
                    sqlColumn.setColumnNullAble(false);
                }

                GeneratedValue annotation = field.getAnnotation(GeneratedValue.class);
                //判断主键是否为自动增长
                if (annotation != null) {
                    sqlColumn.setColumnAuto(true);
                }

                if (sqlColumn.isColumnNullAble()) {
                    columnvalue += " null";
                } else {
                    columnvalue += " not null";
                }

                if (sqlColumn.isColumnkey()) {
                    if (sqlColumn.isColumnAuto()) {
                        columnvalue += " auto_increment";
                    }
                    columnvalue += " primary key";
                }

                sqlColumn.setValue(columnvalue);

                columns.add(sqlColumn);
            } else {
                if (showSql) {
                    log.error("类：" + clazz.getName() + " 字段：" + field.getName() + "类：" + clazz.getName() + " 无法识别的字段：" + field.getName() + " ;");
                }
            }
        }
        if (getColumns(columns, clazz.getSuperclass())) {
            ispakey = true;
        }
        return ispakey;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="创建表 public void createTable(List<Object> objs)">
    /**
     * 创建表
     *
     * @param clazzs 所有需要创建表的实体对象
     * @throws java.lang.Exception
     */
    public void createTable(List<Class<?>> clazzs) throws Exception {
        //遍历所有要创建表的对象
        for (Class<?> clazz : clazzs) {
            createTable(clazz);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="创建表 public void createTable(Object obj)">
    /**
     * 创建表
     *
     * @param clazz
     * @throws java.lang.Exception
     */
    public void createTable(Class<?> clazz) throws Exception {
        if (clazz.isInterface() || clazz.isEnum()
                || Modifier.isAbstract(clazz.getModifiers())
                || Modifier.isStatic(clazz.getModifiers())
                || Modifier.isTransient(clazz.getModifiers())) {
            log.error(clazz.getName() + " 类无法识别实体模型", new RuntimeException());
            return;
        }
        String talbeName = getTableName(clazz);
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);
        try (Connection con = getConnection()) {
            createTable(con, talbeName, columns);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="创建表 public void createTable(Object obj)">
    /**
     * 创建表
     *
     * @param con
     * @param clazz
     * @throws java.lang.Exception
     */
    public void createTable(Connection con, Class<?> clazz) throws Exception {
        if (clazz.isInterface() || clazz.isEnum()
                || Modifier.isAbstract(clazz.getModifiers())
                || Modifier.isStatic(clazz.getModifiers())
                || Modifier.isTransient(clazz.getModifiers())) {
            log.error(clazz.getName() + " 类无法识别实体模型", new RuntimeException());
            return;
        }
        String tableName = getTableName(clazz);
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);
        createTable(con, tableName, columns);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="创建表 protected abstract void createTable(Object o, String tableName, List<SqlColumn> columns)">
    /**
     * 创建表
     *
     * @param con
     * @param tableName
     * @param columns
     * @throws java.lang.Exception
     */
    protected abstract void createTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="检查表是否存在 public abstract boolean existsTable(String tableName)">
    /**
     * 检查表是否存在
     *
     * @param tableName
     * @return
     * @throws java.lang.Exception
     */
    public boolean existsTable(String tableName) throws Exception {
        try (Connection con = getConnection()) {
            return existsTable(con, tableName, null);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="检查表是否存在 public abstract boolean existsTable(String tableName)">
    /**
     * 检查表是否存在
     *
     * @param clazz
     * @return
     * @throws java.lang.Exception
     */
    public boolean existsTable(Class<?> clazz) throws Exception {
        return existsTable(clazz, false);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="检查表是否存在 public abstract boolean existsTable(String tableName)">
    /**
     * 检查表是否存在
     *
     * @param clazz
     * @param isCloumn
     * @return
     * @throws java.lang.Exception
     */
    public boolean existsTable(Class<?> clazz, boolean isCloumn) throws Exception {
        try (Connection con = getConnection()) {
            return existsTable(con, clazz, isCloumn);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="检查表是否存在 public abstract boolean existsTable(String tableName)">
    /**
     * 检查表是否存在
     *
     * @param con
     * @param clazz
     * @param isCloumn
     * @return
     * @throws java.lang.Exception
     */
    public boolean existsTable(Connection con, Class<?> clazz, boolean isCloumn) throws Exception {
        String tableName = getTableName(clazz);
        List<SqlColumn> columns = getColumns(clazz);
        return existsTable(con, tableName, columns);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="检查表是否存在 public abstract boolean existsTable(String tableName)">
    /**
     * 检查表是否存在
     *
     * @param con
     * @param tableName
     * @param columns
     * @return
     * @throws java.lang.Exception
     */
    protected abstract boolean existsTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="检查表是否存在 public abstract boolean existsTable(String tableName)">
    /**
     * 检查表是否存在
     *
     * @param tableName
     * @param columnName
     * @return
     * @throws java.lang.Exception
     */
    protected boolean existsColumn(String tableName, String columnName) throws Exception {
        try (Connection con = getConnection()) {
            return existsColumn(con, tableName, columnName);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="检查表是否存在 public abstract boolean existsColumn(String tableName)">
    /**
     * 检查表是否存在
     *
     * @param con
     * @param tableName
     * @param columnName
     * @return
     * @throws java.lang.Exception
     */
    protected abstract boolean existsColumn(Connection con, String tableName, String columnName) throws Exception;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="插入对象  public int insertList(List<Object> os)">
    /**
     * 插入对象 默认以100的形式批量插入
     *
     * @param os
     * @return
     * @throws java.lang.Exception
     */
    public int insertList(List<Object> os) throws Exception {
        return insert(100, os.toArray());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="插入对象  public int insertList(int constCount, List<Object> os)">
    /**
     * 插入对象
     *
     * @param os
     * @param constCount 批量插入的量
     * @return
     * @throws java.lang.Exception
     */
    public int insertList(int constCount, List<Object> os) throws Exception {
        return insert(constCount, os.toArray());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="插入对象到数据库 public boolean insert(Object... os)">
    /**
     * 插入对象到数据库，默认以100的形式批量插入
     *
     * @param os os 必须是对同一个对象
     * @return
     * @throws java.lang.Exception
     */
    public int insert(Object... os) throws Exception {
        return insert(100, os);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="插入对象到数据库 public boolean insert(Object... os)">
    /**
     * 插入对象到数据库
     *
     * @param os os 必须是对同一个对象
     * @param constCount 批量插入的量
     * @return
     * @throws java.lang.Exception
     */
    public int insert(int constCount, Object... os) throws Exception {
        int insert = 0;
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            insert = insert(con, constCount, os);
            con.commit();
        } catch (Exception ex) {
            if (con != null) {
                con.rollback();
            }
            throw ex;
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return insert;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="写入数据 public boolean insert(Connection con, Object... os)">
    /**
     * 写入数据,默认以100的形式批量插入
     *
     * @param con
     * @param os
     * @return
     * @throws Exception
     */
    public int insert(Connection con, Object... os) throws Exception {
        return insert(con, 100, os);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="写入数据 public int insert(Connection con, int constCount, Object... os)">
    /**
     * 写入数据
     *
     * @param con
     * @param constCount 批量插入的量
     * @param os
     * @return
     * @throws Exception
     */
    public int insert(Connection con, int constCount, Object... os) throws Exception {

        int execute = 0;

        if (os == null || os.length == 0) {
            return execute;
        }

        HashMap<String, ArrayList<Object>> objMap = new HashMap<>();

        for (Object o : os) {
            //得到对象的类
            Class<?> clazz = o.getClass();
            //获取表名
            String tableName = getTableName(clazz);
            ArrayList<Object> get = objMap.get(tableName);
            if (get == null) {
                get = new ArrayList<>();
                objMap.put(tableName, get);
            }
            get.add(o);
        }

        for (Map.Entry<String, ArrayList<Object>> entry : objMap.entrySet()) {
            ArrayList<Object> values = entry.getValue();
            Object objfirst = values.get(0);
            //得到对象的类
            Class<?> clazz = objfirst.getClass();
            //获取表名
            String tableName = getTableName(clazz);
            //拿到表的所有要创建的字段名
            List<SqlColumn> columns = getColumns(clazz);

            if (!existsTable(con, tableName, null)) {
                createTable(con, tableName, columns);
            }

            String inserts = insertSqlMap.get(tableName);

            if (inserts == null) {
                /* 缓存的sql语句 */
                StringBuilder builder = new StringBuilder();
                builder.append("insert into `").append(tableName).append("` (");
                int columnCount = 0;
                //将所有的字段拼接成对应的SQL语句
                for (SqlColumn column : columns) {
                    if (column.isColumnAuto()) {
                        continue;
                    }
                    if (columnCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("`").append(column.getColumnName()).append("`");
                    columnCount++;
                }
                builder.append(") values ");
                builder.append("(");
                for (int j = 0; j < columnCount; j++) {
                    builder.append("?");
                    if (j < columnCount - 1) {
                        builder.append(",");
                    }
                    builder.append(" ");
                }
                builder.append(")");
                inserts = builder.toString();
                insertSqlMap.put(tableName, inserts);
            }

            int forcount = values.size() / constCount + (values.size() % constCount > 0 ? 1 : 0);
            try (PreparedStatement prepareCall = con.prepareStatement(inserts)) {
                if (showSql) {
                    log.error("执行 " + inserts + " 添加数据 表：" + tableName);
                }
                int tmpExecCount = 0;
                for (int k = 0; k < forcount; k++) {
                    int count1 = 0;
                    int forcount1 = 0;
                    count1 = k * constCount;
                    forcount1 = constCount;
                    if (k == 0) {
                        if (constCount > values.size() && count1 < values.size()) {
                            forcount1 = values.size();
                        }
                    } else if (count1 + constCount >= values.size()) {
                        forcount1 = values.size() - count1;
                    }

                    for (int i = 0; i < forcount1; i++) {
                        int j = 1;
                        Object obj = values.get(count1 + i);
                        for (int l = 0; l < columns.size(); l++) {
                            SqlColumn column = columns.get(l);
                            if (column.isColumnAuto()) {
                                continue;
                            }
                            Object invoke = column.getGetMethod().invoke(obj);
                            setStmtParams(prepareCall, column, j, invoke);
                            j++;
                        }
                        prepareCall.addBatch();
                    }

                    int[] executeBatch = prepareCall.executeBatch();

                    if (executeBatch != null && executeBatch.length > 0) {
                        for (int i = 0; i < executeBatch.length; i++) {
                            if (PreparedStatement.SUCCESS_NO_INFO == executeBatch[i] || PreparedStatement.CLOSE_CURRENT_RESULT == executeBatch[i]) {
                                tmpExecCount++;
                            }
                        }
                    }
                    prepareCall.clearParameters();
                    prepareCall.clearBatch();
                }
                if (showSql) {
                    log.error("执行 " + prepareCall.toString() + " 添加数据 表：" + tableName + " 结果 影响行数：" + tmpExecCount);
                }
                execute += tmpExecCount;
            } catch (Exception ex) {
                log.error("执行sql语句错误：" + inserts);
                throw ex;
            }
        }
        return execute;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取所有集合对象 public <T> List<T> getList(Class<T> clazz)">
    /**
     * 获取所有集合对象
     *
     * @param <T>
     * @param clazz
     * @return
     * @throws java.lang.Exception
     */
    public <T> List<T> getList(Class<T> clazz) throws Exception {
        return getListByWhere(clazz, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取所有集合对象 public <T> List<T> getList(Class<T> clazz)">
    /**
     * 获取所有集合对象
     *
     * @param <T>
     * @param con
     * @param clazz
     * @return
     * @throws java.lang.Exception
     */
    public <T> List<T> getList(Connection con, Class<T> clazz) throws Exception {
        return getListByWhere(con, clazz, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回结果集 public <T> List<T> getListByWhere(Class<T> clazz, String whereSqlString, Object... strs)">
    /**
     * 返回结果集
     *
     * @param <T>
     * @param clazz
     * @param whereSqlString 请加入where 例如：where a=? and b=? 或者 a=? or a=?
     * 这样才能防止sql注入攻击
     * @param strs
     * @return
     * @throws java.lang.Exception
     */
    public <T> List<T> getListByWhere(Class<T> clazz, String whereSqlString, Object... strs) throws Exception {
        try (Connection con = getConnection()) {
            return getListByWhere(con, clazz, whereSqlString, strs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回结果集 public <T> List<T> getListByWhere(Connection con, Class<T> clazz, String whereSqlString, Object... strs)">
    /**
     * 返回结果集
     *
     * @param <T>
     * @param con
     * @param clazz
     * @param whereSqlString 请加入where 例如：where a=? and b=? 或者 a=? or a=?
     * 这样才能防止sql注入攻击
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> List<T> getListByWhere(Connection con, Class<T> clazz, String whereSqlString, Object... objs) throws Exception {
        //获取表名
        String tableName = getTableName(clazz);
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);
        String selectSql = getSelectSql(tableName, columns, whereSqlString);
        return getListBySql(con, clazz, selectSql, objs);
    }
    //</editor-fold>

    public <T> int getCount(Class<T> clazz) throws Exception {
        /*获取表名*/
        String tableName = getTableName(clazz);
        String sqlString = "select count(1) usm from `" + tableName + "`";
        return this.getResult(sqlString, "usm", int.class);
    }

    /**
     *
     * @param <T>
     * @param rs
     * @param tableName
     * @param columns
     * @param clazz
     * @return
     * @throws Exception
     */
    protected <T> T getObjectT(ResultSet rs, String tableName, List<SqlColumn> columns, Class<T> clazz) throws Exception {
        /* 生成一个实例 */
        T obj = clazz.newInstance();
        ResultSetMetaData rsmd = rs.getMetaData();

        int count = rsmd.getColumnCount();
        HashSet<String> metaSet = new HashSet<>();

        for (int i = 1; i <= count; i++) {  //第一列,从1开始.所以获取列名,或列值,都是从1开始
            metaSet.add(rsmd.getColumnName(i)); //获得列值的方式一:通过其序号
        }

        for (SqlColumn column : columns) {
            if (metaSet.contains(column.getColumnName())) {
                Object valueObject = null;
                valueObject = rs.getObject(column.getColumnName());
                if (valueObject != null) {
                    Object object = getResultValue(valueObject, tableName, column.getColumnName(), column.getClassType());
                    if (object != null) {
                        column.getSetMethod().invoke(obj, object);
//                        column.getField().set(obj, object);
                    }
                }
            }
        }
        return obj;
    }

    //<editor-fold defaultstate="collapsed" desc="根据传入的sql语句获取对象 public <T> List<T> getListBySql(Class<T> clazz, String sqlString, Object... strs)">
    /**
     * 根据传入的sql语句获取对象
     *
     * @param <T>
     * @param clazz
     * @param sqlString
     * @param strs
     * @return
     * @throws Exception
     */
    public <T> List<T> getListBySql(Class<T> clazz, String sqlString, Object... strs) throws Exception {
        try (Connection con = getConnection()) {
            return getListBySql(con, clazz, sqlString, strs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取对象，根据传入的sql语句 public <T> List<T> getListBySql(Connection con, Class<T> clazz, String sqlString, Object... strs)">
    /**
     * 获取对象，根据传入的sql语句
     *
     * @param <T>
     * @param con
     * @param clazz
     * @param sqlString
     * @param objs 参数列表，防止sql注入攻击
     * @return
     * @throws Exception
     */
    public <T> List<T> getListBySql(Connection con, Class<T> clazz, String sqlString, Object... objs) throws Exception {
        //获取表名
        String tableName = getTableName(clazz);
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);

        List<T> ts = new ArrayList<>();
        try (PreparedStatement prepareStatement = con.prepareStatement(sqlString)) {
            if (objs != null && objs.length > 0) {
                for (int j = 0; j < objs.length; j++) {
                    setStmtParams(prepareStatement, j + 1, objs[j]);
                }
            }
            if (showSql) {
                log.error("\n" + prepareStatement.toString());
            }
            ResultSet rs = prepareStatement.executeQuery();
            while (rs.next()) {
                ts.add(getObjectT(rs, tableName, columns, clazz));
            }
        }
        return ts;
    }
    //</editor-fold>

    protected String getSelectSql(String tableName, List<SqlColumn> columns, String sqlWhere) throws Exception {
        //这里如果不存在字段名就不需要创建了
        if (columns == null || columns.isEmpty()) {
            throw new UnsupportedOperationException("实体类没有任何字段，");
        }
        StringBuilder builder = new StringBuilder();
        String sqlString = selectSqlMap.get(tableName);
        if (sqlString == null) {
            builder.append("SELECT ");
            int i = 0;
            for (SqlColumn value : columns) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append("`").append(value.getColumnName()).append("`");
                i++;
            }
            builder.append(" FROM `").append(tableName).append("` ");
            sqlString = builder.toString();
            selectSqlMap.put(tableName, sqlString);
        } else {
            builder.append(sqlString);
        }
        if (sqlWhere != null && sqlWhere.length() > 0) {
            builder.append(" ").append(sqlWhere);
        }
        return builder.toString();
    }

    //<editor-fold defaultstate="collapsed" desc="返回查询结果集 public List<Map<String, Object>> getResultSet(Connection con, Class<?> clazz, String whereSqlString, Object... strs)">
    /**
     * 返回查询结果集
     *
     * @param con
     * @param clazz
     * @param whereSqlString 例如： a=? and b=? 或者 a=? or a=? 这样才能防止sql注入攻击
     * @param strs
     * @return
     * @throws java.lang.Exception
     */
    public List<Map<String, Object>> getResultSet(Connection con, Class<?> clazz, String whereSqlString, Object... strs) throws Exception {

        if (clazz == null) {
            throw new UnsupportedOperationException("obj or clzz 为 null，");
        }

        //获取表名
        String tableName = getTableName(clazz);
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);

        List<Map<String, Object>> resultSet = getResultSet(con, tableName, columns, whereSqlString, strs);

        return resultSet;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回查询结果集 protected List<Map<String, Object>> getResultSet(Connection con, String tableName, List<SqlColumn> columns, String sqlWhere, Object... objs)">
    /**
     * 返回查询结果集
     *
     * @param con
     * @param tableName
     * @param columns
     * @param sqlWhere 请加入where 范例 a=? and b=? 或者 a=? or a=?
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    protected List<Map<String, Object>> getResultSet(Connection con, String tableName, List<SqlColumn> columns, String sqlWhere, Object... objs) throws Exception {
        //这里如果不存在字段名就不需要创建了
        if (columns == null || columns.isEmpty()) {
            throw new UnsupportedOperationException("实体类没有任何字段，");
        }
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        int i = 0;
        for (SqlColumn value : columns) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append("`").append(value.getColumnName()).append("`");
            i++;
        }
        builder.append(" FROM `").append(tableName).append("` ");
        if (sqlWhere != null && sqlWhere.length() > 0) {
            builder.append(" ").append(sqlWhere);
        }
        String sqlString = builder.toString();
        return getResultSet(con, sqlString, objs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回查询结果集 public List<Map<String, Object>> getResultSet(String sqlString, Object... objs)">
    /**
     * 返回查询结果集
     *
     * @param sqlString 范例 a=? and b=? 或者 a=? or a=?
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public List<Map<String, Object>> getResultSet(String sqlString, Object... objs) throws Exception {
        try (Connection con = getConnection()) {
            return getResultSet(con, sqlString, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回查询结果集 public List<Map<String, Object>> getResultSet(Connection con, String sqlString, Object... objs)">
    /**
     * 返回查询结果集
     *
     * @param con
     * @param sqlString 范例 a=? and b=? 或者 a=? or a=?
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public List<Map<String, Object>> getResultSet(Connection con, String sqlString, Object... objs) throws Exception {
        List<Map<String, Object>> res = new ArrayList<>();
        try (PreparedStatement prepareStatement = con.prepareStatement(sqlString)) {
            if (objs != null && objs.length > 0) {
                for (int j = 0; j < objs.length; j++) {
                    setStmtParams(prepareStatement, j + 1, objs[j]);
                }
            }
            if (showSql) {
                log.error("\n" + prepareStatement.toString());
            }
            ResultSet rs = prepareStatement.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                int columnCount = rs.getMetaData().getColumnCount();
                for (int j = 1; j < columnCount + 1; j++) {
                    Object object = rs.getObject(j);
                    String columnName = rs.getMetaData().getColumnLabel(j);
                    map.put(columnName, object);
                }
                res.add(map);
            }
            if (showSql) {
                log.error("执行影响行数：" + res.size());
            }
        } catch (Exception ex) {
            log.error("执行sql语句错误：" + sqlString);
            throw ex;
        }

        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回查询结果集 public <T> T getResult(String sqlString, String valueName, Class<T> clazz, Object... objs)">
    /**
     * 返回查询结果集
     *
     * @param <T>
     * @param sqlString 完整的sql语句
     * @param valueName 需要获取的字段的名字
     * @param clazz 获取后的类型
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> T getResult(String sqlString, String valueName, Class<T> clazz, Object... objs) throws Exception {
        try (Connection con = getConnection()) {
            return getResult(con, sqlString, valueName, clazz, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回查询结果集 public <T> T getResult(Connection con, String sqlString, String valueName, Class<T> clazz, Object... objs)">
    /**
     * 返回查询结果集
     *
     * @param <T>
     * @param con
     * @param sqlString 完整的sql语句
     * @param valueName 需要获取的字段的名字
     * @param clazz 获取后的类型
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> T getResult(Connection con, String sqlString, String valueName, Class<T> clazz, Object... objs) throws Exception {
        T object = null;
        try (PreparedStatement prepareStatement = con.prepareStatement(sqlString)) {
            if (objs != null && objs.length > 0) {
                for (int j = 0; j < objs.length; j++) {
                    setStmtParams(prepareStatement, j + 1, objs[j]);
                }
            }
            if (showSql) {
                log.error("\n" + prepareStatement.toString());
            }
            ResultSet executeQuery = prepareStatement.executeQuery();
            if (executeQuery.next()) {
                Object object1 = executeQuery.getObject(valueName);
                object = (T) getResultValue(object1, "", "", clazz);
            }
        } catch (Exception ex) {
            log.error("执行sql语句错误：" + sqlString);
            throw ex;
        }
        return object;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回查询结果集 public <T> List<T> getResults(String sqlString, String valueName, Class<T> clazz, Object... objs)">
    /**
     * 返回查询结果集
     *
     * @param <T>
     * @param sqlString 范例 a=? and b=? 或者 a=? or a=?
     * @param valueName 需要获取的字段的名字
     * @param clazz 获取后的类型
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> List<T> getResults(String sqlString, String valueName, Class<T> clazz, Object... objs) throws Exception {
        List<T> objects = new ArrayList<>();
        try (Connection con = getConnection()) {
            try (PreparedStatement prepareStatement = con.prepareStatement(sqlString)) {
                if (objs != null && objs.length > 0) {
                    for (int j = 0; j < objs.length; j++) {
                        setStmtParams(prepareStatement, j + 1, objs[j]);
                    }
                }
                if (showSql) {
                    log.error("\n" + prepareStatement.toString());
                }
                ResultSet executeQuery = prepareStatement.executeQuery();
                while (executeQuery.next()) {
                    objects.add((T) executeQuery.getObject(valueName));
                }
            } catch (Exception ex) {
                log.error("执行sql语句错误：" + sqlString);
                throw ex;
            }
        }
        if (showSql) {
            log.error("执行影响行数：" + objects.size());
        }
        return objects;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取查询出来的第一个对象 public <T> T getObject(Class<T> clazz)">
    /**
     * 获取查询出来的第一个对象
     *
     * @param <T>
     * @param clazz
     * @return
     * @throws java.lang.Exception
     */
    public <T> T getObject(Class<T> clazz) throws Exception {
        return getObjectByWhere(clazz, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回结果对象 public <T> T getObjectByWhere(Class<T> clazz, String sqlWhere, Object... objs)">
    /**
     * 返回结果对象
     *
     * @param <T>
     * @param clazz
     * @param sqlWhere
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> T getObjectByWhere(Class<T> clazz, String sqlWhere, Object... objs) throws Exception {
        if (clazz == null) {
            throw new UnsupportedOperationException("obj or clzz 为 null，");
        }
        try (Connection con = getConnection()) {
            return getObjectByWhere(con, clazz, sqlWhere, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="如果结果是多条，只返回第一条结果 public <T> T getObjectBySql(Class<T> clazz, String sqlString, Object... objs)">
    /**
     * 如果结果是多条，只返回第一条结果
     *
     * @param <T>
     * @param clazz
     * @param sqlString
     * @param objs sql语句的参数 为了防止sql注入攻击
     * @return
     * @throws Exception
     */
    public <T> T getObjectBySql(Class<T> clazz, String sqlString, Object... objs) throws Exception {
        try (Connection con = getConnection()) {
            return getObjectBySql(con, clazz, sqlString, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="如果结果是多条，只返回第一条结果 public <T> T getObjectBySql(Connection con, Class<T> clazz, String sqlString, Object... objs)">
    /**
     * 如果结果是多条，只返回第一条结果
     *
     * @param <T>
     * @param con
     * @param clazz
     * @param sqlString
     * @param objs sql语句的参数 为了防止sql注入攻击
     * @return
     * @throws Exception
     */
    public <T> T getObjectBySql(Connection con, Class<T> clazz, String sqlString, Object... objs) throws Exception {
        if (clazz == null) {
            throw new UnsupportedOperationException("obj or clzz 为 null，");
        }

        //获取表名
        String tableName = getTableName(clazz);
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);

        T obj = null;
        try (PreparedStatement prepareStatement = con.prepareStatement(sqlString)) {
            if (objs != null && objs.length > 0) {
                for (int j = 0; j < objs.length; j++) {
                    setStmtParams(prepareStatement, j + 1, objs[j]);
                }
            }
            if (showSql) {
                log.error("\n" + prepareStatement.toString());
            }
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next()) {
                obj = getObjectT(resultSet, tableName, columns, clazz);
            }
        }
        return obj;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回结果对象 public <T> T getObjectByWhere(Connection con, Class<T> clazz, String sqlWhere, Object... objs)">
    /**
     * 返回结果对象
     *
     * @param <T>
     * @param con
     * @param clazz
     * @param sqlWhereString
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> T getObjectByWhere(Connection con, Class<T> clazz, String sqlWhereString, Object... objs) throws Exception {
        if (clazz == null) {
            throw new UnsupportedOperationException("obj or clzz 为 null，");
        }
        //获取表名
        String tableName = getTableName(clazz);
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);

        String selectSql = getSelectSql(tableName, columns, sqlWhereString);
        return getObjectBySql(con, clazz, selectSql, objs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回结果对象 public <T> T getObject(Class<T> clazz, Object... objs)">
    /**
     * 返回结果对象
     *
     * @param <T>
     * @param clazz
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> T getObject(Class<T> clazz, Object... objs) throws Exception {
        try (Connection con = getConnection()) {
            return getObject(con, clazz, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="返回结果对象 public <T> T getObject(Connection con, Class<T> clazz, Object... objs)">
    /**
     * 返回结果对象
     *
     * @param <T>
     * @param con
     * @param clazz
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public <T> T getObject(Connection con, Class<T> clazz, Object... objs) throws Exception {
        if (clazz == null) {
            throw new UnsupportedOperationException("obj or clzz 为 null，");
        }
        //拿到表的所有要创建的字段名
        List<SqlColumn> columns = getColumns(clazz);

        String sqlWhereString = null;
        if (objs != null && objs.length > 0) {
            StringBuilder builder = new StringBuilder();
            int i = 0;
            builder.append(" where ");
            for (SqlColumn value : columns) {
                if (value.isColumnkey()) {
                    if (i > 0) {
                        builder.append(", ");
                    }
                    builder.append("`").append(value.getColumnName()).append("` = ?");
                    i++;
                }
            }
            sqlWhereString = builder.toString();
        }
        return getObjectByWhere(con, clazz, sqlWhereString, objs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="获取一个已经返回的结果集的值 public Object getResultValue(Object obj, String tableName, String columnName, Class<?> columnType)">
    /**
     * 获取一个已经返回的结果集的值
     *
     * @param obj
     * @param tableName
     * @param columnName
     * @param columnType
     * @return
     * @throws java.lang.Exception
     */
    public Object getResultValue(Object obj, String tableName, String columnName, Class<?> columnType) throws Exception {
        if (obj == null) {
            return obj;
        }
        String toLowerCase = columnType.getName().toLowerCase();
        try {
            switch (toLowerCase) {
                case "int":
                case "java.lang.integer":
                    if (obj instanceof Integer) {
                        obj = (Integer) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = 0;
                        } else {
                            obj = Double.valueOf(obj.toString()).intValue();
                        }
                    }
                    break;
                case "string":
                case "java.lang.string":
                    obj = obj.toString();
                    break;
                case "double":
                case "java.lang.double":
                    obj = (Double) obj;
                    break;
                case "float":
                case "java.lang.float":
                    if (obj instanceof Float) {
                        obj = (Float) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = 0f;
                        } else {
                            obj = Double.valueOf(obj.toString()).floatValue();
                        }
                    }
                    break;
                case "long":
                case "java.lang.long":
                    if (obj instanceof Long) {
                        obj = (Long) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = 0l;
                        } else {
                            obj = Double.valueOf(obj.toString()).longValue();
                        }
                    }
                    break;

                case "bigdecimal":
                case "java.math.bigdecimal":
                    if (obj instanceof BigDecimal) {
                        obj = (BigDecimal) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = BigDecimal.valueOf(1l);
                        } else {
                            obj = BigDecimal.valueOf(Double.valueOf(obj.toString()).longValue());
                        }
                    }
                    break;
                case "biginteger":
                case "java.math.biginteger":
                    if (obj instanceof BigInteger) {
                        obj = (BigInteger) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = BigInteger.valueOf(1l);
                        } else {
                            obj = BigInteger.valueOf(Double.valueOf(obj.toString()).longValue());
                        }
                    }
                    break;
                case "byte":
                case "java.lang.byte":
                    if (obj instanceof Byte) {
                        obj = (Byte) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = 0;
                        } else {
                            obj = Double.valueOf(obj.toString()).byteValue();
                        }
                    }
                    break;
                case "short":
                case "java.lang.short":
                    if (obj instanceof Short) {
                        obj = (Short) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = 0;
                        } else {
                            obj = Double.valueOf(obj.toString()).shortValue();
                        }
                    }
                    break;
                case "boolean":
                case "java.lang.boolean":
                    if (obj instanceof Boolean) {
                        obj = (Boolean) obj;
                    } else {
                        if (StringUtil.isNullOrEmpty(obj.toString())) {
                            obj = 0;
                        } else if (Double.valueOf(obj.toString()).intValue() == 1) {
                            obj = true;
                        } else {
                            obj = false;
                        }
                    }
                    break;
                case "date":
                case "java.lang.date":
                    obj = (Date) obj;
                    break;
                default: {
                    byte[] bytes = (byte[]) obj;
                    obj = ZipUtil.unZipObject(bytes);
                }
            }
        } catch (Exception e) {
            log.error("加载表：" + tableName + " 字段：" + columnName + " 字段类型：" + toLowerCase + " 数据库配置值：" + obj, e);
        }
        return obj;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="插入对象 public boolean updateList(List<Object> os)">
    /**
     * 插入对象
     *
     * @param os
     * @return
     * @throws java.lang.Exception
     */
    public int updateList(List<Object> os) throws Exception {
        return updateList(1, os);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="插入对象 public boolean updateList(List<Object> os)">
    /**
     * 插入对象
     *
     * @param constCount 组织 sql 封装 条数
     * @param os
     * @return
     * @throws java.lang.Exception
     */
    public int updateList(int constCount, List<Object> os) throws Exception {
        return update(constCount, os.toArray());
    }
    //</editor-fold>

    public int update(Object... objs) throws Exception {
        return update(1, objs);
    }

    //<editor-fold defaultstate="collapsed" desc="更新数据 public boolean update(Object... objs)">
    /**
     * 更新数据
     *
     * @param constCount
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int update(int constCount, Object... objs) throws Exception {
        int update;
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            update = update(con, constCount, objs);
            con.commit();
        } catch (Exception ex) {
            if (con != null) {
                con.rollback();
            }
            throw ex;
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return update;
    }
    //</editor-fold>

    public int update(Connection con, Object... objs) throws Exception {
        return update(con, 1, objs);
    }

    //<editor-fold defaultstate="collapsed" desc="更新数据 public boolean update(Object obj)">
    /**
     * 更新数据
     *
     * @param con
     * @param constCount
     * @param objs 数据结构体不一定需要一样的
     * @return
     * @throws java.lang.Exception
     */
    public int update(Connection con, int constCount, Object... objs) throws Exception {
        if (objs == null || objs.length == 0) {
            throw new UnsupportedOperationException("objs is null");
        }
        int executeUpdate = 0;

        HashMap<String, ArrayList<Object>> objMap = new HashMap<>();

        for (Object o : objs) {
            //得到对象的类
            Class<?> clazz = o.getClass();
            //获取表名
            String tableName = getTableName(clazz);
            ArrayList<Object> get = objMap.get(tableName);
            if (get == null) {
                get = new ArrayList<>();
                objMap.put(tableName, get);
            }
            get.add(o);
        }
        for (Map.Entry<String, ArrayList<Object>> entry : objMap.entrySet()) {
            String tableName = entry.getKey();
            ArrayList<Object> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                List<SqlColumn> columns = getColumns(values.get(0).getClass());
                String updateSql = updateSqlMap.get(tableName);
                List<SqlColumn> sqlcloumns = updateColumnMap.get(tableName);
                int i = 0;
                if (updateSql == null) {
                    sqlcloumns = new ArrayList<>();
                    StringBuilder builder = new StringBuilder();
                    builder.append("update `").append(tableName).append("` set");
                    for (SqlColumn column : columns) {
                        if (column.isColumnkey() || column.isColumnAuto()) {
                            continue;
                        }
                        if (i > 0) {
                            builder.append(",");
                        }
                        /* 不是主键 */
                        builder.append(" `").append(column.getColumnName()).append("` = ?");
                        sqlcloumns.add(column);
                        i++;
                    }
                    i = 0;
                    for (SqlColumn column : columns) {
                        if (column.isColumnkey()) {
                            if (i == 0) {
                                builder.append(" where ");
                            } else {
                                builder.append(" and ");
                            }
//                            Object invoke = column.getGetMethod().invoke(obj);
//                            NodeEnty<Object, SqlColumn> value = new NodeEnty<>(invoke, column);
                            sqlcloumns.add(column);

                            /* 不是主键 */
                            builder.append(" `").append(column.getColumnName()).append("` = ? ");
                            i++;
                        }
                    }
                    updateSql = builder.toString();
                    updateSqlMap.put(tableName, updateSql);
                    updateColumnMap.put(tableName, sqlcloumns);
                }
                if (showSql) {
                    log.error(updateSql);
                }
                int forcount = values.size() / constCount + (values.size() % constCount > 0 ? 1 : 0);
                try (PreparedStatement prepareStatement = con.prepareStatement(updateSql)) {
                    int tmpExecCount = 0;
                    for (int k = 0; k < forcount; k++) {
                        int count1 = k * constCount;
                        int forcount1 = constCount;
                        if (k == 0) {
                            if (constCount > values.size() && count1 < values.size()) {
                                forcount1 = values.size();
                            }
                        } else if (count1 + constCount >= values.size()) {
                            forcount1 = values.size() - count1;
                        }

                        for (int j = 0; j < forcount1; j++) {
                            Object obj = values.get(count1 + j);
                            for (int g = 0; g < sqlcloumns.size(); g++) {
                                SqlColumn sqlColumn = sqlcloumns.get(g);
                                Object invoke = sqlColumn.getGetMethod().invoke(obj);
                                setStmtParams(prepareStatement, sqlColumn, g + 1, invoke);
                            }
                            prepareStatement.addBatch();
                        }
                        int[] executeBatch = prepareStatement.executeBatch();
                        for (int j = 0; j < executeBatch.length; j++) {
                            if (executeBatch[j] == PreparedStatement.SUCCESS_NO_INFO || executeBatch[j] == PreparedStatement.CLOSE_CURRENT_RESULT) {
                                tmpExecCount++;
                            }
                        }
                        prepareStatement.clearParameters();
                        prepareStatement.clearBatch();
                    }
                    if (showSql) {
                        log.error("\n" + prepareStatement.toString() + " 执行结果：" + tmpExecCount);
                    }
                    executeUpdate += tmpExecCount;
                } catch (Exception ex) {
                    log.error("执行sql语句错误：" + updateSql);
                    throw ex;
                }
            }
        }
        return executeUpdate;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="更新数据 public int executeUpdate(String sql, Object... objs)">
    /**
     * 更新数据
     *
     * @param sql
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int executeUpdate(String sql, Object... objs) throws Exception {
        try (Connection con = getConnection()) {
            return executeUpdate(con, sql, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="更新数据 public int executeUpdate(String sql, Object... objs)">
    /**
     * 更新数据
     *
     * @param con
     * @param sql
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int executeUpdate(Connection con, String sql, Object... objs) throws Exception {
        try (PreparedStatement prepareStatement = con.prepareStatement(sql)) {
            if (objs != null && objs.length > 0) {
                for (int i = 0; i < objs.length; i++) {
                    setStmtParams(prepareStatement, i + 1, objs[i]);
                }
            }
            int executeUpdate = prepareStatement.executeUpdate();
            if (showSql) {
                log.error("\n" + prepareStatement.toString() + " 执行结果：" + executeUpdate);
            } else {
                log.error("执行结果：" + executeUpdate);
            }
            return executeUpdate;
        } catch (Exception ex) {
            log.error("执行sql语句错误：" + sql);
            throw ex;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除数据 public int delete(Class<?> clazz)">
    /**
     * 删除数据
     *
     * @param clazz
     * @return
     * @throws java.lang.Exception
     */
    public int delete(Class<?> clazz) throws Exception {
        return deleteByWhere(clazz, null);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除行 public int deleteByWhere(Class<?> clazz, String sqlWhere, Object... objs)">
    /**
     * 删除行
     *
     * @param clazz
     * @param sqlWhere 请加入where
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int deleteByWhere(Class<?> clazz, String sqlWhere, Object... objs) throws Exception {
        try (Connection con = getConnection()) {
            return deleteByWhere(con, clazz, sqlWhere, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除行 public int deleteByWhere(Connection con, Class<?> clazz, String sqlWhere, Object... objs)">
    /**
     * 删除行
     *
     * @param con
     * @param clazz
     * @param sqlWhere 请加入where
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int deleteByWhere(Connection con, Class<?> clazz, String sqlWhere, Object... objs) throws Exception {
        StringBuilder builder = new StringBuilder();
        String tableName = getTableName(clazz);
        builder.append("DELETE FROM `").append(tableName).append("`");
        if (!StringUtil.isNullOrEmpty(sqlWhere)) {
            builder.append(sqlWhere);
        }
        return executeUpdate(con, builder.toString(), objs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除行 public int delete(Class<?> clazz, Object... objs)">
    /**
     * 删除行
     *
     * @param clazz 需要删除的表结构
     * @param objs 主键字段的值
     * @return
     * @throws java.lang.Exception
     */
    public int delete(Class<?> clazz, Object... objs) throws Exception {
        try (Connection con = getConnection()) {
            return delete(con, clazz, objs);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除 多条执行总影响行数 public int deleteList(List<Object> objs)">
    /**
     * 删除 多条执行总影响行数
     *
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int deleteList(List<Object> objs) throws Exception {
        return delete(objs.toArray());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除 多条执行总影响行数 public int delete(Object... obj)">
    /**
     * 删除 多条执行总影响行数
     *
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int delete(Object... objs) throws Exception {
        int del;
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            del = delete(con, objs);
            con.commit();
        } catch (Exception ex) {
            if (con != null) {
                con.rollback();
            }
            throw ex;
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return del;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除 多条执行总影响行数 public int delete(Connection con, Object... objs)">
    /**
     * 删除 多条执行总影响行数
     *
     * @param con
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int delete(Connection con, Object... objs) throws Exception {
        int count = 0;
        for (Object obj : objs) {
            StringBuilder builder = new StringBuilder();
            Class<?> clazz = obj.getClass();
            String tableName = getTableName(clazz);
            List<SqlColumn> columns = getColumns(clazz);
            List<Object> values = new ArrayList<>();
            builder.append("DELETE FROM `").append(tableName).append("`").append(" WHERE ");
            int i = 0;
            for (SqlColumn column : columns) {
                if (column.isColumnkey()) {
                    if (i > 0) {
                        builder.append(" and ");
                    }
                    builder.append(column.getColumnName()).append(" = ?");
                    Object invoke = column.getGetMethod().invoke(obj);
                    values.add(invoke);
                    i++;
                }
            }
            count += executeUpdate(con, builder.toString(), values.toArray());
        }
        return count;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除行 public int delete(Connection con, Class<?> clazz, Object... objs)">
    /**
     * 删除行
     *
     * @param con
     * @param clazz
     * @param objs
     * @return
     * @throws java.lang.Exception
     */
    public int delete(Connection con, Class<?> clazz, Object... objs) throws Exception {
        StringBuilder builder = new StringBuilder();
        String tableName = getTableName(clazz);
        List<SqlColumn> columns = getColumns(clazz);
        builder.append("DELETE FROM `").append(tableName).append("`").append(" WHERE ");
        int i = 0;
        for (SqlColumn column : columns) {
            if (column.isColumnkey()) {
                if (i > 0) {
                    builder.append(" and ");
                }
                builder.append(column.getColumnName()).append(" = ?");
                i++;
            }
        }
        return executeUpdate(con, builder.toString(), objs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除表 public int dropTable(Object obj)">
    /**
     * 删除表
     *
     * @param obj
     * @return
     * @throws java.lang.Exception
     */
    public int dropTable(Object obj) throws Exception {
        return dropTable(obj.getClass());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除表 public int dropTable(Class<?> clazz)">
    /**
     * 删除表
     *
     * @param clazz
     * @return
     * @throws java.lang.Exception
     */
    public int dropTable(Class<?> clazz) throws Exception {
        StringBuilder builder = new StringBuilder();
        String tableName = getTableName(clazz);
        builder.append("DROP TABLE IF EXISTS `").append(tableName).append("`;");
        return executeUpdate(builder.toString());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="删除数据库 public int dropDatabase(String database)">
    /**
     * 删除数据库
     *
     * @param database
     * @return
     * @throws java.lang.Exception
     */
    public int dropDatabase(String database) throws Exception {
        try (Connection connection = getConnection("INFORMATION_SCHEMA")) {
            StringBuilder builder = new StringBuilder();
            builder.append("DROP DATABASE IF EXISTS `").append(database).append("`;");
            return executeUpdate(connection, builder.toString());
        }
    }
    //</editor-fold>

    public int createDatabase() throws Exception {
        return createDatabase(this.dbName);
    }

    //<editor-fold defaultstate="collapsed" desc="创建数据库 public int createDatabase(String database)">
    /**
     * 创建数据库 , 吃方法创建数据库后会自动使用 use 语句
     *
     * @param database
     * @return
     * @throws java.lang.Exception
     */
    public int createDatabase(String database) throws Exception {
        try (Connection connection = getConnection("INFORMATION_SCHEMA")) {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE DATABASE IF NOT EXISTS `").append(database).append("` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;");
            return executeUpdate(connection, builder.toString());
        }
    }
    //</editor-fold>

}
