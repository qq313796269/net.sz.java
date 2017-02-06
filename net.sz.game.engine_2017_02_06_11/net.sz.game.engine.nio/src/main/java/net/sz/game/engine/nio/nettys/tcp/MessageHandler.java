package net.sz.game.engine.nio.nettys.tcp;

import com.google.protobuf.Message;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MessageHandler {

    private long threadId;
    private int messageId;
    private int msgQueue;
    private Class<? extends NettyTcpHandler> handler;
    private com.google.protobuf.Message.Builder message;

    public MessageHandler() {
    }

    public MessageHandler(int messageId, long threadId, Class<? extends NettyTcpHandler> handler, com.google.protobuf.Message.Builder message, int msgQueue) {
        this.threadId = threadId;
        this.messageId = messageId;
        this.handler = handler;
        this.message = message;
        this.msgQueue = msgQueue;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMsgQueue() {
        return msgQueue;
    }

    public void setMsgQueue(int msgQueue) {
        this.msgQueue = msgQueue;
    }

    public Class<? extends NettyTcpHandler> getHandler() {
        return handler;
    }

    public void setHandler(Class<? extends NettyTcpHandler> handler) {
        this.handler = handler;
    }

    public Message.Builder getMessage() {
        return message;
    }

    public void setMessage(Message.Builder message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "threadId=" + threadId + ", messageId=" + messageId + ", msgQueue=" + msgQueue + ", handler=" + handler + ", message=" + message;
    }

}
