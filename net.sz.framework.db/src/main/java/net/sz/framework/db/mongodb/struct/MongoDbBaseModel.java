package net.sz.framework.db.mongodb.struct;

import net.sz.framework.db.struct.AttColumn;
import net.sz.framework.db.struct.DataBaseModel;
import org.bson.types.ObjectId;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MongoDbBaseModel extends DataBaseModel {

    /**
     * 忽略字段，mongodb 自动生成的数据
     */
    @AttColumn(alligator = true)
    protected transient ObjectId _id;

    /**
     * 忽略字段，mongodb 自动生成的数据
     *
     * @return
     */
    public ObjectId _getId() {
        return _id;
    }

    /**
     * 忽略字段，mongodb 自动生成的数据
     *
     * @param _id
     */
    @Deprecated
    public void _setId(ObjectId _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "{" + "'_id'='" + _id.toString() + "'," + this.toJson() + '}';
    }

}
