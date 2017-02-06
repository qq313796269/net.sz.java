package net.sz.game.engine.nio.nettys.tcp;

import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;
import net.sz.game.engine.nio.nettys.NettyPool;
import net.sz.game.engine.thread.TimerTaskEvent;
import org.apache.log4j.Logger;

/**
 *
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CheckNettySocketTimerTask extends TimerTaskEvent {

    private static final Logger log = Logger.getLogger(CheckNettySocketTimerTask.class);

    public CheckNettySocketTimerTask() {
        super(10 * 1000);
    }

    @Override
    public void run() {
        HashMap<String, ChannelHandlerContext> hashMap = new HashMap<>(NettyPool.getInstance().getSessions());
        log.debug("tcp session size：" + hashMap.size());
        for (Map.Entry<String, ChannelHandlerContext> entrySet : hashMap.entrySet()) {
            String sessionkey = entrySet.getKey();
            ChannelHandlerContext value = entrySet.getValue();

            Long sessioncreateTime = NettyCoder.getSessionAttr(value, NettyCoder.SessionCreateTime, Long.class);
            if (sessioncreateTime == null || sessioncreateTime == 0) {
                NettyPool.getInstance().closeSession(value, "无效链接");
            }

            Long lastTime = NettyCoder.getSessionAttr(value, NettyCoder.SessionLastTime, Long.class);
            if (lastTime == null || lastTime == 0 || System.currentTimeMillis() - lastTime > NettyCoder.ClearSessionTime) {
                NettyPool.getInstance().closeSession(value, "空闲连接");
            }
            //TODO 暂时注释掉
//            Long loginTime = NettyPool.getInstance().getSessionAttr(value, NettyPool.SessionLoginTime, Long.class);
//            if ((loginTime == null || loginTime == 0) && System.currentTimeMillis() - createTime > NettyPool.ClearSessionTime) {
//                log.error("连接超过时间却尚未登录验证 " + sessionkey);
//                NettyPool.getInstance().closeSession(value, "连接超过时间却尚未登录验证");
//            }
        }
    }

}
