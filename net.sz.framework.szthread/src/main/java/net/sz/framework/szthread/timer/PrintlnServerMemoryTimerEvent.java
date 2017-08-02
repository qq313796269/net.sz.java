package net.sz.framework.szthread.timer;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.TimerTaskModel;
import net.sz.framework.utils.MemoryUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PrintlnServerMemoryTimerEvent extends TimerTaskModel {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -2044702822344935517L;

    public PrintlnServerMemoryTimerEvent() {
        super(-1, 10 * 60 * 1000);
    }

    @Override
    public void run() {
        log.error(MemoryUtil.getMemory());
    }

}
