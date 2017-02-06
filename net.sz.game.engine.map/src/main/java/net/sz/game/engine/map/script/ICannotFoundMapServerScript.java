package net.sz.game.engine.map.script;

import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.scripts.IBaseScript;
import net.sz.game.engine.map.MapInfo;

/**
 * 无法找到玩家对应的地图线程处理(如,玩家在副本中,服务器重启了,再次登录无法找到副本)
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface ICannotFoundMapServerScript extends IBaseScript {

    /**
     *
     * @param person
     * @return
     */
    MapInfo action(Person person);
}
