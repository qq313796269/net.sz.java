package net.sz.game.engine.nio.nettys.tcp;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import net.sz.game.engine.thread.TaskEvent;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class NettyTcpHandler extends TaskEvent {

    private static final Logger log = Logger.getLogger(NettyTcpHandler.class);
    private static final long serialVersionUID = -3530603754431065676L;
//    public static final AttributeKey<Boolean> BooleanKey = new AttributeKey<Boolean>("");
    protected ChannelHandlerContext session;
    protected Message message;
    /**
     * 用于转发消息
     */
    protected String clientSocketId;

    public NettyTcpHandler() {

    }

    public <T> T getSessionAttr(String key, Class<T> t) {
        return NettyCoder.getSessionAttr(session, key, t);
    }

    public void setSessionAttr(String key, Object value) {
        NettyCoder.setSessionAttr(session, key, value);
    }

    public void removeSessionAttr(String key) {
        NettyCoder.removeSessionAttr(session, key);
    }

    /**
     * 用于转发消息
     */
    public String getClientSocketId() {
        return clientSocketId;
    }

    /**
     * 用于转发消息
     */
    public void setClientSocketId(String clientSocketId) {
        this.clientSocketId = clientSocketId;
    }

    public ChannelHandlerContext getSession() {
        return session;
    }

    public void setSession(ChannelHandlerContext session) {
        this.session = session;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

}
