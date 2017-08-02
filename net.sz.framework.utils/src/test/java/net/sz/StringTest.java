package net.sz;

import net.sz.framework.utils.StringUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class StringTest {

    public static void main(String[] args) {
//        String str1 = "~@##@";
//        boolean checkFilter = StringUtil.checkFilter(str1, StringUtil.PATTERN_ABC_2);
//        System.out.println(checkFilter);
        /*                                                             字符串            秘钥   */
        System.out.println("原始字符混淆加密：" + StringUtil.convert_ASE("abcdefghijklmn", 5, 3, 2));
        /*                                                 把原始字符转化base64        字符             秘钥*/
        System.out.println("原始字符base64加密：" + StringUtil.convert_InBase64_ASE("abcdefghijklmn", 5, 3, 2));
    }

}
