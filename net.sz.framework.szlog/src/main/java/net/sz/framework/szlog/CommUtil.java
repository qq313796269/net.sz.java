package net.sz.framework.szlog;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * 日志
 * <br>szlogger.ini 设置 CONSOLE_PRINT 日志是否输出到控制台 true or false
 * <br>szlogger.ini 设置 LOG_LEVEL 日志的等级,忽律大小写 DEBUG INFO WARN ERROR
 * <br>szlogger.ini 设置 LOG_PATH 日志的文件名带目录，log/sz.log
 * <br>szlogger.ini 设置 CONSOLE_FILE 日志是否输出到文件 true or false
 * <br>szlogger.ini 设置 CONSOLE_FILE_BUFFER 日志双缓冲输出到文件 true or false
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CommUtil {

    static final SimpleDateFormat DateFormat_File = new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat DateFormat_Log = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
    static final ThreadGroup LOG_THREAD_GROUP = new ThreadGroup("LOG_THREAD_GROUP");
    /**
     * 缓冲时间
     */
    public static long BUFFER_TIME_LOGSIZE = 2000;
    /**
     * 打印到控制台
     */
    public static boolean LOG_PRINT_CONSOLE = true;
    /**
     * 输出到文件
     */
    public static boolean LOG_PRINT_FILE = true;
    /**
     * 输出到文件,使用 BUUFER 双缓冲
     */
    public static boolean LOG_PRINT_FILE_BUUFER = true;
    /**
     * 日志文件存放路径
     */
    public static String LOG_PRINT_PATH = "../log/sz.log";
    /**
     * 字符集
     */
    public static String CHARSET_NAME = "utf-8";

    public static boolean ISRUN = true;

    /**
     * 日志打印级别
     */
    public static LogLevel LOG_PRINT_LEVEL = LogLevel.DEBUG;

    /**
     * 配置文件名
     */
    private static final String CONFIG_FILE_NAME = "szlogger.ini";

    /**
     * 从配置文件中读取字符串的值 配置文件查找顺序： 1-项目根路径 2-src/main/resources
     *
     * @param keyName 属性名
     * @return 属性值
     */
    static void initConfig() {
        System.getProperties().put("sun.stdout.encoding", "utf-8");
        System.getProperties().put("sun.stderr.encoding", "utf-8");

        System.out.println("设置系统字符集sun.stdout.encoding：" + System.getProperties().get("sun.stdout.encoding"));
        System.out.println("设置系统字符集sun.stderr.encoding：" + System.getProperties().get("sun.stderr.encoding"));

        Properties props = null;

        String filePath = CONFIG_FILE_NAME;

        InputStream resourceAsStream = null;

        try {
            resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(filePath);
            if (resourceAsStream != null) {

                props = new Properties();
                props.load(resourceAsStream);

                String property_LOG_LEVEL = props.getProperty("LOG_LEVEL");
                if (property_LOG_LEVEL != null && !"".equals(property_LOG_LEVEL)) {
                    CommUtil.LOG_PRINT_LEVEL = LogLevel.getLevel(property_LOG_LEVEL);
                }

                String property_CONSOLE_FILE = props.getProperty("CONSOLE_FILE");
                if (property_CONSOLE_FILE != null && !"".equals(property_CONSOLE_FILE)) {
                    CommUtil.LOG_PRINT_FILE = Boolean.parseBoolean(property_CONSOLE_FILE);
                }

                String property_CONSOLE_FILE_BUFFER = props.getProperty("CONSOLE_FILE_BUFFER");
                if (property_CONSOLE_FILE != null && !"".equals(property_CONSOLE_FILE_BUFFER)) {
                    CommUtil.LOG_PRINT_FILE_BUUFER = Boolean.parseBoolean(property_CONSOLE_FILE_BUFFER);
                }

                String property_CONSOLE_PRINT = props.getProperty("CONSOLE_PRINT");
                if (property_CONSOLE_PRINT != null && !"".equals(property_CONSOLE_PRINT)) {
                    CommUtil.LOG_PRINT_CONSOLE = Boolean.parseBoolean(property_CONSOLE_PRINT);
                }

                String property_LOG_PATH = props.getProperty("LOG_PATH");
                if (property_LOG_PATH != null && !"".equals(property_LOG_PATH)) {
                    CommUtil.LOG_PRINT_PATH = property_LOG_PATH;
                }

            }
        } catch (Exception e) {
            System.out.println("配置文件 szlogger.ini 未找到 或 配置有错 使用默认设置");
        } finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            } catch (Exception e) {;
            }
        }

        System.out.println("日志级别：" + CommUtil.LOG_PRINT_LEVEL);
        System.out.println("输出文件日志目录：" + CommUtil.LOG_PRINT_PATH);
        System.out.println("是否输出控制台日志：" + CommUtil.LOG_PRINT_CONSOLE);
        System.out.println("是否输出文件日志：" + CommUtil.LOG_PRINT_FILE);
        System.out.println("是否使用双缓冲输出文件日志：" + CommUtil.LOG_PRINT_FILE_BUUFER);

    }
}
