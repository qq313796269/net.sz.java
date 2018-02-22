package net.sz.framework.scripts;

import java.io.Serializable;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public interface IBaseScript {

    /**
     * 提供快速访问的key值
     *
     * @return
     */
    default Serializable getScriptKey() {
        return null;
    }

    /**
     * 检查比较
     *
     * @param key
     * @return
     */
    default boolean checkScriptKey(Serializable key) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

}
