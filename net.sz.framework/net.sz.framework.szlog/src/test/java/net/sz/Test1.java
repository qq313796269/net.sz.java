package net.sz;

import net.sz.framework.szlog.LogMsg;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class Test1 {

    public static void main(String[] args) {

        LogMsg logMsg = LogMsg.copyMsg("sss", new Exception("1"));
        LogMsg logMsg1 = LogMsg.copyMsg("sss1", new Exception("2"));

        System.out.println(logMsg.getMsg());
        logMsg.getThrowable().printStackTrace(System.out);

    }

    static void tt() {
        throw new UnsupportedOperationException("tt");
    }
}
