package net.sz.game.engine.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sz.game.engine.util.ConcurrentHashSet;
import org.apache.log4j.Logger;

/**
 * 每一个小时可以生成 10万个，0 ~ 99999，重复周期是一个月
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class IntegerIdUtil {

    private static final Logger log = Logger.getLogger(IntegerIdUtil.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("ddHH");

    public static void main(String[] args) throws Exception {
        log.error(formatter.format(new Date()));

        IntegerIdUtil idsUtil = new IntegerIdUtil();

        ConcurrentHashSet<Long> hashids = new ConcurrentHashSet<>();
        System.in.read();
        for (int i = 0; i < 6; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
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

    private String newformatter = "";

    private int staticID = -1;

    public int getId() {
        String tmpyear = formatter.format(new Date());
        long tmpid = 0;
        synchronized (this) {
            if (!newformatter.equals(tmpyear)) {
                staticID = -1;
                newformatter = tmpyear;
            }
            staticID += 1;
            tmpid = staticID;
        }

        if (tmpid > 99999) {
            throw new UnsupportedOperationException("超过每分钟创建量 99999");
        }
        /*这一段相对而言，比较耗时*/
        return Integer.parseInt(String.format("%s%05d", tmpyear, tmpid));
    }
}
