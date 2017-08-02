package net.sz;

import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ObjectTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {
        Object o = "123";
        test(o);
        System.out.println(o);
    }

    public static void test(Object obj) {
        obj = "321";
    }

}
