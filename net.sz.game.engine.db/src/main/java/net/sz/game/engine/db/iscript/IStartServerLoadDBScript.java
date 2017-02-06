package net.sz.game.engine.db.iscript;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IStartServerLoadDBScript {

    /**
     * 如果read失败请停止服务器启动
     *
     * @return
     */
    boolean loadDb();
}
