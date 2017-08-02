package net.sz.framework.scripts.timer.iscript;

import net.sz.framework.scripts.IBaseScript;

/**
 * 每天凌晨执行
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IDayZeroEventTimerScript extends IBaseScript {

    void run(int day);
}
