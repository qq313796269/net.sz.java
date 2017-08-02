package net.sz.framework.nio.tcp;

import io.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;
import net.sz.framework.nio.NettyPool;
import net.sz.framework.struct.thread.BaseThreadModel;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.TimeUtil;

/**
 * 10秒钟检查一次链接情况
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CheckNettySocketTimerTask extends BaseThreadModel {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 3688985389900489875L;

    public CheckNettySocketTimerTask() {
        super("CheckNettySocketTimer");
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (this) {
                    this.wait(5000);
                }

                HashMap<String, ChannelHandlerContext> hashMap = new HashMap<>(NettyPool.getInstance().getSessions());
                if (log.isDebugEnabled()) {
                    log.debug("tcp session size：" + hashMap.size());
                }
                for (Map.Entry<String, ChannelHandlerContext> entrySet : hashMap.entrySet()) {
                    String sessionkey = entrySet.getKey();
                    ChannelHandlerContext session = entrySet.getValue();

                    Long sessioncreateTime = NettyCoder.getSessionAttr(session, NettyCoder.SessionCreateTime, Long.class);
                    if (sessioncreateTime == null || sessioncreateTime == 0) {
                        NettyPool.getInstance().closeSession(session, "无效链接");
                        continue;
                    }

                    Long lastTime = NettyCoder.getSessionAttr(session, NettyCoder.SessionLastTime, Long.class);
                    if (lastTime == null || lastTime == 0 || TimeUtil.currentTimeMillis() - lastTime > NettyCoder.FreeSessionClearTime) {
                        NettyPool.getInstance().closeSession(session, "空闲连接");
                        continue;
                    }
//                    TODO 暂时注释掉
                    Long loginTime = NettyCoder.getSessionAttr(session, NettyCoder.SessionLoginTime, Long.class);
                    if ((loginTime == null || loginTime == 0) && TimeUtil.currentTimeMillis() - loginTime > NettyCoder.LoginSessionClearTime) {
                        NettyPool.getInstance().closeSession(session, "连接超过时间却尚未登录验证");
                        continue;
                    }
                }
            } catch (Throwable e) {
                log.error("链接检查器", e);
            }
        }
    }

}
