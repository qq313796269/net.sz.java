package net.sz.framework.map.spirit;

import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.BitUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PersonState implements java.io.Serializable {

    private static final SzLogger log = SzLogger.getLogger();
    private long state = 0;

    public enum Key {

        /**
         * 正常,清理其他状态
         */
        NORMAL(0, 0, 0, "正常"),
        /**
         * 登陆
         */
        LOGIN(1, 1, 2, "登陆"),
        /**
         * 已经退出
         */
        QUIT(2, 1, 2, "已经退出"),
        /**
         * 切换地图中
         */
        CHANGEMAP(3, 3, 4, "切换地图中"),
        /**
         * 地图中
         */
        INMAP(4, 3, 4, "地图中"),
        /**
         * 站立
         */
        STAND(5, 5, 7, "站立"),
        /**
         * 移动
         */
        RUN(6, 5, 7, "移动"),
        /**
         * 打坐,冥想
         */
        SIT(7, 5, 7, "打坐,冥想"),
        /*===========================依赖buff状态=============================*/
        /**
         * 8, 8, 8, "不受ai控制"
         */
        NOT_AI(8, 8, 8, "不受ai控制"),
        /**
         * 挂机
         */
        AUTOFIGHT(9, 9, 9, "挂机"),
        /**
         * 不能攻击其他对象
         */
        CAN_NOT_ATTACK(10, 10, 10, "可以攻击其他对象"),
        /**
         * 不能被其他对象攻击
         */
        CAN_NOT_UNCOATTACK(11, 11, 11, "不能被其他对象攻击"),
        /**
         * 不能被其他对象魔法攻击
         */
        CAN_NOT_MC_UNCOATTACK(12, 12, 12, "不能被其他对象魔法攻击"),
        /**
         * 不能被其他对象物理攻击
         */
        CAN_NOT_AC_UNCOATTACK(13, 13, 13, "不能被其他对象物理攻击"),
        /**
         * 闪避状态
         */
        DODGE(14, 14, 14, "闪避状态"),
        /**
         * 魔法闪避状态
         */
        DODGE_MC(15, 15, 15, "魔法闪避"),
        /**
         * 物理闪避状态
         */
        DODGE_AC(16, 16, 16, "物理闪避"),
        /**
         * 是否可以移动
         */
        CAN_NOT_MOVE(17, 17, 17, "是否可以移动"),
        /**
         * 不能使用技能，可以用普通攻击
         */
        CAN_NOT_USESKILL(19, 19, 19, "不能使用技能"),
        /**
         * 不能使用道具
         */
        CAN_NOT_USEGOODS(20, 20, 20, "不能使用道具"),
        /**
         * 不能使用传送门
         */
        CAN_NOT_TRANSPORT(21, 21, 21, "不能使用传送门"),
        /**
         * 不能聊天
         */
        CAN_NOT_CHAT(22, 22, 22, "不能聊天"),
        /**
         * 隐身
         */
        ON_HIDE(23, 23, 23, "隐身"),
        /**
         * 不能使用坐骑
         */
        CAN_NOT_USEHOSE(24, 24, 24, "不能使用坐骑"),
        /**
         * 不能飞行
         */
        CAN_NOT_USEFLYHOSE(25, 25, 25, "不能飞行"),
        /**
         * 不能加血
         */
        CAN_NOT_ADDHP(26, 26, 26, "不能加血"),
        /**
         * 不能加魔法
         */
        CAN_NOT_ADDMP(27, 27, 27, "不能加魔法"),
        /**
         * 不能被击退
         */
        CAN_NOT_MOVE_JITUI(28, 28, 28, "不能被击退"),
        /**
         * 不能拉取
         */
        CAN_NOT_MOVE_LAQU(29, 29, 29, "不能拉取"),
        /**
         * 30, 30, 30, "无敌状态"
         */
        Invincible(30, 30, 30, "无敌状态"),
        /**
         * 31, 31, 31, "任何攻击无效,不会有攻击动作"
         */
        ATTMISS(31, 31, 31, "任何攻击无效,不会有攻击动作"),
        /*===========================依赖buff状态=============================*/
        /*===========================下面是需要清理的状态，死亡复活后===========================*/
        /**
         * 50, 50, 50, "眩晕状态"
         */
        Vertigo(50, 50, 50, "眩晕状态"),
        /**
         * 往回跑,比如怪物跑回出生点
         */
        RUN_BACK(51, 51, 51, "往回跑"),
        /**
         * 死亡等待移除
         */
        DIEWAIT(52, 52, 52, "死亡等待移除"),
        /**
         * 死亡中
         */
        DIEING(53, 53, 53, "死亡中"),
        /**
         * 死亡,
         */
        DIE(54, 54, 54, "死亡"),
        /**
         * 战斗
         */
        FIGHT(55, 55, 55, "战斗"),
        /**
         * 交易
         */
        TRANSACTION(56, 56, 56, "交易"),
        /**
         * 骑乘中
         */
        HORSESTATUS(57, 57, 57, "骑乘中"),
        /**
         * 飞行中
         */
        FLYING(58, 58, 58, "飞行中"),
        /**
         * 施法中
         */
        CAST(59, 59, 59, "施法中"),
        /**
         * 采集
         */
        GATHER(60, 60, 60, "采集"),
        /**
         * 恐惧状态
         */
        FEAR(61, 61, 61, "恐惧状态"),
        /**
         * 狂暴状态
         */
        ON_CRAZY(62, 62, 62, "狂暴状态"),
        /**
         * 被击状态
         */
        ON_HIT(63, 63, 63, "被击状态"), /**/;

        int index;
        long value;
        long group;
        String msg;

        /**
         *
         * @param index 只能是 0 - 63 之间，包含
         * @param g1 分组开始坐标 只能是 0 - 63 之间，包含
         * @param g2 分组结束坐标 只能是 0 - 63 之间，包含
         */
        private Key(int index, int g1, int g2, String msg) {
            this.index = index;
            this.value = 1L << this.index;
            /*创建分组信息*/
            for (int i = g1; i <= g2; i++) {
                this.group += 1L << i;
            }
            this.msg = msg;
        }

        public int getIndex() {
            return index;
        }

        public long getValue() {
            return value;
        }

        public long getGroup() {
            return group;
        }

    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public static long ReviveState1 = 0;
    public static long ReviveState2 = 0;

    static {
        /*创建分组信息*/
        for (int i = 9; i <= 29; i++) {
            ReviveState1 += 1L << i;
        }
        /*创建分组信息*/
        for (int i = 48; i <= 63; i++) {
            ReviveState2 += 1L << i;
        }
    }

    public boolean hasFlag(Key key) {
        return BitUtil.hasFlagBitLong(state, key.value);
    }

    public void addStatus(Key key) {
        state = BitUtil.removeBitLong(state, key.group);
        state = BitUtil.addBitLong(state, key.value);
    }

    public void removeStatus(Key key) {
        state = BitUtil.removeBitLong(state, key.value);
    }

    /**
     * 清理状态，ReviveState2，并且设置 Key.STAND<br>
     *
     * @param isPlayer true 表示玩家
     */
    public void resetStatus(boolean isPlayer) {
        if (!isPlayer) {
            /*清理状态*/
            state = BitUtil.removeBitLong(state, ReviveState1);
        }

        /*清理状态*/
        state = BitUtil.removeBitLong(state, ReviveState2);

        /*设置站立状态*/
        addStatus(Key.STAND);
        /*设置正常状态*/
        addStatus(Key.NORMAL);

    }

    public String show() {
        return BitUtil.show(state);
    }

    public static void main(String[] args) {
        PersonState personState = new PersonState();
        personState.addStatus(PersonState.Key.LOGIN);
        personState.addStatus(PersonState.Key.INMAP);
        personState.addStatus(PersonState.Key.RUN);
        log.error(personState.show());
        personState.addStatus(PersonState.Key.RUN_BACK);
        log.error(personState.show());
        personState.removeStatus(PersonState.Key.RUN_BACK);
        log.error(personState.show());
        personState.resetStatus(true);
        log.error(personState.show());
    }
}
