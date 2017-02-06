package net.sz.game.engine.thread.timer;

import net.sz.game.engine.thread.TimerTaskEvent;
import net.sz.game.engine.utils.MemoryUtil;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PrintlnServerMemoryTimerEvent extends TimerTaskEvent {

    private static final Logger log = Logger.getLogger(PrintlnServerMemoryTimerEvent.class);

    public PrintlnServerMemoryTimerEvent() {
        super(-1, 10 * 60 * 1000);
    }

    @Override
    public void run() {
        log.error(MemoryUtil.getMemory());
    }

}
