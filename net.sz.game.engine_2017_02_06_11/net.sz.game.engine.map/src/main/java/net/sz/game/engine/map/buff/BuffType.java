package net.sz.game.engine.map.buff;

import java.io.Serializable;
import net.sz.game.engine.utils.StringUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class BuffType implements Serializable {

    private static final long serialVersionUID = 7642616840757469267L;
    private long typeValue;

    private BuffType() {
    }

    public enum TypeKey {

        /**
         * 0,"无意义buff"
         */
        Null(0, "无意义buff"),
        /**
         * 1 属性buff
         */
        ATTR(1, "属性buff"),
        /**
         * 2, "变身buff"
         */
        MODEL(2, "变身buff"),
        /**
         * 3 状态buff
         */
        State(3, "状态buff"),
        /**
         * 4 特殊buff，执行run方法
         */
        Runnable(4, "特殊buff，执行run方法"),
        /**
         * 5 回复性buff
         */
        Reply(5, "回复性buff"),
        /**
         * 6 回复性buff,直接回满
         */
        ReplyMax(6, "回复性buff,直接回满"),
        /**
         * 7, "定时触发buff"
         */
        Timer(7, "定时触发buff"),
        //以下是倒序，特殊值
        /**
         * 60 死亡需要删除的buffer
         */
        DieDel(60, "死亡需要删除的buffer"),
        /**
         * 61 下线需要删除的buffer
         */
        OffOnlineDel(61, "下线需要删除的buffer"),
        /**
         * 62 切换场景需要删除的buff
         */
        ChangeMapDel(62, "切换场景删除buff"),
        /**
         * 63	脚本BUFF
         */
        SCRIPTBUFF(63, "脚本BUFF");

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

    }

    // <editor-fold defaultstate="collapsed" desc="是否包含一个状态 public boolean hasFlag(BuffType buffType) ">
    /**
     * 是否包含一个状态
     *
     * @param tvalue
     * @return
     */
    public boolean hasFlag(TypeKey tvalue) {
        return (typeValue & tvalue.value) != 0;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 public void addStatus(BuffType buffType)">
    /**
     * 增加一个状态
     *
     * @param buffType
     */
    public void addStatus(TypeKey buffType) {
        typeValue = typeValue | buffType.value;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加多个状态 public void addStatusString(String indexs)">
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
                TypeKey typeKey = getBuffTypeByIndex(parseInt);
                /*添加对应的状态*/
                addStatus(typeKey);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="移除一个状态 public void removeStatus(BuffType buffType)">
    /**
     * 移除一个状态
     *
     * @param buffType
     */
    public void removeStatus(TypeKey buffType) {
        typeValue = typeValue & (~buffType.value);
    }
    // </editor-fold>

    public String show() {
        return "结果：" + StringUtil.padLeft(this.typeValue + "", 19, " ") + " -> " + StringUtil.padLeft(Long.toBinaryString(this.typeValue), 64, "0");
    }

    public static BuffType getNull() {
        BuffType buffType = new BuffType();
        return buffType;
    }

    /**
     * 根据值，策划配置的值
     *
     * @param index
     * @return
     */
    public static TypeKey getBuffTypeByIndex(int index) {
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
    public static TypeKey getBuffType(String typeString) {
        return Enum.valueOf(TypeKey.class, typeString);
    }
}
