package net.sz.game.engine.utils;

import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class BitUtil {

    private static final Logger log = Logger.getLogger(BitUtil.class);

    // <editor-fold defaultstate="collapsed" desc="是否包含一个状态 public static boolean hasFlagBitLong(long value1, long value2)">
    /**
     * 是否包含一个状态
     *
     * @param value1 待验证
     * @param value2 需要验证的值
     * @return
     */
    public static boolean hasFlagBitLong(long value1, long value2) {
        return (value1 & value2) != 0;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="是否包含一个状态 public static boolean hasFlagBitLong(long value1, int index)">
    /**
     * 是否包含一个状态
     *
     * @param value1 待验证
     * @param index 位移偏移量必须在 0 ~ 63
     * @return
     */
    public static boolean hasFlagBitLong(long value1, int index) {
        if (0 < index && index < 64) {
            return (value1 & (1 << index)) != 0;
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 63");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 public static long addBitLong(long value1, long value2)">
    /**
     * 增加一个状态
     *
     * @param value1
     * @param value2 参数设置到参数1
     * @return
     */
    public static long addBitLong(long value1, long value2) {
        return value1 | value2;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 public static long addBitLong(long value1, int index)">
    /**
     * 增加一个状态
     *
     * @param value1
     * @param index 位移偏移量必须在 0 ~ 63
     * @return
     */
    public static long addBitLong(long value1, int index) {
        if (0 <= index && index < 64) {
            return value1 | (1 << index);
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 63");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 (value1 & group) | value2 public static long addBitLong(long value1, long value2, long group)">
    /**
     * 增加一个状态 (value1 & group) | value2
     *
     * @param value1
     * @param value2 参数设置到参数1,求 或 的值
     * @param group 分组信息求 与 的值
     * @return
     */
    public static long addBitLong(long value1, long value2, long group) {
        return (value1 & group) | value2;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 (value1 & group) | (1 << index) public static long addBitLong(long value1, int index, long group)">
    /**
     * 增加一个状态 (value1 & group) | (1 << index)
     *
     * @param value1
     * @param index 位移偏移量必须在 0 ~ 63
     * @param group
     * @return
     */
    public static long addBitLong(long value1, int index, long group) {
        if (0 < index && index < 64) {
            return (value1 & group) | (1 << index);
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 63");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="移除一个状态 public static long removeBitLong(long value1, long value2)">
    /**
     * 移除一个状态
     *
     * @param value1
     * @param value2
     * @return
     */
    public static long removeBitLong(long value1, long value2) {
        return value1 & (~value2);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="移除一个状态 public static long removeBitLong(long value1, int index)">
    /**
     * 移除一个状态
     *
     * @param value1
     * @param index ,位移偏移量必须在 0 ~ 63
     * @return
     */
    public static long removeBitLong(long value1, int index) {
        if (0 < index && index < 64) {
            return value1 & (~(1 << index));
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 63");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="是否包含一个状态 public static boolean hasFlagBitInt(int value1, int value2)">
    /**
     * 是否包含一个状态
     *
     * @param value1 待验证
     * @param value2 需要验证的值
     * @return
     */
    public static boolean hasFlagBitInt(int value1, int value2) {
        return (value1 & value2) != 0;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="是否包含一个状态 public static boolean hasFlagBitInt(int value1, int value2)">
    /**
     * 是否包含一个状态
     *
     * @param value1 待验证
     * @param index ,位移偏移量 偏移量必须在 0 ~ 31
     * @return
     */
    public static boolean hasFlagBitIntIndex(int value1, int index) {
        if (0 < index && index < 32) {
            return (value1 & (1 << index)) != 0;
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 31");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 public static long addBitInt(long value1, long value2)">
    /**
     * 增加一个状态
     *
     * @param value1
     * @param value2 参数设置到参数1
     * @return
     */
    public static int addBitInt(int value1, int value2) {
        return value1 | value2;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 public static long addBitInt(long value1, long value2)">
    /**
     * 增加一个状态
     *
     * @param value1
     * @param index ,位移偏移量 偏移量必须在 0 ~ 31
     * @return
     */
    public static int addBitIntIndex(int value1, int index) {
        if (0 < index && index < 32) {
            return value1 | (1 << index);
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 31");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 (value1 & group) | public static int addBitInt(int value1, int value2, int group)">
    /**
     * 增加一个状态 (value1 & group) | value2
     *
     * @param value1
     * @param value2 参数设置到参数1
     * @param group
     * @return
     */
    public static int addBitInt(int value1, int value2, int group) {
        return (value1 & group) | value2;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="增加一个状态 (value1 & group) | (1 << index) public static int addBitIntIndex(int value1, int index, int group)">
    /**
     * 增加一个状态 (value1 & group) | (1 << index)
     *
     * @param value1
     * @param index ,位移偏移量 偏移量必须在 0 ~ 31
     * @param group
     * @return
     */
    public static int addBitIntIndex(int value1, int index, int group) {
        if (0 < index && index < 32) {
            return (value1 & group) | (1 << index);
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 31");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="移除一个状态 public static long removeBitInt(long value1, long value2)">
    /**
     * 移除一个状态
     *
     * @param value1
     * @param value2
     * @return
     */
    public static int removeBitInt(int value1, int value2) {
        return value1 & (~value2);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="移除一个状态 public static long removeBitInt(long value1, long value2)">
    /**
     * 移除一个状态
     *
     * @param value1
     * @param index ,位移偏移量 偏移量必须在 0 ~ 31
     * @return
     */
    public static int removeBitIntIndex(int value1, int index) {
        if (0 < index && index < 32) {
            return value1 & (~(1 << index));
        }
        throw new UnsupportedOperationException("1 << index 偏移量必须在 0 ~ 31");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="打印输出二进制结果对比 public static void show(long value)">
    /**
     * 打印输出二进制结果对比
     *
     * @param value
     * @return
     */
    public static String show(long value) {
        return "结果：" + StringUtil.padLeft(value, 19, " ") + " -> " + StringUtil.padLeft(Long.toBinaryString(value), 64, "0");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="测试代码 public static void main(String[] args)">
    public static void main(String[] args) {
        long l = 16;
        l = addBitLong(l, 0l, 16);
        show(l);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="保留2位小数函数 static float getFloat2(float souse)">
    /**
     * 保留2位小数函数
     *
     * @param souse
     * @return
     */
    static public float getFloat2(float souse) {
        return Math.round(souse * 100f) / 100f;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="保留4位小数函数 static float getFloat4(float souse)">
    /**
     * 保留4位小数函数
     *
     * @param souse
     * @return
     */
    static public float getFloat4(float souse) {
        return Math.round(souse * 10000f) / 10000f;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="保留2位小数函数 static double getDouble2(double souse)">
    /**
     * 保留2位小数函数
     *
     * @param souse
     * @return
     */
    static public double getDouble2(double souse) {
        return Math.round(souse * 100d) / 100d;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="保留4位小数函数 static double getDouble4(double souse)">
    /**
     * 保留4位小数函数
     *
     * @param souse
     * @return
     */
    static public double getDouble4(double souse) {
        return Math.round(souse * 10000d) / 10000d;
    }
    // </editor-fold>

}
