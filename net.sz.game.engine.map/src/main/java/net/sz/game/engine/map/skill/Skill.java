package net.sz.game.engine.map.skill;

import net.sz.game.engine.struct.ObjectBase;
import net.sz.game.engine.map.spirit.BaseAbility;
import net.sz.game.engine.utils.GlobalUtil;

/**
 * 技能
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Skill extends ObjectBase {

    private static final long serialVersionUID = -4048773480706290213L;

    //技能模板Id
    private int skillModelId;
    //技能等级
    private int skillLevel;
    //被动技能增加的属性
    private transient BaseAbility skillAbility;
    // 产生技能的原因
    private transient SkillFrom skillFrom = SkillFrom.FROM_DEFAULT;

    public Skill() {
        super.id = GlobalUtil.getId();
    }

    public int getSkillModelId() {
        return skillModelId;
    }

    public void setSkillModelId(int skillModelId) {
        this.skillModelId = skillModelId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public BaseAbility getSkillAbility() {
        return skillAbility;
    }

    public void setSkillAbility(BaseAbility skillAbility) {
        this.skillAbility = skillAbility;
    }

    public SkillFrom getSkillFrom() {
        return skillFrom;
    }

    public void setSkillFrom(SkillFrom skillFrom) {
        this.skillFrom = skillFrom;
    }

    @Override
    public String toString() {
        return "Skill{" + "skillModelId=" + skillModelId + ", skillLevel=" + skillLevel + ", skillAbility=" + skillAbility + ", skillFrom=" + skillFrom + '}';
    }

}
