package net.sz;

import java.util.ArrayList;
import java.util.HashSet;
import net.sz.game.engine.util.ConcurrentHashSet;
import net.sz.game.engine.utils.LongId0Util;
import net.sz.game.engine.utils.ObjectStreamUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ContainsTest {

    public static void main(String[] args) {

        LongId0Util idsId0Util = new LongId0Util();

        HashSet<Long> set = new HashSet<>();
        ArrayList<Long> list = new ArrayList<>();

        long id = 0;
        String strs = "";
        for (int i = 0; i < 10000; i++) {
            id = idsId0Util.getId();
            list.add(id);
            set.add(id);
            strs += "[" + id + "],";
        }

        byte[] toBytes = ObjectStreamUtil.toBytes(set);
        byte[] toBytes1 = ObjectStreamUtil.toBytes(strs);

        byte[] toBytes2 = ObjectStreamUtil.toBytes(list);

        System.out.println(toBytes.length);
        System.out.println(toBytes1.length);
        System.out.println(toBytes2.length);

        long currentTimeMillis = System.currentTimeMillis();
        System.out.println(set.contains(id));
        long d = System.currentTimeMillis();
        System.out.println((d - currentTimeMillis));

        currentTimeMillis = System.currentTimeMillis();
        System.out.println(strs.contains("[" + id + "]"));
        d = System.currentTimeMillis();
        System.out.println((d - currentTimeMillis));

        currentTimeMillis = System.currentTimeMillis();
        System.out.println(list.contains(id));
        d = System.currentTimeMillis();
        System.out.println((d - currentTimeMillis));
    }

}
