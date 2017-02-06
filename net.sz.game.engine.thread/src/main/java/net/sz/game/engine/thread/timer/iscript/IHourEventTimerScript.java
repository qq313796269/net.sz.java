package net.sz.game.engine.thread.timer.iscript;

import net.sz.game.engine.scripts.IBaseScript;

/**
 * 每小时执行
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IHourEventTimerScript extends IBaseScript {

    void run(int hour);
}
