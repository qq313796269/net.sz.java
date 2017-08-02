package net.sz.framework.util;

/**
 * 每秒钟能产生 100万 个id
 * <br>
 * 分100组（0-99），分别计算id,
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
public class LongId2 extends LongId1 {

    protected LongId2() {
    }

    /**
     *
     * @param IDEN 0 ~ 99
     */
    public LongId2(int IDEN) {
        this.format = "%s%02d";
        this.MAXID = 999999L;
      this.MAXHEAD = 1000000L;
        if (0 <= IDEN && IDEN < 100) {
            this.IDEN = IDEN;
            return;
        }
        throw new UnsupportedOperationException("参数 IDEN=" + IDEN + " 值不能超过 0 ~ 99");
    }

    public static void main(String[] args) throws Exception {
        System.err.println(Long.MAX_VALUE);
        LongId2 idsUtil = new LongId2(99);
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
    }
}
