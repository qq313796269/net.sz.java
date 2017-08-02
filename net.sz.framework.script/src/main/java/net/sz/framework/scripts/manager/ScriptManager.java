package net.sz.framework.scripts.manager;

import net.sz.framework.scripts.ScriptPool;
import java.io.File;
import java.util.ArrayList;
import net.sz.framework.szlog.SzLogger;

/**
 * 脚本管理器
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ScriptManager {

    private static final SzLogger log = SzLogger.getLogger();

    private static final ScriptManager instance = new ScriptManager();
    private final ScriptPool SManager;	//基础脚本类

    public static ScriptManager getInstance() {
        return instance;
    }

    /*创建全局脚本执行定时器线程*/
    private GlobalScriptTimerThread GlobalScriptTimerThread = null;

    ScriptManager() {
        SManager = new ScriptPool();
        try {
            String property = System.getProperty("user.dir");
            String path = property + "-scripts" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
            String outpath = property + "-scripts" + File.separator + "target" + File.separator + "classes" + File.separator;
            SManager.setSource(path, outpath);
        } catch (Exception ex) {
        }
    }

    /**
     * 开启脚步定时器
     * <br>
     * 需要设置 GlobalUtil.SERVERSTARTEND = true; 标识服务器启动完成
     */
    public void startScriptTimer() {
        if (GlobalScriptTimerThread == null) {
            GlobalScriptTimerThread = new GlobalScriptTimerThread();
        }
    }

    /**
     * 停止脚步定时器
     */
    public void stopScriptTimer() {
        if (GlobalScriptTimerThread != null) {
            GlobalScriptTimerThread.close();
        }
    }

    /**
     * 获取脚步管理器
     *
     * @return
     */
    public ScriptPool getBaseScriptEntry() {
        return SManager;
    }

    /**
     * 读取scripts 和 protohandler 目录下面的所有脚本文件
     * <br>
     * 这个方法会替换和清空所有已在内存中的脚本实例
     *
     * @return
     */
    public ArrayList<String> reload() {
        synchronized (SManager) {
            return SManager.loadJava();
        }
    }

    /**
     * 加载指定实例，可以是文件也可以是目录
     *
     * @param source
     * @return
     */
    public ArrayList<String> loadJava(String... source) {
        synchronized (SManager) {
            return SManager.loadJava(source);
        }
    }

    /**
     * 加载指定实例，可以是文件也可以是目录
     *
     * @param source
     * @return
     */
    public ArrayList<String> loadClass(String... source) {
        synchronized (SManager) {
            return SManager.loadClass(source);
        }
    }

    /**
     * 加载scripts目录下面的所有脚本文件
     *
     * @return
     */
    public ArrayList<String> loadJavaScripts() {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        synchronized (SManager) {
            return SManager.loadJava(p + File.separator + "scripts");
        }
    }

    /**
     * 加载scripts目录下面的所有脚本文件
     *
     * @return
     */
    public ArrayList<String> loadClassScripts() {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        synchronized (SManager) {
            return SManager.loadClass(p + File.separator + "scripts");
        }
    }

    /**
     * 重新加载handler
     *
     * @return
     */
    public ArrayList<String> loadJavaHandlers() {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        synchronized (SManager) {
            return SManager.loadJava(p + File.separator + "proto");
        }
    }

    /**
     * 重新加载handler
     *
     * @return
     */
    public ArrayList<String> loadClassHandlers() {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        synchronized (SManager) {
            return SManager.loadClass(p + File.separator + "proto");
        }
    }

    public static void main(String[] args) {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        log.error(p);
    }
}
