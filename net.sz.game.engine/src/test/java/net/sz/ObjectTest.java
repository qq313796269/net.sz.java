package net.sz;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ObjectTest {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        Object o = "123";
        test(o);
        System.out.println(o);
    }

    public static void test(Object obj) {
        obj = "321";
    }

}
