package net.sz.framework.sznio;

import com.google.protobuf.Message;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import net.sz.framework.util.ObjectAttribute;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.sznio.http.NioHttpRequest;
import net.sz.framework.sznio.http.NioHttpServer;
import net.sz.framework.sznio.tcp.NioTcpServer;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NioSession {

    private static final SzLogger log = SzLogger.getLogger();



    protected long id;

    /**
     * 是否可用
     */
    protected volatile boolean available;
    protected byte[] readByteList = null;
    protected long createTime;
    protected long lastTime;
    protected String ip;
    protected int port;
    protected SocketChannel channel;
    //一秒钟接收消息数量
    protected int secondMsgCount;
    //当前秒
    protected int second = 0;
    //每一秒收取消息的总量
    protected int secondBytesCount = 0;
    //累计收取消息总量
    protected long bytesCount = 0;
    protected ObjectAttribute attribute = new ObjectAttribute();


    public NioSession() {
        this.id = GlobalUtil.getUUIDToLong();
        this.lastTime = TimeUtil.currentTimeMillis();
        this.createTime = TimeUtil.currentTimeMillis();
        available = true;
    }

    public NioSession(SocketChannel channel) {
        this();
        this.channel = channel;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public boolean isAvailable() {
        return available;
    }

    public ObjectAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(ObjectAttribute attribute) {
        this.attribute = attribute;
    }

    public byte[] getReadByteList() {
        return readByteList;
    }

    public void setReadByteList(byte[] readByteList) {
        this.readByteList = readByteList;
    }

    public long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /**
     * tcp or udp
     *
     * @param buffer
     */
    public void send(byte[] buffer) {
        send(0, buffer);
    }

    public int getSecondMsgCount() {
        return secondMsgCount;
    }

    public void setSecondMsgCount(int secondMsgCount) {
        this.secondMsgCount = secondMsgCount;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getSecondBytesCount() {
        return secondBytesCount;
    }

    public void setSecondBytesCount(int secondBytesCount) {
        this.secondBytesCount = secondBytesCount;
    }

    public long getBytesCount() {
        return bytesCount;
    }

    public void setBytesCount(long bytesCount) {
        this.bytesCount = bytesCount;
    }

    /**
     * tcp or udp
     *
     * @param msgId
     * @param buffer
     */
    public void send(int msgId, byte[] buffer) {
        ByteBuffer buffercontent = ByteBuffer.allocate(buffer.length + 8);
        buffercontent.putInt(buffer.length + 4);
        buffercontent.putInt(msgId);
        buffercontent.put(buffer);
        send(buffercontent);
    }

    /**
     * tcp or udp
     *
     * @param buffer
     */
    public void send(ByteBuffer buffer) {
        try {
            this.channel.write(ByteBuffer.wrap(buffer.array()));
            buffer = null;
            this.lastTime = TimeUtil.currentTimeMillis();
        } catch (Exception e) {
            if (!(this instanceof NioHttpRequest)) {
                log.error("", e);
            }
        }
    }

    /**
     * tcp or udp
     *
     * @param msg
     */
    public void send(com.google.protobuf.Message.Builder msg) {
        Message build = msg.build();
        com.google.protobuf.Descriptors.EnumValueDescriptor field = (com.google.protobuf.Descriptors.EnumValueDescriptor) build.getField(build.getDescriptorForType().findFieldByNumber(1));
        int msgID = field.getNumber();
        byte[] toByteArray = build.toByteArray();
        this.send(msgID, toByteArray);
    }

    /**
     *
     */
    public void close() {
        if (available) {
            this.available = false;
            if (this instanceof NioSession) {
                NioTcpServer.sessionMap.remove(this.getId());
            } else if (this instanceof NioHttpRequest) {
                NioHttpServer.httpRequestMap.remove(this.getId());
            }
            try {
                this.channel.close();
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public String toString() {
        return "NioSession{" + "id=" + id + ", available=" + available + ", ip=" + ip + ", port=" + port + '}';
    }

}
