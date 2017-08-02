package net.sz.framework.scripts;

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
     * 检查地图id，
     * <br>
     * 默认是不可能执行的脚本 throw new UnsupportedOperationException
     *
     * @param scriptId
     * @return
     */
    default boolean checkMapId(int scriptId) {
        throw new UnsupportedOperationException("未实现默认函数 default boolean checkMapId(int scriptId)");
    }

    /**
     * 检查常规id，
     * <br>
     * 默认是不可能执行的脚本 throw new UnsupportedOperationException
     *
     * @param scriptId
     * @return
     */
    default boolean checkScriptId1(int scriptId) {
        throw new UnsupportedOperationException("未实现默认函数 default boolean checkScriptId1(int scriptId)");
    }

    /**
     * 检查常规id，
     * <br>
     * 默认是不可能执行的脚本 throw new UnsupportedOperationException
     *
     * @param scriptId
     * @return
     */
    default boolean checkScriptId2(int scriptId) {
        throw new UnsupportedOperationException("未实现默认函数 default boolean checkScriptId2(int scriptId)");
    }

    /**
     * 检查常规id，
     * <br>
     * 默认是不可能执行的脚本 throw new UnsupportedOperationException
     *
     * @param scriptId
     * @return
     */
    default boolean checkScriptLongId1(long scriptId) {
        throw new UnsupportedOperationException("未实现默认函数 default boolean checkScriptLongId1(int scriptId)");
    }

    /**
     * 检查常规id，
     * <br>
     * 默认是不可能执行的脚本 throw new UnsupportedOperationException
     *
     * @param scriptId
     * @return
     */
    default boolean checkScriptLongId2(long scriptId) {
        throw new UnsupportedOperationException("未实现默认函数 default boolean checkScriptLongId2(int scriptId)");
    }

    /**
     * 检查常规id，
     * <br>
     * 默认是不可能执行的脚本 throw new UnsupportedOperationException
     *
     * @param scriptId
     * @return
     */
    default boolean checkStringScriptId1(String scriptId) {
        throw new UnsupportedOperationException("未实现默认函数 default boolean checkStringScriptId1(int scriptId)");
    }

    /**
     * 检查常规id，
     * <br>
     * 默认是不可能执行的脚本 throw new UnsupportedOperationException
     *
     * @param scriptId
     * @return
     */
    default boolean checkStringScriptId2(String scriptId) {
        throw new UnsupportedOperationException("未实现默认函数 default boolean checkStringScriptId2(int scriptId)");
    }

}
