package net.sz.game.engine.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sz.game.engine.util.ConcurrentHashSet;
import org.apache.log4j.Logger;

/**
 * 适当创建以后，分别计算id,每分钟量为1000万个id 0 ~ 9999999，重复周期是 10 年
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LongIdUtil {

    private static final Logger log = Logger.getLogger(LongIdUtil.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");

    private String newformatter = "";

    private long staticID = 0;

    private final int IDEN;

    /**
     *
     * @param IDEN 1 ~ 999
     */
    public LongIdUtil(int IDEN) {
        if (0 < IDEN && IDEN < 1000) {
            this.IDEN = IDEN;
            return;
        }
        throw new UnsupportedOperationException("参数 IDEN=" + IDEN + " 值不能超过 1 ~ 999");
    }

    public long getId() {

        /*这一段相对而言，比较耗时*/
        String tmpyear = formatter.format(new Date()).substring(3);

        long tmpid = 0;
        /* 这里的锁基本不耗时 */
        synchronized (this) {
            if (!newformatter.equals(tmpyear)) {
                staticID = 0;
                newformatter = tmpyear;
            }
            staticID += 1;
            tmpid = staticID;
        }

        String tmptime;
//        if (IDEN > 99) {
        if (tmpid > 9999999) {
            throw new UnsupportedOperationException("超过每分钟创建量 9999999");
        }
        /*这一段相对而言，比较耗时*/
        tmptime = String.format("%s%d%07d", tmpyear, IDEN, tmpid);
        /*注释代码，产生的id是可能会重复的*/
//        } else if (IDEN > 9) {
//            if (tmpid > 99999999) {
//                throw new UnsupportedOperationException("超过每分钟创建量 99999999");
//            }
//            /*这一段相对而言，比较耗时*/
//            tmptime = String.format("%s%d%08d", tmpyear, IDEN, tmpid);
//        } else {
//            if (tmpid > 999999999) {
//                throw new UnsupportedOperationException("超过每分钟创建量 999999999");
//            }
//            /*这一段相对而言，比较耗时*/
//            tmptime = String.format("%s%d%09d", tmpyear, IDEN, tmpid);
//        }
        /*这一段相对而言，比较耗时*/
        return Long.parseLong(tmptime);
    }

    public static void main(String[] args) throws Exception {
        LongIdUtil idsUtil = new LongIdUtil(901);

        ConcurrentHashSet<Long> hashids = new ConcurrentHashSet<>();
        System.in.read();
        for (int i = 0; i < 6; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100000; j++) {
                        long id = idsUtil.getId();
                        if (!hashids.add(id)) {
                            System.out.println(Thread.currentThread().getId() + " 重复id：" + id + " for：" + j + " size：" + hashids.size());
                        }
                    }
                }
            });
            System.err.println(thread.getId());
            thread.start();
        }
    }
}
