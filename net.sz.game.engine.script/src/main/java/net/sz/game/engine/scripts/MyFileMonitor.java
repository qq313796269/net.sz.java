package net.sz.game.engine.scripts;

import java.io.File;
import java.util.ArrayList;
//import org.apache.commons.io.monitor.FileAlterationListener;
//import org.apache.commons.io.monitor.FileAlterationMonitor;
//import org.apache.commons.io.monitor.FileAlterationObserver;

import net.sz.game.engine.szlog.SzLogger;

/**
 * 文件和文件夹监控器
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MyFileMonitor {

    private static SzLogger log = SzLogger.getLogger();
//    static ArrayList<FileAlterationMonitor> monitors = new ArrayList<>(0);

    /**
     * 初始化监控文件夹
     *
     * @param interval 监控扫描文件的间隔时间
     * @param clazz 继承至 org.apache.commons.io.monitor.FileAlterationListener
     * @param dirPaths 需要监控扫描的文件夹列表
     */
//    public MyFileMonitor(long interval, FileAlterationListener clazz, String... dirPaths) {
//        for (String dirPath : dirPaths) {
//            FileAlterationObserver observer = new FileAlterationObserver(new File(dirPath));
//            observer.addListener(clazz);
//
//            FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor(interval);
//            fileAlterationMonitor.addObserver(observer);
//
//            monitors.add(fileAlterationMonitor);
//            log.info("添加监控：" + observer.getDirectory().getPath());
//        }
//    }
//
//    public void dispose() {
//        try {
//            for (FileAlterationMonitor monitor : monitors) {
//                monitor.stop();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void start() {
//        try {
//            for (FileAlterationMonitor monitor : monitors) {
//                monitor.start();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            log.error("删除文件失败:" + fileName + "不存在！");
            return false;
        } else if (file.isFile()) {
            return deleteFile(fileName);
        } else {
            return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                log.error("删除文件" + fileName + "成功！");
                return true;
            } else {
                log.error("删除文件" + fileName + "失败！");
                return false;
            }
        } else {
            log.error("删除文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            log.error("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            log.error("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            log.error("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }
}
