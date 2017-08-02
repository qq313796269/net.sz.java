package net.sz.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ZipUtil {

    /**
     * 扩展数组，
     *
     * @param oldArray
     * @param newSize
     * @return
     */
    public static Object resizeArray(Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(
                elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0) {
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        }
        return newArray;
    }

    /**
     * 使用zip进行压缩
     *
     * @param bytes 压缩前
     * @return 返回压缩后
     */
    public static final byte[] zip(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (ZipOutputStream zout = new ZipOutputStream(out)) {
                zout.putNextEntry(new ZipEntry("0"));
                zout.write(bytes);
                zout.closeEntry();
                return out.toByteArray();
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * 使用zip进行压缩
     *
     * @param object 压缩前
     * @return 返回压缩后
     */
    public static final byte[] zipObject(Object object) {
        if (object == null) {
            return null;
        }
        return zip(ObjectStreamUtil.toBytes(object));
    }

    /**
     * 使用zip进行压缩
     *
     * @param str 压缩前的文本
     * @return 返回压缩后的文本
     */
    public static final String zipString(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] zip = zip(str.getBytes("utf-8"));
            return StringUtil.convertToBase64String(zip);
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * 使用zip进行解压缩
     *
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    public static final String unZipString(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] compressed = StringUtil.convertFromBase64Byte(compressedStr);
            try (ByteArrayInputStream in = new ByteArrayInputStream(compressed)) {
                try (ZipInputStream zin = new ZipInputStream(in)) {
                    zin.getNextEntry();
                    byte[] buffer = new byte[1024];
                    int offset = -1;
                    while ((offset = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, offset);
                    }
                    return out.toString("utf-8");
                }
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * 使用zip进行解压缩
     *
     * @param bytes 压缩后
     * @return
     */
    public static final Object unZipObject(byte[] bytes) {
        byte[] unZip = unZip(bytes);
        return ObjectStreamUtil.toObject(unZip);
    }

    /**
     * 使用zip进行解压缩
     *
     * @param bytes 压缩后
     * @return
     */
    public static final byte[] unZip(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
                try (ZipInputStream zin = new ZipInputStream(in)) {
                    zin.getNextEntry();
                    byte[] buffer = new byte[1024];
                    int offset = -1;
                    while ((offset = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, offset);
                    }
                    return out.toByteArray();
                }
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     *
     * @param zipPath zip文件名和路径，
     * @param fileName 存入zip文件的文件名 建议文件名是 .db 或者 .tmp
     * @param source 默认会是utf-8编码
     */
    public static void writeObjectZip(String zipPath, String fileName, Object source) {
        try {
            byte[] bytes = ObjectStreamUtil.toBytes(source);
            writeZip(zipPath, fileName, bytes);
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     *
     * @param zipPath zip文件名和路径，
     * @param fileName 存入zip文件的文件名
     * @param source 默认会是utf-8编码
     */
    public static void writeStringZip(String zipPath, String fileName, String source) {
        writeStringZip(zipPath, fileName, source, "utf-8");
    }

    /**
     *
     * @param zipPath zip文件名和路径，
     * @param fileName 存入zip文件的文件名
     * @param source
     * @param charsetName 编码字符集
     */
    public static void writeStringZip(String zipPath, String fileName, String source, String charsetName) {
        try {
            byte[] bytes = source.getBytes(charsetName);
            writeZip(zipPath, fileName, bytes);
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     *
     * @param zipPath zip文件名和路径，
     * @param fileName 存入zip文件的文件名
     * @param bytes
     */
    public static void writeZip(String zipPath, String fileName, byte[] bytes) {
        File file = new File(zipPath).getParentFile();
        if (!file.exists()) {
            file.mkdir();
        }
        try (FileOutputStream fos = new FileOutputStream(zipPath, true)) {
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                try (BufferedOutputStream out = new BufferedOutputStream(zos)) {
                    out.write(bytes);
                    out.flush();
                    zos.closeEntry();
                }
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * 检查zip文件
     *
     * @param zipPath
     */
    public static void scanZipFile(String zipPath) {
        try (FileInputStream fileInputStream = new FileInputStream(zipPath)) {
            try (ZipInputStream zin = new ZipInputStream(fileInputStream)) {
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    zin.closeEntry();
                }
                zin.close();
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     *
     * @param zipPath zip文件路径和名字
     * @param filename zip文件中的文件名字
     * @return
     */
    public static byte[] loadZipFile(String zipPath, String filename) {
        try (FileInputStream fileInputStream = new FileInputStream(zipPath)) {
            try (ZipInputStream zin = new ZipInputStream(fileInputStream)) {
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    if (entry.getName().equalsIgnoreCase(filename)) {
                        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(zin)) {
                            byte[] readbuffer = new byte[1024];
                            byte[] buffer = new byte[0];
                            int count = 0;
                            while ((count = bufferedInputStream.read(readbuffer, 0, readbuffer.length)) != -1) {
                                buffer = ObjectStreamUtil.concat(buffer, readbuffer, count);
                            }
                            return buffer;
                        }
                    }
                    zin.closeEntry();
                }
                zin.close();
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
        return null;
    }

    /**
     * 默认 字符集 utf-8 读取内容
     *
     * @param zipPath zip文件路径和名字
     * @param filename zip文件中的文件名字
     * @return
     */
    public static String loadStringZipFile(String zipPath, String filename) {
        return loadStringZipFile(zipPath, filename, "utf-8");
    }

    /**
     *
     * @param zipPath zip文件路径和名字
     * @param filename zip文件中的文件名字
     * @param charsetName 字符集 utf-8
     * @return
     */
    public static String loadStringZipFile(String zipPath, String filename, String charsetName) {
        try {
            byte[] loadZipFile = loadZipFile(zipPath, filename);
            if (loadZipFile == null) {
                return null;
            }
            return new String(loadZipFile, charsetName);
        } catch (Throwable ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     *
     * @param zipPath zip文件路径和名字
     * @param filename zip文件中的文件名字
     * @return
     */
    public static Object loadObjectZipFile(String zipPath, String filename) {
        byte[] loadZipFile = loadZipFile(zipPath, filename);
        if (loadZipFile == null) {
            return null;
        }
        return ObjectStreamUtil.toObject(loadZipFile);
    }

    /**
     *
     * @param <T>
     * @param zipPath zip文件路径和名字
     * @param filename zip文件中的文件名字
     * @param clazz
     * @return
     */
    public static <T> T loadObjectZipFile(String zipPath, String filename, Class<T> clazz) {
        byte[] loadZipFile = loadZipFile(zipPath, filename);
        if (loadZipFile == null) {
            return null;
        }
        return (T) ObjectStreamUtil.toObject(loadZipFile);
    }

    public static void main(String[] args) {
        String zipString = zipString("sdfagasgasf阿萨德刚发生的嘎嘎额外嘎斯vfasfsgag");
        System.out.println(zipString);
        System.out.println(unZipString(zipString));
        System.exit(0);
    }

}
