package net.sz.net.sz.framework.caches;

import net.sz.framework.struct.thread.BaseThreadModel;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.util.concurrent.ConcurrentHashSet;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class CheckCacheTimer extends BaseThreadModel {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -171241151780911504L;
    private final ConcurrentHashSet<CachePool> cachePools = new ConcurrentHashSet<>();

    public ConcurrentHashSet<CachePool> getCachePools() {
        return cachePools;
    }

    public CheckCacheTimer() {
        this.start();
    }

    public void addCachePool(CachePool cachePool) {
        cachePools.add(cachePool);
    }

    public void removeCachePool(CachePool cachePool) {
        cachePools.remove(cachePool);
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (cachePools) {
                    cachePools.wait(1000);
                }
                long startTime = TimeUtil.currentTimeMillis();
                CachePool[] toArray = cachePools.toArray(new CachePool[0]);
//                log.info("缓存检测定时器耗时 1：" + (TimeUtil.currentTimeMillis() - startTime));
                for (CachePool cachePool : toArray) {
                    /*考虑缓存的清理的都放在这里、当然有很多值的注意细节有待细化*/

                    CacheValue[] toArray1 = cachePool.cacheMap.values().toArray(new CacheValue[0]);
//                    log.info("缓存检测定时器耗时 2：" + (TimeUtil.currentTimeMillis() - startTime));
                    for (int i = 0; i < toArray1.length; i++) {
                        CacheValue value = toArray1[i];
                        if (value.getClearTime() < 0) {
                            /*无需清理的集合*/
                            continue;
                        }
                        /*理论上，这里是能够保证绝对缓存，同步*/
                        if (!value.isEdit()) {
                            /*滑动缓存清理*/
                            if (value.isSlide() && TimeUtil.currentTimeMillis() - value.getLastGetCacheTime() < value.getClearTime()) {
                                continue;
                            }
                            /*固定缓存清理*/
                            if (!value.isSlide() && TimeUtil.currentTimeMillis() - value.getCreateTime() < value.getClearTime()) {
                                continue;
                            }
                            boolean remove = cachePool.remove(value.getKeyString());
                            if (log.isDebugEnabled()) {
                                log.debug("缓存：" + value.getKeyString() + " 超时过期被清理！");
                            }
                        }
                    }
                }
                if (log.isDebugEnabled()) {
                    long time = TimeUtil.currentTimeMillis() - startTime;
                    if (time > 300) {
                        log.debug("缓存检测定时器耗时：" + time);
                    }
                }
            } catch (Throwable e) {
                log.error("", e);
            }
        }
    }
}
