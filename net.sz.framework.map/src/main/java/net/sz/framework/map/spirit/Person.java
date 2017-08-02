package net.sz.framework.map.spirit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.framework.map.MapObject;
import net.sz.framework.map.MapObjectType;
import net.sz.framework.map.buff.Buff;
import net.sz.framework.map.skill.Skill;
import net.sz.framework.struct.Cooldown;

/**
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class Person extends MapObject implements Cloneable {

    private static final long serialVersionUID = -7778568015291171928L;

    // <editor-fold desc="施法类型">
    /**
     * 1-采集 2-使用坐骑 3-使用物品 , 11 工会战建筑物采集
     */
    public enum CastType {
        /**
         * 30, "普通施法"
         */
        Cast(0, 0, "普通施法"),
        /**
         * 1, 1, "普通采集"
         */
        GATHER(1, 1, "普通采集"),
        /**
         * 11, 11, "战场采集"
         */
        BATTLEGather(11, 11, "战场采集"),
        /**
         * 12, 1, "环境之地采集"
         */
        FairylandCast(12, 1, "环境之地采集"),
        /**
         * 13, 1, "野外挖矿"
         */
        WildGather(13, 1, "野外挖矿"),
        /**
         * 2, 2, "坐骑"
         */
        Horse(2, 2, "使用坐骑"),
        /**
         * 3, 3, "使用物品"
         */
        UseGOODS(3, 3, "使用物品"),
        /**
         * 4, 4, "回城"
         */
        BackCity(4, 4, "回城"),
        /**
         * 20, 20, "吟唱施法"
         */
        UseSkill_1(20, 20, "吟唱施法"),
        /**
         * 21, 21, "引导技能施法"
         */
        UseSkill_2(21, 21, "引导技能施法"),;
        final int key;
        final int group;
        final String msg;

        private CastType(int key, int group, String msg) {
            this.key = key;
            this.group = group;
            this.msg = msg;
        }

        public int getKey() {
            return key;
        }

        public int getGroup() {
            return group;
        }

        public String getMsg() {
            return msg;
        }

    }
    // </editor-fold>

    /*是否结束机器人ai处理*/
    private transient boolean robotAI;
    //人物属性锁(锁属性计算多线程时的重复计算错误)
    private transient Object abilityLock = new Object();
    /*用于移动同步的时候判断是否已经同步过了*/
    private transient HashMap<MapObjectType, HashSet<Long>> lockingIn = null;
    /*资源*/
    private String res;
    /*玩家当前状态*/
    private transient PersonState personState;
    /*数据库记录当前pk状态*/
    private PersonPKState dbPKState;
    /*当前pk状态*/
    private transient PersonPKState personPKState;
    /*头像*/
    private String icon;
    /*玩家在数据中心中专消息的客户端连接Id*/
    private transient String clientSocketId;
    //老旧的
    private int oldModelID;
    //时装模板Id
    private int fashionModelID;
    //等级
    private volatile int level;

    //经验
    private volatile long exp;

    //当前生命
    private volatile int hp;

    //当前魔法
    private volatile int mp;

    /*对象死亡时间*/
    private long dieTimer;

    //BUFF列表
    private ArrayList<Buff> buffs = new ArrayList<>();

    //冷却列表
    private ConcurrentHashMap<String, Cooldown> cooldowns = new ConcurrentHashMap<>();

    // ------------------技能部分begin--------------------//
    /**
     * 技能,技能模板id，技能
     */
    private HashMap<Integer, Skill> skillsHashMap = new HashMap<>();
    // ------------------技能部分end----------------------//
    // 属性
    //最终计算属性
    private BaseAbility finalAbility = new BaseAbility();

    //-------------------属性部分-------------------------//
    private /*transient这样可能会导致离线竞技场角色数据不正确*/ HashMap<Integer, PersonAttribute> attributes = new HashMap<>();
    //其他属性
    private transient PersonAttribute tmpAttribute = new PersonAttribute();
    //-------------------属性部分end-------------------------//

    /*经验加成*/
    private transient double expMultiple;
    /*对象攻击其他对象或被其他对象攻击*/
    private transient long lastAiTimer;
    /*最后一次攻击结束*/
    private transient boolean lastAttckEnd;
    /*施法类型定义*/
    private transient CastType castType = null;
    /*施法值，比如建筑物id*/
    private transient long castValue;
    /*施法对象存储*/
    private transient Object castObject;
    /*开始施法时间*/
    private transient long castStarttime;
    /*施法耗时*/
    private transient int castCosttime;
    /*地图线程*/
    private transient long mapThreadId;

    public Person(long id) {
        super(id);
    }

    /**
     * 用于移动同步的时候判断是否已经同步过了
     *
     * @return
     */
    public HashMap<MapObjectType, HashSet<Long>> getLockingIn() {
        if (lockingIn == null) {
            lockingIn = new HashMap<>();
        }
        return lockingIn;
    }

    public void setLockingIn(HashMap<MapObjectType, HashSet<Long>> lockingIn) {
        this.lockingIn = lockingIn;
    }

    public long getLastAiTimer() {
        return lastAiTimer;
    }

    public void setLastAiTimer(long lastAiTimer) {
        this.lastAiTimer = lastAiTimer;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getClientSocketId() {
        return clientSocketId;
    }

    public void setClientSocketId(String clientSocketId) {
        this.clientSocketId = clientSocketId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getOldModelID() {
        return oldModelID;
    }

    public void setOldModelID(int oldModelID) {
        this.oldModelID = oldModelID;
    }

    public ArrayList<Buff> getBuffs() {
        if (buffs == null) {
            buffs = new ArrayList<>();
        }
        return buffs;
    }

    public void setBuffs(ArrayList<Buff> buffs) {
        this.buffs = buffs;
    }

    public ConcurrentHashMap<String, Cooldown> getCooldowns() {
        return cooldowns;
    }

    public void setCooldowns(ConcurrentHashMap<String, Cooldown> cooldowns) {
        this.cooldowns = cooldowns;
    }

    public boolean isLastAttckEnd() {
        return lastAttckEnd;
    }

    public void setLastAttckEnd(boolean lastAttckEnd) {
        this.lastAttckEnd = lastAttckEnd;
    }

    public BaseAbility getFinalAbility() {
        return finalAbility;
    }

    public void setFinalAbility(BaseAbility finalAbility) {
        this.finalAbility = finalAbility;
    }

    public double getExpMultiple() {
        return expMultiple;
    }

    public void setExpMultiple(double expMultiple) {
        this.expMultiple = expMultiple;
    }

    public HashMap<Integer, PersonAttribute> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }

    public void setAttributes(HashMap<Integer, PersonAttribute> attributes) {
        this.attributes = attributes;
    }

    public PersonAttribute getTmpAttribute() {
        return tmpAttribute;
    }

    public void setTmpAttribute(PersonAttribute tmpAttribute) {
        this.tmpAttribute = tmpAttribute;
    }

    public long getMapThreadId() {
        return mapThreadId;
    }

    public void setMapThreadId(long mapThreadId) {
        this.mapThreadId = mapThreadId;
    }

    public Object getAbilityLock() {
        if (abilityLock == null) {
            abilityLock = new Object();
        }
        return abilityLock;
    }

    public void setAbilityLock(Object abilityLock) {
        this.abilityLock = abilityLock;
    }

    public int getFashionModelID() {
        return fashionModelID;
    }

    public void setFashionModelID(int fashionModelID) {
        this.fashionModelID = fashionModelID;
    }

    public HashMap<Integer, Skill> getSkillsHashMap() {
        return skillsHashMap;
    }

    public void setSkillsHashMap(HashMap<Integer, Skill> skillsHashMap) {
        this.skillsHashMap = skillsHashMap;
    }

    public PersonState getPersonState() {
        if (personState == null) {
            personState = new PersonState();
        }
        return personState;
    }

    public void setPersonState(PersonState personState) {
        this.personState = personState;
    }

    public PersonPKState getDbPKState() {
        if (dbPKState == null) {
            dbPKState = new PersonPKState();
        }
        return dbPKState;
    }

    public void setDbPKState(PersonPKState dbPKState) {
        this.dbPKState = dbPKState;
    }

    public PersonPKState getPersonPKState() {
        if (personPKState == null) {
            personPKState = new PersonPKState();
        }
        return personPKState;
    }

    public void setPersonPKState(PersonPKState personPKState) {
        this.personPKState = personPKState;
    }

    public boolean isRobotAI() {
        return robotAI;
    }

    public void setRobotAI(boolean robotAI) {
        this.robotAI = robotAI;
    }

    public long getDieTimer() {
        return dieTimer;
    }

    public void setDieTimer(long dieTimer) {
        this.dieTimer = dieTimer;
    }

    public CastType getCastType() {
        return castType;
    }

    public void setCastType(CastType castType) {
        this.castType = castType;
    }

    public long getCastValue() {
        return castValue;
    }

    public void setCastValue(long castValue) {
        this.castValue = castValue;
    }

    public Object getCastObject() {
        return castObject;
    }

    public void setCastObject(Object castObject) {
        this.castObject = castObject;
    }

    public long getCastStarttime() {
        return castStarttime;
    }

    public void setCastStarttime(long castStarttime) {
        this.castStarttime = castStarttime;
    }

    public int getCastCosttime() {
        return castCosttime;
    }

    public void setCastCosttime(int castCosttime) {
        this.castCosttime = castCosttime;
    }

    /*=========================附加函数值==========================*/
    public boolean isQuit() {
        return this.getPersonState().hasFlag(PersonState.Key.QUIT);
    }

    public boolean isDie() {
        return this.getPersonState().hasFlag(PersonState.Key.DIE)
                || this.getPersonState().hasFlag(PersonState.Key.DIEING)
                || this.getPersonState().hasFlag(PersonState.Key.DIEWAIT);
    }

    public boolean isHPDie() {
        return this.getHp() <= 0;
    }

    /**
     * 检查是否可以攻击
     *
     * @return boolean true 能 false 不能
     */
    @Override
    public boolean canAttack() {
        return super.canAttack() && !this.getPersonState().hasFlag(PersonState.Key.CAN_NOT_ATTACK);
    }

    /**
     * 检查是否可以攻击
     *
     * @param target 目标对象
     * @return boolean true 能 false 不能
     */
    @Override
    public boolean canAttack(Person target) {
        return super.canAttack(target) && !this.getPersonState().hasFlag(PersonState.Key.RUN_BACK);
    }

    /**
     * 是否允许释放技能,不能使用技能的时候是可以使用普通攻击
     *
     * @return boolean true 能 false 不能
     */
    @Override
    public boolean canUseSkill() {
        return super.canUseSkill() && !this.getPersonState().hasFlag(PersonState.Key.CAN_NOT_USESKILL);
    }

    /**
     * 检查是否可以被攻击
     *
     * @return boolean true 能 false 不能
     */
    @Override
    public boolean canUnCoAttack() {
        return super.canUnCoAttack() && !this.getPersonState().hasFlag(PersonState.Key.CAN_NOT_UNCOATTACK) && !this.getPersonState().hasFlag(PersonState.Key.RUN_BACK);
    }

    /**
     * 检查是否能移动
     *
     * @return boolean true 能 false 不能
     */
    @Override
    public boolean canMove() {
        return super.canMove() && !this.getPersonState().hasFlag(PersonState.Key.CAN_NOT_MOVE);
    }

    @Override
    public boolean canMoveJiTui() {
        return super.canMoveJiTui() && !this.getPersonState().hasFlag(PersonState.Key.CAN_NOT_MOVE_JITUI);
    }

    @Override
    public boolean canMoveLaQu() {
        return super.canMoveLaQu() && !this.getPersonState().hasFlag(PersonState.Key.CAN_NOT_MOVE_LAQU);
    }

    /**
     * 是否可以看见
     *
     * @param person
     * @return
     */
    @Override
    public boolean canSee(MapObject person) {
        if (super.canSee(person)) {
            Person tmp = (Person) person;
            if (!tmp.getPersonState().hasFlag(PersonState.Key.ON_HIDE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString() + ",level=" + level + ", exp=" + exp + ", hp=" + hp + ", mp=" + mp;
    }

}
