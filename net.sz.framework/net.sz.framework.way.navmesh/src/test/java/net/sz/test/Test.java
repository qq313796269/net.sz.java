package net.sz.test;

import net.sz.framework.way.navmesh.Vector3;
import net.sz.framework.way.navmesh.path.NavMap;
import net.sz.framework.way.navmesh.path.PathData;
import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws Exception {
        //String path = "D:\\worker\\zc\\ServerCode\\newGame\\net.sz.game.gamesr\\src\\main\\resources\\mapblock\\100.navmesh";
        String path = "D:\\worker\\zc\\ServerCode\\newGame\\net.sz.game.gamesr\\target\\mapblock\\100.navmesh";

        NavMap navMap = new NavMap(path);
        test(navMap, 31.537, 27.1545, 53.4568, 5.73);
        test(navMap, 29.0, 18.0, 29.0, 49.0);
        test(navMap, 64.930, 39.840, 26.739, 50.284);
    }

    static void test(NavMap navMap, double startx, double starty, double endx, double endy) {

        Vector3 pos = new Vector3(startx, starty); //navMap.getPointInPaths(startx, starty);
        Vector3 clone = pos.clone();
        if (pos == clone || clone.equals(pos)) {

        }
        Vector3 target = new Vector3(endx, endy);//navMap.getPointInPaths(endx, endy);

        PathData path1 = navMap.path(pos, target);
        log.error(String.format("路径->%s 位置->%s Check->%s 终点->%s Check-> %s",
                path1.points.size(), pos, navMap.isPointInPaths(pos), target, navMap.isPointInPaths(target)));
        System.out.println("=======================================================");
        System.out.println("");
    }
}
