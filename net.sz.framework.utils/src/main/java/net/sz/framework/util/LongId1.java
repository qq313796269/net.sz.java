package net.sz.framework.util;

import java.util.Date;
import net.sz.framework.utils.TimeUtil;

/**
 * 每秒钟能产生 1000万 个id
 * <br>
 * yMMddHHmmppxxxxxxxx
 * <br>
 * 分10组（0-9），分别计算id,
 * <br>
 * 启动程序后，重复周期是 10 年,
 * <br>
 * 上一次启动和下一次启动之间重启周期是 1 秒钟
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongId1 extends LongId0 {

    protected int IDEN;

    protected LongId1() {
    }

    /**
     *
     * @param IDEN 0 ~ 9
     */
    public LongId1(int IDEN) {
        this.format = "%s%01d";
        this.MAXID = 9999999L;
        this.MAXHEAD = 10000000L;
        if (0 <= IDEN && IDEN < 10) {
            this.IDEN = IDEN;
            return;
        }
        throw new UnsupportedOperationException("参数 IDEN=" + IDEN + " 值不能超过 0 ~ 9");
    }

    @Override
    public long getId() {
        long now = TimeUtil.currentTimeMillis() / 1000;
        long tmpid = 0;
        /* 这里的锁基本不耗时 */
        synchronized (this) {
            if (now != upTime) {
                upTime = now;
                /*这一段相对而言，比较耗时*/
                String tmpyear = FORMATTER_DATE_FORMAT.format(new Date()).substring(3);
                idHead = Long.parseLong(String.format(this.format, tmpyear, this.IDEN)) * MAXHEAD;
                id = -1;
            }
            ++id;
            if (id > MAXID) {
                throw new UnsupportedOperationException("超过每秒钟创建量 " + MAXID);
            }
            tmpid = idHead + id;
        }
        return tmpid;
    }

    public static void main(String[] args) throws Exception {
        System.err.println(Long.MAX_VALUE);
        LongId1 idsUtil = new LongId1(9);
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
    }
}
