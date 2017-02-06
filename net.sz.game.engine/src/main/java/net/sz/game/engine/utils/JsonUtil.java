package net.sz.game.engine.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * JSON序列化和反序列化
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class JsonUtil {

    static final Gson gson = new Gson();

    /**
     * 把类对象还原成json对象
     *
     * @param obj
     * @return
     */
    public static String toGSONString(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 把json对象还原成类对象
     *
     * @param <T>
     * @param strjson
     * @param t
     * @return
     */
    public static <T> T parseGSONObject(Class<T> t, String strjson) {
        return gson.fromJson(strjson, t);
    }

    /**
     *
     * @param <T>
     * @param strjson
     */
    public static <T> T toJSONObject(String strjson, Type tt) {
        return gson.fromJson(strjson, tt);
    }

    static class BaseJson {

        public static String toJSONString(Object paramObject) {
            return JSON.toJSONString(paramObject, new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect});
        }

        public static String toJSONStringWriteClassName(Object paramObject) {
            return JSON.toJSONString(paramObject, new SerializerFeature[]{SerializerFeature.WriteClassName, SerializerFeature.DisableCircularReferenceDetect});
        }

        public static String toJSONStringWithDateFormat(long paramLong) {
            Date localDate = new Date(paramLong);
            return toJSONStringWithDateFormat(localDate);
        }

        public static String toJSONStringWithDateFormat(long paramLong, String paramString) {
            Date localDate = new Date(paramLong);
            return toJSONStringWithDateFormat(localDate, paramString);
        }

        public static String toJSONStringWithDateFormat(Date paramDate) {
            return JSON.toJSONStringWithDateFormat(paramDate, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat});
        }

        public static String toJSONStringWithDateFormat(Date paramDate, String paramString) {
            return JSON.toJSONStringWithDateFormat(paramDate, paramString, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat});
        }

        public static Object parse(String paramString) {
            return JSON.parse(paramString);
        }

        public static <T extends Object> T parseObject(String paramString, Class<T> paramClass) {
            return JSON.parseObject(paramString, paramClass);
        }

        public static Object parseObject(String paramString, TypeReference paramTypeReference) {
            return JSON.parseObject(paramString, paramTypeReference, new Feature[0]);
        }

        public static List parseArray(String paramString, Class paramClass) {
            return JSON.parseArray(paramString, paramClass);
        }

        public static List parseArray(String paramString, Type[] paramArrayOfType) {
            return JSON.parseArray(paramString, paramArrayOfType);
        }
    }

    /**
     * 把类对象还原成json对象
     *
     * @param obj
     * @return
     */
    public static String toJSONString(Object obj) {
        //return gson.toJson(obj);
        return JSON.toJSONString(obj);
    }

    /**
     * 把json对象还原成类对象
     *
     * @param <T>
     * @param strjson
     * @param t
     * @return
     */
    public static <T> T parseObject(String strjson, Class<T> t) {
        //return gson.fromJson(strjson, t);
        return (T) JSON.parseObject(strjson, t);
    }

    /**
     * JSON序列化(默认写入类型名称)
     *
     * @param object 传入对象
     * @return String
     */
    public static String toJSONStringWriteClassName(Object object) {
        return BaseJson.toJSONStringWriteClassName(object);
    }

    /**
     * JSON序列化时间(默认格式yyyy-MM-dd HH:mm:ss)
     *
     * @param dateTime 传入时间
     * @return String
     */
    public static String toJSONStringWithDateFormat(long dateTime) {
        return BaseJson.toJSONStringWithDateFormat(dateTime);
    }

    /**
     * JSON序列化时间(自定义格式)
     *
     * @param dateTime 传入时间
     * @param dateFormat
     * @return String
     */
    public static String toJSONStringWithDateFormat(long dateTime, String dateFormat) {
        return BaseJson.toJSONStringWithDateFormat(dateTime, dateFormat);
    }

    /**
     * JSON序列化时间(默认格式yyyy-MM-dd HH:mm:ss)
     *
     * @param date 传入时间结构
     * @return String
     */
    public static String toJSONStringWithDateFormat(Date date) {
        return BaseJson.toJSONStringWithDateFormat(date);
    }

    /**
     * JSON序列化时间(自定义格式)
     *
     * @param date 传入时间结构
     * @param dateFormat
     * @return String
     */
    public static String toJSONStringWithDateFormat(Date date, String dateFormat) {
        return BaseJson.toJSONStringWithDateFormat(date, dateFormat);
    }

    /**
     * JSON反序列化
     *
     * @param text 传入被反序列化的对象
     * @return Object
     */
    public static Object parse(String text) {
        return BaseJson.parse(text);
    }

    /**
     * JSON反序列化(强制转化为T类型)
     *
     * @param <T>
     * @param text 传入被反序列化的对象
     * @param type 要被反序列化成的类型
     * @return
     */
    public static <T extends Object> T parseObject(String text, TypeReference<T> type) {
        return (T) BaseJson.parseObject(text, type);
    }

    /**
     * JSON反序列化(反序列化为List)
     *
     * @param <T>
     * @param text 传入被反序列化的对象
     * @param clazz 要被反序列化成的类型
     * @return
     */
    public static <T extends Object> List<T> parseArray(String text, Class<T> clazz) {
        return (List<T>) BaseJson.parseArray(text, clazz);
    }

    /**
     * JSON反序列化(反序列化为List) Type[] types = new Type[] {String.class, Byte.class};
     *
     * @param text 传入被反序列化的对象
     * @param types 要被反序列化成的类型数组(可多个类型，按照顺序反序列化)
     * @return
     */
    public static List<Object> parseArray(String text, Type[] types) {
        return BaseJson.parseArray(text, types);
    }

    public static String toJsonFile(Object object, String filePath) {
        String toJSONString = toJSONString(object);
        FileUtil.writerFile(toJSONString, filePath, "utf-8");
        return toJSONString;
    }

    public static <T> T parseJsonFile(Class<T> clazz, String filePath) {
        String readFileToString = FileUtil.readFileToString(filePath, "utf-8");
        return parseObject(readFileToString, clazz);
    }

}
