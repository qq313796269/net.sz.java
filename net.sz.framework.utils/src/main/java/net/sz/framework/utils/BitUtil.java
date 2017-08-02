package net.sz.framework.utils;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class BitUtil {

    // <editor-fold desc="是否包含一个状态 public static boolean hasFlagBitLong(long value1, long value2)">
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

    // <editor-fold desc="是否包含一个状态 public static boolean hasFlagBitLong(long value1, int index)">
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

    // <editor-fold desc="增加一个状态 public static long addBitLong(long value1, long value2)">
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

    // <editor-fold desc="增加一个状态 public static long addBitLong(long value1, int index)">
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

    // <editor-fold desc="增加一个状态 (value1 & group) | value2 public static long addBitLong(long value1, long value2, long group)">
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

    // <editor-fold desc="增加一个状态 (value1 & group) | (1 << index) public static long addBitLong(long value1, int index, long group)">
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

    // <editor-fold desc="移除一个状态 public static long removeBitLong(long value1, long value2)">
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

    // <editor-fold desc="移除一个状态 public static long removeBitLong(long value1, int index)">
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

    // <editor-fold desc="是否包含一个状态 public static boolean hasFlagBitInt(int value1, int value2)">
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

    // <editor-fold desc="是否包含一个状态 public static boolean hasFlagBitInt(int value1, int value2)">
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

    // <editor-fold desc="增加一个状态 public static long addBitInt(long value1, long value2)">
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

    // <editor-fold desc="增加一个状态 public static long addBitInt(long value1, long value2)">
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

    // <editor-fold desc="增加一个状态 (value1 & group) | public static int addBitInt(int value1, int value2, int group)">
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

    // <editor-fold desc="增加一个状态 (value1 & group) | (1 << index) public static int addBitIntIndex(int value1, int index, int group)">
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

    // <editor-fold desc="移除一个状态 public static long removeBitInt(long value1, long value2)">
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

    // <editor-fold desc="移除一个状态 public static long removeBitInt(long value1, long value2)">
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

    // <editor-fold desc="打印输出二进制结果对比 public static void show(long value)">
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

    // <editor-fold desc="测试代码 public static void main(String[] args)">
    public static void main(String[] args) {
        long l = 16;
        l = addBitLong(l, 0l, 16);
        show(l);
    }
    // </editor-fold>

    // <editor-fold desc="保留2位小数函数 static float getFloat2(float souse)">
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

    // <editor-fold desc="保留4位小数函数 static float getFloat4(float souse)">
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

    // <editor-fold desc="保留2位小数函数 static double getDouble2(double souse)">
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

    // <editor-fold desc="保留4位小数函数 static double getDouble4(double souse)">
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

    /**
     * 将int转为低字节在前，高字节在后的byte数组
     *
     * @param n
     * @return
     */
    public static final byte[] writeIntToBytesLittleEnding(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 读取小端绪的long
     *
     * @param buf
     * @return
     */
    public static final long readBytesToLongLittleEnding(ByteBuf buf) {
        byte[] b = new byte[8];
        buf.readBytes(b, 0, 8);
        return bytes2Long(b, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 端序换算
     *
     * @param buf
     * @return
     */
    public static final int readBytesToIntLittleEnding(ByteBuf buf) {
        byte[] b = new byte[4];
        buf.readBytes(b, 0, 4);

        return bytes2Int(b, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 端序换算
     *
     * @param buf
     * @return
     */
    public static final short readBytesToShortLittleEnding(ByteBuf buf) {
        byte[] b = new byte[2];
        buf.readBytes(b, 0, 2);
        return bytes2Short(b, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 将int转为低字节在前，高字节在后的byte数组
     *
     * @param bytes
     * @return
     */
    public static byte[] reverseBytes(byte[] bytes) {
        int len = bytes.length;
        byte[] b = new byte[len];
        for (int i = len - 1; i >= 0; i--) {
            b[len - i - 1] = bytes[i];
        }
        return b;
    }

    /**
     * 端序换算
     *
     * @param from
     * @param fromIndex
     * @return
     */
    public static short get2Bytes(byte[] from, int fromIndex) {
        int high = from[fromIndex] & 0xff;
        int low = from[fromIndex + 1] & 0xff;
        return (short) (high << 8 + low);
    }

    /*
    public static byte[] short2Bytes(short val){
        byte[] res=new byte[2];
        res[0]=(byte)(val>>8 & 0xff);
        res[1]=(byte)(val & 0xff);
        return res;
    }

    public static byte[] int2Bytes(int val){
        byte[] res = new byte[4];

        res[0] = (byte) (val >> 24);
        res[1] = (byte) (val >> 16);
        res[2] = (byte) (val >> 8);
        res[3] = (byte) (val);

        return res;
    }
     */
    /**
     * 端序换算
     *
     * @param x
     * @param byteOrder
     * @return
     */
    public static byte[] short2Bytes(short x, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(byteOrder);
        buffer.putShort(x);
        return buffer.array();
    }

    /**
     *
     * @param x
     * @return
     */
    public static byte[] short2Bytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(x);
        return buffer.array();
    }

    /**
     * 端序换算
     *
     * @param x
     * @param byteOrder
     * @return
     */
    public static final byte[] int2Bytes(int x, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(byteOrder);
        buffer.putInt(x);
        return buffer.array();
    }

    /**
     * 端序换算
     *
     * @param x
     * @param byteOrder
     * @return
     */
    public static byte[] long2Bytes(long x, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(byteOrder);
        buffer.putLong(x);
        return buffer.array();
    }

    /**
     * 端序换算
     *
     * @param src
     * @param byteOrder
     * @return
     */
    public static final short bytes2Short(byte[] src, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.wrap(src);
        buffer.order(byteOrder);
        return buffer.getShort();
    }

    /**
     * 端序换算
     *
     * @param src
     * @param byteOrder
     * @return
     */
    public static final int bytes2Int(byte[] src, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.wrap(src);
        buffer.order(byteOrder);
        return buffer.getInt();
    }

    /**
     * 端序换算
     *
     * @param src
     * @param byteOrder
     * @return
     */
    public static long bytes2Long(byte[] src, ByteOrder byteOrder) {
        ByteBuffer buffer = ByteBuffer.wrap(src);
        buffer.order(byteOrder);
        return buffer.getLong();
    }

}
