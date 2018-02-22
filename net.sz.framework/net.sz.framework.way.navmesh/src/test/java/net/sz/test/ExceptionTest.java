package net.sz.test;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import net.sz.framework.way.navmesh.path.NavMap;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.JsonUtil;
import net.sz.framework.utils.ObjectStreamUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ExceptionTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws IOException {
//        while (true) {

//            System.in.read();
        NavMap navMap = new NavMap("D:\\worker\\zc\\ServerCode\\newGame\\net.sz.game.gamesr\\target\\mapblock\\110.navmesh", false);
        //JsonUtil.toGSONString(navMap);
//            ObjectStreamUtil.deepCopy(navMap);
        long currentTimeMillis = System.currentTimeMillis();

        NavMap clone = navMap.clone();

        long end = System.currentTimeMillis();

        log.error(end - currentTimeMillis);
//        }
        //ArrayList
//        A a = new A();
//        B b = new B();
//        b.a = a;
//        a.b = b;
//        A deepCopy = net.sz.framework.utils.ObjectStreamUtil.deepCopy(a, A.class);
    }

    static class A implements Serializable {

        B b;
    }

    static class B implements Serializable {

        A a;
    }
}
