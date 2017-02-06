package net.sz.game.engine.scripts.manager;

import net.sz.game.engine.scripts.ScriptPool;
import java.io.File;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * 脚本管理器
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ScriptManager {

    private static final Logger log = Logger.getLogger(ScriptManager.class);

    private static final ScriptManager instance = new ScriptManager();
    private static final ScriptPool SManager;	//基础脚本类

    static {
        SManager = new ScriptPool();
        try {
            String property = System.getProperty("user.dir");
            String path = property + "-scripts" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
            String outpath = property + File.separator + "target" + File.separator + "scriptsbin" + File.separator;
            SManager.setSource(path, outpath);
        } catch (Exception ex) {
        }
    }

    public static ScriptManager getInstance() {
        return instance;
    }

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
        return SManager.loadJava();
    }

    /**
     * 加载指定实例，可以是文件也可以是目录
     *
     * @param source
     * @return
     */
    public ArrayList<String> loadJava(String... source) {
        return SManager.loadJava(source);
    }

    /**
     * 加载scripts目录下面的所有脚本文件
     *
     * @return
     */
    public ArrayList<String> loadScripts() {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        return SManager.loadJava(p + File.separator + "scripts");
    }

    /**
     * 重新加载handler
     *
     * @return
     */
    public ArrayList<String> loadHandlers() {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        return SManager.loadJava(p + File.separator + "proto");
    }

    public static void main(String[] args) {
        String replace = System.getProperty("user.dir").replace(File.separator, "=");
        String[] split = replace.split("=");
        String p = split[split.length - 1];
        p = p.replace(".", File.separator);
        log.error(p);
    }
}
