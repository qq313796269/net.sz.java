package net.sz.game.engine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ObjectStreamUtil {

    private static final Logger log = Logger.getLogger(ObjectStreamUtil.class);

    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
                oos.flush();
                return baos.toByteArray();
            }
        } catch (Exception ex) {
            log.error("对象转 byte[] 出现错误", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 数组转对象
     *
     * @param <T>
     * @param clazz
     * @param bytes
     * @return
     */
    public static <T> T toObject(Class<T> clazz, byte[] bytes) {
        try {
            return (T) toObject(bytes);
        } catch (Exception ex) {
            log.error(" byte[] 转 对象 出现错误", ex);
            throw new RuntimeException(ex);
        }
    }

    public static Object toObject(byte[] buf) {
        if (buf == null || buf.length < 1) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buf)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return ois.readObject();
            }
        } catch (Exception ex) {
            log.error(" byte[] 转 对象 出现错误", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 深拷贝 效率,高于Fastjson以及BeanUtils
     *
     * @param obj
     * @return
     */
    public static Object deepCopy(Object obj) {
        try {
            return toObject(toBytes(obj));
        } catch (Exception ex) {
            log.error("深拷贝对象出现错误", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 合并数组
     *
     * @param <T>
     * @param first
     * @param rest
     * @return
     */
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        final T[] result = (T[]) java.lang.reflect.Array.newInstance(first.getClass().getComponentType(), totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * 合并数组
     *
     * @param <T>
     * @param first
     * @param rest
     * @param length
     * @return
     */
    public static <T> T[] concat(T[] first, T[] rest, int length) {
        int totalLength = first.length + length;
        final T[] result = (T[]) java.lang.reflect.Array.newInstance(first.getClass().getComponentType(), totalLength);
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(rest, 0, result, first.length, length);
        return result;
    }

    /**
     * 合并数组
     *
     * @param first
     * @param rest
     * @param length
     * @return
     */
    public static byte[] concat(byte[] first, byte[] rest, int length) {
        int totalLength = first.length + length;
        byte[] result = new byte[totalLength];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(rest, 0, result, first.length, length);
        return result;
    }
}
