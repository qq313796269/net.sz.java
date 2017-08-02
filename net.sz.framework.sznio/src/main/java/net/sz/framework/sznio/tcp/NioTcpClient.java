package net.sz.framework.sznio.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.sznio.NioSession;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.szthread.ThreadPool;
import net.sz.framework.struct.thread.ThreadType;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NioTcpClient extends NioSession {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        while (true) {
            new Thread(() -> {
                String requestData = "Actions speak louder than words!";
                String hostname = "127.0.0.1";
                int port = 1000;
                NioTcpClient nioTcpClient = new NioTcpClient(hostname, port, 1, new NioTcpServerHandler() {
                    @Override
                    public void handleAccept(NioSession session) {
                        log.debug(session);
                        try {
                            session.send(requestData.getBytes("utf-8"));
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void handleRead(NioSession session, int msgid, byte[] array) {
                        try {
                            log.debug(session + new String(array, "utf-8"));
                        } catch (Exception e) {
                            log.debug("ss", e);
                        }
                    }

                    @Override
                    public void handleClose(NioSession session, Exception ex) {
                    }
                });
                nioTcpClient.connect();

            }).start();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
    }

    private InetSocketAddress inetSocketAddress;
    private int threadCount;
    private NioTcpServerHandler handler;
    private Selector selectorRead = null;
    private static final Object SYNC_OBJECT = new Object();
    SzQueueThread thread = null;
    private static final ThreadGroup threadGroup = new ThreadGroup(BaseThreadRunnable.GlobalThreadGroup, "net sz nio client");

    public NioTcpClient(String hostname, int port, int threadCount, NioTcpServerHandler handler) {
        inetSocketAddress = new InetSocketAddress(hostname, port);
        this.handler = handler;
        this.threadCount = threadCount;

        this.thread = new SzQueueThread(ThreadType.Sys, threadGroup, "net sz nio client", threadCount) {

            @Override
            public void run() {

                ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
                while (thread.isRuning()) {
                    try {
                        if (selectorRead == null) {
                            synchronized (SYNC_OBJECT) {
                                SYNC_OBJECT.wait(20);
                            }
                            continue;
                        }

                        //获取选择器
                        int nKeys = selectorRead.select();
                        if (nKeys > 0) {
                            log.debug("PollingAccepRun 1 " + nKeys);
                            //拿到所有选择器类型
                            Set<SelectionKey> selectedKeys = selectorRead.selectedKeys();
                            Iterator<SelectionKey> it = selectedKeys.iterator();
                            while (it.hasNext()) {
                                SelectionKey key = it.next();
                                try {
                                    if (key.isReadable()) {
                                        log.debug("Readable");
                                        try {
                                            if (!NioTcpClient.this.isAvailable()) {
                                                return;
                                            }
                                            for (;;) {
                                                //当开始处理一次收取消息的时候很有可能当前数据大于你要读取的缓冲区
                                                //所有需要循环读取数据直到为0
                                                byteBuffer.clear();
                                                int readBytes = NioTcpClient.this.channel.read(byteBuffer);
                                                if (readBytes > 0) {
                                                    actionReadByte(NioTcpClient.this, byteBuffer, readBytes);
                                                    NioTcpClient.this.setLastTime(TimeUtil.currentTimeMillis());
                                                } else if (readBytes == 0) {
                                                    break;
                                                } else {
                                                    NioTcpClient.this.close();
                                                    return;
                                                }
                                            }
                                        } catch (Exception e) {
                                            log.debug("", e);
                                        }
                                        NioTcpClient.this.close();
                                    }
                                } catch (CancelledKeyException e) {
                                    //有可能该链接失效了，所有是取消状态
                                    key.cancel();
                                }
                                it.remove();
                            }
                        }
                    } catch (Exception e) {
                        NioTcpClient.this.close();
                    }
                }
            }
        };
    }

    void actionReadByte(NioSession nioSession, ByteBuffer byteBuffer, int readBytes) {
        byte[] ret = null;
        if (nioSession.getReadByteList() != null) {
            ret = new byte[nioSession.getReadByteList().length + readBytes];
            System.arraycopy(nioSession.getReadByteList(), 0, ret, 0, nioSession.getReadByteList().length);
            System.arraycopy(byteBuffer.array(), 0, ret, nioSession.getReadByteList().length, readBytes);
            nioSession.setReadByteList(null);
        } else {
            ret = new byte[readBytes];
            System.arraycopy(byteBuffer.array(), 0, ret, 0, readBytes);
        }

        ByteBuffer wrap = ByteBuffer.wrap(ret);
        int offset = 0;
        for (;;) {
            if (ret.length - offset > 8) {
                //够一个的组合
                int msglen = wrap.getInt();//消息长度
                offset += 4;
                if (ret.length - 4 >= msglen) {
                    int msgid = wrap.getInt();//读取消息id
                    //设置目前读取的长度
                    offset += 4;
                    //拷贝出来待用
                    byte[] array = new byte[msglen - 4];
                    System.arraycopy(ret, offset, array, 0, array.length);
                    handler.handleRead(nioSession, msgid, array);
                    //设置目前读取的长度
                    offset += array.length;
                    //设置目前读取的长度
                    wrap.position(offset);
                } else {
                    offset -= 4;
                    byte[] array = new byte[ret.length - (offset)];
                    System.arraycopy(ret, offset, array, 0, array.length);
                    nioSession.setReadByteList(array);
                    return;
                }
            } else {
                if (ret.length - offset > 0) {
                    byte[] array = new byte[ret.length - offset];
                    System.arraycopy(ret, offset, array, 0, array.length);
                    nioSession.setReadByteList(array);
                }
                return;
            }
        }
    }

    public void connect() {
        try {
            this.channel = SocketChannel.open(inetSocketAddress);
            this.channel.configureBlocking(false);
            channel.socket().setReceiveBufferSize(4000);
            channel.socket().setSendBufferSize(4000);
            this.selectorRead = Selector.open();
            this.channel.register(selectorRead, SelectionKey.OP_READ);
            this.handler.handleAccept(this);
        } catch (Exception e) {
            log.error("open " + inetSocketAddress.toString() + " 失败！！！！", e);
            this.close();
        }
    }

    @Override
    public void close() {
        super.close();
        try {
            selectorRead.close();
        } catch (Exception ex) {
        }
        thread.close();
    }

}
