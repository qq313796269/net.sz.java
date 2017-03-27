package net.sz.game.test.scripts.main;

import net.sz.game.engine.scripts.IInitBaseScript;
import net.sz.game.engine.szlog.SzLogger;
import net.sz.game.test.main.iscript.IApp_ManagerScript;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test_App_ManagerScript implements IInitBaseScript, IApp_ManagerScript {

    private static SzLogger log = SzLogger.getLogger();

    @Override
    public void _init() {
        log.error("初始化函数，每次加载都会调用");
    }

}
