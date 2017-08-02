package net.sz.test;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class StringRegex {

    public static void main(String[] args) {

        String reg = "^(GL|SL).*";
        System.out.println("sL".matches(reg));
        System.out.println(String.join(",", "sssSLfdsd".split(reg)));

    }
}
