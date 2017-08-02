package net.sz;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TimerTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        log.error(TimeUtil.verifyDateTime("[*][*][*][*][04:00-04:00]"));
        System.exit(0);
    }
}
