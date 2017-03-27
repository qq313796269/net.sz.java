package net.sz.game.engine.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 每分钟能产生 100亿 个id 0 ~ 9999999999，
 * <br>
 * 启动程序后，重复周期是 10 年,不保证多程序重复情况
 * <br>
 * 重启周期是1分钟
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongId0Util {

    protected static final SimpleDateFormat FORMATTER_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
    protected String DateFormatter = "";
    protected long StaticID = -1;
    protected String format = "%s%010d";
    protected long MAXID = 9999999999L;

    public LongId0Util() {
    }

    public long getId() {

        /*这一段相对而言，比较耗时*/
        String tmpyear = FORMATTER_DATE_FORMAT.format(new Date()).substring(3);

        long tmpid = 0;
        /* 这里的锁基本不耗时 */
        synchronized (this) {
            if (!DateFormatter.equals(tmpyear)) {
                StaticID = -1;
                DateFormatter = tmpyear;
            }
            ++StaticID;
            tmpid = StaticID;
        }

        String tmptime;
        if (tmpid > MAXID) {
            throw new UnsupportedOperationException("超过每分钟创建量 " + MAXID);
        }
        /*这一段相对而言，比较耗时*/
        tmptime = String.format(format, tmpyear, tmpid);
        /*这一段相对而言，比较耗时*/
        return Long.parseLong(tmptime);
    }

    public static void main(String[] args) throws Exception {
        LongId0Util idsUtil = new LongId0Util();
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
    }
}
