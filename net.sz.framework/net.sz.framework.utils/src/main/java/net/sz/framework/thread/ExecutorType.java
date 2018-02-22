package net.sz.framework.thread;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程分类
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class ExecutorType {

    /**
     * 表示当前线程是系统线程
     */
    public static final ExecutorType Sys = new ExecutorType(1, "系统线程");
    /**
     * 表当前线程是用户线程
     */
    public static final ExecutorType User = new ExecutorType(2, "用户线程");

    int index;
    String msg;

    private ExecutorType(int index, String msg) {
        this.index = index;
        this.msg = msg;
    }

    public int getIndex() {
        return index;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "{" + "index=" + index + ", msg=" + msg + '}';
    }

}
