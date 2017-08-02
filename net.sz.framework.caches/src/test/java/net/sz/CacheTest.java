package net.sz;

import net.sz.framework.szlog.SzLogger;
import net.sz.net.sz.framework.caches.CacheValue;
import net.sz.net.sz.framework.caches.CachePool;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CacheTest {

    private static final SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) throws InterruptedException {

        String keyString = "111111";

        CachePool cache = new CachePool();

        cache.add(keyString, "123", 2000, false);

        for (int i = 0; i < 2000000; i++) {
            cache.add("K" + i, "123", 200000, false);
        }

        CacheValue cache1 = cache.get(keyString);

        cache.update(keyString, cache1);
        cache.update(keyString, cache1);
        cache.update(keyString, cache1);

        cache1 = cache.get(keyString);

    }

}
