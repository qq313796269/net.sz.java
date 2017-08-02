package net.sz.framework.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 辅助类型转换，泛型类型转换
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ConvertTypeUtil {

    /**
     * 常量类型
     */
    public enum TypeCode {
        /**
         * 默认值，null
         */
        Default(ConvertTypeUtil.class),
        Boolean(java.lang.Boolean.class),
        Char(char.class),
        Date(java.util.Date.class),
        String(java.lang.String.class),
        Object(java.lang.Object.class),
        Byte(java.lang.Byte.class, byte.class),
        Short(java.lang.Short.class, short.class),
        Integer(java.lang.Integer.class, int.class),
        Long(java.lang.Long.class, long.class),
        Float(java.lang.Float.class, float.class),
        Double(java.lang.Double.class, double.class),
        BigInteger(java.math.BigInteger.class),
        BigDecimal(java.math.BigDecimal.class),;

        private Class<?>[] clazzs;

        private TypeCode(Class<?>... clazzs) {
            this.clazzs = clazzs;
        }

        public Class<?>[] getClazzs() {
            return clazzs;
        }

        /**
         *
         * @param clazz
         * @return
         */
        public static TypeCode getTypeCode(Class<?> clazz) {
            if (clazz != null) {
                TypeCode[] values = TypeCode.values();
                for (TypeCode value : values) {
                    for (Class<?> tmpClass : value.getClazzs()) {
                        if (tmpClass.equals(clazz)) {
                            return value;
                        }
                    }
                }
            }
            return TypeCode.Default;
        }

        /**
         *
         * @param clazz
         * @return
         */
        public static TypeCode getTypeCode(String clazz) {
            if (clazz != null) {
                TypeCode[] values = TypeCode.values();
                for (TypeCode value : values) {
                    for (Class<?> tmpClass : value.getClazzs()) {
                        if (tmpClass.getName().equalsIgnoreCase(clazz) || tmpClass.getSimpleName().equalsIgnoreCase(clazz)) {
                            return value;
                        }
                    }
                }
            }
            return TypeCode.Default;
        }

    }

    /**
     * 类型转换
     *
     * @param obj
     * @param clazz
     * @return
     */
    public static Object changeType(Object obj, Class<?> clazz) {

        if (obj == null || clazz.isInstance(obj) || clazz.isAssignableFrom(obj.getClass())) {
            return obj;
        }

        TypeCode typeCode = TypeCode.getTypeCode(clazz);

        return changeType(obj, typeCode, clazz);
    }

    /**
     * 类型转换
     *
     * @param obj
     * @param typeCode
     * @param clazz
     * @return
     */
    public static Object changeType(Object obj, TypeCode typeCode, Class<?> clazz) {
        /*如果等于，或者所与继承关系*/
        if (obj == null || clazz.isInstance(obj) || clazz.isAssignableFrom(obj.getClass())) {
            return obj;
        }

        switch (typeCode) {
            case Char:
                throw new UnsupportedOperationException();
            case String:
                return String.valueOf(obj);
            case Boolean:
                return Boolean.valueOf((String) changeType(obj, TypeCode.String, String.class));
            case Byte:
                return Byte.valueOf((String) changeType(obj, TypeCode.String, String.class));
            case Short:
                return Short.valueOf((String) changeType(obj, TypeCode.String, String.class));
            case Integer:
                return Integer.valueOf((String) changeType(obj, TypeCode.String, String.class));
            case BigInteger:
                return BigInteger.valueOf((Long) changeType(obj, TypeCode.Long, Long.class));
            case Long:
                return Long.valueOf((String) changeType(obj, TypeCode.String, String.class));
            case Float:
                return Float.valueOf((String) changeType(obj, TypeCode.String, String.class));
            case Double:
                return Double.valueOf((String) changeType(obj, TypeCode.String, String.class));
            case BigDecimal:
                return BigDecimal.valueOf((Long) changeType(obj, TypeCode.Long, Long.class));
            default: {
                return obj;
            }
        }
    }

    /**
     * 把对象转化成 Byte
     *
     * @param obj
     * @return
     */
    public static Byte toByte(Object obj) {
        return (Byte) changeType(obj, TypeCode.Byte, Byte.class);
    }

    /**
     * 把对象转化成 Short
     *
     * @param obj
     * @return
     */
    public static Short toShort(Object obj) {
        return (Short) changeType(obj, TypeCode.Short, Short.class);
    }

    /**
     * 把对象转化成 Integer
     *
     * @param obj
     * @return
     */
    public static Integer toInteger(Object obj) {
        return (Integer) changeType(obj, TypeCode.Integer, Integer.class);
    }

    /**
     * 把对象转化成 Long
     *
     * @param obj
     * @return
     */
    public static Long toLong(Object obj) {
        return (Long) changeType(obj, TypeCode.Long, Long.class);
    }

    /**
     * 把对象转化成 Float
     *
     * @param obj
     * @return
     */
    public static Float toFloat(Object obj) {
        return (Float) changeType(obj, TypeCode.Float, Float.class);
    }

    /**
     * 把对象转化成 Double
     *
     * @param obj
     * @return
     */
    public static Double toDouble(Object obj) {
        return (Double) changeType(obj, TypeCode.Double, Double.class);
    }

    /**
     * 把对象转化为字符串
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return (String) changeType(obj, TypeCode.String, String.class);
    }

    public static void main(String[] args) {
        Object ob = 123;
        try {
            String str = (String) ob;
        } catch (Exception e) {
            e.printStackTrace(System.out);

        }
        String str = (String) changeType(ob, String.class);
    }

}
