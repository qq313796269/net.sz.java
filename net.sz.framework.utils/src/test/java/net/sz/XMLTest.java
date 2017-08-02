package net.sz;

import java.util.HashSet;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.ObjectStreamUtil;
import net.sz.framework.utils.XmlUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class XMLTest {

    private static final SzLogger log = SzLogger.getLogger();

    HashSet<String> hashSet = new HashSet<>();
    public static void main(String[] args) {

        XMLTest xmlTest = new XMLTest();

        xmlTest.hashSet.add("sss");

        System.out.println(XmlUtil.witerSimpleXml(xmlTest));

        byte[] toBytes = ObjectStreamUtil.toBytes(xmlTest.hashSet);

        HashSet<String> toObject = ObjectStreamUtil.toObject(HashSet.class, toBytes);
        System.out.println(XmlUtil.witerSimpleXml(toObject));

    }
}
