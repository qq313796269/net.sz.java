package net.sz.game.engine.map.script;

import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.navmesh.Vector3;
import net.sz.game.engine.scripts.IBaseScript;

/**
 * 寻路中执行
 */
public interface IRunningScript extends IBaseScript {

    void action(Person person, Vector3 begin, Vector3 end);
}
