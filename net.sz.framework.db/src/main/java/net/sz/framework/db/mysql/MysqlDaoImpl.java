package net.sz.framework.db.mysql;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sz.framework.db.Dao;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.db.struct.SqlColumn;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.GlobalUtil;

/**
 * mysql 数据库
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <T>
 */
public class MysqlDaoImpl<T extends DataBaseModel> extends Dao<T> {

    private static final SzLogger log = SzLogger.getLogger();

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
        if (this.isC3p0()) {
            return super.getConnection();
        }
        return getConnection(this.getDbName());
    }

    /**
     *
     * @param dbnameString
     * @return
     * @throws Exception
     */
    @Override
    protected Connection getConnection(String dbnameString) throws Exception {
        if (!this.isStartend()) {
            try {
                Class.forName(getConnectionDriverName());
            } catch (Throwable e) {
                log.error(getConnectionDriverName(), e);
            }
            this.setStartend(true);
        }
        return DriverManager.getConnection(getConnectionString(dbnameString), this.getDbUser(), this.getDbPwd());
    }

    /**
     * 获取连接地址，连接字符串
     *
     * @param dbnameString
     * @return
     */
    @Override
    public String getConnectionString(String dbnameString) {
        return String.format("jdbc:mysql://%s/%s?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=UTF-8&useSSL=true&zeroDateTimeBehavior=convertToNull", this.getDbUrl(), dbnameString);
    }

    @Override
    public void close() {
        if (this.getBds() != null) {
            this.getBds().close();
        }
    }

    @Override
    public boolean existsTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception {
        Integer aInt1 = this.getExecuteScalar(con, ifexitstable, Integer.class, this.getDbName(), tableName);
        if (aInt1 != null && aInt1 > 0) {
            if (this.isShowSql()) {
                log.error("数据库：" + this.getDbName() + " 表：" + tableName + " 检查结果：已存在");
            }
            if (columns != null && !columns.isEmpty()) {
                for (SqlColumn column : columns) {
                    boolean existsColumn = existsColumn(con, tableName, column.getColumnName());
                    if (!existsColumn) {
                        throw new UnsupportedOperationException("数据库：" + this.getDbName() + " 表：" + tableName + " 字段：" + column.getColumnName() + " 检查结果：无此字段 ");
                    }
                }
            }
            return true;
        } else {
            if (this.isShowSql()) {
                log.error("数据库：" + this.getDbName() + " 表：" + tableName + " 检查结果：无此表 ");
            }
        }
        return false;
    }

    private static final String ifexitscolumn = "SELECT sum(1) usm FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA` = ? AND `TABLE_NAME` = ? AND COLUMN_NAME = ? ;";
    private static final String ifexitstable = "select sum(1) usm from `INFORMATION_SCHEMA`.`TABLES` where `TABLE_SCHEMA` = ? and `TABLE_NAME` = ? ;";

    @Override
    protected boolean existsColumn(Connection con, String tableName, String columnName) throws Exception {
        Integer aInt1 = this.getExecuteScalar(con, ifexitscolumn, Integer.class, this.getDbName(), tableName, columnName);
        return aInt1 != null && aInt1 > 0;
    }

    /**
     *
     * @param tableName
     * @param columns
     * @throws java.lang.Exception
     */
    @Override
    protected void createTable(Connection con, String tableName, List<SqlColumn> columns, List<List<String>> columnKeys) throws Exception {
        String sqls = null;
        if (existsTable(con, tableName, null)) {
            //执行对应的创建表操作
            try {
                for (SqlColumn column : columns) {
                    if (existsColumn(con, tableName, column.getColumnName())) {
                        if (this.isShowSql()) {
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
                        sqls = "ALTER TABLE `" + tableName + "` ADD `" + column.getColumnName() + "` " + column.getValue() + ";\n";
                        this.executeUpdate(con, sqls);
                        if (column.isColumnUnique()) {
                            sqls = "ALTER TABLE `" + tableName + "` ADD " + "Unique Key un_key_" + tableName + "_" + column.getColumnName() + " (" + "`" + column.getColumnName() + "`) USING HASH ;\n";
                            this.executeUpdate(con, sqls);
                        } else if (column.isColumnIndex()) {
                            sqls = "ALTER TABLE `" + tableName + "` ADD " + "INDEX in_key_" + tableName + "_" + column.getColumnName() + " (" + "`" + column.getColumnName() + "`) USING HASH ;\n";
                            this.executeUpdate(con, sqls);
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
            for (int i = 0; i < columns.size(); i++) {
                SqlColumn sqlColumn = columns.get(i);
                sb.append("     `").append(sqlColumn.getColumnName()).append("` ");
                sb.append(sqlColumn.getValue());
                sb.append(" COMMENT '").append(sqlColumn.getColumnDefinition()).append("' ");
                if (i < columns.size() - 1) {
                    sb.append(",\n");
                }
            }

            int pi = 0;
            for (SqlColumn sqlColumn : columns) {
                if (sqlColumn.isColumnkey()) {
                    if (pi == 0) {
                        sb.append(",\n").append("     PRIMARY KEY p_key_").append(tableName).append(" (");
                    } else if (pi > 0) {
                        sb.append(", ");
                    }
                    sb.append("`").append(sqlColumn.getColumnName()).append("`");
                    pi++;
                }
            }

            if (pi > 0) {
                sb.append(")");
            }

            sb.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8;");

            //执行对应的创建表操作
            sqls = sb.toString();
            this.executeUpdate(con, sqls);

            for (SqlColumn sqlColumn : columns) {
                if (sqlColumn.isColumnUnique()) {
                    sqls = "ALTER TABLE `" + tableName + "` ADD " + "Unique Key un_key_" + tableName + "_" + sqlColumn.getColumnName() + " (" + "`" + sqlColumn.getColumnName() + "`) USING HASH ;\n";
                    this.executeUpdate(con, sqls);
                } else if (sqlColumn.isColumnIndex()) {
                    sqls = "ALTER TABLE `" + tableName + "` ADD " + "INDEX in_key_" + tableName + "_" + sqlColumn.getColumnName() + " (" + "`" + sqlColumn.getColumnName() + "`) USING HASH ;\n";
                    this.executeUpdate(con, sqls);
                }
            }

            if (columnKeys != null) {
                for (List<String> columnKey : columnKeys) {
                    String ret = "`" + String.join("`,`", columnKey) + "`";
                    sqls = "ALTER TABLE `" + tableName + "` ADD " + "INDEX in_keys_" + tableName + "_" + GlobalUtil.getUUID() + " (" + ret + ") USING HASH ;\n";
                    this.executeUpdate(con, sqls);
                }
            }

            if (this.isShowSql()) {
                log.error("\n表：" + tableName + "\n 创建完成；");
            }
        }
    }
}
