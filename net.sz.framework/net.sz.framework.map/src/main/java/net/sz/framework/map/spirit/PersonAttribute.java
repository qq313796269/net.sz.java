package net.sz.framework.map.spirit;

import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.utils.RandomUtils;
import net.sz.framework.utils.StringUtil;

import net.sz.framework.szlog.SzLogger;

/**
 * 属性
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PersonAttribute extends ConcurrentHashMap<PersonAttribute.AttKey, Integer> {

    private static final SzLogger log = SzLogger.getLogger();

    private static final long serialVersionUID = -3258690074056212218L;
    private Long expMax = 0l;
    private Double fight = 0d;

    public enum AttKey {

        /**
         * 120, "最大开孔数"
         */
        EGAP_KONGMAX(120, "最大开孔数"),
        /**
         * 121, "当前开孔数"
         */
        EGAP_KONG(121, "当前开孔数"),
        /**
         * 122, "孔1"
         */
        EGAP_KONG_1(122, "孔1"),
        /**
         * 123, "孔2"
         */
        EGAP_KONG_2(123, "孔2"),
        /**
         * 124, "孔3"
         */
        EGAP_KONG_3(124, "孔3"),
        /**
         * 125, "孔4"
         */
        EGAP_KONG_4(125, "孔4"),
        /**
         * 126, "孔5"
         */
        EGAP_KONG_5(126, "孔5"),
        /**
         * 144, "精炼消耗"
         */
        REFINE_Star_Cost(144, "精炼消耗"),
        /**
         * 力量,1001
         */
        Strength(1001, "力量"),
        /**
         * 主动,1002
         */
        Initiative(1002, "主动"),
        /**
         * 智慧,1003
         */
        Intelligence(1003, "智慧"),
        /**
         * 意志,1004
         */
        Willpower(1004, "意志"),
        /**
         * 体魄,1005
         */
        Wounds(1005, "体魄"),
        /**
         * 物理攻击下限,2001
         */
        DCmin(2001, "物理攻击下限"),
        /**
         * 物理攻击上限,2002
         */
        DCmax(2002, "物理攻击上限"),
        /**
         * 物理攻击增加,2003
         */
        DCcount(2003, "物理攻击增加"),
        /**
         * 物理攻击万分比提升,2004
         */
        DCper(2004, "物理攻击万分比提升"),
        /**
         * 魔法攻击下限,2011
         */
        MCmin(2011, "魔法攻击下限"),
        /**
         * 魔法攻击上限,2012
         */
        MCmax(2012, "魔法攻击上限"),
        /**
         * 魔法攻击增加，2013
         */
        MCcount(2013, "魔法攻击增加"),
        /**
         * 魔法攻击万分比提升,2014
         */
        MCper(2014, "魔法攻击万分比提升"),
        /**
         * 物理防御上限,2021
         */
        AC(2021, "物理防御"),
        /**
         * 物防增加,2023
         */
        ACCount(2023, "物防增加"),
        /**
         * 物理防御万分比提升,2024
         */
        ACPer(2024, "物理防御万分比提升"),
        /**
         * 魔法防御上限,2031
         */
        Mac(2031, "魔法防御"),
        /**
         * 魔防增加,2033
         */
        MacCount(2033, "魔防增加"),
        /**
         * 魔法防御万分比提升,2034
         */
        MacPer(2034, "魔法防御万分比提升"),
        /**
         * 最大魔法,2041
         */
        MpMax(2041, "最大魔法"),
        /**
         * 最大魔法万分比提升,2042
         */
        MPMaxper(2042, "最大魔法万分比提升"),
        /**
         * 当前魔法回复(战斗状态)，2043
         */
        MPFightCount(2043, "当前魔法回复(战斗状态)"),
        /**
         * 当前魔法回复(非战斗状态)，2044
         */
        MPCount(2044, "当前魔法回复(非战斗状态)"),
        /**
         * 当前魔法值回复万分比，2045
         */
        MPPer(2045, "当前魔法值回复万分比"),
        /**
         * 当前魔法值，2046
         */
        MP(2047, "当前魔法值"),
        /**
         * 自动回复生命，2058
         */
        AutoMP(2048, "自动回复魔法值"),
        /**
         * 战斗状态自动回复魔法值，2049
         */
        AutoBattleMP(2049, "战斗状态自动回复魔法值"),
        /**
         * 最大血量,2051
         */
        HPMax(2051, "最大血量"),
        /**
         * 最大血量万分比提升,2052
         */
        HPMaxper(2052, "最大血量万分比提升"),
        /**
         * 当前生命回复(战斗状态)，2053
         */
        HPFightCount(2053, "当前生命回复(战斗状态)"),
        /**
         * 当前生命回复(非战斗状态)，2054
         */
        HPCount(2054, "当前生命回复(非战斗状态)"),
        /**
         * 当前生命回复万分比，2055
         */
        HPPer(2055, "当前生命回复万分比"),
        /**
         * 当前生命，2057
         */
        HP(2057, "当前生命"),
        /**
         * 自动回复生命，2058
         */
        AutoHP(2058, "自动回复生命"),
        /**
         * 战斗状态自动回复生命，2059
         */
        AutoBattleHP(2059, "战斗状态自动回复生命"),
        /**
         * 闪避，2061
         */
        Dcuk(2061, "闪避"),
        /**
         * 韧性,2071
         */
        Toughness(2071, "韧性"),
        /**
         * 命中准确,2081
         */
        Hit(2081, "命中准确"),
        /**
         * 暴击,2091
         */
        CritValue(2091, "暴击"),
        /**
         * 物理伤害减免,2101
         */
        DCharmdel(2101, "物理伤害减免"),
        /**
         * 魔法减伤,2111
         */
        MCharmdel(2111, "魔法减伤"),
        /**
         * 物理反伤,2121
         */
        UnDCharmdel(2121, "物理反伤"),
        /**
         * 魔法反伤,2131
         */
        UnMCharmdel(2131, "魔法反伤"),
        /**
         * 暴击伤害加成的倍率,2141
         */
        CritValuePer(2141, "暴击伤害加成的倍率"),
        /**
         * 2151, "升级所需经验值"
         */
        //        ExpMax(2151, "升级所需经验值"),
        /**
         * 当前经验值,2152
         */
        Exp(2152, "获得经验值"),
        /**
         * 经验加成(万分比),2153
         */
        ExpPer(2153, "经验加成(万分比)"),
        /**
         * 经验加成(固定点数),2154
         */
        ExpCount(2154, "经验加成(固定点数)"),
        /**
         * 移动速度,2161
         */
        Speed(2161, "移动速度"),
        /**
         * 移动速度加成(万分比),2162
         */
        SpeedPer(2162, "移动速度加成(万分比)"),
        /**
         * 移动速度加成（点数）,2163
         */
        SpeedCount(2163, "移动速度加成（点数）"),
        /**
         * 攻击速度
         */
        AttackSpeed(2171, "攻击速度"),
        /**
         * 3001, "反伤盾所用的根据攻击力计算，根据不同的职业用不同的攻击力"
         */
        AttackPower(3001, "反伤盾所用的根据攻击力计算，根据不同的职业用不同的攻击力"),
        /**
         * 9993, "赠送道具"
         */
        Goods(9993, "赠送道具"),
        /**
         * 9994,魔法和生命值都加
         */
        HPMP(9994, "魔法和生命值都加"),
        /**
         * 9995,魔法和生命值都加，百分比
         */
        HPMPPer(9995, "魔法和生命值都加，百分比"),
        /**
         * 9996, vip经验
         */
        VIPExp(9996, "vip经验"),
        /**
         * 9997, "buff效果"
         */
        BuffState(9997, "buff效果"),
        /**
         * 9998, "脚本执行"
         */
        SCRIPT(9998, "脚本执行"), /**
         * 战斗力,9999
         */
        //Fight(9999, "战斗力")
        ;

        private int key;
        private String value;

        private AttKey(int key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * 根据索引获取key
         *
         * @param key
         * @return
         */
        public static AttKey parse(int key) {
            AttKey[] values = AttKey.values();
            for (AttKey value : values) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        /**
         *
         * @param key
         * @return
         */
        public static AttKey parse(String key) {
            return Enum.valueOf(AttKey.class, key);
        }

        /**
         * 键值
         *
         * @return
         */
        public int getKey() {
            return key;
        }

        /**
         * 描述字符
         *
         * @return
         */
        public String getValue() {
            return value;
        }

    }

    @Override
    public Integer put(AttKey key, Integer value) {
        return super.put(key, value); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 重写，如果没有建会返回0而不是null
     *
     * @param key
     * @return
     */
    @Override
    public Integer get(Object key) {
        Integer value = super.get(key);
        if (value != null) {
            return value;
        }
        return 0;
    }

    public Double getFight() {
        return fight;
    }

    public void setFight(Double fight) {
        this.fight = fight;
    }

    /**
     * 仅限buff计算调用
     * <br>
     * 慎重调用
     *
     * @param job 职业编号
     * @param baseAbility
     * @param attKey
     */
    @Deprecated
    public static int getBuffAbility(int job, BaseAbility baseAbility, AttKey attKey) {
        int value = 0;
        switch (attKey) {
            case AttackPower: //
            {
                switch (job) {
                    //物理系列
                    case 1://骑士
                    case 3://射手
                    case 5://屠杀者
                        value = RandomUtils.random(baseAbility.getDcmin(), baseAbility.getDcmax());
                        break;
                    //魔法系列
                    case 2://法师
                    case 4://刺客
                        value = RandomUtils.random(baseAbility.getMcmin(), baseAbility.getMcmax());
                        break;
                }
            }
            break;
            case AC:
            case ACCount:
                value = baseAbility.getAc();
                break;
            case AttackSpeed:
                value = baseAbility.getAttackSpeed();
                break;
            case AutoBattleHP:
                value = baseAbility.getAuto_battle_hp();
                break;
            case AutoBattleMP:
                value = baseAbility.getAuto_battle_mp();
                break;
            case AutoHP:
                value = baseAbility.getAutohp();
                break;
            case AutoMP:
                value = baseAbility.getAutomp();
                break;
            case CritValue:
                value = baseAbility.getCritvalue();
                break;
            case CritValuePer:
                value = baseAbility.getCritValuePer();
                break;
            case DCharmdel:
                value = baseAbility.getDcharmdel();
                break;
            case DCmin:
            case DCmax:
                value = RandomUtils.random(baseAbility.getDcmin(), baseAbility.getDcmax());
                break;
            case Dcuk:
                value = baseAbility.getDuck();
                break;
            case HPMax:
                value = baseAbility.getHpmax();
                break;
            case Hit:
                value = baseAbility.getHit();
                break;
            case Initiative:
                value = baseAbility.getInitiative();
                break;
            case Intelligence:
                value = baseAbility.getIntelligence();
                break;
            case MCharmdel:
                value = baseAbility.getMcharmdel();
                break;
            case MCmax:
            case MCmin:
                value = RandomUtils.random(baseAbility.getMcmin(), baseAbility.getMcmax());
                break;
            case Mac:
            case MacCount:
                value = baseAbility.getMac();
                break;
            case MpMax:
                value = baseAbility.getMpmax();
                break;
            case Strength:
                value = baseAbility.getStrength();
                break;
            case Speed:
            case SpeedCount:
                value = baseAbility.getSpeed();
                break;
            case Toughness:
                value = baseAbility.getToughness();
                break;
            case UnDCharmdel:
                value = baseAbility.getUndcharmdel();
                break;
            case UnMCharmdel:
                value = baseAbility.getUnmcharmdel();
                break;
            case Willpower:
                value = baseAbility.getWillpower();
                break;
            case Wounds:
                value = baseAbility.getWounds();
                break;
        }
        return value;
    }

    /**
     * 构建属性
     * <br>
     * 慎重调用
     *
     * @param baseAbility
     */
    @Deprecated
    public void buildBaseAbility(BaseAbility baseAbility) {
        baseAbility.setFight(baseAbility.getFight() + this.getFight());
        baseAbility.setExpmax(baseAbility.getExpmax() + this.getExpMax());
        for (Entry<AttKey, Integer> entry : this.entrySet()) {
            AttKey key = entry.getKey();
            Integer value = entry.getValue();
            switch (key) {
                case AC:
                case ACCount:
                    baseAbility.setAc(baseAbility.getAc() + value);
                    break;
                case AttackSpeed:
                    baseAbility.setAttackSpeed(baseAbility.getAttackSpeed() + value);
                    break;
                case AutoBattleHP:
                    baseAbility.setAuto_battle_hp(baseAbility.getAuto_battle_hp() + value);
                    break;
                case AutoBattleMP:
                    baseAbility.setAuto_battle_mp(baseAbility.getAuto_battle_mp() + value);
                    break;
                case AutoHP:
                    baseAbility.setAutohp(baseAbility.getAutohp() + value);
                    break;
                case AutoMP:
                    baseAbility.setAutomp(baseAbility.getAutomp() + value);
                    break;
                case CritValue:
                    baseAbility.setCritvalue(baseAbility.getCritvalue() + value);
                    break;
                case CritValuePer:
                    baseAbility.setCritValuePer(baseAbility.getCritValuePer() + value);
                    break;
                case DCcount:
                    baseAbility.setDcmin(baseAbility.getDcmin() + value);
                    baseAbility.setDcmax(baseAbility.getDcmax() + value);
                    break;
                case DCharmdel:
                    baseAbility.setDcharmdel(baseAbility.getDcharmdel() + value);
                    break;
                case DCmax:
                    baseAbility.setDcmax(baseAbility.getDcmax() + value);
                    break;
                case DCmin:
                    baseAbility.setDcmin(baseAbility.getDcmin() + value);
                    break;
                case Dcuk:
                    baseAbility.setDuck(baseAbility.getDuck() + value);
                    break;
                case HPMax:
                    baseAbility.setHpmax(baseAbility.getHpmax() + value);
                    break;
                case Hit:
                    baseAbility.setHit(baseAbility.getHit() + value);
                    break;
                case Initiative:
                    baseAbility.setInitiative(baseAbility.getInitiative() + value);
                    break;
                case Intelligence:
                    baseAbility.setIntelligence(baseAbility.getIntelligence() + value);
                    break;
                case MCcount:
                    baseAbility.setMcmin(baseAbility.getMcmin() + value);
                    baseAbility.setMcmax(baseAbility.getMcmax() + value);
                    break;
                case MCharmdel:
                    baseAbility.setMcharmdel(baseAbility.getMcharmdel() + value);
                    break;
                case MCmax:
                    baseAbility.setMcmax(baseAbility.getMcmax() + value);
                    break;
                case MCmin:
                    baseAbility.setMcmin(baseAbility.getMcmin() + value);
                    break;
                case Mac:
                case MacCount:
                    baseAbility.setMac(baseAbility.getMac() + value);
                    break;
                case MpMax:
                    baseAbility.setMpmax(baseAbility.getMpmax() + value);
                    break;
                case Strength:
                    baseAbility.setStrength(baseAbility.getStrength() + value);
                    break;
                case Speed:
                case SpeedCount:
                    baseAbility.setSpeed(baseAbility.getSpeed() + value);
                    break;
                case Toughness:
                    baseAbility.setToughness(baseAbility.getToughness() + value);
                    break;
                case UnDCharmdel:
                    baseAbility.setUndcharmdel(baseAbility.getUndcharmdel() + value);
                    break;
                case UnMCharmdel:
                    baseAbility.setUnmcharmdel(baseAbility.getUnmcharmdel() + value);
                    break;
                case Willpower:
                    baseAbility.setWillpower(baseAbility.getWillpower() + value);
                    break;
                case Wounds:
                    baseAbility.setWounds(baseAbility.getWounds() + value);
                    break;
            }
        }
    }

    /**
     * 构建属性，构建属性集合中比例属性加成，万分比格式
     * <br>
     * 慎重调用
     *
     * @param baseAbility
     */
    @Deprecated
    public void buildBaseAbilityPer(BaseAbility baseAbility) {
        for (Entry<AttKey, Integer> entry : this.entrySet()) {
            AttKey key = entry.getKey();
            Integer value = entry.getValue();
            switch (key) {
                case ACPer:
                    baseAbility.setAc(baseAbility.getAc() + (int) (baseAbility.getAc() * (value / 10000D)));
                    break;
                case DCper:
                    baseAbility.setDcmin(baseAbility.getDcmin() + (int) (baseAbility.getDcmin() * (value / 10000D)));
                    baseAbility.setDcmax(baseAbility.getDcmax() + (int) (baseAbility.getDcmax() * (value / 10000D)));
                    break;
                case HPMaxper:
                    baseAbility.setHpmax(baseAbility.getHpmax() + (int) (baseAbility.getHpmax() * (value / 10000D)));
                    break;
                case MCper:
                    baseAbility.setMcmin(baseAbility.getMcmin() + (int) (baseAbility.getMcmin() * (value / 10000D)));
                    baseAbility.setMcmax(baseAbility.getMcmax() + (int) (baseAbility.getMcmax() * (value / 10000D)));
                    break;
                case MacPer:
                    baseAbility.setMac(baseAbility.getMac() + (int) (baseAbility.getMac() * (value / 10000D)));
                    break;
                case SpeedPer:
                    baseAbility.setSpeed(baseAbility.getSpeed() + (int) (baseAbility.getSpeed() * (value / 10000D)));
                    break;
            }
        }
    }

    @Override
    public String toString() {
        for (Entry<AttKey, Integer> entry : this.entrySet()) {
            AttKey key = entry.getKey();
            Integer value = entry.getValue();
        }
        return "PlayerAttribute{" + '}';
    }

    /**
     * 所有属性清零
     */
    public void clearZoer() {
        for (Entry<AttKey, Integer> entry : this.entrySet()) {
            AttKey key = entry.getKey();
            this.put(key, 0);
        }
    }

    /**
     * 属性值会自动叠加
     *
     * @param playerAttribute
     */
    public void add(PersonAttribute playerAttribute) {
        for (Entry<AttKey, Integer> entry : playerAttribute.entrySet()) {
            AttKey key = entry.getKey();
            Integer value = entry.getValue();
            Integer get = this.get(key);
            value += get;
            this.put(key, value);
        }
        this.expMax += playerAttribute.expMax;
        this.fight += playerAttribute.fight;
    }

    /**
     * 属性值会自动叠加
     *
     * @param key
     * @param value
     */
    public void add(int key, Integer value) {
        add(AttKey.parse(key), value);
    }

    /**
     * 属性值会自动叠加
     *
     * @param key
     * @param value
     */
    public void add(AttKey key, Integer value) {
        if (key != null) {
            Integer get = this.get(key);
            value += get;
            this.put(key, value);
        }
    }

    public Long getExpMax() {
        return expMax;
    }

    public void setExpMax(Long expMax) {
        this.expMax = expMax;
    }

    // <editor-fold desc="属性值会自动叠加 public void add(String attString)">
    /**
     * 属性值会自动叠加
     *
     * @param attString 配置的属性
     */
    public void add(String attString) {
        if (StringUtil.isNullOrEmpty(attString)) {
            return;
        }
        String[] atts = attString.split(StringUtil.DOUHAO_REG);
        for (String att : atts) {
            String[] split = att.split("=");
            Integer integer = Integer.parseInt(split[0]);
            Integer integer1 = Integer.parseInt(split[1]);
            this.add(integer, integer1);
        }
    }
    // </editor-fold>

    public static void main(String[] args) {

        String toString = PersonAttribute.AttKey.Initiative.toString();
        System.out.println(toString);
        System.out.println(PersonAttribute.AttKey.parse(toString).getValue());
    }
}
