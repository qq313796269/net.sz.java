package net.sz.game.engine.utils;

import java.util.Date;

/**
 * 每分钟能产生 10亿 个id 0 ~ 999999999，
 * <br>
 * yMMddHHmmppxxxxxxxx
 * <br>
 * 分10组（0-9），分别计算id,
 * <br>
 * 启动程序后，重复周期是 10 年,
 * <br>
 * 上一次启动和下一次启动之间重启周期是1分钟
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongId1Util extends LongId0Util {

    protected int IDEN;

    protected LongId1Util() {
    }

    /**
     *
     * @param IDEN 0 ~ 9
     */
    public LongId1Util(int IDEN) {
        this.format = "%s%01d%09d";
        this.MAXID = 999999999;
        if (0 <= IDEN && IDEN < 10) {
            this.IDEN = IDEN;
            return;
        }
        throw new UnsupportedOperationException("参数 IDEN=" + IDEN + " 值不能超过 0 ~ 9");
    }

    @Override
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
        if (tmpid > this.MAXID) {
            throw new UnsupportedOperationException("超过每分钟创建量 " + this.MAXID);
        }
        /*这一段相对而言，比较耗时*/
        tmptime = String.format(this.format, tmpyear, this.IDEN, tmpid);
        /*这一段相对而言，比较耗时*/
        return Long.parseLong(tmptime);
    }

    public static void main(String[] args) throws Exception {
        LongId1Util idsUtil = new LongId1Util(1);
        System.err.println(idsUtil.getId());
    }
}
