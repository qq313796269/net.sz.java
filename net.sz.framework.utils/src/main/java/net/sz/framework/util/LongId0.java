package net.sz.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sz.framework.utils.TimeUtil;

/**
 * 每秒钟能产生 1亿 个ID
 * <br>
 * 启动程序后，重复周期是 10 年,不保证多程序重复情况
 * <br>
 * 重启周期是 1 秒钟
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongId0 {

    protected static final SimpleDateFormat FORMATTER_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    protected String format = "0";
    protected long id = -1;
    protected long MAXID = 99999999L;
    protected long MAXHEAD = 100000000L;
    protected long upTime = 0;
    protected long idHead = 0;

    public long getId() {
        long now = TimeUtil.currentTimeMillis() / 1000;
        long tmpid = 0;
        /* 这里的锁基本不耗时 */
        synchronized (this) {
            if (now != upTime) {
                upTime = now;
                /*这一段相对而言，比较耗时*/
                String tmpyear = FORMATTER_DATE_FORMAT.format(new Date()).substring(3);
                idHead = Long.parseLong(tmpyear) * MAXHEAD;
                id = -1;
            }
            ++id;
            if (id > MAXID) {
                throw new UnsupportedOperationException("超过每分钟创建量 " + MAXID);
            }
            tmpid = idHead + id;
        }
        return tmpid;
    }

    public static void main(String[] args) throws Exception {
        System.err.println(Long.MAX_VALUE);
        LongId0 idsUtil = new LongId0();
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
    }
}
