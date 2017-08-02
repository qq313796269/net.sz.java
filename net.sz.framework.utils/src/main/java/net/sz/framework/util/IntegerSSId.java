package net.sz.framework.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sz.framework.utils.TimeUtil;

/**
 * 每一秒钟 1万 个 id 0 ~ 9999，
 * <br>
 * 不保证多程序重复情况
 * <br>
 * 重复周期是 12小时，
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class IntegerSSId {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");

    protected long upTime = 0;
    protected long MAXID = 9999L;
    protected int MAXHEAD = 10000;
    protected int idHead = 0;
    protected int id = -1;

    /**
     * 每一秒钟 1万 个 id 0 ~ 9999，
     *
     * @return
     */
    public int getId() {

        long now = TimeUtil.currentTimeMillis() / 1000;
        int tmpid = 0;
        /* 这里的锁基本不耗时 */
        synchronized (this) {
            if (now != upTime) {
                upTime = now;
                /*这一段相对而言，比较耗时*/
                String tmpyear = formatter.format(new Date()).substring(8);
                idHead = Integer.parseInt(tmpyear) * MAXHEAD;
                id = -1;
            }
            ++id;
            if (id > MAXID) {
                throw new UnsupportedOperationException("超过每秒钟创建量 " + MAXID);
            }
            tmpid = idHead + id;
        }
        return tmpid;
    }

    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
        IntegerSSId idsUtil = new IntegerSSId();
        System.out.println(idsUtil.getId());
        System.out.println(idsUtil.getId());
        System.out.println(idsUtil.getId());
        System.out.println(idsUtil.getId());
        System.exit(0);
    }
}
