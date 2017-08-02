package net.sz;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface ITest {

    default void foo() {
        System.out.println("Calling A.foo()");
    }

    public class Clazz implements ITest {

    }

}

class t {

    public static void main(String[] args) {
        
        ITest.Clazz cl = new ITest.Clazz();
        cl.foo();

    }

}
