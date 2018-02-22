package net.sz.test;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.MoveUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        double aTan360 = MoveUtil.getATan360(31,22,35,22);
        log.error(aTan360);
    }
}
