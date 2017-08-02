package net.sz.framework.nio.tcp;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import net.sz.framework.szthread.TaskModel;

import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class NettyTcpHandler extends TaskModel {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -3530603754431065676L;

    private ChannelHandlerContext session;
    private Message message;
    /**
     * 用于转发消息
     */
    private String clientSocketId;

    public NettyTcpHandler() {

    }

    /**
     * 获取session的临时变量
     *
     * @param <T>
     * @param key
     * @param t
     * @return
     */
    public <T> T getSessionAttr(String key, Class<T> t) {
        return NettyCoder.getSessionAttr(session, key, t);
    }

    /**
     * 设置session中的临时变量
     *
     * @param key
     * @param value
     */
    public void setSessionAttr(String key, Object value) {
        NettyCoder.setSessionAttr(session, key, value);
    }

    /**
     * 删除附加到session中的临时变量
     *
     * @param key
     */
    public void removeSessionAttr(String key) {
        NettyCoder.removeSessionAttr(session, key);
    }

    /**
     * 用于转发消息
     *
     * @return
     */
    public String getClientSocketId() {
        return clientSocketId;
    }

    /**
     * 用于转发消息
     *
     * @param clientSocketId
     */
    public void setClientSocketId(String clientSocketId) {
        this.clientSocketId = clientSocketId;
    }

    /**
     * session对象
     *
     * @return
     */
    public ChannelHandlerContext getSession() {
        return session;
    }

    /**
     * session对象
     *
     * @param session
     */
    public void setSession(ChannelHandlerContext session) {
        this.session = session;
    }

    /**
     * 消息对象
     *
     * @return
     */
    public Message getMessage() {
        return message;
    }

    /**
     * 消息对象
     *
     * @param message
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public NettyTcpHandler clone() throws CloneNotSupportedException {
        NettyTcpHandler clone = null;
        clone = (NettyTcpHandler) super.clone();
        return clone;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

}
