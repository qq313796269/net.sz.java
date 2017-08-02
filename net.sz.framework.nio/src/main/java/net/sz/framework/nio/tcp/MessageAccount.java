package net.sz.framework.nio.tcp;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sz.framework.db.struct.AttColumn;
import net.sz.framework.db.struct.DataBaseModel;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.LongId0;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MessageAccount extends DataBaseModel {

    private static final SzLogger log = SzLogger.getLogger();
    private static LongId0 ids = new LongId0();
    protected static final SimpleDateFormat FORMATTER_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");

    @AttColumn(key = true)
    private long Id;
    private int messageId;
    private int messageCount;
    private String timer;

    public MessageAccount() {
    }

    public MessageAccount(int messageId, int messageCount) {
        this.Id = ids.getId();
        this.messageId = messageId;
        this.messageCount = messageCount;
        this.timer = FORMATTER_DATE_FORMAT.format(new Date());
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

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

}
