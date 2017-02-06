package net.sz.game.engine.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 尚未完善的数据集合
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class SqliteDaoImpl extends Dao {

    private static final Logger log = Logger.getLogger(SqliteDaoImpl.class);

    private static final String ifexitstable = "select sum(1) `TABLE_NAME` from sqlite_master where type ='table' and `name`= ? ;";

    /**
     * 默认不使用连接池
     *
     * @param dbUrl
     * @param dbName
     * @param dbUser
     * @param dbPwd
     * @throws PropertyVetoException
     */
    public SqliteDaoImpl(String dbUrl, String dbName, String dbUser, String dbPwd) throws Exception {
        this(dbUrl, dbName, dbUser, dbPwd, false);
    }

    /**
     * 默认不使用连接池
     *
     * @param dbUrl
     * @param dbName
     * @param dbUser
     * @param dbPwd
     * @param showSql
     * @throws PropertyVetoException
     */
    public SqliteDaoImpl(String dbUrl, String dbName, String dbUser, String dbPwd, boolean showSql) throws Exception {
        this(dbUrl, dbName, dbUser, dbPwd, false, showSql);
    }

    public SqliteDaoImpl(String dbUrl, String dbName, String dbUser, String dbPwd, boolean isC3p0, boolean showSql) throws Exception {
        super(dbUrl, dbName, DriverName.SqliteDriver, dbUser, dbPwd, isC3p0, showSql);
    }

    /**
     * 获取数据库的连接
     *
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
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
            } catch (ClassNotFoundException e) {
                log.error(getConnectionDriverName(), e);
            }
            startend = true;
        }
        return DriverManager.getConnection(getConnectionString(dbnameString), this.dbUser, this.dbPwd);
    }

    @Override
    public String getConnectionString(String dbnameString) {
        return String.format("jdbc:sqlite:%s", dbnameString);
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean existsTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception {
        try (PreparedStatement createStatement = con.prepareStatement(ifexitstable)) {
            createStatement.setString(1, tableName);
            ResultSet executeQuery = createStatement.executeQuery();
            if (executeQuery != null && executeQuery.next()) {
                int aInt = executeQuery.getInt("TABLE_NAME");
                if (showSql) {
                    log.error("表：" + tableName + " 检查结果：" + (aInt > 0 ? " 已存在 " : " 无此表 "));
                }
                if (aInt > 0) {

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean existsColumn(Connection con, String tableName, String columnName) throws Exception {
        String ifexitscolumn = "SELECT sum(1) usm FROM sqlite_master WHERE name='" + tableName + "' AND sql like '%`" + columnName + "`%'";
        try (PreparedStatement createStatement = con.prepareStatement(ifexitscolumn)) {
            ResultSet executeQuery = createStatement.executeQuery();
            if (executeQuery != null && executeQuery.next()) {
                int aInt1 = executeQuery.getInt("usm");
                if (aInt1 > 0) {
                    if (showSql) {
                        log.error("数据库：" + dbName + " 表：" + tableName + " 映射数据库字段：" + columnName + " 检查结果：已存在，将不会修改");
                    }
                    return true;
                } else {
                    if (showSql) {
                        log.error("数据库：" + dbName + " 表：" + tableName + " 映射数据库字段：" + columnName + " 检查结果：无此字段 ");
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param tableName
     * @param columns
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    protected void createTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception {
        String sqls = null;
        if (existsTable(con, tableName, null)) {
            //执行对应的创建表操作
            try (Statement createStatement = con.createStatement()) {
                for (SqlColumn value : columns) {
                    boolean existsColumn = existsColumn(con, tableName, tableName);
                    if (!existsColumn) {

                        sqls = "ALTER TABLE `" + tableName + "` ADD `" + value.getColumnName() + "` " + value.getValue() + ";";
                        int execute1 = createStatement.executeUpdate(sqls);
                        if (showSql) {
                            log.error("执行语句：" + sqls + " 执行结果：" + execute1);
                        }
                    } else {
                        if (showSql) {
                            log.error("表：" + tableName + " 字段：" + value.getFieldName() + " 映射数据库字段：" + value.getColumnName() + " 存在，将不会修改，");
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
                    }
                }
            } catch (Exception ex) {
                log.error("执行sql语句错误：" + sqls);
                throw ex;
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\n create table if not exists `").append(tableName).append("` (\n");
            //将所有的字段拼接成对应的SQL语句
            for (SqlColumn value : columns) {
                sb.append("     `").append(value.getColumnName()).append("` ").append(value.getValue()).append(",\n");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("\n);");
            //执行对应的创建表操作
            sqls = sb.toString();
            try (PreparedStatement p1 = con.prepareStatement(sqls)) {
                int execute = p1.executeUpdate();
                if (showSql) {
                    log.error("\n表：" + sqls + " \n创建完成；");
                }
            } catch (Exception ex) {
                log.error("执行sql语句错误：" + sqls);
                throw ex;
            }
        }
    }

    @Override
    public int createDatabase(String database) throws Exception {
        throw new UnsupportedOperationException("Create Database do not Operation");
    }

    @Override
    public int dropDatabase(String database) throws Exception {
        throw new UnsupportedOperationException("Drop Database do not Operation");
    }

}
