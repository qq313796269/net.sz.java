package net.sz.framework.utils;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MD5Util {

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 空白字符
     */
    private static final char Null = '\b';

    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 会把参数拼接起来，
     *
     * @param origin
     * @return
     */
    public static String md5Encode(String... origin) {
        return md5Encode(MD5Util.Null, origin);
    }

    /**
     * 会把参数用 joinStr 间隔字符 拼接起来
     *
     * @param joinStr 连接字符串 空白字符为 '\b'
     * @param origins 需要验证的字符组合
     * @return
     */
    public static String md5Encode(char joinStr, String... origins) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String join = String.join(String.valueOf(joinStr), origins);
            resultString = byteArrayToHexString(md.digest(join.getBytes("utf-8")));
        } catch (Exception ex) {
            throw new UnsupportedOperationException(ex);
        }
        return resultString;
    }

    /**
     * 验证MD5值
     *
     * @param token token
     * @param joinStr 连接字符 空白字符为 '\b'
     * @param origins 验证组合字符串
     * @return
     */
    public static boolean verifyToken(String token, char joinStr, String... origins) {
        String md5Encode = md5Encode(joinStr, origins);
        return md5Encode.equals(token);
    }

    public static void main(String[] args) {
        String md5Encode = md5Encode("21", "3333", "333");
        System.out.println(md5Encode);
        System.err.println(verifyToken(md5Encode, MD5Util.Null, "21", "3333", "333"));
    }

}
