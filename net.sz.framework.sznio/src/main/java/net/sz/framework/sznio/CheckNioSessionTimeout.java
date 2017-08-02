package net.sz.framework.sznio;

import java.util.HashMap;
import java.util.Map;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.sznio.http.NioHttpServer;
import net.sz.framework.sznio.tcp.NioTcpServer;
import net.sz.framework.szthread.TimerTaskModel;
import net.sz.framework.utils.TimeUtil;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class CheckNioSessionTimeout extends TimerTaskModel {

    private static final SzLogger log = SzLogger.getLogger();

    public CheckNioSessionTimeout() {
        super(1000);
    }

    @Override
    public void run() {
        long bigen = TimeUtil.currentTimeMillis();
        HashMap<Long, NioSession> hashSet = new HashMap<>(NioHttpServer.httpRequestMap);
        for (Map.Entry<Long, NioSession> entry : hashSet.entrySet()) {
            NioSession nioHttpRequest = entry.getValue();
            if (TimeUtil.currentTimeMillis() - nioHttpRequest.getCreateTime() > 5000) {
                nioHttpRequest.close();
            }
        }

        HashMap<Long, NioSession> hashMap = new HashMap<>(NioTcpServer.sessionMap);
        for (Map.Entry<Long, NioSession> entry : hashMap.entrySet()) {
            Long key = entry.getKey();
            NioSession value = entry.getValue();
            if (TimeUtil.currentTimeMillis() - value.getLastTime() > 20 * 1000) {
                value.close();
            }
        }

        log.info("CheckNioSessionTimeout cast : " + (TimeUtil.currentTimeMillis() - bigen));
    }

}
