package net.sz.game.engine.thread.timer;

import net.sz.game.engine.thread.TimerTaskModel;
import net.sz.game.engine.utils.MemoryUtil;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PrintlnServerMemoryTimerEvent extends TimerTaskModel {

    private static SzLogger log = SzLogger.getLogger();

    public PrintlnServerMemoryTimerEvent() {
        super(-1, 10 * 60 * 1000);
    }

    @Override
    public void run() {
        log.error(MemoryUtil.getMemory());
    }

}
