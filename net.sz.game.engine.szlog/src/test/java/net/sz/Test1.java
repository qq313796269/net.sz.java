package net.sz;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test1 {

    public static void main(String[] args) {
        try {
            tt();
        } catch (Throwable e) {
            e.printStackTrace(System.out);
            System.out.println(e.toString());
            System.out.println("=========================================");
            System.out.println(e.getLocalizedMessage());
            System.out.println("=========================================");
            System.out.println(e.getMessage());

        }

    }

    static void tt() {
        throw new UnsupportedOperationException("s");
    }
}
