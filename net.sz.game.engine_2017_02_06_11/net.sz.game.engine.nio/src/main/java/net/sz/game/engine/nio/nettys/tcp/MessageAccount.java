package net.sz.game.engine.nio.nettys.tcp;

import javax.persistence.Id;
import net.sz.game.engine.utils.GlobalUtil;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MessageAccount {

    private static final Logger log = Logger.getLogger(MessageAccount.class);

    @Id
    private long Id;
    private int messageId;
    private int messageCount;

    public MessageAccount(int messageId, int messageCount) {
        this.Id = GlobalUtil.getId();
        log.debug("MessageAccount：" + this.Id);
        this.messageId = messageId;
        this.messageCount = messageCount;
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

}
