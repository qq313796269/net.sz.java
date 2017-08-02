package net.sz.framework.struct.thread;

import net.sz.framework.szlog.SzLogger;

/**
 * 线程分类
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public enum ThreadType {

    /**
     * 表示当前线程是系统线程
     */
    Sys(1, "系统线程"),
    /**
     * 表当前线程是用户线程
     */
    User(2, "用户线程");

    int index;
    String msg;

    private static final SzLogger log = SzLogger.getLogger();

    private ThreadType(int index, String msg) {
        this.index = index;
        this.msg = msg;
    }

    public int getIndex() {
        return index;
    }

    public String getMsg() {
        return msg;
    }

}
