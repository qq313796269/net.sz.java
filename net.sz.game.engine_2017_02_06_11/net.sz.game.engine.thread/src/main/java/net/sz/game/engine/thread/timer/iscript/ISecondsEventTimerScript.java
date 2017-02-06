package net.sz.game.engine.thread.timer.iscript;

import net.sz.game.engine.scripts.IBaseScript;

/**
 * 每秒执行
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface ISecondsEventTimerScript extends IBaseScript {

    void run(int sec);
}
