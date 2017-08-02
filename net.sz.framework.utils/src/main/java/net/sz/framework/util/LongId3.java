package net.sz.framework.util;

/**
 * 每秒钟能产生 10万 个id
 * <br>
 * 分1000组（0-999），分别计算id,
 * <br>
 * 启动程序后，重复周期是 10 年
 * <br>
 * 上一次启动和下一次启动之间重启周期是 1 秒钟
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongId3 extends LongId2 {

    protected LongId3() {
    }

    /**
     * yMMddHHmmssxxxx
     * @param IDEN 0 ~ 999
     */
    public LongId3(int IDEN) {
        this.format = "%s%03d";
        this.MAXID = 99999L;
      this.MAXHEAD = 100000L;
        if (0 < IDEN && IDEN < 1000) {
            this.IDEN = IDEN;
            return;
        }
        throw new UnsupportedOperationException("参数 IDEN=" + IDEN + " 值不能超过 1 ~ 999");
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Long.MAX_VALUE);
        LongId3 idsUtil = new LongId3(1);
        System.out.println(idsUtil.getId());
        System.out.println(idsUtil.getId());
        System.out.println(idsUtil.getId());
    }
}
