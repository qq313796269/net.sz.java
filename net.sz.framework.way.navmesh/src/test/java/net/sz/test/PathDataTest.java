package net.sz.test;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.way.navmesh.Vector3;
import net.sz.framework.way.navmesh.path.PathData;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PathDataTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        PathData pathData = new PathData();
        pathData.points.add(Vector3.ZERO);
        pathData.points.add(Vector3.ZERO);
        pathData.points.add(Vector3.ZERO);

        PathData clone = pathData.clone();

        Vector3 remove = pathData.points.remove(0);

        log.debug(pathData.points.size() + " " + clone.points.size());
    }

}
