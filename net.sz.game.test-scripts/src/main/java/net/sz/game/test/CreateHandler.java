package net.sz.game.test;

import net.sz.game.engine.nio.nettys.ProtoBufHandlerUtil;

/**
 *
 *
 * @author 失足程序员
 * @mail 492794628@qq.com
 * @phone 13882122019
 */
public class CreateHandler {

    public static void main(String[] args) throws Exception {
        ProtoBufHandlerUtil.createScriptHandler("/../net.sz.game.test.message", ProtoBufHandlerUtil.CreateType.GSReq);
        //System.out.println(System.getProperty("user.dir"));//
    }
}
