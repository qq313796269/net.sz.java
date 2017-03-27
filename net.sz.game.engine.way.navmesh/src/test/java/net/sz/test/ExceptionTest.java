package net.sz.test;

import net.sz.game.engine.navmesh.path.NavMap;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ExceptionTest {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {

        for (int i = 0; i < 1; i++) {
            log.error("===================" + i + "==========================");
            NavMap navMap = new NavMap("D:\\worker\\zc\\ServerCode\\newGame\\net.sz.game.gamesr\\target\\mapblock\\10000.navmesh", false);

        }
        log.error("=============================================");
    }
}
