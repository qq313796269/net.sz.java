package net.sz.framework.sznio.tcp;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MessageBean {

    private long threadId;
    private long messageId;
    private Class<? extends TcpHandler> handler;
    private com.google.protobuf.Message.Builder message;

    public MessageBean(long threadId, long messageId, Class<? extends TcpHandler> handler, com.google.protobuf.Message.Builder message) {
        this.threadId = threadId;
        this.messageId = messageId;
        this.handler = handler;
        this.message = message;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public Class<? extends TcpHandler> getHandler() {
        return handler;
    }

    public void setHandler(Class<? extends TcpHandler> handler) {
        this.handler = handler;
    }

    public com.google.protobuf.Message.Builder getMessage() {
        return message;
    }

    public void setMessage(com.google.protobuf.Message.Builder message) {
        this.message = message;
    }
}
