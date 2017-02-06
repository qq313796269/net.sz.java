package net.sz.game.engine.map.spirit;

import net.sz.game.engine.utils.BitUtil;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class PersonPKState implements java.io.Serializable {

    private static final Logger log = Logger.getLogger(PersonPKState.class);

    private int state = Key.PKMODEL_PEACEMODE.getIndex();

    public enum Key {
        /**
         * 0-和平
         */
        PKMODEL_PEACEMODE(0, "和平"),
        /**
         * 1-组队
         */
        PKMODEL_TEAMMODE(1, "组队"),
        /**
         * 2-帮会
         */
        PKMODEL_GUILDMODE(2, "帮会"),
        /**
         * 3-帮会战
         */
        PKMODEL_GUILDWARMODE(3, "帮会战"),
        /**
         * 4-善恶
         */
        PKMODEL_GOODANDEVILMODE(4, "善恶"),
        /**
         * 5-全体
         */
        PKMODEL_ALLTHEMODE(5, "自由模式"),
        /**
         * 6-战场模式A组
         */
        PKMODEL_BATTLE_GROUP_A(6, "战场模式A组"),
        /**
         * 7-战场模式B组
         */
        PKMODEL_BATTLE_GROUP_B(7, "战场模式B组"),;

        int index;
        String msg;

        private Key(int index, String msg) {
            this.index = index;
            this.msg = msg;
        }

        public int getIndex() {
            return index;
        }

        public String getMsg() {
            return msg;
        }

    }

    public boolean hasFlag(Key key) {
        return state == key.index;
    }

    public int getState() {
        return state;
    }

    public Key getStateKey() {
        return getKey(state);
    }

    public void setState(Key state) {
        this.state = state.getIndex();
    }

    public void setState(int state) {
        this.state = state;
    }

    public static Key getKey(int index) {
        Key[] values = Key.values();
        for (Key value : values) {
            if (value.index == index) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getKey(state).getMsg();
    }

}
