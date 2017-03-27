package net.sz.game.engine.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MysqlDaoImpl extends Dao {

    private static SzLogger log = SzLogger.getLogger();

    /**
     * 默认不使用连接池，不开启sql语句监听显示
     *
     * @param dbUrl
     * @param dbName
     * @param dbUser
     * @param dbPwd
     * @throws PropertyVetoException
     */
    public MysqlDaoImpl(String dbUrl, String dbName, String dbUser, String dbPwd) throws Exception {
        this(dbUrl, dbName, dbUser, dbPwd, false);
    }

    /**
     * 默认不使用连接池，不开启sql语句监听显示
     *
     * @param dbUrl
     * @param dbName
     * @param dbUser
     * @param dbPwd
     * @param showSql
     * @throws PropertyVetoException
     */
    public MysqlDaoImpl(String dbUrl, String dbName, String dbUser, String dbPwd, boolean showSql) throws Exception {
        this(dbUrl, dbName, dbUser, dbPwd, DriverName.MysqlDriver56Left, false, showSql);
    }

    /**
     *
     * @param dbUrl
     * @param dbName
     * @param dbUser
     * @param dbPwd
     * @param driverName com.mysql.jdbc.Driver or com.mysql.cj.jdbc.Driver
     * @param isC3p0 是否使用c3p0连接池
     * @param showSql
     * @throws PropertyVetoException
     */
    public MysqlDaoImpl(String dbUrl, String dbName, String dbUser, String dbPwd, DriverName driverName, boolean isC3p0, boolean showSql) throws Exception {
        super(dbUrl, dbName, driverName, dbUser, dbPwd, isC3p0, showSql);
    }

    /**
     * 获取数据库的连接
     *
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public Connection getConnection() throws Exception {
        if (this.c3p0) {
            return super.getConnection();
        }
        return getConnection(this.dbName);
    }

    @Override
    protected Connection getConnection(String dbnameString) throws Exception {
        if (!startend) {
            try {
                Class.forName(getConnectionDriverName());
            } catch (Throwable e) {
                log.error(getConnectionDriverName(), e);
            }
            startend = true;
        }
        return DriverManager.getConnection(getConnectionString(dbnameString), this.dbUser, this.dbPwd);
    }

    @Override
    public String getConnectionString(String dbnameString) {
        return String.format("jdbc:mysql://%s/%s?rewriteBatchedStatements=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull", dbUrl, dbnameString);
    }

    @Override
    public void close() {
        if (this.bds != null) {
            this.bds.close();
        }
    }

    @Override
    public boolean existsTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception {
        int aInt = 0;
        try (PreparedStatement createStatement = con.prepareStatement(ifexitstable)) {
            createStatement.setString(1, dbName);
            createStatement.setString(2, tableName);
            ResultSet executeQuery = createStatement.executeQuery();
            if (executeQuery != null && executeQuery.next()) {
                aInt = executeQuery.getInt("TABLE_NAME");
            }
        }
        if (aInt > 0) {
            if (showSql) {
                log.error("数据库：" + dbName + " 表：" + tableName + " 检查结果：已存在");
            }
            if (columns != null && !columns.isEmpty()) {
                for (SqlColumn column : columns) {
                    boolean existsColumn = existsColumn(con, tableName, column.getColumnName());
                    if (!existsColumn) {
                        throw new UnsupportedOperationException("数据库：" + dbName + " 表：" + tableName + " 字段：" + column.getColumnName() + " 检查结果：无此字段 ");
                    }
                }
            }
            return true;
        } else {
            if (showSql) {
                log.error("数据库：" + dbName + " 表：" + tableName + " 检查结果：无此表 ");
            }
        }
        return false;
    }

    private static final String ifexitscolumn = "SELECT sum(1) usm FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA= ? AND table_name= ? AND COLUMN_NAME = ? ;";
    private static final String ifexitstable = "select sum(1) `TABLE_NAME` from `INFORMATION_SCHEMA`.`TABLES` where `TABLE_SCHEMA`= ? and `TABLE_NAME`= ? ;";

    @Override
    protected boolean existsColumn(Connection con, String tableName, String columnName) throws Exception {
        String toString = null;
        try (PreparedStatement createStatement = con.prepareStatement(ifexitscolumn)) {
            createStatement.setString(1, dbName);
            createStatement.setString(2, tableName);
            createStatement.setString(3, columnName);

            toString = createStatement.toString();
            ResultSet executeQuery = createStatement.executeQuery();
            if (executeQuery != null && executeQuery.next()) {
                int aInt1 = executeQuery.getInt("usm");
                if (aInt1 > 0) {
                    if (showSql) {
                        log.error("数据库：" + dbName + " 表：" + tableName + " 映射数据库字段：" + columnName + " 检查结果：已存在");
                    }
                    return true;
                } else {
                    if (showSql) {
                        log.error("数据库：" + dbName + " 表：" + tableName + " 映射数据库字段：" + columnName + " 检查结果：无此字段 ");
                    }
                }
            }
        } catch (Throwable ex) {
            log.error("执行sql语句错误：" + toString);
            throw ex;
        }
        return false;
    }

    /**
     *
     * @param tableName
     * @param columns
     * @throws java.lang.Exception
     */
    @Override
    protected void createTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception {
        String sqls = null;
        if (existsTable(con, tableName, null)) {
            //执行对应的创建表操作
            try (Statement createStatement = con.createStatement()) {
                for (SqlColumn column : columns) {
                    if (existsColumn(con, tableName, column.getColumnName())) {
                        if (showSql) {
                            log.error("表：" + tableName + " 字段：" + column.getFieldName() + " 映射数据库字段：" + column.getColumnName() + " 存在，将不会修改，");
                        }
                        /*   String sqls = "ALTER TABLE " + tableName + " CHANGE `" + key + "` " + value.getValue() + ";";
                                    if (showSql) {
                                        log.error("执行语句：" + sqls);
                                    }
                                    try (Statement cs1 = con.createStatement()) {
                                        boolean execute1 = cs1.execute(sqls);
                                        if (showSql) {
                                            log.error("执行结果：" + execute1);
                                        }
                                    }*/
                    } else {

                        sqls = "ALTER TABLE `" + tableName + "` ADD `" + column.getColumnName() + "` " + column.getValue() + ";";
                        if (showSql) {
                            log.error("执行语句：" + sqls);
                        }
                        boolean execute1 = createStatement.execute(sqls);
                        if (showSql) {
                            log.error("执行语句：" + sqls + " 执行结果：" + execute1);
                        }
                    }
                }
            } catch (Throwable ex) {
                log.error("执行sql语句错误：" + sqls);
                throw ex;
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\n create table if not exists `").append(tableName).append("` (\n");
            //将所有的字段拼接成对应的SQL语句
            for (SqlColumn sqlColumn : columns) {
                sb.append("     `").append(sqlColumn.getColumnName()).append("` ").append(sqlColumn.getValue()).append(" COMMENT '").append(sqlColumn.getColumnDefinition()).append("' ").append(",\n");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            //执行对应的创建表操作
            sqls = sb.toString();
            try (PreparedStatement p1 = con.prepareStatement(sqls)) {
                boolean execute = p1.execute();
                if (showSql) {
                    log.error("\n表：" + sqls + "\n 创建完成；");
                }
            } catch (Throwable ex) {
                log.error("执行sql语句错误：" + sqls);
                throw ex;
            }
        }
    }
}
