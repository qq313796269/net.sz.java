package net.sz.framework.map.buff;

import java.io.Serializable;
import net.sz.framework.map.MapObjectType;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.map.spirit.PersonAttribute;
import net.sz.framework.util.ObjectAttribute;
import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Buff implements Serializable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = -5268440796228819406L;
    private transient Person attacker;
    //buffid
    private long id;
    //buff模型
    private int buffModelId;
    //buff等级
    private int buffModelLevel;
    //技能模型
    private int skillModelId;
    //技能等级
    private int skillModelLevel;
    //buff分组 0 无分组，1是增益buff，2是减易buff 3和2 互斥状态
    private int buffGroup;

    //buff类型
    private BuffType buffType = BuffType.getNull();
    //状态buff的限制状态
    private BuffStateType buffStateType = BuffStateType.getNull();
    //作用方
    private long effect;
    //作用方
    private MapObjectType effectType;
    //来源类型 来源类型 Person的类型
    private long source;
    //来源者类型
    private MapObjectType sourceType;
    //创建时间
    private long createTime;
    //开始执行的时间
    private long startTime;
    //间隔时间
    private long intervalTime;
    //当前执行几次
    private int execCount;
    //总共执行几次
    private int allCount;
    //持续时间
    private long continuedTime;
    //buff执行概率
    private int gailv;
    //最后一次执行时间
    private long lastTime;
    //下线是否继续计算时间
    private boolean offline;

    //BUFF触发BUFF成功几率
    private int qprobability;
    //BUFF触发的BUFF ID
    private String qidbuff;
    //BUFF触发的技能成功几率
    private int qprobabilityJN;
    //BUFF触发的技能ID
    private String qskillid;
    //叠加次数
    private int overlay;
    //效果总量
    private long resultTotal;
    //特殊类型，自定义
    private Runnable runnable;
    //是否显示
    private int qcast_type;
    //属性值
    private PersonAttribute att = null;
    //不需要保存的
    private transient ObjectAttribute tmpOthers = null;

    public Buff() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBuffModelId() {
        return buffModelId;
    }

    public void setBuffModelId(int buffModelId) {
        this.buffModelId = buffModelId;
    }

    public int getBuffModelLevel() {
        return buffModelLevel;
    }

    public void setBuffModelLevel(int buffModelLevel) {
        this.buffModelLevel = buffModelLevel;
    }

    public int getSkillModelId() {
        return skillModelId;
    }

    public void setSkillModelId(int skillModelId) {
        this.skillModelId = skillModelId;
    }

    public int getSkillModelLevel() {
        return skillModelLevel;
    }

    public void setSkillModelLevel(int skillModelLevel) {
        this.skillModelLevel = skillModelLevel;
    }

    public long getEffect() {
        return effect;
    }

    public void setEffect(long effect) {
        this.effect = effect;
    }

    public int getQcast_type() {
        return qcast_type;
    }

    public void setQcast_type(int qcast_type) {
        this.qcast_type = qcast_type;
    }

    public MapObjectType getEffectType() {
        return effectType;
    }

    public void setEffectType(MapObjectType effectType) {
        this.effectType = effectType;
    }

    public long getSource() {
        return source;
    }

    public void setSource(long source) {
        this.source = source;
    }

    public MapObjectType getSourceType() {
        return sourceType;
    }

    public void setSourceType(MapObjectType sourceType) {
        this.sourceType = sourceType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 间隔时间
     *
     * @return
     */
    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    /**
     * 当前执行次数
     *
     * @return
     */
    public int getExecCount() {
        return execCount;
    }

    public void setExecCount(int execCount) {
        this.execCount = execCount;
    }

    /**
     * 总执行次数
     *
     * @return
     */
    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    /**
     * 持续时间
     *
     * @return
     */
    public long getContinuedTime() {
        return continuedTime;
    }

    public void setContinuedTime(long continuedTime) {
        this.continuedTime = continuedTime;
    }

    /**
     * 最后执行时间
     *
     * @return
     */
    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public PersonAttribute getAtt() {
        return att;
    }

    public void setAtt(PersonAttribute att) {
        this.att = att;
    }

    public ObjectAttribute getTmpOthers() {
        if (tmpOthers == null) {
            tmpOthers = new ObjectAttribute();
        }
        return tmpOthers;
    }

    public void setTmpOthers(ObjectAttribute tmpOthers) {
        this.tmpOthers = tmpOthers;
    }

    public int getOverlay() {
        return overlay;
    }

    public void setOverlay(int overlay) {
        this.overlay = overlay;
    }

    public long getResultTotal() {
        return resultTotal;
    }

    public void setResultTotal(long resultTotal) {
        this.resultTotal = resultTotal;
    }

    public BuffType getBuffType() {
        return buffType;
    }

    public void setBuffType(BuffType buffType) {
        this.buffType = buffType;
    }

    /**
     * 状态buff的限制状态
     *
     * @return
     */
    public BuffStateType getBuffStateType() {
        return buffStateType;
    }

    public void setBuffStateType(BuffStateType buffStateType) {
        this.buffStateType = buffStateType;
    }

    /**
     * BUFF触发BUFF成功几率
     *
     * @return
     */
    public int getQprobability() {
        return qprobability;
    }

    public void setQprobability(int qprobability) {
        this.qprobability = qprobability;
    }

    /**
     * BUFF触发的BUFF ID
     *
     * @return
     */
    public String getQidbuff() {
        return qidbuff;
    }

    public void setQidbuff(String qidbuff) {
        this.qidbuff = qidbuff;
    }

    public int getQprobabilityJN() {
        return qprobabilityJN;
    }

    public void setQprobabilityJN(int qprobabilityJN) {
        this.qprobabilityJN = qprobabilityJN;
    }

    public String getQskillid() {
        return qskillid;
    }

    public void setQskillid(String qskillid) {
        this.qskillid = qskillid;
    }

    public Person getAttacker() {
        return attacker;
    }

    public void setAttacker(Person attacker) {
        this.attacker = attacker;
    }

    public int getBuffGroup() {
        return buffGroup;
    }

    public void setBuffGroup(int buffGroup) {
        this.buffGroup = buffGroup;
    }

    public int getGailv() {
        return gailv;
    }

    public void setGailv(int gailv) {
        this.gailv = gailv;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Buff other = (Buff) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
