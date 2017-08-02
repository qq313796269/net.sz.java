package net.sz.framework.sznio.tcp;

import java.nio.channels.SelectionKey;
import net.sz.framework.sznio.NioSession;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface TcpChannelHandler {

    /**
     * 简单处理器接口
     *
     * @author shirdrn
     */
    /**
     * 处理{@link SelectionKey#OP_ACCEPT}事件
     *
     * @param session
     */
    void handleAccept(NioSession session);

    /**
     *
     * @param session
     */
    void handleClose(NioSession session, Exception ex);

    /**
     * 处理{@link SelectionKey#OP_READ}事件
     *
     * @param session
     * @param array
     */
    void handleRead(NioSession session, byte[] array);

}
