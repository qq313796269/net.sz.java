package net.sz.game.engine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class FileUtil {

    private static final Logger log = Logger.getLogger(FileUtil.class);

    /**
     * 读取文件里面所有字符串
     *
     * @param filePath
     * @param charsetName
     * @return
     */
    public static String readFileToString(String filePath, String charsetName) {
        File file = new File(filePath);
        try (FileInputStream in = new FileInputStream(file)) {
            Long filelength = file.length();     //获取文件长度
            byte[] filecontent = new byte[filelength.intValue()];
            in.read(filecontent);
            return new String(filecontent, charsetName);//返回文件内容,默认编码
        } catch (Exception ex) {
            log.error("读取文件内容：", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 读取文件里面所有字符串
     *
     * @param filePath
     * @return
     */
    public static byte[] readFileToBytes(String filePath) {
        File file = new File(filePath);
        try (FileInputStream in = new FileInputStream(file)) {
            Long filelength = file.length();     //获取文件长度
            byte[] filecontent = new byte[filelength.intValue()];
            in.read(filecontent);
            return filecontent;//返回文件内容,默认编码
        } catch (Exception ex) {
            log.error("读取文件内容：", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 读取文件并且解压缩字节
     *
     * @param filePath
     * @return
     */
    public static byte[] readFileToZipBytes(String filePath) {
        File file = new File(filePath);
        try (FileInputStream in = new FileInputStream(file)) {
            Long filelength = file.length();     //获取文件长度
            byte[] filecontent = new byte[filelength.intValue()];
            in.read(filecontent);
            return ZipUtil.zip(filecontent);//返回文件内容,默认编码
        } catch (Exception ex) {
            log.error("读取文件内容：", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 读取文件里面所有字符串
     *
     * @param filePath
     * @return
     */
    public static Object readFileToObject(String filePath) {
        byte[] readFileToBytes = readFileToBytes(filePath);
        return ObjectStreamUtil.toObject(readFileToBytes);
    }

    /**
     * 读取文件内容，解压缩，返回对象
     *
     * @param filePath
     * @return
     */
    public static Object readFileToZipObject(String filePath) {
        byte[] readFileToBytes = readFileToZipBytes(filePath);
        return ZipUtil.unZipObject(readFileToBytes);
    }

    /**
     * 把字符串书序到文件错误
     *
     * @param obj
     * @param filePath
     * @param charsetName
     */
    public static void writerFile(String obj, String filePath, String charsetName) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            try (OutputStreamWriter osw = new OutputStreamWriter(fos, charsetName)) {
                osw.write(obj);
                osw.flush();
            }
        } catch (Exception ex) {
            log.error("把字符串书序到文件错误：", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param obj
     * @param filePath
     */
    public static void writerFile(byte[] obj, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(obj);
            fos.flush();
        } catch (Exception ex) {
            log.error("把字符串书序到文件错误：", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param obj
     * @param filePath
     */
    public static void writerObjectFile(Object obj, String filePath) {
        byte[] toBytes = ObjectStreamUtil.toBytes(obj);
        writerFile(toBytes, filePath);
    }

    /**
     * 书写对象字节流，并且压缩
     *
     * @param obj
     * @param filePath
     */
    public static void writerZipObjectFile(Object obj, String filePath) {
        byte[] zipObject = ZipUtil.zipObject(obj);
        writerFile(zipObject, filePath);
    }
}
