package net.sz.framework.sznio.tcp;

import com.google.protobuf.Message;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.szlog.SzLogger;

/**
 * 管理类
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MessagePool {

    private static final SzLogger log = SzLogger.getLogger();
    private static final ConcurrentHashMap<Integer, MessageBean> messageBeanMap = new ConcurrentHashMap<>();

    private MessagePool() {
    }

    public static ConcurrentHashMap<Integer, MessageBean> getMessageBeanMap() {
        return messageBeanMap;
    }

    public static void register(int messageId, Class<? extends Message> messageClass, Class<? extends TcpHandler> handler, long threadId, Message.Builder builder, int mapThreadQueue) {
        messageBeanMap.put(messageId, new MessageBean(threadId, messageId, handler, builder));
        log.error("注册消息：threadId：" + threadId + " messageId：" + messageId + " handler：" + handler + " message：" + builder);
    }
}
