package net.sz.game.engine.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 每小时能产生 114749 个 id 100000 ~ 214748，
 * <br>
 * 不保证多程序重复情况
 * <br>
 * 重复周期是 一个月，
 * <br>
 * 重启程序重复周期是一小时
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class IntegerIdUtil {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("ddHH");

    public static void main(String[] args) throws Exception {
        IntegerIdUtil idsUtil = new IntegerIdUtil();
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
        System.err.println(idsUtil.getId());
        System.exit(0);
    }

    private String newformatter = "";

    private int thisStaticID = 99999;

    /**
     * 每小时能产生 114749 个 id
     *
     * @return
     */
    public int getId() {
        String tmpyear = formatter.format(new Date());
        Integer tmpid = 0;
        synchronized (this) {
            if (!newformatter.equals(tmpyear)) {
                thisStaticID = 99999;
                newformatter = tmpyear;
            }
            ++thisStaticID;
            tmpid = thisStaticID;
        }

        if (214748 < tmpid) {
            throw new UnsupportedOperationException("超过每分钟创建量 214748");
        }

        /*这一段相对而言，比较耗时*/
        return Integer.parseInt(tmpid + tmpyear);
    }
}
