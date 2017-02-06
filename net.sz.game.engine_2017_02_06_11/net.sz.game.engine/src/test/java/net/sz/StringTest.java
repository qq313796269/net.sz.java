package net.sz;

import com.google.gson.LongSerializationPolicy;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class StringTest {

    private static final Logger log = Logger.getLogger(StringTest.class);

    public static void main(String[] args) {
        int forcount = 10000;
        long begin = 0, end = 0;

        begin = System.currentTimeMillis();
        for (int i = 0; i < forcount; i++) {
            String str;
            str = "i" + i + "i" + i
                    + "i" + i + "i" + i
                    + "i" + i + "i" + i
                    + "i" + i + "i" + i
                    + "i" + i + "i" + i
                    + "i" + i + "i" + i
                    + "i" + i + "i" + i
                    + "i" + i + "i" + i + "i" + i + "i" + i + "i" + i + "i" + i;
        }
        end = System.currentTimeMillis();
        log.error("String -> cost time ->" + (end - begin));

        begin = System.currentTimeMillis();
        for (int i = 0; i < forcount; i++) {
            String str;
            str = String.format("i%si%si%si%si%si%si%si%si%si%si%si%si%si%si%si%si%si%si%si%s", i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i);
        }
        end = System.currentTimeMillis();
        log.error("String format-> cost time ->" + (end - begin));

        begin = System.currentTimeMillis();
        for (int i = 0; i < forcount; i++) {
            String str;
            str = new StringBuilder()
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i)
                    .append("i").append(i).append("i").append(i).toString();
        }
        end = System.currentTimeMillis();
        log.error("StringBuilder -> cost time ->" + (end - begin));

    }
}
