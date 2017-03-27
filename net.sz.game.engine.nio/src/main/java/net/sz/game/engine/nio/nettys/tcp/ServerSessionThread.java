package net.sz.game.engine.nio.nettys.tcp;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.SzThread;
import net.sz.game.engine.thread.ThreadType;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ServerSessionThread extends SzThread {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        ConcurrentLinkedQueue<ByteBuf> byteBufs1 = new ConcurrentLinkedQueue<>();
        ByteBuf poll = byteBufs1.poll();
        log.error(poll);
    }

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(ThreadPool.GlobalThreadGroup.getParent(), "Server-Session-Thread-Group");

    public final ConcurrentLinkedQueue<ByteBuf> byteBufs = new ConcurrentLinkedQueue<>();

    public final ArrayList<Channel> channels = new ArrayList<>();

    public ServerSessionThread(int threadcount) {
        super(ThreadType.Sys, THREAD_GROUP, "Server-Session-Thread", threadcount);

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
                synchronized (taskQueue) {
                    taskQueue.notifyAll();
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
                synchronized (taskQueue) {
                    taskQueue.notifyAll();
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
        return addMessage(NettyCoder.getByteBufFormMessage(message));
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param message
     * @return
     */
    public boolean addMessage(Message message) {
        return addMessage(NettyCoder.getByteBufFormMessage(message));
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param msgid
     * @param bytebuf
     * @return
     */
    public boolean addMessage(int msgid, byte[] bytebuf) {
        return addMessage(NettyCoder.getByteBufFormBytes(msgid, bytebuf));
    }

    /**
     * 增加消息，并且唤醒当前线程
     *
     * @param bytebuf
     * @return
     */
    public boolean addMessage(byte[] bytebuf) {
        return addMessage(NettyCoder.getByteBufFormBytes(bytebuf));
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
                synchronized (taskQueue) {
                    taskQueue.notifyAll();
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
        while (runing) {
            ByteBuf poll = byteBufs.poll();
            if (poll == null) {
                try {
                    /* 任务队列为空，则等待有新任务加入从而被唤醒 */
                    synchronized (taskQueue) {
                        taskQueue.wait();
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
        while (runing) {
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
                    synchronized (taskQueue) {
                        taskQueue.wait();
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
        ThreadModel currentThread = (ThreadModel) Thread.currentThread();
        while (runing) {
            try {
                /* 阻塞等待 */
                Channel channel = getChannel();
                if (channel != null && channel.isOpen()) {
                    /* 之所以获取连接对象是因为保证能发送消息 阻塞等待 */
                    ByteBuf byteBuf = getByteBuf();
                    /* 取出任务执行 */
                    if (byteBuf != null && channel.isOpen()) {
                        try {
                            NettyCoder.send(channel, byteBuf);
                        } catch (Throwable e) {
                            log.error("工人<“" + currentThread.getName() + "”> 发送消息 遇到错误", e);
                            addMessage(byteBuf);
                        }
                    } else {
                        log.error("工人<“" + currentThread.getName() + "”> 发送消息 空值 byteBuf=" + (byteBuf == null));
                        addMessage(byteBuf);
                    }
                } else {
                    log.error("工人<“" + currentThread.getName() + "”> 发送消息 空值 channel=" + (channel == null));
                }
            } catch (Throwable e) {
                log.error("工人<“" + currentThread.getName() + "”> 发送消息 遇到错误", e);
            }
        }
        log.error("线程结束, 工人<“" + currentThread.getName() + "”>退出");
    }

}
