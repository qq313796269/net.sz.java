package net.sz.framework.nio.tcp;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.struct.thread.ThreadType;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.SzThread;
import net.sz.framework.szthread.SzThreadModel;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class ServerSessionThread extends SzThread {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -3046738293158506425L;

    public static void main(String[] args) {
        ConcurrentLinkedQueue<ByteBuf> byteBufs1 = new ConcurrentLinkedQueue<>();
        ByteBuf poll = byteBufs1.poll();
        log.error(poll);
    }

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(BaseThreadRunnable.GlobalThreadGroup.getParent(), "Server-Session-Thread-Group");

    public final ConcurrentLinkedQueue<ByteBuf> byteBufs = new ConcurrentLinkedQueue<>();

    public final ArrayList<Channel> channels = new ArrayList<>();

    NettyCoder nettyCoder;

    public ServerSessionThread(NettyCoder nettyCoder, int threadcount) {
        super(ThreadType.Sys, THREAD_GROUP, "Server-Session-Thread", threadcount);
        this.nettyCoder = nettyCoder;
    }

    /**
     * 增加发送消息，并且唤醒当前线程
     *
     * @param channel
     * @return
     */
    public boolean addSession(Channel channel) {
        boolean add = false;
        synchronized (channels) {
            for (int i = 0; i < channels.size(); i++) {
                Channel channel1 = channels.get(i);
                if (channel1.id().asLongText().equals(channel.id().asLongText())) {
                    add = false;
                    return add;
                }
            }
            add = channels.add(channel);
            if (add) {
                synchronized (getTaskQueue()) {
                    getTaskQueue().notify();
                }
            }
            return add;
        }
    }

    /**
     * 增加发送消息，并且唤醒当前线程
     *
     * @param channel
     * @return
     */
    public boolean removeSession(Channel channel) {
        synchronized (channels) {
            boolean remove = channels.remove(channel);
            if (remove) {
                synchronized (getTaskQueue()) {
                    getTaskQueue().notify();
                }
            }
            return remove;
        }
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param message
     * @return
     */
    public boolean addMessage(Message.Builder message) {
        return addMessage(nettyCoder.getByteBufFormMessage(message));
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param message
     * @return
     */
    public boolean addMessage(Message message) {
        return addMessage(nettyCoder.getByteBufFormMessage(message));
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param msgid
     * @param bytebuf
     * @return
     */
    public boolean addMessage(int msgid, byte[] bytebuf) {
        return addMessage(nettyCoder.getByteBufFormBytes(msgid, bytebuf));
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param bytebuf
     * @return
     */
    public boolean addMessage(byte[] bytebuf) {
        return addMessage(nettyCoder.getByteBufFormBytes(bytebuf));
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param byteBuf
     * @return
     */
    public boolean addMessage(ByteBuf byteBuf) {
        if (byteBufs.size() < 600000) {
            boolean add = byteBufs.add(byteBuf);
            if (add) {
                synchronized (getTaskQueue()) {
                    getTaskQueue().notify();
                }
            }
            return add;
        }
        return false;
    }

    /**
     * 同步等待消息
     *
     * @return
     */
    ByteBuf getByteBuf() {
        while (this.isRuning()) {
            ByteBuf poll = byteBufs.poll();
            if (poll == null) {
                try {
                    /* 任务队列为空，则等待有新任务加入从而被唤醒 */
                    synchronized (getTaskQueue()) {
                        getTaskQueue().wait();
                    }
                } catch (InterruptedException ie) {
                    log.error(ie);
                }
            } else {
                return poll;
            }
        }
        return null;
    }

    /* 循环获取通信 */
    int channelId = 0;

    /**
     * 同步获取通信
     *
     * @return
     */
    Channel getChannel() {
        while (this.isRuning()) {
            Channel poll = null;
            synchronized (channels) {
                if (channels.size() > 0) {
                    poll = channels.get(channelId);
                    channelId++;
                    if (channelId >= channels.size()) {
                        channelId = 0;
                    }
                }
            }
            if (poll == null || !poll.isOpen()) {
                try {
                    /* 任务队列为空，则等待有新任务加入从而被唤醒 */
                    synchronized (getTaskQueue()) {
                        getTaskQueue().wait();
                    }
                } catch (InterruptedException ie) {
                    log.error(ie);
                }
            } else {
                return poll;
            }
        }
        return null;
    }

    @Override
    public void run() {
        SzThreadModel currentThread = (SzThreadModel) Thread.currentThread();
        while (this.isRuning()) {
            try {
                /* 取出任务执行 */
                ByteBuf byteBuf = getByteBuf();
                /* 阻塞等待 */
                Channel channel = getChannel();
                /* 之所以获取连接对象是因为保证能发送消息 阻塞等待 */
                if (byteBuf != null && channel != null && channel.isOpen()) {
                    try {
                        nettyCoder.send(channel, byteBuf);
                    } catch (Throwable e) {
                        log.error("工人<“" + currentThread.getName() + "”> 发送消息 遇到错误", e);
                        addMessage(byteBuf);
                    }
                } else {
                    log.error("工人<“" + currentThread.getName() + "”> 发送消息 空值 byteBuf=" + (byteBuf == null));
                    addMessage(byteBuf);
                }
            } catch (Throwable e) {
                log.error("工人<“" + currentThread.getName() + "”> 发送消息 遇到错误", e);
            }
        }
        log.error("线程结束, 工人<“" + currentThread.getName() + "”>退出");
    }
}
