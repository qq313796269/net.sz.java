package net.sz.game.engine.nio.nettys.tcp;


import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class NettyMessageBean {

    private static SzLogger log = SzLogger.getLogger();

    private int msgid;
    private byte[] msgbuffer;

    public NettyMessageBean(int msgid, byte[] msgbuffer) {
        this.msgid = msgid;
        this.msgbuffer = msgbuffer;
    }

    public int getMsgid() {
        return msgid;
    }

    public byte[] getMsgbuffer() {
        return msgbuffer;
    }

    public void setMsgbuffer(byte[] msgbuffer) {
        this.msgbuffer = msgbuffer;
    }

    @Override
    public String toString() {
        return "消息ID<" + msgid + '>';
    }

}
