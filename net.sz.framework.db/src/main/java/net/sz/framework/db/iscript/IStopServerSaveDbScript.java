package net.sz.framework.db.iscript;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IStopServerSaveDbScript {

    /**
     * 如果写入数据失败，请自行处理
     */
    void saveDb();
}
