package net.sz.framework.utils;

import com.thoughtworks.xstream.XStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class XmlUtil {

    /*
     * XStream 不关心序列化/逆序列化的类的字段的可见性。
     * 序列化/逆序列化类的字段不需要 getter 和 setter 方法。
     * 序列化/逆序列化的类不需要有默认构造函数。
     */
    private static final XStream xStream;
    private static final Serializer simpleXml;

    static {
        xStream = new XStream();
        xStream.ignoreUnknownElements();
        simpleXml = new Persister();
    }

    /**
     * obj to xml(object类型转换为xml类型),包含数据的复杂类型
     *
     * @param obj
     * @return
     */
    public static String writerXml(Object obj) {
        return xStream.toXML(obj);
    }

    /**
     * 将object类型转换为xml类型，并写入XML文件(其他格式也可以，比如txt文件)
     *
     * @param obj
     * @param path
     */
    public static void writerXmlToFile(Object obj, String path) {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            try (OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")) {
                String xml = writerXml(obj);
                osw.write(xml);
                osw.flush();
            }
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("序列化对象为xml错误：", ex);
        }
    }

    /**
     * 把xml数据写到zip文件中
     *
     * @param zipPath
     * @param fileName
     * @param object
     */
    public static void writerXmlZip(String zipPath, String fileName, Object object) {
        String toXMLString = writerXml(object);
        ZipUtil.writeStringZip(zipPath, fileName, toXMLString, "utf-8");
    }

    /**
     * 不包含包名
     *
     * @param source
     * @return
     */
    public static String witerSimpleXml(Object source) {
        try (java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            simpleXml.write(source, baos);
            return new String(baos.toByteArray(), "utf-8");
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("witerSimpleXml：" + source.getClass().getName(), ex);
        }
    }

    /**
     * 将object类型转换为xml类型，并写入XML文件(其他格式也可以，比如txt文件)
     *
     * @param obj
     * @param path
     */
    public static void writerSimpleXmlToFile(Object obj, String path) {
        String witerSimpleXml = witerSimpleXml(obj);
        FileUtil.writerFile(witerSimpleXml, path, "utf-8");
    }

    /**
     * 把xml数据写到zip文件中
     *
     * @param object
     * @param fileName
     * @param zipPath
     */
    public static void writerSimpleXmlZip(Object object, String fileName, String zipPath) {
        //使用GZIPOutputStream包装OutputStream流，使其具体压缩特性，最后会生成test.txt.gz压缩包
        //并且里面有一个名为test.txt的文件
        String toXMLString = witerSimpleXml(object);
        ZipUtil.writeStringZip(zipPath, fileName, toXMLString);
    }

    /**
     * 读取XML文件，加载进相应Object类型
     *
     * @param <T>
     * @param path
     * @param t
     * @return
     */
    public static <T> T readerXmlToFile(String path, Class<T> t) {
        String xml = FileUtil.readFileToString(path, "utf-8");
        return readerXml(xml, t);
    }

    /**
     * 读取XML文件，加载进相应Object类型
     *
     * @param <T>
     * @param xml
     * @param t
     * @return
     */
    public static <T> T readerXml(String xml, Class<T> t) {
        try {
            Object fromXML = xStream.fromXML(xml);
            return (T) fromXML;
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("反序列化对象为xml错误", ex);
        }
    }

    /**
     * 读取XML文件，加载进相应Object类型
     *
     * @param <T>
     * @param path
     * @param t
     * @return
     */
    public static <T> T readerSimpleXmlToFile(String path, Class<T> t) {
        try {
            return simpleXml.read(t, new File(path));
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("反序列化对象为xml错误", ex);
        }
    }

    /**
     * 不包含包名
     *
     * @param <T>
     * @param xml
     * @param t
     * @return
     */
    public static <T> T readerSimpleXml(String xml, Class<T> t) {
        try {
            return simpleXml.read(t, xml);
        } catch (Throwable ex) {
            throw new UnsupportedOperationException("反序列化对象为xml错误", ex);
        }
    }

}
