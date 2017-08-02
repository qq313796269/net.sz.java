package net.sz.net.sz.framework.caches;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.ObjectStreamUtil;
import net.sz.framework.utils.TimeUtil;

/**
 * 缓存管理器，可以分片管理，自定义
 * <br>
 * 如需销毁请调用close关闭缓存
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CachePool {

    private static final SzLogger log = SzLogger.getLogger();
    static final CheckCacheTimer CHECK_CACHE_TIMER = new CheckCacheTimer();

    /*缓存集合*/
    final ConcurrentHashMap<String, CacheValue> cacheMap = new ConcurrentHashMap<>();

    public CachePool() {
        CHECK_CACHE_TIMER.addCachePool(this);
    }

    /**
     * 关闭缓存
     */
    public void close() {
        cacheMap.clear();
        CHECK_CACHE_TIMER.removeCachePool(this);
    }

    /**
     * 获取缓存副本
     * <br>
     * 数据越大，越耗时
     *
     * @return
     */
    public HashMap<String, CacheValue> getCachesCopy() {
        return new HashMap<>(cacheMap);
    }

    /**
     * 当前缓存数量
     *
     * @return
     */
    public int size() {
        return cacheMap.size();
    }

    /**
     * 默认30分钟清理的滑动缓存、如果存在缓存键、将不再添加
     *
     * @param key
     * @param object
     * @return
     */
    public CacheValue add(String key, Object object) {
        return add(key, object, 30 * 60 * 1000);
    }

    /**
     * 默认滑动缓存、如果存在缓存键、将不再添加
     *
     * @param key
     * @param object
     * @param clearTime 滑动缓存的清理时间，小于 0 不会被清理
     * @return
     */
    public CacheValue add(String key, Object object, long clearTime) {
        return add(key, object, clearTime, true);
    }

    /**
     * 默认滑动缓存、如果存在缓存键、将不再添加
     *
     * @param key
     * @param object
     * @param clearTime 清理缓存的间隔时间 小于 0 不会被清理
     * @param isSlide true表示滑动缓存，
     * @return
     */
    public CacheValue add(String key, Object object, long clearTime, boolean isSlide) {
        CacheValue cacheBase = get(key);
        if (cacheBase == null) {
            synchronized (key.intern()) {
                cacheBase = get(key);
                if (cacheBase == null) {
                    cacheBase = new CacheValue(isSlide, clearTime, key, object);
                    cacheMap.put(key, cacheBase);
                }
            }
        }
        return cacheBase;
    }

    /**
     * 删除一个缓存
     *
     * @param key
     * @return
     */
    public boolean remove(String key) {
        return cacheMap.remove(key) != null;
    }

    /**
     * 获取一个缓存
     *
     * @param key
     * @return
     */
    public CacheValue get(String key) {
        CacheValue cacheBase = cacheMap.get(key);
        if (cacheBase != null) {
            /*更新最后获取缓存的时间*/
            cacheBase.setLastGetCacheTime(TimeUtil.currentTimeMillis());
        }
        return cacheBase;
    }

    /**
     * 获取缓存的对象值
     *
     * @param key
     * @return
     */
    public Object getValue(String key) {
        CacheValue cacheBase = cacheMap.get(key);
        if (cacheBase != null) {
            /*更新最后获取缓存的时间*/
            cacheBase.setLastGetCacheTime(TimeUtil.currentTimeMillis());
            return cacheBase.getValue();
        }
        return null;
    }

    /**
     * 获取一条数据，这里我只是测试，提供思路，
     * <br>
     * 所以不会去考虑list等情况;
     * <br>
     * 需要的话可以自行修改
     *
     * @param <T>
     * @param clazz
     * @param key
     * @return
     */
    public <T> T getValue(String key, Class<T> clazz) {
        CacheValue cache = get(key);
        return cache == null ? null : (T) cache.getValue();
    }

    /**
     * 更新缓存数据同时更新数据库数据
     * <br>
     * 如果版本号不正确，或者不存在该键 throw new UnsupportedOperationException
     *
     * @param key
     * @param object
     * @return
     */
    public boolean update(String key, CacheValue object) {
        if (object == null) {
            throw new UnsupportedOperationException("参数 object 为 null");
        }
        CacheValue cacheBase = cacheMap.get(key);
        /*理论上，控制得当这里是不可能为空的*/
        if (cacheBase != null) {
            /*理论上是能绝对同步的，你也可以稍加修改*/
            synchronized (key.intern()) {
                /*验证编辑状态和版号,保证写入数据是绝对正确的*/
                if (cacheBase.getVersionId() == object.getVersionId()) {
                    /*拷贝最新数据操作*/
                    cacheMap.put(key, object);
                    /*保证写入数据库后进行修改 对版本号进行加一操作*/
                    object.getVersionId().changeZero(1);
                    /*设置最新的最后访问时间*/
                    object.setLastGetCacheTime(TimeUtil.currentTimeMillis());
                    /*修改编辑状态*/
                    object.setEdit(false);
                    if (log.isDebugEnabled()) {
                        log.debug("数据已修改，最新版号：" + object.getVersionId());
                    }
                    return true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("版本已经修改无法进行更新操作");
                    }
                    throw new UnsupportedOperationException("版本已经修改无法进行更新操作");
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("缓存不存在无法修改数据");
            }
            throw new UnsupportedOperationException("缓存不存在无法修改数据");
        }
    }

    /**
     * 获取独占编辑状态
     *
     * @param key
     * @return
     */
    public boolean updateEdit(String key) {
        CacheValue cacheBase = null;
        cacheBase = cacheMap.get(key);
        if (cacheBase == null) {
            throw new UnsupportedOperationException("未找到数据源");
        }
        return updateEdit(key, cacheBase);
    }

    /**
     * 获取独占编辑状态
     *
     * @param key
     * @param cacheBase
     * @return
     */
    boolean updateEdit(String key, CacheValue cacheBase) {
        if (cacheBase == null) {
            throw new UnsupportedOperationException("参数 cacheBase 为 null");
        }
        if (!cacheBase.isEdit()) {
            synchronized (key.intern()) {
                if (!cacheBase.isEdit()) {
                    /*同步后依然需要双重判定*/
                    cacheBase.setEdit(true);
                    /*设置最新的最后访问时间*/
                    cacheBase.setLastGetCacheTime(TimeUtil.currentTimeMillis());
                    return true;
                }
            }
        }
        return false;
    }
}
