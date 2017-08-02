package net.sz.net.sz.framework.caches;

import java.io.Serializable;
import net.sz.framework.util.AtomInteger;
import net.sz.framework.utils.TimeUtil;

/**
 * 缓存类
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public final class CacheValue implements Cloneable, Serializable {

    private static final long serialVersionUID = -5039348954128869838L;

    /*编辑状态 */
    private volatile boolean edit;
    /*版本号 */
    private volatile AtomInteger versionId;
    /*创建时间*/
    private volatile long createTime;
    /*最后获取缓存时间*/
    private volatile long lastGetCacheTime;
    /*true 表示是滑动缓存*/
    private volatile boolean slide;
    /*清理时间*/
    private volatile long clearTime;
    /*与value有关的键值*/
    private volatile String keyString;
    /*value*/
    private volatile Object value;

    /**
     * 创建
     *
     * @param slide
     * @param clearTime
     */
    public CacheValue(boolean slide, long clearTime) {
        this.slide = slide;
        this.clearTime = clearTime;
        createCache();
    }

    public CacheValue(boolean slide, long clearTime, String keyString, Object value) {
        this.slide = slide;
        this.clearTime = clearTime;
        this.keyString = keyString;
        this.value = value;
        createCache();
    }

    /**
     *
     */
    protected void createCache() {
        this.edit = false;
        this.versionId = new AtomInteger(1);
        this.createTime = TimeUtil.currentTimeMillis();
        this.lastGetCacheTime = TimeUtil.currentTimeMillis();
    }

    /**
     *
     * @param tmp
     */
    protected void copyCache(CacheValue tmp) {
        this.edit = tmp.edit;
        this.keyString = tmp.keyString;
        this.versionId = tmp.getVersionId();
        this.createTime = tmp.getClearTime();
        this.lastGetCacheTime = TimeUtil.currentTimeMillis();
    }

    @Override
    protected CacheValue clone() {
        try {
            return (CacheValue) super.clone(); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 编辑状态
     *
     * @return
     */
    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public AtomInteger getVersionId() {
        return versionId;
    }

    public void setVersionId(AtomInteger versionId) {
        this.versionId = versionId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastGetCacheTime() {
        return lastGetCacheTime;
    }

    public void setLastGetCacheTime(long lastGetCacheTime) {
        this.lastGetCacheTime = lastGetCacheTime;
    }

    public boolean isSlide() {
        return slide;
    }

    public void setSlide(boolean slide) {
        this.slide = slide;
    }

    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = clearTime;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getKeyString() {
        return keyString;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

}
