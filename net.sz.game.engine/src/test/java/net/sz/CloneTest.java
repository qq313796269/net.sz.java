package net.sz;

import java.util.ArrayList;
import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CloneTest {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws Exception {
        Test4 test4 = new Test4();

        ArrayList<Test4> strings = new ArrayList<>();
        strings.add(test4);

        Test4 clone1 = ((ArrayList<Test4>) strings.clone()).get(0);

        clone1.msgString = "1";
        clone1.msgs.add("11");
        clone1.test3.msgString = "2";
        clone1.test3.msgs.add("22");
        clone1.test3.test2.msgString = "3";
        clone1.test3.test2.msgs.add("33");

        Test4 clone2 = ((ArrayList<Test4>) strings.clone()).get(0);
        clone2.msgString = "10";
        clone2.msgs.add("10");
        clone2.test3.msgString = "20";
        clone2.test3.msgs.add("20");
        clone2.test3.test2.msgString = "30";
        clone2.test3.test2.msgs.add("30");
        System.in.read();
    }

    public static class Test1 implements Cloneable {

        public String msgString;

        public ArrayList<ArrayList<String>> msgs = new ArrayList<>();

        @Override
        public Object clone() throws CloneNotSupportedException {
            Test1 ob = null;
            ob = (Test1) super.clone();
            ob.msgs = (ArrayList<ArrayList<String>>) msgs.clone();
            return ob;
        }
    }

    public static class Test2 implements Cloneable {

        public String msgString;

        public ArrayList<String> msgs = new ArrayList<>();

        public Test1 test1 = new Test1();

        @Override
        public Object clone() throws CloneNotSupportedException {
            Test2 ob = null;
            ob = (Test2) super.clone();
            ob.test1 = (Test1) test1.clone();
            ob.msgs = (ArrayList<String>) msgs.clone();
            return ob;
        }
    }

    public static class Test3 implements Cloneable {

        public String msgString;

        public ArrayList<String> msgs = new ArrayList<>();

        public Test2 test2 = new Test2();

        @Override
        public Object clone() throws CloneNotSupportedException {
            Test3 ob = null;
            ob = (Test3) super.clone();
            ob.test2 = (Test2) test2.clone();
            ob.msgs = (ArrayList<String>) msgs.clone();
            return ob;
        }
    }

    public static class Test4 implements Cloneable {

        public String msgString;

        public ArrayList<String> msgs = new ArrayList<>();

        public Test3 test3 = new Test3();

        @Override
        public Object clone() throws CloneNotSupportedException {
            Test4 ob = null;
            ob = (Test4) super.clone();
            ob.test3 = (Test3) test3.clone();
            ob.msgs = (ArrayList<String>) msgs.clone();
            return ob;
        }
    }
}
