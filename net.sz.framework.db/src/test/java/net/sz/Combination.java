package net.sz;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Combination {

    /**
     * 字符串的组合
     *
     * @param chs
     */
    public static void combiantion(String chs[]) {
        if (chs == null || chs.length == 0) {
            return;
        }
        List<String> listIn = new ArrayList();
        List<String> listOut = new ArrayList();
        for (int i = 1; i <= chs.length; i++) {
            combine(chs, 0, i, listIn, listOut);
        }
    }

    /**
     * 从字符数组中第begin个字符开始挑选number个字符加入list中
     *
     * @param cs
     * @param begin
     * @param number
     * @param listIn
     * @param listOut
     */
    private static void combine(String[] cs, int begin, int number, List<String> listIn, List<String> listOut) {
        if (number == 0) {
            String ret = "`" + String.join("`,`", listIn) + "`";
            listOut.add(ret);
            System.out.println(ret);
            return;
        }
        if (begin == cs.length) {
            return;
        }
        listIn.add(cs[begin]);
        combine(cs, begin + 1, number - 1, listIn, listOut);
        listIn.remove(cs[begin]);
        combine(cs, begin + 1, number, listIn, listOut);
    }

    public static void main(String args[]) {
        String chs[] = {"a", "b", "c"};
        combiantion(chs);
    }
}
