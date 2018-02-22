package net.sz.framework.szlog;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class WriterFile implements Closeable{


    public WriterFile() {

    }

    File file = null;
    BufferedWriter out = null;

    /**
     * 创建文件
     */
    void createFileWriter(String fileName) {
        try {
            String filepath=fileName;
            if (fileName == null || fileName.trim().isEmpty()) {
                filepath = CommUtil.LOG_PRINT_PATH;
            }

            if (file == null) {
                file = new File(filepath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            }

            long lastModified = file.lastModified();
            String ctime = CommUtil.DateFormat_File.format(new Date(lastModified));
            String ctimeNew = CommUtil.DateFormat_File.format(new Date());
            /*日志按照每天进行备份*/
            if (!ctimeNew.equalsIgnoreCase(ctime)) {
                File filenew = new File(filepath + "_" + ctime + ".log");
                if (filenew.exists()) {
                    filenew.deleteOnExit();
                }
                file.renameTo(filenew);
                /* 重新 new 新文件 */
                file = new File(filepath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                flush();
                /* 清空 */
                if (out != null) {
                    out.flush();
                    out.close();
                }
                out = null;
            }
            if (out == null) {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * 写入日志内容
     *
     * @param conent
     */
    void write(String conent) {
        try {
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * 刷新缓冲区
     */
    void flush() {
        try {
            if (out != null) {
                out.flush();
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }

    /**
     * 资源
     */
    @Override
    public void close() {

        try {
            if (out != null) {
                out.flush();
                out.close();
            }
        } catch (Throwable e) {
            e.printStackTrace(System.out);
        }
    }
}
