package net.sz.game.test.main;

import java.util.ArrayList;
import net.sz.game.engine.scripts.manager.ScriptManager;
import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test_App_Manager {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        
        /*加载所有脚本文件*/
        ArrayList<String> loadScripts = ScriptManager.getInstance().reload();
        String join = String.join(",", loadScripts);
        log.error(join);

    }
}
