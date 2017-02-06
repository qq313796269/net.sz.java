package net.sz.game.engine.map.script;

import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.scripts.IBaseScript;

/**
 * 寻路结束后执行
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IRunningEndScript extends IBaseScript {

    /**
     * 移动结束
     *
     * @param person
     * @param reason 1:寻路正常完毕 2:寻路遇到阻挡点 3:玩家取消寻路
     */
    void action(Person person, byte reason);
}
