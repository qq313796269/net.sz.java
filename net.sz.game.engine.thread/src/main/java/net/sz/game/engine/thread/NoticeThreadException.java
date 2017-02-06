package net.sz.game.engine.thread;

import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class NoticeThreadException {

    private static final Logger log = Logger.getLogger(NoticeThreadException.class);

    abstract void noticeThreadException(Throwable e);

}
