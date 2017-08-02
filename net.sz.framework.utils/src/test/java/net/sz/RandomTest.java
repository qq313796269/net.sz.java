package net.sz;

import java.util.ArrayList;
import net.sz.framework.utils.RandomUtils;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class RandomTest {

    public static void main(String[] args) {
        ArrayList<String> ins = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ins.add(RandomUtils.random(100, 600) + "");
        }
        System.out.println(String.join(",", ins));
    }
}
