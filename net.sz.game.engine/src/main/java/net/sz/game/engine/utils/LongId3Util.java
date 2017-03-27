package net.sz.game.engine.utils;

/**
 * 每分钟能产生 1000万 个id 0 ~ 9999999，
 * <br>
 * 分1000组（0-999），分别计算id,
 * <br>
 * 启动程序后，重复周期是 10 年
 * <br>
 * 上一次启动和下一次启动之间重启周期是1分钟
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongId3Util extends LongId2Util {

    protected LongId3Util() {
    }

    /**
     *
     * @param IDEN 0 ~ 999
     */
    public LongId3Util(int IDEN) {
        this.format = "%s%03d%07d";
        this.MAXID = 9999999;
        if (0 < IDEN && IDEN < 1000) {
            this.IDEN = IDEN;
            return;
        }
        throw new UnsupportedOperationException("参数 IDEN=" + IDEN + " 值不能超过 1 ~ 999");
    }

    public static void main(String[] args) throws Exception {
        LongId3Util idsUtil = new LongId3Util(1);
        System.err.println(idsUtil.getId());
    }
}
