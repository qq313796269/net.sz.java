package net.sz.framework.szlog;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public enum LogLevel {
    /**
     * DEBUG,调试信息
     */
    DEBUG("DEBUG", 1, "DEBUG"),
    /**
     * 正常信息
     */
    INFO("INFO", 2, "INFO "),
    /**
     * 表明会出现潜在错误的情形
     */
    WARN("WARN", 3, "WARN "),
    /**
     * 指出虽然发生错误事件，但仍然不影响系统的继续运行
     */
    ERROR("ERROR", 4, "ERROR"),
    /**
     * 指出每个严重的错误事件将会导致应用程序的退出
     */
    FATAL("FATAL", 5, "FATAL"),;
    private String level;
    private int group;
    private String levelString;

    private LogLevel(String level, int group, String levelString) {
        this.level = level;
        this.group = group;
        this.levelString = levelString;
    }

    public String getLevel() {
        return level;
    }

    public int getGroup() {
        return group;
    }

    public String getLevelString() {
        return levelString;
    }

    public static LogLevel getLevel(String le) {
        LogLevel[] values = LogLevel.values();
        for (int i = 0; i < values.length; i++) {
            LogLevel value = values[i];
            if (value.getLevel().equalsIgnoreCase(le)) {
                return value;
            }
        }
        throw new UnsupportedOperationException("无法确认日志级别：" + le);
    }
    
}
