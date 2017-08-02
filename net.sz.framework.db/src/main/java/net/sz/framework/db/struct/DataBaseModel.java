package net.sz.framework.db.struct;

import java.io.Serializable;
import net.sz.framework.utils.JsonUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class DataBaseModel implements Serializable, Cloneable {

    private static final long serialVersionUID = -873763588518052228L;

    /**
     * 忽律字段，表牵涉的数据库名称
     */
    @AttColumn(alligator = true)
    protected transient String dataBaseName;
    /**
     * 忽律字段,表牵涉的表名字
     */
    @AttColumn(alligator = true)
    protected transient String dataTableName;

    /**
     * 忽律字段,是否启用复合主键
     */
    @AttColumn(alligator = true)
    protected transient boolean compositePrimaryKey = false;

    /**
     * 忽律字段，表牵涉的数据库名称
     *
     * @return
     */
    public String getDataBaseName() {
        return dataBaseName;
    }

    /**
     * 忽律字段，表牵涉的数据库名称
     *
     * @param dataBaseName
     */
    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    /**
     * 忽律字段,表牵涉的表名字
     *
     * @return
     */
    public String getDataTableName() {
        return dataTableName;
    }

    /**
     * 忽律字段,表牵涉的表名字
     *
     * @param dataTableName
     */
    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    /**
     * 忽律字段,是否启用复合主键
     *
     * @return
     */
    public boolean isCompositePrimaryKey() {
        return compositePrimaryKey;
    }

    /**
     * 忽律字段,是否启用复合主键
     *
     * @param compositePrimaryKey
     */
    public void setCompositePrimaryKey(boolean compositePrimaryKey) {
        this.compositePrimaryKey = compositePrimaryKey;
    }

    @Override
    public Object clone() {
        try {
            return super.clone(); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * fastjson 转化
     *
     * @return
     */
    public String toJson() {
        return JsonUtil.toJSONString(this);
    }

    @Override
    public String toString() {
        return "DataBaseModel{" + "dataBaseName=" + dataBaseName + ", dataTableName=" + dataTableName + ", compositePrimaryKey=" + compositePrimaryKey + '}';
    }

}
