package net.sz.framework.map.buff;

import java.io.Serializable;
import net.sz.framework.utils.StringUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class BuffStateType implements Serializable {

    private static final long serialVersionUID = -8510371436325937201L;

    private long typeValue = 0;

    public enum TypeKey {

        /**
         * 0, "无任何意义的buff"
         */
        Null(0, "无任何意义的buff"),
        /**
         * 1.攻击玩家时触发
         */
        Attack(1, "发出攻击"),
        /**
         * 2.受到攻击时触发
         */
        BeAttack(2, "受到攻击时触发"),
        /**
         * 3.移动时触发【不骑坐骑】
         */
        Move(3, "移动时触发【不骑坐骑】"),
        /**
         * 4.移动时触发【骑坐骑】
         */
        HoseMove(4, "移动时触发【骑坐骑】"),
        /**
         * 5.传送时触发
         */
        Transfer(5, "传送时触发"),
        /**
         * 6.升级时触发
         */
        LevelUp(6, "升级时触发"),
        /**
         * 7.死亡时触发
         */
        Die(7, "死亡时触发"),
        /**
         * 8.使用指定技能时触发
         */
        UseKillByModelId(8, "使用指定技能时触发"),
        /**
         * 9.站立一定时间后触发
         */
        StandOverTime(9, "站立一定时间后触发"),
        /**
         * 10.无法使用物品
         */
        CAN_NOT_USE_Goods(10, "无法使用物品"),
        /**
         * 11.无法进行传送
         */
        CAN_NOT_Transfer(11, "无法进行传送"),
        /**
         * 12.无法使用技能
         */
        CAN_NOT_USE_SKILL(12, "无法使用技能"),
        /**
         * 13.无法进行乘骑
         */
        CAN_NOT_USE_HOSE(13, "无法进行乘骑"),
        /**
         * 14, "无敌buff"
         */
        Invincible(14, "无敌buff"),
        /**
         * 15, "无法移动"
         */
        CAN_NOT_MOVE(15, "无法移动"),
        /**
         * 16, "恐惧状态"
         */
        FEAR(16, "恐惧状态"),
        /**
         * 17, "眩晕buff"
         */
        Vertigo(17, "眩晕buff"),
        /**
         * 18, "反弹buff"
         */
        Rebound(18, "反弹buff"),
        /**
         * 19, 经验翻倍计算buff，根据buff等级计算，2级就是双倍buff
         */
        ExpPer(19, "经验翻倍计算buff"),
        /**
         * 20, 技能物cd时间 buff
         */
        SkillNoneCD(20, "技能物cd时间 buff"),
        /**
         * buff类型是定时触发
         */
        Timers(21, "定时触发buff"),
        /**
         * 22, "吸收伤害"
         */
        Absorb(22, "吸收伤害"),
        /**
         * 23, "任何攻击无效,不会有攻击动作"
         */
        ATTMiss(23, "任何攻击无效,不会有攻击动作"),
        /**
         * 24, "按照当前生命最大值，百分比吸收伤害"
         */
        AbsorbPer(24, "按照当前生命最大值，百分比吸收伤害"),
        /**
         * 25, "伤害计算结果值比例值"
         */
        AttackHp(25, "伤害计算结果值比例值"),
        /**
         * 26, "伤害计算结果恢复血量"
         */
        AttackHpReg(26, "伤害计算结果恢复血量"),
        /**
         * 27, "伤害计算结果恢复魔法值"
         */
        AttackMpReg(27, "伤害计算结果恢复魔法值"),
        /**
         * 28, "伤害计算结果恢复血量 百分百"
         */
        AttackHpRegPer(28, "伤害计算结果恢复血量 百分百"),
        /**
         * 29, "伤害计算结果恢复魔法值 百分百"
         */
        AttackMpRegPer(29, "伤害计算结果恢复魔法值百分百"),;

        private long value;
        private int index;
        private String valueMsg;

        private TypeKey(int index, String valueMsg) {
            this.value = 1L << index;
            this.index = index;
            this.valueMsg = valueMsg;
        }

        public long getValue() {
            return value;
        }

        public int getIndex() {
            return index;
        }

        public String getValueMsg() {
            return valueMsg;
        }

        public String show() {
            return "结果：" + StringUtil.padLeft(this.value + "", 19, " ") + " -> " + StringUtil.padLeft(Long.toBinaryString(this.value), 64, "0");
        }
    }

    // <editor-fold desc="是否包含一个状态 public boolean hasFlag(BuffStateType buffType) ">
    /**
     * 是否包含一个状态
     *
     * @param buffType
     * @return
     */
    public boolean hasFlag(TypeKey buffType) {
        return (typeValue & buffType.value) != 0;
    }
    // </editor-fold>

    // <editor-fold desc="增加一个状态 public void addStatus(BuffStateType buffType)">
    /**
     * 增加一个状态
     *
     * @param buffType
     */
    public void addStatus(TypeKey buffType) {
        typeValue = typeValue | buffType.value;
    }
    // </editor-fold>

    // <editor-fold desc="增加多个状态 public void addStatusString(String indexs)">
    /**
     * 增加多个状态
     *
     * @param indexs 1,2,3
     */
    public void addStatusString(String indexs) {

        if (StringUtil.isNullOrEmpty(indexs)) {
            throw new UnsupportedOperationException("indexs null or Empty");
        }

        String[] split = indexs.split(StringUtil.DOUHAO_REG);
        if (split != null && split.length > 0) {
            for (int i = 0; i < split.length; i++) {
                String tmp = split[i];
                int parseInt = Integer.parseInt(tmp);
                /*拿到对应的字段*/
                TypeKey buffStateIndex = getBuffStateIndex(parseInt);
                /*添加对应的状态*/
                addStatus(buffStateIndex);
            }
        }
    }
    // </editor-fold>

    // <editor-fold desc="移除一个状态 public void removeStatus(BuffStateType buffType)">
    /**
     * 移除一个状态
     *
     * @param buffType
     */
    public void removeStatus(TypeKey buffType) {
        typeValue = typeValue & (~buffType.value);
    }
    // </editor-fold>

    /**
     *
     * @return
     */
    public static BuffStateType getNull() {
        BuffStateType buffStateType = new BuffStateType();
        return buffStateType;
    }

//    /**
//     * 位移值
//     *
//     * @param value
//     * @return
//     */
//    public static TypeKey getBuffStateValue(int value) {
//        TypeKey[] values = TypeKey.values();
//        for (TypeKey buffType : values) {
//            if (buffType.getValue() == value) {
//                return buffType;
//            }
//        }
//        return null;
//    }
    /**
     * 根据值，策划配置的值
     *
     * @param index
     * @return
     */
    public static TypeKey getBuffStateIndex(int index) {
        TypeKey[] values = TypeKey.values();
        for (TypeKey buffType : values) {
            if (buffType.getIndex() == index) {
                return buffType;
            }
        }
        return null;
    }

    /**
     * 枚举类型的字符串
     *
     * @param typeString
     * @return
     */
    public static TypeKey getBuffStateType(String typeString) {
        TypeKey buffType = Enum.valueOf(TypeKey.class, typeString);
        return buffType;
    }

}
