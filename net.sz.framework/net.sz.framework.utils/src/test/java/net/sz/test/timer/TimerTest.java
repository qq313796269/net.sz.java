package net.sz.test.timer;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TimerTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
//        log.error(TimeUtil.verifyConfigTimeStr("[2017-2018][*][*][*][00:00-23:59]"));
//        log.error(TimeUtil.verifyDateTime("[2017-2018][*][*][*][00:00-23:59]"));
//
//        log.error(TimeUtil.verifyDateEndTime("[2017][12][18-20][*][00:00-23:59]"));

        log.error(TimeUtil.verifyDateEndTime("[2017][12][18-31][*][00:00-23:59]"));

        System.exit(0);
    }
}
