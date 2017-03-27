package net.sz.game.engine.nio.nettys.tcp;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.sz.game.engine.db.SqliteDaoImpl;
import net.sz.game.engine.db.thread.CUDThread;
import net.sz.game.engine.nio.nettys.NettyPool;
import net.sz.game.engine.nio.nettys.tcp.IHandler.IAfterHandler;
import net.sz.game.engine.nio.nettys.tcp.IHandler.IBeforeHandler;
import net.sz.game.engine.nio.nettys.tcp.IHandler.INotFoundMessageHandler;
import net.sz.game.engine.scripts.manager.ScriptManager;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.utils.GlobalUtil;
import net.sz.game.engine.utils.ObjectStreamUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyCoder {

    private static SzLogger log = SzLogger.getLogger();

    private static final String REVE_ZREO_BYTE_COUNT_STRING = "REVE_ZREO_BYTE_COUNT_STRING";
    private static final String REVE_BYTES_STRING = "REVE_BYTES_STRING";
    private static final String REVE_SECOND_TIME_STRING = "REVE_SECOND_TIME_STRING";
    private static final String REVE_COUNT_STRING = "REVE_COUNT_STRING";
    private static final String REVE_BYTE_COUNT_STRING = "REVE_BYTE_COUNT_STRING";
    private static final String REVE_MAX_BYTE_COUNT_STRING = "REVE_MAX_BYTE_COUNT_STRING";

    private static final String SEND_ZREO_BYTE_COUNT_STRING = "SEND_ZREO_BYTE_COUNT_STRING";
    private static final String SEND_BYTES_STRING = "SEND_BYTES_STRING";
    private static final String SEND_SECOND_TIME_STRING = "SEND_SECOND_TIME_STRING";
    private static final String SEND_COUNT_STRING = "SEND_COUNT_STRING";
    private static final String SEND_BYTE_COUNT_STRING = "SEND_BYTE_COUNT_STRING";
    private static final String SEND_MAX_BYTE_COUNT_STRING = "SEND_MAX_BYTE_COUNT_STRING";

    static SqliteDaoImpl impl;
    static CUDThread cUDThread;

    static {
        try {
            String gamesr_dbname = "/home/sqlitedata/message_" + GlobalUtil.GameID + "_" + GlobalUtil.PlatformId + "_" + GlobalUtil.getServerID() + ".dbs";
            impl = new SqliteDaoImpl("", gamesr_dbname, "", "");
            impl.createTable(MessageAccount.class);
            cUDThread = new CUDThread(impl, "MessageAccount", 1);
        } catch (Throwable e) {
            log.error("", e);
        }
    }

    /**
     * 清理链接的时间
     */
    public static Long ClearSessionTime = 2 * 60 * 1000L;
    public static int ReveCount = 100;
    /**
     * __SessionCreateTime__
     */
    public static final String SessionCreateTime = "__SessionCreateTime__";
    /**
     * __SessionLastTime__
     */
    public static final String SessionLastTime = "__SessionLastTime__";
    /**
     * __SessionLogin__
     */
    public static final String SessionLoginTime = "__SessionLogin__";

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
     * @param chc
     * @param key
     */
    static public void removeSessionAttr(ChannelHandlerContext chc, String key) {
        if (chc != null) {
            removeSessionAttr(chc.channel(), key);
        }
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
                    return (T) channel.attr(AttributeKey.valueOf(key)).get();
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
    static public void send(ChannelHandlerContext chc, com.google.protobuf.Message.Builder build) {
        Message build1 = build.build();
        send(chc, build1);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param message
     */
    static public void send(ChannelHandlerContext chc, com.google.protobuf.Message message) {
        int msgID = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        send(chc, msgID, toByteArray);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param msgid
     * @param buf
     */
    static public void send(ChannelHandlerContext chc, int msgid, byte[] buf) {
        if (chc != null) {
            send(chc.channel(), msgid, buf);
        }
    }

    /**
     * 发送消息包
     *
     * @param chc
     * @param buf 完整的消息包
     */
    static public void send(ChannelHandlerContext chc, byte[] buf) {
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
    static void send(ChannelHandlerContext chc, ByteBuf byteBuf) {
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
    static public void send(Channel chc, com.google.protobuf.Message.Builder build) {
        Message build1 = build.build();
        send(chc, build1);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param message
     */
    static public void send(Channel chc, com.google.protobuf.Message message) {
        int msgID = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        send(chc, msgID, toByteArray);
    }

    /**
     * 会封装完整的消息包
     *
     * @param chc
     * @param msgid
     * @param buf
     */
    static public void send(Channel chc, int msgid, byte[] buf) {
        ByteBuf byteBufFormBytes = getByteBufFormBytes(msgid, buf);
        send(chc, byteBufFormBytes);
    }

    /**
     *
     * @param chc
     * @param buf 完整的消息包
     */
    static public void send(Channel chc, byte[] buf) {
        ByteBuf byteBufFormBytes = getByteBufFormBytes(buf);
        send(chc, byteBufFormBytes);
    }

    /**
     *
     * @param chc
     * @param byteBuf 完整的消息包
     */
    static void send(Channel chc, ByteBuf byteBuf) {
        if (chc != null) {
            setSessionAttr(chc, SessionLastTime, System.currentTimeMillis());
            Long secondTime = getSessionAttr(chc, SEND_SECOND_TIME_STRING, Long.class);
            if (secondTime == null) {
                secondTime = 0l;
            }
            Integer reveCount = getSessionAttr(chc, SEND_COUNT_STRING, Integer.class);
            if (reveCount == null) {
                reveCount = 0;
            }
            Integer byteCount = getSessionAttr(chc, SEND_BYTE_COUNT_STRING, Integer.class);
            if (byteCount == null) {
                byteCount = 0;
            }
            Double maxByteCount = getSessionAttr(chc, SEND_MAX_BYTE_COUNT_STRING, Double.class);
            if (maxByteCount == null) {
                maxByteCount = 0d;
            }
            byteCount += byteBuf.writerIndex();
            reveCount++;
            if (System.currentTimeMillis() - secondTime > 1000L) {
                setSessionAttr(chc, SEND_SECOND_TIME_STRING, System.currentTimeMillis());
                maxByteCount += byteCount / 1024d;
                if (log.isDebugEnabled()) {
                    log.debug("每一秒钟 发送 包量：" + reveCount + " 数据量：" + (byteCount) + " B 当前在线总字节量：" + maxByteCount + " KB");
                }
                reveCount = 0;
                byteCount = 0;
            }
            setSessionAttr(chc, SEND_COUNT_STRING, reveCount);
            setSessionAttr(chc, SEND_BYTE_COUNT_STRING, byteCount);
            setSessionAttr(chc, SEND_MAX_BYTE_COUNT_STRING, maxByteCount);
            chc.writeAndFlush(byteBuf);
        }
    }

    /**
     * 完整的消息包
     *
     * @param builder
     * @return
     */
    static public ByteBuf getByteBufFormMessage(com.google.protobuf.Message.Builder builder) {
        return getByteBufFormMessage(builder.build());
    }

    /**
     * 完整的消息包
     *
     * @param message
     * @return
     */
    static public ByteBuf getByteBufFormMessage(com.google.protobuf.Message message) {
        int msgID = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        return getByteBufFormBytes(msgID, toByteArray);
    }

    /**
     * 完整的消息包
     *
     * @param message
     * @return
     */
    static public byte[] getBytesFormMessage(com.google.protobuf.Message message) {
        int msgID = getMessageId(message);
        byte[] toByteArray = message.toByteArray();
        return getBytesFormBytes(msgID, toByteArray);
    }

    /**
     * 完整的消息包
     *
     * @param message
     * @return
     */
    static public byte[] getBytesFormMessage(com.google.protobuf.Message.Builder message) {
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
     * @param msgid
     * @param buf
     * @return
     */
    static public ByteBuf getByteBufFormBytes(int msgid, byte[] buf) {
        ByteBuf buffercontent = Unpooled.buffer();
        if (buf == null) {
            buffercontent.writeInt(4).writeInt(msgid);
        } else {
            buffercontent.writeInt(buf.length + 4).writeInt(msgid).writeBytes(buf);
        }
        if (log.isDebugEnabled()) {
//            if (115206 == msgid) {
//                log.debug("发送消息ID：" + msgid + " 长度：" + buffercontent.writerIndex(), new Exception());
//            } else {
            log.debug("发送消息ID：" + msgid + " 长度：" + buffercontent.writerIndex());
//            }
        }

        cUDThread.insert_Sync(new MessageAccount(msgid, buffercontent.writerIndex(), System.currentTimeMillis()));

        return buffercontent;
    }

    /**
     * 完整的消息包
     *
     * @param msgid
     * @param buf
     * @return
     */
    static public byte[] getBytesFormBytes(int msgid, byte[] buf) {
        ByteBuf buffercontent = getByteBufFormBytes(msgid, buf);
        byte[] byteBuffer = new byte[buffercontent.writerIndex()];
        buffercontent.getBytes(buffercontent.readerIndex(), byteBuffer);
        return byteBuffer;
    }

    /**
     * 仅仅包装
     *
     * @param buf
     * @return
     */
    static public ByteBuf getByteBufFormBytes(byte[] buf) {
        ByteBuf buffercontent = Unpooled.buffer();
        if (buf != null) {
            buffercontent.writeBytes(buf);
        }
        return buffercontent;
    }

    /**
     * 重载上一次预留的字节数
     *
     * @param bytes
     * @param inputBuf
     * @return
     */
    static public ByteBuf bytesAction(ByteBuf bytes, ByteBuf inputBuf) {
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
    static public ByteBuf bytesAction(ByteBuf intputBuf) {
        ByteBuf bytes = Unpooled.buffer();
        bytes.writeBytes(intputBuf);
        return bytes;
    }

    static public List<Object> decode(ChannelHandlerContext chc, ByteBuf inputBuf) {

        List<Object> outputMessage = new ArrayList<>();

        Byte ZreoByteCount = NettyCoder.getSessionAttr(chc, REVE_ZREO_BYTE_COUNT_STRING, Byte.class);
        if (ZreoByteCount == null) {
            ZreoByteCount = 0;
        }
        Long secondTime = NettyCoder.getSessionAttr(chc, REVE_SECOND_TIME_STRING, Long.class);
        if (secondTime == null) {
            secondTime = 0l;
        }
        Integer reveCount = NettyCoder.getSessionAttr(chc, REVE_COUNT_STRING, Integer.class);
        if (reveCount == null) {
            reveCount = 0;
        }
        Integer byteCount = NettyCoder.getSessionAttr(chc, REVE_BYTE_COUNT_STRING, Integer.class);
        if (byteCount == null) {
            byteCount = 0;
        }
        Double maxByteCount = NettyCoder.getSessionAttr(chc, REVE_MAX_BYTE_COUNT_STRING, Double.class);
        if (maxByteCount == null) {
            maxByteCount = 0d;
        }
        ByteBuf bytes = NettyCoder.getSessionAttr(chc, REVE_BYTES_STRING, ByteBuf.class);

        if (inputBuf.readableBytes() > 0) {
            NettyCoder.setSessionAttr(chc, SessionLastTime, System.currentTimeMillis());
            byteCount += inputBuf.readableBytes();
            ZreoByteCount = 0;
            //重新组装字节数组
            ByteBuf buffercontent = bytesAction(bytes, inputBuf);
            List<NettyMessageBean> megsList = new ArrayList<>(0);
            for (;;) {
                //读取 消息长度（short）和消息ID（int） 需要 8 个字节
                if (buffercontent.readableBytes() >= 8) {
                    //读取消息长度
                    int len = buffercontent.readInt();
                    if (buffercontent.readableBytes() >= len) {
                        int messageid = buffercontent.readInt();///读取消息ID
                        byte[] byteBuffer = new byte[len - 4];
                        buffercontent.getBytes(buffercontent.readerIndex(), byteBuffer);
                        buffercontent.readerIndex(buffercontent.readerIndex() + (len - 4));
                        megsList.add(new NettyMessageBean(messageid, byteBuffer));

//                        ByteBuf buf = buffercontent.readBytes(len - 4);//读取可用字节数;
//                        megsList.add(new NettyMessageBean(messageid, buf.array()));
                    } else {
                        //重新设置读取进度
                        buffercontent.readerIndex(buffercontent.readerIndex() - 4);
                        break;
                    }
                } else {
                    break;
                }
            }
            if (buffercontent.readableBytes() > 0) {
                ///缓存预留的字节
                ByteBuf bytesAction = bytesAction(buffercontent);
                NettyCoder.setSessionAttr(chc, REVE_BYTES_STRING, bytesAction);
            }

            if (!megsList.isEmpty()) {
                if (System.currentTimeMillis() - secondTime < 1000L) {
                    reveCount += megsList.size();
                } else {
                    NettyCoder.setSessionAttr(chc, REVE_SECOND_TIME_STRING, System.currentTimeMillis());
                    maxByteCount += byteCount / 1024d;
                    if (log.isDebugEnabled()) {
                        log.debug("每一秒钟 接收 包量：" + reveCount + " 数据量：" + (byteCount) + " B 当前在线总字节量：" + maxByteCount + " KB");
                    }
                    reveCount = 0;
                    byteCount = 0;
                }
                NettyCoder.setSessionAttr(chc, REVE_COUNT_STRING, reveCount);
                NettyCoder.setSessionAttr(chc, REVE_BYTE_COUNT_STRING, byteCount);
                NettyCoder.setSessionAttr(chc, REVE_MAX_BYTE_COUNT_STRING, maxByteCount);
                if (reveCount > (ReveCount * 0.75)) {
                    if (log.isDebugEnabled()) {
                        log.debug("发送消息过于频繁----" + reveCount);
                    }
                }
                if (reveCount > ReveCount) {
                    if (log.isDebugEnabled()) {
                        log.debug("发送消息过于频繁----" + reveCount);
                    }
                    NettyPool.getInstance().closeSession(chc, "发送消息过于频繁----" + reveCount);
                } else {
                    outputMessage.addAll(megsList);
                }
            }
        } else {
            ZreoByteCount++;
            if (ZreoByteCount >= 3) {
                if (log.isDebugEnabled()) {
                    //todo 空包处理 考虑连续三次空包，断开链接
                    log.debug("decode 空包处理 连续三次空包");
                }
                NettyPool.getInstance().closeSession(chc, "decode 空包处理 连续三次空包");
            }
        }
        NettyCoder.setSessionAttr(chc, REVE_ZREO_BYTE_COUNT_STRING, ZreoByteCount);
        return outputMessage;
    }

    static public byte[] getBytesFormByteString(ByteString byteString) {
        byte[] array = null;
        try {
            ByteBuffer asReadOnlyByteBuffer = byteString.asReadOnlyByteBuffer();
            if (asReadOnlyByteBuffer.capacity() > 0) {
                int capacity = asReadOnlyByteBuffer.capacity();
                array = new byte[capacity];
                asReadOnlyByteBuffer.get(array);
            }
        } catch (Throwable e) {
            log.error("反序列化错误", e);
        }
        return array;
    }

    static public <T> T getBytesFormByteString(ByteString byteString, Class<T> clazz) {
        byte[] array = NettyCoder.getBytesFormByteString(byteString);
        if (array != null) {
            try {
                return ObjectStreamUtil.toObject(clazz, array);
            } catch (Throwable e) {
                log.error("反序列化错误", e);
            }
        }
        return null;
    }

    static public ByteString getByteStringFormObject(Object obj) {
        return getByteStringFormBytes(ObjectStreamUtil.toBytes(obj));
    }

    static public ByteString getByteStringFormBytes(byte[] bytes) {
        return ByteString.copyFrom(bytes);
    }

    static public MessageHandler getMessageHandler(int msgId) {
        return NettyPool.getInstance().getHandlerMap().get(msgId);
    }

    static public NettyTcpHandler getNettyTcpHandler(ChannelHandlerContext ctx, int msgId, byte[] bytebuf) {
        MessageHandler messageHandler = getMessageHandler(msgId);
        return getNettyTcpHandler(ctx, messageHandler, msgId, bytebuf);
    }

    /**
     *
     * @param ctx
     * @param _msghandler
     * @param msgId
     * @param bytebuf
     * @return
     */
    static public NettyTcpHandler getNettyTcpHandler(ChannelHandlerContext ctx, MessageHandler _msghandler, int msgId, byte[] bytebuf) {
        if (_msghandler == null) {
            {
                ArrayList<INotFoundMessageHandler> evts = ScriptManager.getInstance().getBaseScriptEntry().getEvts(INotFoundMessageHandler.class);
                for (int i = 0; i < evts.size(); i++) {
                    INotFoundMessageHandler iNotFoundMessageHandler = evts.get(i);
                    if (iNotFoundMessageHandler.notFoundHandler(ctx, msgId, bytebuf)) {
                        return null;
                    }
                }
            }
            log.error("尚未注册消息：" + msgId);
        } else {
            try {
                Message.Builder parseFrom = _msghandler.getMessage().clone().mergeFrom(bytebuf);
                Message message = parseFrom.build();
                Descriptors.FieldDescriptor fd = message.getDescriptorForType().findFieldByName("_syncId");
                if (fd != null) {
                    Long syncId = (Long) message.getField(fd);
                    if (syncId != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("收到同步消息{" + syncId + "}");
                        }
                        SyncRequestResponse<Message> response = new SyncRequestResponse<>();
                        response.setSyncId(syncId);
                        response.setResponse(message);
                        SyncRequestFuture.received(response);
                    }
                } else {
                    NettyTcpHandler newInstance = (NettyTcpHandler) _msghandler.getHandler().clone();
                    // 设置网络Session对应的Player
                    newInstance.setSession(ctx);
                    newInstance.setMessage(message);
                    return newInstance;
                }
            } catch (Throwable e) {
                log.error("工人<“" + Thread.currentThread().getName() + "”> 执行任务<" + msgId + "(“" + _msghandler.getMessage().getClass().getName() + "”)> 遇到错误: ", e);
            }
        }
        return null;
    }

    /**
     * 处理消息，并且派发到对应线程
     *
     * @param ctx
     * @param msgId
     * @param bytebuf
     * @param objs
     */
    @Deprecated
    static public void actionMessage(ChannelHandlerContext ctx, int msgId, byte[] bytebuf, Object... objs) {
        if (log.isDebugEnabled()) {
            log.debug("收到消息：" + msgId + " 消息长度：" + bytebuf.length);
        }

        ArrayList<IBeforeHandler> evts = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IBeforeHandler.class);

        ArrayList<IAfterHandler> evt1s = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IAfterHandler.class);

        lab_continue_IBeforeHandler:

        {
            if (evts != null && !evts.isEmpty()) {
                for (int j = 0; j < evts.size(); j++) {
                    IBeforeHandler get = evts.get(j);
                    if (get.beforeHandler(ctx, msgId, bytebuf)) {
                        /* 跳出循环 */
                        break lab_continue_IBeforeHandler;
                    }
                }
            }
            MessageHandler _msghandler = getMessageHandler(msgId);
            NettyTcpHandler nettyTcpHandler = getNettyTcpHandler(ctx, _msghandler, msgId, bytebuf);
            actionMessage(ctx, _msghandler, nettyTcpHandler);
        }
        lab_continue_IAfterHandler:
        {
            if (evt1s != null && !evt1s.isEmpty()) {
                for (int j = 0; j < evt1s.size(); j++) {
                    IAfterHandler get = evt1s.get(j);
                    if (get.beforeHandler(ctx, msgId, bytebuf)) {
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
            ThreadPool.addTask(_msghandler.getThreadId(), nettyTcpHandler);
        }
    }

}
