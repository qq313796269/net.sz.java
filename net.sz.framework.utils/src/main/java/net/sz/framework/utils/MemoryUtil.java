package net.sz.framework.utils;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MemoryUtil {

    public static String getMemory() {
        //虚拟机占用的内存
        long maxMemory = Runtime.getRuntime().maxMemory(); // 返回 Java 虚拟机试图使用的最大内存量。
        long freeMemory = Runtime.getRuntime().freeMemory(); //返回 Java 虚拟机中的空闲内存量。
        long totalMemory = Runtime.getRuntime().totalMemory(); // 返回 Java 虚拟机中的内存总量。
        return "虚拟机当前内存总量：" + ((double) totalMemory / 1024 / 1024) + "mb，虚拟机中的空闲内存量：" + ((double) freeMemory / 1024 / 1024) + "mb，虚拟机试图使用的最大内存量：" + ((double) maxMemory / 1024 / 1024) + "mb";
    }

}
