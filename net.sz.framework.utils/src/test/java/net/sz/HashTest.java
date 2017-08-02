package net.sz;

import java.util.HashSet;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class HashTest {

    private  int baseId;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.baseId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HashTest other = (HashTest) obj;
        if (this.baseId != other.baseId) {
            return false;
        }
        return true;
    }


    public static void main(String[] args) {

        new InterfaceDemo() {
            @Override
            public double needActrulize(int a) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        HashSet<String> ses;

        System.out.println(rotatingHash("ddddddddddddddddaagaewgawraewgfdasgrae", 131));
        System.out.println(additiveHash("ddddddddddddddddaagaewgawraewgfdasgrae", 131));
        System.out.println(additiveHash("ddddddddddddddddaagaewgawraewgfgarg4eyg4whrwh34q554w5t2345阿飞暗示过暗杀神个发大水管矮冬瓜阿萨德刚暗示过奥德赛dasgrae", 333333333));
        System.out.println(additiveHash("ddddddddddddddddaagaewgawraewgft2345阿飞暗示过暗dasgrae", 333333333));

    }

    static int additiveHash(String key, int prime) {
        int hash, i;
        for (hash = key.length(), i = 0; i < key.length(); i++) {
            hash += key.charAt(i);
        }
        return (hash % prime);
    }

    static int rotatingHash(String key, int prime) {
        int hash, i;
        for (hash = key.length(), i = 0; i < key.length(); ++i) {
            hash = (hash << 4) ^ (hash >> 28) ^ key.charAt(i);
        }
        return (hash % prime);
    }

    interface InterfaceDemo {

        double needActrulize(int a);

        /**
         * 接口中定义实现的方法，需要加上default或static关键字
         *
         * @param a
         * @return
         */
        default double needNotActrulize(int a) {
            return Math.random() - 0.99;
        }

        /**
         * 接口中定义实现的方法，需要加上default或static关键字
         *
         * @param a
         * @return
         */
        static double needNotActrulize1(int a) {
            return Math.random() - 0.99;
        }
    }

}
