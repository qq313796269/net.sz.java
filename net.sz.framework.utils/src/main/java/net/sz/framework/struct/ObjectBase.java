package net.sz.framework.struct;

import java.io.Serializable;
import net.sz.framework.util.ObjectAttribute;
import net.sz.framework.utils.JsonUtil;
import net.sz.framework.utils.TimeUtil;

/**
 *
 *
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * phone 13882122019<br>
 * email 492794628@qq.com<br>
 */
public class ObjectBase implements Cloneable, Serializable {

    private static final long serialVersionUID = -8981799590065464386L;

    /**
     * 对应的唯一键ID
     */
    private long id;
    /**
     * 唯一的键值，用于同步器
     */
    private String SyncKey = null;
    /**
     * id
     */
    private String name;
    /**
     * 该对象创建的时间
     */
    private long createTime;
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 平台ID
     */
    private String platformId;
    /**
     * 渠道ID
     */
    private String channelId;
    /**
     * 服务器ID
     */
    private int serverId;
    /**
     * 服务器名称
     */
    private String servername;
    /**
     * 其他属性
     */
    private ObjectAttribute<Serializable> variables = null;
    /**
     * 不需要保存的序列化对象
     */
    private transient ObjectAttribute<Object> tmpOthers = null;

    public ObjectBase() {
        createTime = TimeUtil.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        this.SyncKey = "Sync_" + id;
    }

    /**
     *
     * @return
     */
    public final int getServerId() {
        return serverId;
    }

    public final void setServerId(int serverId) {
        this.serverId = serverId;
    }

    /**
     *
     * @return
     */
    public final String getGameId() {
        return gameId;
    }

    public final void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /**
     *
     * @return
     */
    public final String getPlatformId() {
        return platformId;
    }

    public final void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public final String getChannelId() {
        return channelId;
    }

    public final void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public final String getServername() {
        return servername;
    }

    public final void setServername(String servername) {
        this.servername = servername;
    }

    /**
     * 临时变量,不会被序列化的数据
     *
     * @return
     */
    public ObjectAttribute<Object> getTmpOthers() {
        if (tmpOthers == null) {
            tmpOthers = new ObjectAttribute<>();
        }
        return tmpOthers;
    }

    /**
     * ,不会被序列化的数据
     *
     * @param tmpOthers
     * @deprecated
     */
    @Deprecated
    public void setTmpOthers(ObjectAttribute<Object> tmpOthers) {
        this.tmpOthers = tmpOthers;
    }

    /**
     * 其他变量
     *
     * @return
     */
    public ObjectAttribute<Serializable> getVariables() {
        if (variables == null) {
            variables = new ObjectAttribute<>();
        }
        return variables;
    }

    @Deprecated
    public void setVariables(ObjectAttribute<Serializable> variables) {
        this.variables = variables;
    }

    public long getCreateTime() {
        return createTime;
    }

    @Deprecated
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getSyncKey() {
        if (SyncKey == null) {
            this.SyncKey = "Sync_" + id;
        }
        return SyncKey;
    }

    public void setSyncKey(String SyncKey) {
        this.SyncKey = SyncKey;
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
        return "id=" + id + ", name=" + name;
    }

    public String showString() {
        return "id=" + id + ", name=" + name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectBase other = (ObjectBase) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ObjectBase ob = null;
        ob = (ObjectBase) super.clone();

        if (this.tmpOthers == null) {
            ob.tmpOthers = null;
        } else {
            ob.tmpOthers = (ObjectAttribute<Object>) tmpOthers.clone();
        }

        if (this.variables == null) {
            ob.variables = null;
        } else {
            ob.variables = (ObjectAttribute<Serializable>) variables.clone();
        }

        return super.clone();
    }

}
