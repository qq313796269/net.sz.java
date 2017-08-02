package net.sz.framework.map.spirit;

import java.io.Serializable;

/**
 * 基础属性结果
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class BaseAbility implements Serializable {

    private static final long serialVersionUID = 7331996190994084714L;
    /**
     * 攻击速度
     */
    private int attackSpeed;
    /**
     * 力量,1001
     */
    private int strength;
    /**
     * 主动,1002
     */
    private int initiative;
    /**
     * 智慧,1003
     */
    private int intelligence;
    /**
     * 意志,1004
     */
    private int willpower;
    /**
     * 体魄,1005
     */
    private int wounds;
    /**
     * 物理攻击下限
     */
    private int dcmin;
    /**
     * 物理攻击上限
     */
    private int dcmax;
    /**
     * 魔法攻击下限
     */
    private int mcmin;
    /**
     * 魔法攻击上限
     */
    private int mcmax;
    /**
     * 物理防御
     */
    private int ac;
    /**
     * 魔法防御
     */
    private int mac;
    /**
     * 闪避
     */
    private int duck;
    /**
     * 命中率
     */
    private int hit;
    /**
     * 韧性
     */
    private int toughness;
    /**
     * 暴击值（概率）
     */
    private int critValuePer;
    /**
     * 暴击值
     */
    private int critvalue;
    /**
     * 自动回血
     */
    private int autohp;
    /**
     * 战斗中回复
     */
    private int auto_battle_hp;
    /**
     * 自动回魔法值
     */
    private int automp;
    /**
     * 战斗中自动回魔法值
     */
    private int auto_battle_mp;
    /**
     * 最大血量
     */
    private int hpmax;
    /**
     * 最大魔法
     */
    private int mpmax;
    /**
     * 升级的最大经验值
     */
    private long expmax;
    /**
     * 速度
     */
    private int speed;
    /**
     * 物理减伤
     */
    private int dcharmdel;
    /**
     * 魔法减伤
     */
    private int mcharmdel;
    /**
     * 物理反伤
     */
    private int undcharmdel;
    /**
     * 魔法反伤
     */
    private int unmcharmdel;
    /**
     * 吸血属性
     */
    private int drains;

    //--------------上面需要传到前端，下面不需要----------------//
    /**
     * 经验加成
     */
    private int expmultiple;

    /**
     * 属性提供的战斗力
     */
    private Double fight = 0d;

    public BaseAbility() {
    }

    /**
     * 攻击速度
     */
    public int getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * 攻击速度
     */
    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    /**
     * 力量
     *
     * @return
     */
    public int getStrength() {
        return strength;
    }

    /**
     * 力量
     *
     * @param strength
     */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    /**
     * 主动
     *
     * @return
     */
    public int getInitiative() {
        return initiative;
    }

    /**
     * 主动
     *
     * @param initiative
     */
    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    /**
     * 智慧
     *
     * @return
     */
    public int getIntelligence() {
        return intelligence;
    }

    /**
     * 智慧
     *
     * @param intelligence
     */
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    /**
     * 意志
     *
     * @return
     */
    public int getWillpower() {
        return willpower;
    }

    /**
     * 意志
     *
     * @param willpower
     */
    public void setWillpower(int willpower) {
        this.willpower = willpower;
    }

    /**
     * 体魄
     *
     * @return
     */
    public int getWounds() {
        return wounds;
    }

    /**
     * 体魄
     *
     * @param wounds
     */
    public void setWounds(int wounds) {
        this.wounds = wounds;
    }

    /**
     * 物理攻击下限
     *
     * @return
     */
    public int getDcmin() {
        return dcmin;
    }

    /**
     * 物理攻击下限
     *
     * @param dcmin
     */
    public void setDcmin(int dcmin) {
        this.dcmin = dcmin;
    }

    /**
     * 物理攻击上限
     */
    public int getDcmax() {
        return dcmax;
    }

    /**
     * 物理攻击上限
     */
    public void setDcmax(int dcmax) {
        this.dcmax = dcmax;
    }

    /**
     * 魔法攻击下限
     */
    public int getMcmin() {
        return mcmin;
    }

    /**
     * 魔法攻击下限
     */
    public void setMcmin(int mcmin) {
        this.mcmin = mcmin;
    }

    /**
     * 魔法攻击上限
     */
    public int getMcmax() {
        return mcmax;
    }

    /**
     * 魔法攻击上限
     */
    public void setMcmax(int mcmax) {
        this.mcmax = mcmax;
    }

    /**
     * 物理防御
     */
    public int getAc() {
        return ac;
    }

    /**
     * 物理防御
     */
    public void setAc(int ac) {
        this.ac = ac;
    }

    /**
     * 魔法防御
     */
    public int getMac() {
        return mac;
    }

    /**
     * 魔法防御
     */
    public void setMac(int mac) {
        this.mac = mac;
    }

    /**
     * 闪避
     */
    public int getDuck() {
        return duck;
    }

    /**
     * 闪避
     */
    public void setDuck(int duck) {
        this.duck = duck;
    }

    /**
     * 命中率
     */
    public int getHit() {
        return hit;
    }

    /**
     * 命中率
     */
    public void setHit(int hit) {
        this.hit = hit;
    }

    /**
     * 韧性
     */
    public int getToughness() {
        return toughness;
    }

    /**
     * 韧性
     */
    public void setToughness(int toughness) {
        this.toughness = toughness;
    }

    /**
     * 暴击值（概率）
     */
    public int getCritValuePer() {
        return critValuePer;
    }

    /**
     * 暴击值（概率）
     */
    public void setCritValuePer(int critValuePer) {
        this.critValuePer = critValuePer;
    }

    /**
     * 暴击值
     */
    public int getCritvalue() {
        return critvalue;
    }

    /**
     * 暴击值
     */
    public void setCritvalue(int critvalue) {
        this.critvalue = critvalue;
    }

    /**
     * 自动回血
     */
    public int getAutohp() {
        return autohp;
    }

    /**
     * 自动回血
     */
    public void setAutohp(int autohp) {
        this.autohp = autohp;
    }

    /**
     * 战斗中回复
     */
    public int getAuto_battle_hp() {
        return auto_battle_hp;
    }

    /**
     * 战斗中回复
     */
    public void setAuto_battle_hp(int auto_battle_hp) {
        this.auto_battle_hp = auto_battle_hp;
    }

    /**
     * 自动回魔法值
     */
    public int getAutomp() {
        return automp;
    }

    /**
     * 自动回魔法值
     */
    public void setAutomp(int automp) {
        this.automp = automp;
    }

    /**
     * 战斗中自动回魔法值
     */
    public int getAuto_battle_mp() {
        return auto_battle_mp;
    }

    /**
     * 战斗中自动回魔法值
     */
    public void setAuto_battle_mp(int auto_battle_mp) {
        this.auto_battle_mp = auto_battle_mp;
    }

    /**
     * 最大血量
     */
    public int getHpmax() {
        return hpmax;
    }

    /**
     * 最大血量
     */
    public void setHpmax(int hpmax) {
        this.hpmax = hpmax;
    }

    /**
     * 最大魔法
     */
    public int getMpmax() {
        return mpmax;
    }

    /**
     * 最大魔法
     */
    public void setMpmax(int mpmax) {
        this.mpmax = mpmax;
    }

    /**
     * 升级的最大经验值
     */
    public long getExpmax() {
        return expmax;
    }

    /**
     * 升级的最大经验值
     */
    public void setExpmax(long expmax) {
        this.expmax = expmax;
    }

    /**
     * 速度
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * 速度
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * 物理减伤
     */
    public int getDcharmdel() {
        return dcharmdel;
    }

    /**
     * 物理减伤
     */
    public void setDcharmdel(int dcharmdel) {
        this.dcharmdel = dcharmdel;
    }

    /**
     * 魔法减伤
     */
    public int getMcharmdel() {
        return mcharmdel;
    }

    /**
     * 魔法减伤
     */
    public void setMcharmdel(int mcharmdel) {
        this.mcharmdel = mcharmdel;
    }

    /**
     * 物理反伤
     */
    public int getUndcharmdel() {
        return undcharmdel;
    }

    /**
     * 物理反伤
     */
    public void setUndcharmdel(int undcharmdel) {
        this.undcharmdel = undcharmdel;
    }

    /**
     * 魔法反伤
     */
    public int getUnmcharmdel() {
        return unmcharmdel;
    }

    /**
     * 魔法反伤
     */
    public void setUnmcharmdel(int unmcharmdel) {
        this.unmcharmdel = unmcharmdel;
    }

    /**
     * 吸血属性
     */
    public int getDrains() {
        return drains;
    }

    /**
     * 吸血属性
     */
    public void setDrains(int drains) {
        this.drains = drains;
    }

    /**
     * 经验加成
     */
    public int getExpmultiple() {
        return expmultiple;
    }

    /**
     * 经验加成
     */
    public void setExpmultiple(int expmultiple) {
        this.expmultiple = expmultiple;
    }

    /**
     * 属性提供的战斗力
     */
    public Double getFight() {
        return fight;
    }

    /**
     * 属性提供的战斗力
     */
    public void setFight(Double fight) {
        this.fight = fight;
    }

    /**
     * 属性清零
     *
     */
    public void clearZero() {
        this.strength = 0;
        /**
         * 主动,1002
         */
        this.initiative = 0;
        /**
         * 智慧,1003
         */
        this.intelligence = 0;
        /**
         * 意志,1004
         */
        this.willpower = 0;
        /**
         * 体魄,1005
         */
        this.wounds = 0;
        this.attackSpeed = 0;
        this.dcmin = 0;
        this.dcmax = 0;
        this.mcmin = 0;
        this.mcmax = 0;
        this.ac = 0;
        this.mac = 0;
        this.duck = 0;
        this.hit = 0;
        this.toughness = 0;
        this.critvalue = 0;
        this.critValuePer = 0;
        this.autohp = 0;
        this.auto_battle_hp = 0;
        this.automp = 0;
        this.auto_battle_mp = 0;
        this.hpmax = 0;
        this.mpmax = 0;
        this.expmax = 0;
        this.speed = 0;
        this.dcharmdel = 0;
        this.undcharmdel = 0;
        this.mcharmdel = 0;
        this.unmcharmdel = 0;
        this.expmultiple = 0;
        this.drains = 0;
        this.fight = 0d;
    }

    /**
     * 属性小于0的处理
     */
    public void zeroAbility() {
        this.strength = this.strength > 0 ? this.strength : 0;
        /**
         * 主动,1002
         */
        this.initiative = this.initiative > 0 ? this.initiative : 0;
        /**
         * 智慧,1003
         */
        this.intelligence = this.intelligence > 0 ? this.intelligence : 0;
        /**
         * 意志,1004
         */
        this.willpower = this.willpower > 0 ? this.willpower : 0;
        /**
         * 体魄,1005
         */
        this.wounds = this.wounds > 0 ? this.wounds : 0;
        this.attackSpeed = this.attackSpeed > 0 ? this.attackSpeed : 0;
        this.dcmin = this.dcmin > 0 ? this.dcmin : 0;
        this.dcmax = this.dcmax > 0 ? this.dcmax : 0;
        this.mcmin = this.mcmin > 0 ? this.mcmin : 0;
        this.mcmax = this.mcmax > 0 ? this.mcmax : 0;
        this.ac = this.ac > 0 ? this.ac : 0;
        this.mac = this.mac > 0 ? this.mac : 0;
        this.duck = this.duck > 0 ? this.duck : 0;
        this.hit = this.hit > 0 ? this.hit : 0;
        this.toughness = this.toughness > 0 ? this.toughness : 0;
        this.critvalue = this.critvalue > 0 ? this.critvalue : 0;
        this.critValuePer = this.critValuePer > 0 ? this.critValuePer : 0;
        this.autohp = this.autohp > 0 ? this.autohp : 0;
        this.auto_battle_hp = this.auto_battle_hp > 0 ? this.auto_battle_hp : 0;
        this.automp = this.automp > 0 ? this.automp : 0;
        this.auto_battle_mp = this.auto_battle_mp > 0 ? this.auto_battle_mp : 0;
        this.hpmax = this.hpmax > 0 ? this.hpmax : 0;
        this.mpmax = this.mpmax > 0 ? this.mpmax : 0;
        this.expmax = this.expmax > 0 ? this.expmax : 0;
        this.speed = this.speed < 0 ? 0 : (this.speed > 12000 ? 12000 : this.speed); // 移动速度介于 0 - 12000
        this.dcharmdel = this.dcharmdel > 0 ? this.dcharmdel : 0;
        this.undcharmdel = this.undcharmdel > 0 ? this.undcharmdel : 0;
        this.mcharmdel = this.mcharmdel > 0 ? this.mcharmdel : 0;
        this.unmcharmdel = this.unmcharmdel > 0 ? this.unmcharmdel : 0;
        this.expmultiple = this.expmultiple > 0 ? this.expmultiple : 0;
        this.drains = this.drains > 0 ? this.drains : 0;
    }

    @Override
    public String toString() {
        return "BaseAbility{" + "attackSpeed=" + attackSpeed + ", strength=" + strength + ", initiative=" + initiative + ", intelligence=" + intelligence + ", willpower=" + willpower + ", wounds=" + wounds + ", dcmin=" + dcmin + ", dcmax=" + dcmax + ", mcmin=" + mcmin + ", mcmax=" + mcmax + ", ac=" + ac + ", mac=" + mac + ", duck=" + duck + ", hit=" + hit + ", toughness=" + toughness + ", crit=" + critValuePer + ", critvalue=" + critvalue + ", autohp=" + autohp + ", auto_battle_hp=" + auto_battle_hp + ", automp=" + automp + ", auto_battle_mp=" + auto_battle_mp + ", hpmax=" + hpmax + ", mpmax=" + mpmax + ", expmax=" + expmax + ", speed=" + speed + ", dcharmdel=" + dcharmdel + ", mcharmdel=" + mcharmdel + ", undcharmdel=" + undcharmdel + ", unmcharmdel=" + unmcharmdel + ", drains=" + drains + ", expmultiple=" + expmultiple + ", fight=" + fight + '}';
    }

}
