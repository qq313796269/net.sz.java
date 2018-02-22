package net.sz.framework.db.sqlite;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import net.sz.framework.db.Dao;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.db.struct.SqlColumn;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.GlobalUtil;

/**
 * 尚未完善的数据集合
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 *
 * @param <T>
 */
public class SqliteDaoImpl<T extends DataBaseModel> extends Dao<T> {

    private static final SzLogger log = SzLogger.getLogger();

    private static final String ifexitstable = "select sum(1) `TABLE_NAME` from sqlite_master where type ='table' and `name`= ? ;";

    /**
     * 默认不使用连接池
     *
     * @param dbUrl
     * @throws PropertyVetoException
     */
    public SqliteDaoImpl(String dbUrl) throws Exception {
        this(dbUrl, false);
    }

    /**
     * 默认不使用连接池
     *
     * @param dbUrl
     * @param showSql
     * @throws PropertyVetoException
     */
    public SqliteDaoImpl(String dbUrl, boolean showSql) throws Exception {
        super(dbUrl, dbUrl, DriverName.SqliteDriver, null, null, false, showSql);
//        new File(dbName).
        new File(dbUrl).getParentFile().mkdirs();
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
        return getConnection(this.getDbUrl());
    }

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

    @Override
    public String getConnectionString(String dbnameString) {
        return String.format("jdbc:sqlite:%s", dbnameString);
    }

    @Override
    public void close() {
        if (this.getBds() == null) {
            this.getBds().close();
        }
    }

    @Override
    public boolean existsTable(Connection con, String tableName, List<SqlColumn> columns) throws Exception {
        Integer aInt1 = this.getExecuteScalar(con, ifexitstable, Integer.class, tableName);
        if (aInt1 != null && aInt1 > 0) {
            if (columns != null && !columns.isEmpty()) {
                for (SqlColumn column : columns) {
                    if (existsColumn(con, tableName, column.getColumnName())) {
                        if (this.isShowSql()) {
                            log.error(this.getDbName() + " 数据库：" + this.getDbUrl() + " 表：" + tableName + " 映射数据库字段：" + column.getColumnName() + " 检查结果：已存在，将不会修改");
                        } else {
                            throw new UnsupportedOperationException(this.getDbName() + " 数据库：" + this.getDbName() + " 表：" + tableName + " 字段：" + column.getColumnName() + " 检查结果：无此字段 ");
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean existsColumn(Connection con, String tableName, String columnName) throws Exception {
        String ifexitscolumn = "SELECT sum(1) usm FROM sqlite_master WHERE name='" + tableName + "' AND sql like '%`" + columnName + "`%'";
        Integer aInt1 = this.getExecuteScalar(con, ifexitscolumn, Integer.class);
        return aInt1 != null && aInt1 > 0;
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
    protected void createTable(Connection con, String tableName, List<SqlColumn> columns, List<List<String>> columnKeys) throws Exception {
        String sqls = null;
        if (existsTable(con, tableName, null)) {
            //执行对应的创建表操作
            for (SqlColumn column : columns) {
                if (existsColumn(con, tableName, column.getColumnName())) {
                    if (this.isShowSql()) {
                        log.error(this.getDbName() + " 表：" + tableName + " 字段：" + column.getFieldName() + " 映射数据库字段：" + column.getColumnName() + " 存在，将不会修改，");
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
                    if (column.isColumnkey() && !column.isColumnUnique()) {
                        sqls = "CREATE Unique INDEX un_key_" + tableName + "_" + column.getColumnName() + " ON " + tableName + " (`" + column.getColumnName() + "`);";
                        this.executeUpdate(con, sqls);
                    } else if (column.isColumnUnique()) {
                        sqls = "CREATE INDEX in_key_" + tableName + "_" + column.getColumnName() + " ON " + tableName + " (`" + column.getColumnName() + "`);";
                        this.executeUpdate(con, sqls);
                    }
                }
            }
        } else {
            StringBuilder sb = new StringBuilder();

            sb.append("\n create table if not exists `").append(tableName).append("` (\n");

            //将所有的字段拼接成对应的SQL语句
            for (int i = 0; i < columns.size(); i++) {
                SqlColumn column = columns.get(i);
                sb.append("     `").append(column.getColumnName()).append("` ");
                sb.append(column.getValue());
                if (i < columns.size() - 1) {
                    sb.append(",\n");
                }
            }

            int pi = 0;
            for (SqlColumn sqlColumn : columns) {
                if (sqlColumn.isColumnkey()) {
                    if (pi == 0) {
                        sb.append(",\n").append("     PRIMARY KEY (");
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
            sb.append("\n)");
            //执行对应的创建表操作
            sqls = sb.toString();
            this.executeUpdate(con, sqls);

            for (SqlColumn sqlColumn : columns) {
                if (sqlColumn.isColumnUnique()) {
                    sqls = "CREATE Unique INDEX un_key_" + tableName + "_" + sqlColumn.getColumnName() + " ON " + tableName + " (`" + sqlColumn.getColumnName() + "`);";
                    this.executeUpdate(con, sqls);
                } else if (sqlColumn.isColumnIndex()) {
                    sqls = "CREATE INDEX in_key_" + tableName + "_" + sqlColumn.getColumnName() + " ON " + tableName + " (`" + sqlColumn.getColumnName() + "`);";
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
                log.error("\n" + this.getDbName() + " 表：" + sqls + "\n 创建完成；");
            }
        }
    }

    @Override
    public int createDatabase(String database) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " Create Database do not Operation by sqlite");
    }

    @Override
    public int dropDatabase(String database) throws Exception {
        throw new UnsupportedOperationException(this.getDbName() + " Drop Database do not Operation by sqlite");
    }

}
