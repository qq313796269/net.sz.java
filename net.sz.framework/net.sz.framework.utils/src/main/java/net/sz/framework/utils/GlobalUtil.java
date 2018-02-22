package net.sz.framework.utils;

import java.util.UUID;
import net.sz.framework.util.IntegerSSId;
import net.sz.framework.util.LongId3;

/**
 *
 */
public class GlobalUtil {

    /**
     * 万分比
     */
    public static final double MAX_PROBABILITY_D = 10000D;

    /**
     * 万分比
     */
    public static final int MAX_PROBABILITY = 10000;

    /**
     * 默认读取屏蔽字库平台名
     */
    public static String WORDFILTER_AGENT = "";

    /**
     * 根据策划要求，修改此版本的最大等级为60级，原定的级数为70级
     */
    public static int MAX_LEVEL = 60;

    /**
     * 初始背包数量
     */
    public static int DEFAULT_BAG_CELLS = 24;

    /**
     * 最大背包数量
     */
    public static int DEFAULT_BAG_CELLS_MAX = 64;
    /**
     * 小金库的最大格子数
     */
    public static int DEFAULT_BAG_TEMP_MAX = 432;

    /**
     * 玩家等级超过怪物等级不再掉落东西
     */
    public static int DEFAULT_DROP_LEVELC_I = 15;

    /**
     * 掉落物品在地图存在时间周期
     */
    public static int DEFAULT_DROP_LOSETIME = 2 * 10 * 1000;

    /**
     * 掉落物品在地图存在玩家专属时间周期
     */
    public static int DEFAULT_DROP_LOSEPLAYERTIME = 2 * 10 * 1000;

    /**
     * 初始仓库数量
     */
    public static int DEFAULT_STORE_CELLS = 10;

    /**
     * 最大仓库数量
     */
    public static int DEFAULT_STORE_CELLS_MAX = 125;

    /**
     * 击杀精英怪物有效任务计数所需的伤害比例
     */
    public static double TASK_EFFECTIVE_JINYIN_DAMAGE_RATIO = 0.01;

    /**
     * 击杀怪物有效任务计数所需的伤害比例
     */
    public static double TASK_EFFECTIVE_DAMAGE_RATIO = 0.02;

    /**
     * boss死亡后站立时间
     */
    public static int BOSS_DIEING = 300000;

    /**
     * 自动复活间隔
     */
    public static long PLAYER_RECOVER_TIME = 5000;

    /**
     * 战场自动复活间隔
     */
    public static long BATTLE_RECOVER_TIME = 5000;

    /**
     * 与NPC的交易/对话距离(米) 8
     */
    public static float NPC_TRADE_DISTANCE = 8;

    /**
     * 怪物回血间隔 5 * 1000
     */
    public static int MONSTER_RECOVERY_INTERVALTIME = 5 * 1000;

    /**
     * 新手保护等级
     */
    public static int NEWBIE_LEVEL = 18;

    /**
     * 战斗状态过期时间.也就是说,6秒内,玩家没攻击,或被攻击,那么玩家退出战斗状态
     */
    public static int FIGHT_OVERDUE = 3000;

    /**
     * 组队BUFF距离限制
     */
    public static int TEAM_BUFF = 10;
    /**
     * 服务器启动完成
     */
    public static Boolean SERVERSTARTEND = false;

    /**
     * 游戏ID
     */
    private static String gameId = "1";
    /**
     * 平台ID
     */
    private static String platformId = "1";
    /**
     * 服务器id不能超过999
     */
    private static int serverId = 0;
    /**
     * 服务器id不能超过999
     */
    private static String serverName = "";
    /**
     *
     */
    private static LongId3 ids = null;
    /**
     * 用于场景的一切对象生成id，包 玩家id，怪物id，保证不重复
     */
    private static LongId3 mapObjectIdUtil = null;
    /**
     * 用于场景副本 ID 保证不重复
     */
    private static IntegerSSId mapIdUtil = null;

    /**
     * 用于邮件的id
     */
    private static LongId3 mailIdUtil = null;

    public static int getServerId() {
        return serverId;
    }

    public static void setServerId(int serverId) {
        GlobalUtil.serverId = serverId;
        mapIdUtil = new IntegerSSId();
        ids = new LongId3(GlobalUtil.getServerId());
        mapObjectIdUtil = new LongId3(GlobalUtil.getServerId());
        mailIdUtil = new LongId3(GlobalUtil.getServerId());
    }

    public static String getGameId() {
        return gameId;
    }

    public static void setGameId(String gameId) {
        GlobalUtil.gameId = gameId;
    }

    public static String getPlatformId() {
        return platformId;
    }

    public static void setPlatformId(String platformId) {
        GlobalUtil.platformId = platformId;
    }

    public static String getServerName() {
        return serverName;
    }

    public static void setServerName(String serverName) {
        GlobalUtil.serverName = serverName;
    }

    /**
     * 通用,，每秒钟10万
     *
     * @return
     */
    public static long getId() {
        return ids.getId();
    }

    /**
     * 包括副本ID，战场 ，每秒钟1万
     *
     * @return
     */
    public static int getMapId() {
        return mapIdUtil.getId();
    }

    /**
     * 用于场景的一切对象生成id，玩家id，怪物id，掉落物，npc，保证不重复，每秒钟10万
     *
     * @return
     */
    public static long getMapObjectId() {
        return mapObjectIdUtil.getId();
    }

    /**
     * 邮件，保证不重复，每秒钟10万
     * @return
     */
    public static long getMailId() {
        return mailIdUtil.getId();
    }

    /**
     * 根据uuid生成id
     * <br>
     * 无规则可言
     *
     * @return
     */
    static public long getUUIDToLong() {
        String toString = getUUID();
        byte[] bytes = toString.getBytes();
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (bytes[ix] & 0xff);
        }
        return num;
    }

    /**
     * 根据uuid生成id
     * <br>
     * 无规则可言
     *
     * @return
     */
    static public String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    /**
     * 运营分区：平台_区域。规范服务器ID
     */
    public static PlatZone PLATZONE = PlatZone.NONE;

    public static void checkPlatZone(String gameid, String platform) {
        PLATZONE = PlatZone.getPlatZone(gameid, platform);
    }

    /**
     * 运营分区：平台_区域。规范服务器ID Ios越狱	1-1000 台湾	1001-2000 越南	2001-3000 新马	3001-4000 欧美
     * 4001-5000 Ios	5001-6000 安卓	6001-7000 私服	7001-8000
     */
    public enum PlatZone {
        /**
         * 0, 0, "无平台"
         */
        NONE("0", "0", "无平台"),
        /**
         * 10001, 100, "PC包平台"
         */
        LOCAL("10001", "100", "PC包平台"),
        /**
         * 10001, 150, "提审服平台"
         */
        TISHEN("10001", "150", "提审服平台"),
        /**
         * 10001, 200, "IOS，APP 平台"
         */
        IOS("10001", "200", "IOS，APP 平台"),
        /**
         * 10001, 600, "台湾平台"
         */
        TaiWan("10001", "600", "台湾平台"),;
        private final String gameid;
        private final String platformId;
        private final String platformName;

        private PlatZone(String gameid, String platform, String platformName) {
            this.gameid = gameid;
            this.platformId = platform;
            this.platformName = platformName;
        }

        public String getGameid() {
            return gameid;
        }

        public String getPlatformId() {
            return platformId;
        }

        public String getPlatformName() {
            return platformName;
        }

        public static PlatZone getPlatZone(String gameid, String platform) {
            PlatZone pz = PlatZone.NONE;
            for (PlatZone p : PlatZone.values()) {
                if (p.platformId.equalsIgnoreCase(platform) && p.gameid.equalsIgnoreCase(gameid)) {
                    pz = p;
                    break;
                }
            }
            if (pz == PlatZone.NONE) {
                throw new UnsupportedOperationException("服务器配置错误，不属于任何一个渠道");
            }
            return pz;
        }
    }

    /**
     * 渠道标识
     */
    public enum PlatChannel {

        /**
         * 0, "无渠道"
         */
        None("0", "0", "0", "无渠道"),
        /**
         * 100, "PC包"
         */
        PC("0", "0", "100", "PC包"),
        /**
         * 200, "IOS渠道"
         */
        IOS("0", "0", "200", "IOS渠道"),
        /**
         * 200, "安卓渠道"
         */
        AND("0", "0", "200", "安卓渠道"),
        /**
         * 502, "MyCard渠道"
         */
        MyCard("0", "0", "502", "MyCard渠道"),;

        private final String gameid;
        private final String platformId;
        private final String channelid;
        private final String channelName;

        PlatChannel(String gameid, String platformId, String channelid, String channelName) {
            this.gameid = gameid;
            this.platformId = platformId;
            this.channelid = channelid;
            this.channelName = channelName;
        }

        public String getGameid() {
            return gameid;
        }

        public String getPlatformId() {
            return platformId;
        }

        public String getChannelid() {
            return channelid;
        }

        public String getChannelName() {
            return channelName;
        }

        @Override
        public String toString() {
            return "{" + "gameid=" + gameid + ", platformId=" + platformId + ", channelid=" + channelid + ", channelName=" + channelName + '}';
        }

        public static PlatChannel getPlatChannel(String channelid) {
            PlatChannel pz = PlatChannel.None;
            for (PlatChannel p : PlatChannel.values()) {
                if (p.channelid.equalsIgnoreCase(channelid)) {
                    pz = p;
                    break;
                }
            }
            if (pz == PlatChannel.None) {
                throw new IllegalArgumentException("不是任何配置渠道信息");
            }
            return pz;
        }
    }

}
