package net.sz.framework.nio.tcp;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.db.sqlite.SqliteDaoImpl;
import net.sz.framework.db.thread.CUDThread;
import net.sz.framework.nio.NettyPool;
import net.sz.framework.nio.tcp.IHandler.IAfterHandler;
import net.sz.framework.nio.tcp.IHandler.IBeforeHandler;
import net.sz.framework.nio.tcp.IHandler.INotFoundMessageHandler;
import net.sz.framework.scripts.manager.ScriptManager;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.utils.ConvertTypeUtil;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.ObjectStreamUtil;
import net.sz.framework.utils.StringUtil;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyCoder {

    static private final SzLogger log = SzLogger.getLogger();

    static private final NettyCoder defaultCoder = new NettyCoder();

    static private final String REVE_ZREO_BYTE_COUNT_STRING = "REVE_ZREO_BYTE_COUNT_STRING";
    static private final String REVE_BYTES_STRING = "REVE_BYTES_STRING";
    static private final String REVE_SECOND_TIME_STRING = "REVE_SECOND_TIME_STRING";
    static private final String REVE_COUNT_STRING = "REVE_COUNT_STRING";
    static private final String REVE_BYTE_COUNT_STRING = "REVE_BYTE_COUNT_STRING";
    static private final String REVE_MAX_BYTE_COUNT_STRING = "REVE_MAX_BYTE_COUNT_STRING";

    static private final String SEND_ZREO_BYTE_COUNT_STRING = "SEND_ZREO_BYTE_COUNT_STRING";
    static private final String SEND_BYTES_STRING = "SEND_BYTES_STRING";
    static private final String SEND_SECOND_TIME_STRING = "SEND_SECOND_TIME_STRING";
    static private final String SEND_COUNT_STRING = "SEND_COUNT_STRING";
    static private final String SEND_BYTE_COUNT_STRING = "SEND_BYTE_COUNT_STRING";
    static private final String SEND_MAX_BYTE_COUNT_STRING = "SEND_MAX_BYTE_COUNT_STRING";

    static SqliteDaoImpl impl = null;
    static CUDThread cUDThread = null;

    /**
     * 通信消息记录sqlite数据库
     */
    static public void initMessageDb() {
        try {
            String gamesr_dbname = "/home/sqlitedata/message_" + GlobalUtil.getGameId() + "_" + GlobalUtil.getPlatformId() + "_" + GlobalUtil.getServerName() + "_" + GlobalUtil.getServerId() + ".dbs";
            impl = new SqliteDaoImpl(gamesr_dbname);
            impl.createTable(new MessageAccount());
            cUDThread = new CUDThread(impl, "MessageAccount");
        } catch (Throwable e) {
            log.error("", e);
        }
    }

    static public NettyCoder getDefaultCoder() {
        return defaultCoder;
    }

    /**
     * 空闲链接清理时间，默认 15秒
     */
    static public Long FreeSessionClearTime = 15 * 1000L;
    /**
     * 长时间未登录链接的清理时间，默认 30秒
     */
    static public Long LoginSessionClearTime = 30 * 1000L;
    /**
     * 每秒钟的消息量不能超过，默认 100条
     */
    static public int ReveCount = 100;
    /**
     * __SessionCreateTime__
     */
    static public final String SessionCreateTime = "__SessionCreateTime__";
    /**
     * __SessionLastTime__
     */
    static public final String SessionLastTime = "__SessionLastTime__";
    /**
     * __SessionLogin__
     */
    static public final String SessionLoginTime = "__SessionLogin__";

    static private final ConcurrentHashMap<Integer, MessageHandler> handlerMap = new ConcurrentHashMap<>(0);

    protected NettyCoder() {
    }

    static public INotFoundMessageHandler iNotFoundMessageHandler;

    /**
     * 设置未注册的消息处理器
     *
     * @return
     */
    static public INotFoundMessageHandler getiNotFoundMessageHandler() {
        return iNotFoundMessageHandler;
    }

    /**
     * 设置未注册的消息处理器
     *
     * @param iNotFoundMessageHandler
     */
    static public void setiNotFoundMessageHandler(INotFoundMessageHandler iNotFoundMessageHandler) {
        iNotFoundMessageHandler = iNotFoundMessageHandler;
    }

    /**
     * 获取链接的ip地址
     *
     * @param session
     * @return
     */
    static public String getIP(ChannelHandlerContext session) {
        try {
            InetSocketAddress insocket = (InetSocketAddress) session.channel().remoteAddress();
            return insocket.getAddress().getHostAddress().toLowerCase();
        } catch (Throwable e) {
            log.error("获取IP地址失败：：：", e);
        }
        return null;
    }

    /**
     * 获取链接的ip地址
     *
     * @param session
     * @return
     */
    static public String getIP(Channel session) {
        try {
            InetSocketAddress insocket = (InetSocketAddress) session.remoteAddress();
            return insocket.getAddress().getHostAddress().toLowerCase();
        } catch (Throwable e) {
            log.error("获取IP地址失败：：：", e);
        }
        return null;
    }

    /**
     * 获取链接的ip地址
     *
     * @param session
     * @return
     */
    static public String getChannelId(ChannelHandlerContext session) {
        try {
            return session.channel().id().asLongText();
        } catch (Throwable e) {
            log.error("获取链接唯一id失败：：：", e);
        }
        return null;
    }

    /**
     * 获取链接的ip地址
     *
     * @param session
     * @return
     */
    static public String getChannelId(Channel session) {
        try {
            return session.id().asLongText();
        } catch (Throwable e) {
            log.error("获取链接唯一id失败：：：", e);
        }
        return null;
    }

    /**
     * 获取连接信息设置的参数
     *
     * @param <T>
     * @param chc
     * @param key
     * @param t
     * @return
     */
    static public <T> T getSessionAttr(ChannelHandlerContext chc, String key, Class<T> t) {
        if (chc != null) {
            return getSessionAttr(chc.channel(), key, t);
        }
        return null;
    }

    /**
     * 获取连接信息设置的参数
     *
     * @param <T>
     * @param channel
     * @param key
     * @param t
     * @return
     */
    static public <T> T getSessionAttr(Channel channel, String key, Class<T> t) {
        if (channel != null) {
            AttributeKey<Object> valueOf = AttributeKey.valueOf(key);
            if (channel instanceof io.netty.util.DefaultAttributeMap) {
                io.netty.util.DefaultAttributeMap defaultAttributeMap = (io.netty.util.DefaultAttributeMap) channel;
                if (defaultAttributeMap.hasAttr(valueOf)) {
                    Object object = channel.attr(AttributeKey.valueOf(key)).get();
                    return (T) ConvertTypeUtil.changeType(object, t);
                }
            } else {
                log.error("ioSession.channel() is not instanceof io.netty.util.DefaultAttributeMap");
            }
        }
        return null;
    }

    /**
     * 设置链接参数信息
     *
     * @param chc
     * @param key
     * @param value
     */
    static public void setSessionAttr(ChannelHandlerContext chc, String key, Object value) {
        if (chc != null) {
            setSessionAttr(chc.channel(), key, value);
        }
    }

    /**
     * 设置链接参数信息
     *
     * @param channel
     * @param key
     * @param value
     */
    static public void setSessionAttr(Channel channel, String key, Object value) {
        if (channel != null) {
            channel.attr(AttributeKey.valueOf(key)).set(value);
        }
    }

    /**
     * 设置链接参数信息
     *
     * @param chc
     * @param key
     */
    static public void removeSessionAttr(ChannelHandlerContext chc, String key) {
        if (chc != null) {
            removeSessionAttr(chc.channel(), key);
        }
    }

    /**
     * 设置链接参数信息
     *
     * @param channel
     * @param key
     */
    static public void removeSessionAttr(Channel channel, String key) {
        if (channel != null) {
            channel.attr(AttributeKey.valueOf(key)).remove();
        }
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param build
     */
    public void send(ChannelHandlerContext chc, com.google.protobuf.Message.Builder build) {
        Message build1 = build.build();
        send(chc, build1);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param message
     */
    public void send(ChannelHandlerContext chc, com.google.protobuf.Message message) {
        int mid = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        send(chc, mid, toByteArray);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param mid
     * @param buf
     */
    public void send(ChannelHandlerContext chc, int mid, byte[] buf) {
        if (chc != null) {
            send(chc.channel(), mid, buf);
        }
    }

    /**
     * 发送消息包
     *
     * @param chc
     * @param buf 完整的消息包
     */
    public void send(ChannelHandlerContext chc, byte[] buf) {
        if (chc != null) {
            send(chc.channel(), buf);
        }
    }

    /**
     * 完整的消息包，并且不可以重复使用
     *
     * @param chc
     * @param byteBuf 完整的消息包，并且不可以重复使用
     */
    public void send(ChannelHandlerContext chc, ByteBuf byteBuf) {
        if (chc != null) {
            send(chc.channel(), byteBuf);
        }
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param build
     */
    public void send(Channel chc, com.google.protobuf.Message.Builder build) {
        Message build1 = build.build();
        send(chc, build1);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param message
     */
    public void send(Channel chc, com.google.protobuf.Message message) {
        int mid = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        send(chc, mid, toByteArray);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param mid
     * @param buf
     */
    public void send(Channel chc, int mid, byte[] buf) {
        ByteBuf byteBufFormBytes = getByteBufFormBytes(mid, buf);
        send(chc, byteBufFormBytes);
    }

    /**
     *
     * @param chc
     * @param buf 完整的消息包
     */
    public void send(Channel chc, byte[] buf) {
        ByteBuf byteBufFormBytes = getByteBufFormBytes(buf);
        send(chc, byteBufFormBytes);
    }

    /**
     *
     * @param chc
     * @param byteBuf 完整的消息包
     */
    public void send(Channel chc, ByteBuf byteBuf) {
        if (chc != null) {

            Long secondTime = getSessionAttr(chc, SEND_SECOND_TIME_STRING, Long.class);
            if (secondTime == null) {
                secondTime = 0l;
            }
            Integer sendCount = getSessionAttr(chc, SEND_COUNT_STRING, Integer.class);
            if (sendCount == null) {
                sendCount = 0;
            }
//            Integer byteCount = getSessionAttr(chc, SEND_BYTE_COUNT_STRING, Integer.class);
//            if (byteCount == null) {
//                byteCount = 0;
//            }
//            Double maxByteCount = getSessionAttr(chc, SEND_MAX_BYTE_COUNT_STRING, Double.class);
//            if (maxByteCount == null) {
//                maxByteCount = 0d;
//            }
//            byteCount += byteBuf.writerIndex();
            sendCount++;
            if (TimeUtil.currentTimeMillis() - secondTime > 1000L) {
                setSessionAttr(chc, SEND_SECOND_TIME_STRING, TimeUtil.currentTimeMillis());
                sendCount = 0;
//                maxByteCount += byteCount / 1024d;
//                byteCount = 0;
            }
            setSessionAttr(chc, SEND_COUNT_STRING, sendCount);
//            setSessionAttr(chc, SEND_BYTE_COUNT_STRING, byteCount);
//            setSessionAttr(chc, SEND_MAX_BYTE_COUNT_STRING, maxByteCount);
            setSessionAttr(chc, SessionLastTime, TimeUtil.currentTimeMillis());
            chc.writeAndFlush(byteBuf);
        }
    }

    /**
     * 完整的消息包
     *
     * @param builder
     * @return
     */
    public ByteBuf getByteBufFormMessage(com.google.protobuf.Message.Builder builder) {
        return getByteBufFormMessage(builder.build());
    }

    /**
     * 完整的消息包
     *
     * @param message
     * @return
     */
    public ByteBuf getByteBufFormMessage(com.google.protobuf.Message message) {
        int mid = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        return getByteBufFormBytes(mid, toByteArray);
    }

    /**
     * 完整的消息包
     *
     * @param message
     * @return
     */
    public byte[] getBytesFormMessage(com.google.protobuf.Message message) {
        int mid = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        return getBytesFormBytes(mid, toByteArray);
    }

    /**
     * 完整的消息包
     *
     * @param message
     * @return
     */
    public byte[] getBytesFormMessage(com.google.protobuf.Message.Builder message) {
        return getBytesFormMessage(message.build());
    }

    /**
     * 获取消息id
     *
     * @param message
     * @return
     */
    static public int getMessageId(com.google.protobuf.Message.Builder message) {
        return getMessageId(message.build());
    }

    /**
     * 获取消息id
     *
     * @param message
     * @return
     */
    static public int getMessageId(com.google.protobuf.Message message) {
        com.google.protobuf.Descriptors.EnumValueDescriptor field
                = (com.google.protobuf.Descriptors.EnumValueDescriptor) message.getField(message.getDescriptorForType().findFieldByNumber(1));
        return field.getNumber();
    }

    /**
     * 完整的消息包
     *
     * @param mid
     * @param buf
     * @return
     */
    public ByteBuf getByteBufFormBytes(int mid, byte[] buf) {
        ByteBuf buffercontent = Unpooled.buffer();
        if (buf == null) {
            buffercontent.writeInt(4).writeInt(mid);
        } else {
            buffercontent.writeInt(buf.length + 4).writeInt(mid).writeBytes(buf);
        }

        if (log.isDebugEnabled()) {
            log.debug("发送消息ID：" + mid + " 长度：" + buffercontent.writerIndex());
        }

        try {
            if (cUDThread != null) {
                cUDThread.insert_Sync(new MessageAccount(mid, buffercontent.writerIndex()));
            }
        } catch (Exception e) {
            log.error("消息记录日志", e);
        }

        return buffercontent;
    }

    /**
     * 完整的消息包
     *
     * @param mid
     * @param bytes
     * @return
     */
    public byte[] getBytesFormBytes(int mid, byte[] bytes) {
        ByteBuf buffercontent = getByteBufFormBytes(mid, bytes);
        byte[] byteBuffer = new byte[buffercontent.writerIndex()];
        buffercontent.getBytes(buffercontent.readerIndex(), byteBuffer);
        return byteBuffer;
    }

    /**
     * 仅仅包装
     *
     * @param bytes
     * @return
     */
    public ByteBuf getByteBufFormBytes(byte[] bytes) {
        ByteBuf buffercontent = Unpooled.buffer();
        if (bytes != null) {
            buffercontent.writeBytes(bytes);
        }
        return buffercontent;
    }

    /**
     * 仅仅包装
     *
     * @param buf
     * @return
     */
    public byte[] getByteBufFormBytes(ByteBuf buf) {
        byte[] byteBuffer = new byte[buf.writerIndex()];
        buf.getBytes(buf.readerIndex(), byteBuffer);
        return byteBuffer;
    }

    /**
     * 重载上一次预留的字节数
     *
     * @param bytes
     * @param inputBuf
     * @return
     */
    private ByteBuf bytesAction(ByteBuf bytes, ByteBuf inputBuf) {
        if (bytes != null) {
            bytes.writeBytes(inputBuf);
            return bytes;
        } else {
            return inputBuf;
        }
    }

    /**
     * 留存无法读取的byte等待下一次接受的数据包
     *
     * @param intputBuf
     * @return
     */
    private ByteBuf bytesAction(ByteBuf intputBuf) {
        ByteBuf bytes = Unpooled.buffer();
        bytes.writeBytes(intputBuf);
        return bytes;
    }

    /**
     * 解析
     *
     * @param chc
     * @param inputBuf
     */
    public void decode0(ChannelHandlerContext chc, ByteBuf inputBuf) {

        Byte ZreoByteCount = getSessionAttr(chc, REVE_ZREO_BYTE_COUNT_STRING, Byte.class);
        if (ZreoByteCount == null) {
            ZreoByteCount = 0;
        }

        Long secondTime = getSessionAttr(chc, REVE_SECOND_TIME_STRING, Long.class);
        if (secondTime == null) {
            secondTime = 0l;
        }

        Integer reveCount = getSessionAttr(chc, REVE_COUNT_STRING, Integer.class);
        if (reveCount == null) {
            reveCount = 0;
        }

        ByteBuf bytes = getSessionAttr(chc, REVE_BYTES_STRING, ByteBuf.class);

        if (inputBuf.readableBytes() > 0) {
            setSessionAttr(chc, SessionLastTime, TimeUtil.currentTimeMillis());
//            byteCount += inputBuf.readableBytes();
            ZreoByteCount = 0;
            //重新组装字节数组
            ByteBuf buffercontent = bytesAction(bytes, inputBuf);
            for (;;) {
                if (decode1(chc, inputBuf)) {
                    reveCount++;
                } else {
                    break;
                }
            }

            if (buffercontent.readableBytes() > 0) {
                /*缓存预留的字节*/
                ByteBuf bytesAction = bytesAction(buffercontent);
                setSessionAttr(chc, REVE_BYTES_STRING, bytesAction);
            }

            if (TimeUtil.currentTimeMillis() - secondTime >= 1000L) {
                setSessionAttr(chc, REVE_SECOND_TIME_STRING, TimeUtil.currentTimeMillis());
                reveCount = 0;
//                maxByteCount += byteCount / 1024d;
//                if (log.isDebugEnabled()) {
//                    log.debug("每一秒钟 接收 包量：" + reveCount + " 数据量：" + (byteCount) + " B 当前在线总字节量：" + maxByteCount + " KB");
//                }
//                byteCount = 0;
            }

//                NettyCoder0.setSessionAttr(chc, REVE_BYTE_COUNT_STRING, byteCount);
//                NettyCoder0.setSessionAttr(chc, REVE_MAX_BYTE_COUNT_STRING, maxByteCount);
            setSessionAttr(chc, REVE_COUNT_STRING, reveCount);

            if (reveCount > (ReveCount * 0.75)) {
                if (log.isDebugEnabled()) {
                    log.debug("收取消息过于频繁----" + reveCount);
                }
            }
            if (reveCount > ReveCount) {
                if (log.isDebugEnabled()) {
                    log.debug("收取消息过于频繁----" + reveCount);
                }
                NettyPool.getInstance().closeSession(chc, "收取消息过于频繁----" + reveCount);
            }
        } else {
            ZreoByteCount++;
            if (ZreoByteCount >= 3) {
                if (log.isDebugEnabled()) {
                    //todo 空包处理 考虑连续三次空包，断开链接
                    log.debug("decode 空包处理 连续三次空包");
                }
                NettyPool.getInstance().closeSession(chc, "decode 空包处理 连续三次空包");
                return;
            }
            setSessionAttr(chc, REVE_ZREO_BYTE_COUNT_STRING, ZreoByteCount);
        }
    }

    /**
     * 读取一个消息包处理
     *
     * @param chc
     * @param inputBuf
     * @return
     */
    protected boolean decode1(ChannelHandlerContext chc, ByteBuf inputBuf) {
        //读取 消息长度（int）和消息ID（int） 需要 8 个字节
        if (inputBuf.readableBytes() >= 8) {
            //读取消息长度
            int len = inputBuf.readInt();
            if (inputBuf.readableBytes() >= len) {
                /*读取消息ID*/
                int mid = inputBuf.readInt();
                byte[] byteBuffer = new byte[len - 4];
                /*读取报文类容*/
                inputBuf.getBytes(inputBuf.readerIndex(), byteBuffer);
                /*设置读取进度*/
                inputBuf.readerIndex(inputBuf.readerIndex() + (len - 4));
                /*处理消息--理论上是丢出去了的*/
                actionMessage(chc, mid, byteBuffer);
                return true;
            } else {
                /*重新设置读取进度*/
                inputBuf.readerIndex(inputBuf.readerIndex() - 4);
            }
        }
        return false;
    }

    /**
     * protobuf 辅助
     *
     * @param byteString
     * @return
     */
    static public byte[] getBytesFormByteString(ByteString byteString) {
        byte[] bytes = null;
        try {
            ByteBuffer asReadOnlyByteBuffer = byteString.asReadOnlyByteBuffer();
            if (asReadOnlyByteBuffer.capacity() > 0) {
                int capacity = asReadOnlyByteBuffer.capacity();
                bytes = new byte[capacity];
                asReadOnlyByteBuffer.get(bytes);
            }
        } catch (Throwable e) {
            log.error("反序列化错误", e);
        }
        return bytes;
    }

    static public <T> T getBytesFormByteString(ByteString byteString, Class<T> clazz) {
        byte[] bytes = getBytesFormByteString(byteString);
        if (bytes != null) {
            try {
                return ObjectStreamUtil.toObject(clazz, bytes);
            } catch (Throwable e) {
                log.error("反序列化错误", e);
            }
        }
        return null;
    }

    /**
     * 把对象转化成bytestring对象
     *
     * @param obj
     * @return
     */
    static public ByteString getByteStringFormObject(Object obj) {
        return getByteStringFormBytes(ObjectStreamUtil.toBytes(obj));
    }

    /**
     * 把对象转化成bytestring对象
     *
     * @param bytes
     * @return
     */
    static public ByteString getByteStringFormBytes(byte[] bytes) {
        return ByteString.copyFrom(bytes);
    }

    /**
     * 获取消息注册信息
     *
     * @param mid
     * @return
     */
    public MessageHandler getMessageHandler(int mid) {
        return getHandlerMap().get(mid);
    }

    /**
     * 获取消息的注册处理器
     *
     * @param ctx
     * @param mid
     * @param bytes
     * @return
     */
    public NettyTcpHandler getNettyTcpHandler(ChannelHandlerContext ctx, int mid, byte[] bytes) {
        MessageHandler messageHandler = getMessageHandler(mid);
        return getNettyTcpHandler(ctx, messageHandler, mid, bytes);
    }

    /**
     * 解析消息，并且反封装消息处理器
     *
     * @param ctx
     * @param _msghandler
     * @param mid
     * @param bytes
     * @return
     */
    static public NettyTcpHandler getNettyTcpHandler(ChannelHandlerContext ctx, MessageHandler _msghandler, int mid, byte[] bytes) {
        NettyTcpHandler newInstance = null;
        if (_msghandler == null) {
            if (getiNotFoundMessageHandler() != null) {
                getiNotFoundMessageHandler().notFoundHandler(ctx, mid, bytes);
                log.error("尚未注册消息：" + mid + " 转发到 INotFoundMessageHandler 接口处理程序中;");
            } else {
                log.error("尚未注册消息：" + mid + " 且未找到 INotFoundMessageHandler 接口处理程序;");
            }
        } else {
            try {
                Message.Builder parseFrom = _msghandler.getMessage().clone().mergeFrom(bytes);
                Message message = parseFrom.build();
//                Descriptors.FieldDescriptor fd = message.getDescriptorForType().findFieldByName("_syncId");
//                if (fd != null) {
//                    Long syncId = (Long) message.getField(fd);
//                    if (syncId != null) {
//                        if (log.isDebugEnabled()) {
//                            log.debug("收到同步消息{" + syncId + "}");
//                        }
//                        SyncRequestResponse<Message> response = new SyncRequestResponse<>();
//                        response.setSyncId(syncId);
//                        response.setResponse(message);
//                        SyncRequestFuture.received(response);
//                    }
//                } else
                {
                    newInstance = (NettyTcpHandler) _msghandler.getHandler().clone();
                    // 设置网络Session对应的Player
                    newInstance.setClientSocketId(getChannelId(ctx));
                    newInstance.setSession(ctx);
                    newInstance.setMessage(message);
                }
            } catch (Throwable e) {
                log.error("工人<“" + Thread.currentThread().getName() + "”> 执行任务<" + mid + "(“" + _msghandler.getMessage().getClass().getName() + "”)> 遇到错误: ", e);
                newInstance = null;
            }
        }
        return newInstance;
    }

    /**
     * 处理消息，并且派发到对应线程
     *
     * @param ctx
     * @param mid
     * @param bytes
     * @param objs
     */
    @Deprecated
    public void actionMessage(ChannelHandlerContext ctx, int mid, byte[] bytes, Object... objs) {
        if (log.isDebugEnabled()) {
            log.debug("收到消息：" + mid + " 消息长度：" + bytes.length);

        }

        ArrayList<IBeforeHandler> evts = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IBeforeHandler.class);

        ArrayList<IAfterHandler> evt1s = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IAfterHandler.class);

        lab_continue_IBeforeHandler:
        {
            if (evts != null && !evts.isEmpty()) {
                for (int j = 0; j < evts.size(); j++) {
                    IBeforeHandler get = evts.get(j);
                    if (get.beforeHandler(ctx, mid, bytes)) {
                        /* 跳出循环 */
                        break lab_continue_IBeforeHandler;
                    }
                }
            }

            MessageHandler _msghandler = getMessageHandler(mid);
            NettyTcpHandler nettyTcpHandler = getNettyTcpHandler(ctx, _msghandler, mid, bytes);
            actionMessage(ctx, _msghandler, nettyTcpHandler);

        }

        lab_continue_IAfterHandler:
        {
            if (evt1s != null && !evt1s.isEmpty()) {
                for (int j = 0; j < evt1s.size(); j++) {
                    IAfterHandler get = evt1s.get(j);
                    if (get.afterHandler(ctx, mid, bytes)) {
                        /* 跳出循环 */
                        break lab_continue_IAfterHandler;
                    }
                }
            }
        }

    }

    /**
     * 处理消息，并且派发到对应线程
     *
     * @param ctx
     * @param _msghandler
     * @param nettyTcpHandler
     */
    static public void actionMessage(ChannelHandlerContext ctx, MessageHandler _msghandler, NettyTcpHandler nettyTcpHandler) {
        if (_msghandler != null && nettyTcpHandler != null) {
            if (StringUtil.isNullOrEmpty(_msghandler.getMsgQueue())) {
                /*分发消息*/
                ThreadPool.addTask(_msghandler.getThreadId(), nettyTcpHandler);
            } else {
                /*有队列的分发消息*/
                ThreadPool.addTask(_msghandler.getThreadId(), _msghandler.getMsgQueue(), nettyTcpHandler);
            }
        }
    }

    public ConcurrentHashMap<Integer, MessageHandler> getHandlerMap() {
        return handlerMap;
    }

    /**
     * 注册默认消息 defaultCoder
     *
     * @param builder
     * @param threadId
     * @param handler
     * @param msgQueue
     */
    static public void register(com.google.protobuf.Message.Builder builder, long threadId, NettyTcpHandler handler, String msgQueue) {
        register(getMessageId(builder), builder, threadId, handler, msgQueue);
    }

    /**
     * 注册默认消息 defaultCoder
     *
     * @param messageId
     * @param builder
     * @param threadId
     * @param handler
     * @param msgQueue
     */
    static public void register(int messageId, com.google.protobuf.Message.Builder builder, long threadId, NettyTcpHandler handler, String msgQueue) {
        defaultCoder.register0(messageId, builder, threadId, handler, msgQueue);
    }

    /**
     * 注册消息
     *
     * @param builder
     * @param threadId
     * @param handler
     * @param msgQueue
     */
    public void register0(com.google.protobuf.Message.Builder builder, long threadId, NettyTcpHandler handler, String msgQueue) {
        register0(getMessageId(builder), builder, threadId, handler, msgQueue);
    }

    /**
     * 注册消息
     *
     * @param messageId
     * @param builder
     * @param threadId
     * @param handler
     * @param msgQueue
     */
    public void register0(int messageId, com.google.protobuf.Message.Builder builder, long threadId, NettyTcpHandler handler, String msgQueue) {
        MessageHandler msgold = handlerMap.get(messageId);
        MessageHandler messageHandler = new MessageHandler(messageId, threadId, handler, builder, msgQueue);
        /* TODO 验证线程模型 */
        if (builder == null) {
            log.error("MessagePool.register 异常! messageClass 不能为null!" + messageHandler, new Exception());
            if (GlobalUtil.SERVERSTARTEND) {
                return;
            } else {
                System.exit(1);
            }
        }

        if (handler == null) {
            log.error("MessagePool.register 异常! handler 不能为null!" + messageHandler, new Exception());
            if (GlobalUtil.SERVERSTARTEND) {
                return;
            } else {
                System.exit(1);
            }
        }

        // TODO 验证线程模型
        if (threadId != 0 && ThreadPool.getThread(threadId) == null) {
            log.error("无法找到线程模型:" + threadId + "对应的处理器.请确保服务器启动时先初始化线程模型对象!" + messageHandler, new Exception());
            if (GlobalUtil.SERVERSTARTEND) {
                return;
            } else {
                System.exit(1);
            }
        }
        if (msgold != null) {
            if (!msgold.getHandler().getClass().getName().equals(handler.getClass().getName())) {
                log.error("已注册消息：" + msgold + " 新注册的重复消息：" + messageHandler, new Exception());
                if (GlobalUtil.SERVERSTARTEND) {
                    return;
                } else {
                    System.exit(1);
                }
            }
        }
        handlerMap.put(messageId, messageHandler);
        log.error("注册消息：" + messageHandler);
    }
}
