package net.sz.game.engine.utils;

/**
 * 每分钟能产生 一亿 个id 0 ~ 99999999，
 * <br>
 * 分100组（0-99），分别计算id,
 * <br>
 * 启动程序后，重复周期是 10 年,
 * <br>
 * 上一次启动和下一次启动之间重启周期是1分钟
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongId2Util extends LongId1Util {

    protected LongId2Util() {
    }

    /**
     *
     * @param IDEN 0 ~ 99
     */
    public LongId2Util(int IDEN) {
        this.format = "%s%02d%08d";
        this.MAXID = 99999999;
        if (0 <= IDEN && IDEN < 100) {
            this.IDEN = IDEN;
            return;
        }
        throw new UnsupportedOperationException("参数 IDEN=" + IDEN + " 值不能超过 0 ~ 99");
    }

    public static void main(String[] args) throws Exception {
        LongId2Util idsUtil = new LongId2Util(1);
        System.err.println(idsUtil.getId());
    }
}
