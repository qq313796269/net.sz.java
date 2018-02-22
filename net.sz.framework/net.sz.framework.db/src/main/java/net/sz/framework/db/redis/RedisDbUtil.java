package net.sz.framework.db.redis;

import java.io.IOException;
import net.sz.framework.szlog.SzLogger;
import redis.clients.jedis.Jedis;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class RedisDbUtil {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws Exception {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("192.168.1.220", 6379);
        jedis.auth("123456");
        jedis.append("key1", "value11");
        jedis.append("key1", "value11");
        System.in.read();
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            jedis.append("keuierfyh" + i, "sfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasfsfafasf");
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - currentTimeMillis));

//        jedis.save();
        //查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping());
        System.out.println("连接成功");
    }
}
