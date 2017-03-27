package net.sz.game.engine.thread;


import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class NoticeThreadException {

    private static SzLogger log = SzLogger.getLogger();

    public abstract void noticeThreadException(Throwable e);

}
