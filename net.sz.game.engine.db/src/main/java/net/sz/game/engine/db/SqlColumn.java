package net.sz.game.engine.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class SqlColumn {

    private static SzLogger log = SzLogger.getLogger();
    //数据库映射名字
    private String columnName;
    //字段名字
    private String fieldName;
    //字段长度
    private int colunmLength;
    //是否是自增列表
    private boolean columnAuto;
    //是否是主键列
    private boolean columnkey;
    //字段是否为空
    private boolean columnNullAble;
    //字段描述
    private String columnDefinition;
    //最后拼接
    private String value;
    //
    private Class<?> classType;

    private Method setMethod;

    private Method getMethod;

    private Field field;

    public SqlColumn() {
        this.columnName = "";
        this.fieldName = "";
        this.colunmLength = 255;
        this.columnAuto = false;
        this.columnkey = false;
        this.columnNullAble = true;
        this.columnDefinition = "";
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColunmLength() {
        return colunmLength;
    }

    public void setColunmLength(int colunmLength) {
        this.colunmLength = colunmLength;
    }

    public boolean isColumnAuto() {
        return columnAuto;
    }

    public void setColumnAuto(boolean columnAuto) {
        this.columnAuto = columnAuto;
    }

    public boolean isColumnkey() {
        return columnkey;
    }

    public void setColumnkey(boolean columnkey) {
        this.columnkey = columnkey;
    }

    public boolean isColumnNullAble() {
        return columnNullAble;
    }

    public void setColumnNullAble(boolean columnNullAble) {
        this.columnNullAble = columnNullAble;
    }

    public String getColumnDefinition() {
        return columnDefinition;
    }

    public void setColumnDefinition(String columnDefinition) {
        this.columnDefinition = columnDefinition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "SqlColumn{" + "columnName=" + columnName + ", colunmLength=" + colunmLength + ", columnAuto=" + columnAuto + ", columnkey=" + columnkey + ", columnNullAble=" + columnNullAble + ", columnDefinition=" + columnDefinition + ", value=" + value + '}';
    }

}
