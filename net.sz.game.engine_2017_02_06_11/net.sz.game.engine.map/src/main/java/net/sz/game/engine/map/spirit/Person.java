package net.sz.game.engine.map.spirit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.sz.game.engine.map.MapObject;
import net.sz.game.engine.map.buff.Buff;
import net.sz.game.engine.map.skill.Skill;
import net.sz.game.engine.struct.Cooldown;
import net.sz.game.engine.struct.Lock;

/**
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public abstract class Person extends MapObject {

    private static final long serialVersionUID = -7778568015291171928L;

    //人物属性锁(锁属性计算多线程时的重复计算错误)
    private transient Lock abilityLock = new Lock();
    /*用于移动同步的时候判断是否已经同步过了*/
    private transient HashMap<SpiritType, HashSet<Long>> lockingIn = null;
    //资源
    protected String res;

    protected transient PersonState personState;
    /*数据库记录当前pk状态*/
    protected PersonPKState dbPKState;
    /*当前pk状态*/
    protected transient PersonPKState personPKState;
    //头像
    protected String icon;
    //玩家在数据中心中专消息的客户端连接Id
    protected transient String clientSocketId;
    //老旧的
    protected int oldModelID;
    //时装模板Id
    protected int fashionModelID;
    //等级
    protected int level;

    //经验
    protected long exp;

    //当前生命
    protected int hp;

    //当前魔法
    protected int mp;

    //移动请求时间
    protected transient long lastMoveBeginTime;

    //BUFF列表
    protected ArrayList<Buff> buffs = new ArrayList<>();

    //冷却列表
    protected HashMap<String, Cooldown> cooldowns = new HashMap<>();
    // 模型半径,一般用于体形巨大的怪物上,如炎魔
    protected float modelRadius;
    // ------------------技能部分begin--------------------//
    /**
     * 技能,技能模板id，技能
     */
    private HashMap<Integer, Skill> skillsHashMap = new HashMap<>();
    // ------------------技能部分end----------------------//
    // 属性
    //最终计算属性
    protected BaseAbility finalAbility = new BaseAbility();

    //-------------------属性部分-------------------------//
    private /*transient这样可能会导致离线竞技场角色数据不正确*/ HashMap<Integer, PersonAttribute> attributes = new HashMap<>();
    //其他属性
    protected transient PersonAttribute tmpAttribute = new PersonAttribute();
    //-------------------属性部分end-------------------------//

    //经验加成
    protected transient double expMultiple;

    protected transient long execTime = 0l;
    /*对象死亡时间*/
    protected long dieTimer;
    /*最后一次战斗时间*/
    protected transient long lastFightTime;
    /*最后一次移动时间*/
    protected transient long lastMoveTime;
    //最后一次被攻击时间（boss用）
    protected transient long lastAttackTime;

    public Person(long id) {
        super(id);
    }

    /**
     * 用于移动同步的时候判断是否已经同步过了
     *
     * @return
     */
    public HashMap<SpiritType, HashSet<Long>> getLockingIn() {
        if (lockingIn == null) {
            lockingIn = new HashMap<>();
        }
        return lockingIn;
    }

    public void setLockingIn(HashMap<SpiritType, HashSet<Long>> lockingIn) {
        this.lockingIn = lockingIn;
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(long execTime) {
        this.execTime = execTime;
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

    public HashMap<String, Cooldown> getCooldowns() {
        return cooldowns;
    }

    public void setCooldowns(HashMap<String, Cooldown> cooldowns) {
        this.cooldowns = cooldowns;
    }

    public long getLastMoveBeginTime() {
        return lastMoveBeginTime;
    }

    public void setLastMoveBeginTime(long lastMoveBeginTime) {
        this.lastMoveBeginTime = lastMoveBeginTime;
    }

    public long getLastFightTime() {
        return lastFightTime;
    }

    public void setLastFightTime(long lastFightTime) {
        this.lastFightTime = lastFightTime;
    }

    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
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

    public Lock getAbilityLock() {
        if (abilityLock == null) {
            abilityLock = new Lock();
        }
        return abilityLock;
    }

    public void setAbilityLock(Lock abilityLock) {
        this.abilityLock = abilityLock;
    }

    public int getFashionModelID() {
        return fashionModelID;
    }

    public void setFashionModelID(int fashionModelID) {
        this.fashionModelID = fashionModelID;
    }

    public float getModelRadius() {
        return modelRadius;
    }

    public void setModelRadius(float modelRadius) {
        this.modelRadius = modelRadius;
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

    public long getDieTimer() {
        return dieTimer;
    }

    public void setDieTimer(long dieTimer) {
        this.dieTimer = dieTimer;
    }

    public boolean isQuit() {
        return this.personState.hasFlag(PersonState.Key.QUIT);
    }

    public boolean isDie() {
        return this.personState.hasFlag(PersonState.Key.DIE)
                || this.personState.hasFlag(PersonState.Key.DIEING)
                || this.personState.hasFlag(PersonState.Key.DIEWAIT);
    }

    public boolean isHPDie() {
        return this.getHp() <= 0;
    }

    /**
     * 检查是否可以攻击
     *
     * @return boolean true 能 false 不能
     */
    public boolean canAttack() {
        return !this.personState.hasFlag(PersonState.Key.CAN_NOT_ATTACK);
    }

    /**
     * 检查是否可以攻击
     *
     * @param target 目标对象
     * @return boolean true 能 false 不能
     */
    public boolean canAttack(Person target) {
        if (this.canAttack()) {
            /*自己可攻击的情况下判断对方是否是可被攻击状态*/
            return target.canUnCoAttack();
        }
        return false;
    }

    /**
     * 是否允许释放技能,不能使用技能的时候是可以使用普通攻击
     *
     * @return boolean true 能 false 不能
     */
    public boolean canUseSkill() {
        return !this.personState.hasFlag(PersonState.Key.CAN_NOT_USESKILL);
    }

    /**
     * 检查是否可以被攻击
     *
     * @return boolean true 能 false 不能
     */
    public boolean canUnCoAttack() {
        return !this.personState.hasFlag(PersonState.Key.CAN_NOT_UNCOATTACK);
    }

    /**
     * 检查是否能移动
     *
     * @return boolean true 能 false 不能
     */
    public boolean canMove() {
        return !this.personState.hasFlag(PersonState.Key.CAN_NOT_MOVE);
    }

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
