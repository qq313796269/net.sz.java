package net.sz.framework.sznio.tcp;

import com.google.protobuf.Message;
import net.sz.framework.sznio.NioSession;
import net.sz.framework.szthread.TaskModel;

public abstract class TcpHandler extends TaskModel {

//    private Person player;
    private NioSession session;       // 消息来源
    private Message message;         // 请求消息
    private long createTime;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public NioSession getSession() {
        return session;
    }

    public void setSession(NioSession session) {
        this.session = session;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

//    public Person getParameter() {
//        return this.player;
//    }
//
//    public void setParameter(Object parameter) {
//        this.player = (Person) parameter;
//    }
    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
