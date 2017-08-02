package net.sz.framework.sznio.tcp;

import com.google.protobuf.Message;
import java.nio.channels.SelectionKey;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.sznio.NioSession;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.utils.TimeUtil;

/**
 * 收取消息处理protobuffer，消息请注册在messsagepool里面
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class NioTcpServerProbufferHandler implements NioTcpServerHandler {

    private static final SzLogger log = SzLogger.getLogger();

    /**
     * 处理{@link SelectionKey#OP_READ}事件
     *
     * @param session
     * @param msgId
     * @param array
     */
    @Override
    public void handleRead(NioSession session, int msgId, byte[] array) {
        MessageBean messageBean = MessagePool.getMessageBeanMap().get(msgId);
        if (messageBean != null) {
            try {
                BaseThreadRunnable threadModel = ThreadPool.getThread(messageBean.getThreadId());
                if (threadModel != null) {
                    Message.Builder mergeFrom = messageBean.getMessage().mergeFrom(array);
                    Message build = mergeFrom.build();
                    TcpHandler newInstance = messageBean.getHandler().newInstance();
                    newInstance.setSession(session);
                    newInstance.setMessage(build);
                    newInstance.setCreateTime(TimeUtil.currentTimeMillis());
                    threadModel.addTask(newInstance);
                }
            } catch (Throwable e) {
                log.error("工人<“" + Thread.currentThread().getName() + "”> 执行任务<" + msgId + "(“" + messageBean.getMessage().getClass().getName() + "”)> 遇到错误: ", e);
            }
        } else {
            log.error("尚未注册的消息 msgId: " + msgId);
        }
    }
}
