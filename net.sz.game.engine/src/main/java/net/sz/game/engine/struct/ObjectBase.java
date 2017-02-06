package net.sz.game.engine.struct;

import java.io.Serializable;

/**
 *
 *
 * author 失足程序员<br>
 * phone 13882122019<br>
 * email 492794628@qq.com<br>
 */
public class ObjectBase implements Serializable {

    private static final long serialVersionUID = -8981799590065464386L;

    public transient final Object OBJ_SYAN_OBJECT = new Object();

    /* 其他属性 */
    protected ObjectAttribute<Serializable> variables = null;
    /* 不需要保存的序列化对象 */
    protected transient ObjectAttribute<Object> tmpOthers = null;
    /* 对应的唯一键ID */
    protected long id;
    protected int serverId;
    //创建游戏id
    private int gameId;
    //创建平台id
    private int PlatformID;
    // 服务器名称
    private String servername;
    //id
    protected String name;
    //该对象创建的时间
    protected long createTime;

    public ObjectBase() {
//        id = GlobalUtil.getUUIDToLong();
        createTime = System.currentTimeMillis();
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
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPlatformID() {
        return PlatformID;
    }

    public void setPlatformID(int PlatformID) {
        this.PlatformID = PlatformID;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public long getCreateTime() {
        return createTime;
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

    @Deprecated
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
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

}
