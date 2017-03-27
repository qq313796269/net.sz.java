package net.sz.game.engine.nio.nettys.tcp;

import javax.persistence.Id;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.engine.utils.LongId0Util;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MessageAccount {

    private static SzLogger log = SzLogger.getLogger();
    private static LongId0Util ids = new LongId0Util();

    @Id
    private long Id;
    private int messageId;
    private int messageCount;
    private long timer;

    public MessageAccount(int messageId, int messageCount, long timer) {
        this.Id = ids.getId();
        if (log.isDebugEnabled()) {
            log.debug("MessageAccount：" + this.Id);
        }
        this.messageId = messageId;
        this.messageCount = messageCount;
        this.timer = timer;
    }

    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

}
