package net.sz.game.engine.utils;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class LogUtil {

    private static SzLogger log = SzLogger.getLogger();

    static private final ThreadGroup LogThreadGroup = new ThreadGroup("Log ThreadGroup");

    static private final LogUtil instance = new LogUtil();

    static public LogUtil getInstance() {
        return instance;
    }

    private final java.util.concurrent.ConcurrentLinkedQueue<String> logsQueue = new ConcurrentLinkedQueue<>();
    private volatile String UrlString;
    private String sign = "";

    private LogUtil() {
//        LogRunnable logRunnable = new LogRunnable();
//        for (int i = 0; i < 5; i++) {
//            Thread thread = new Thread(LogThreadGroup, logRunnable, "Log Thread " + i);
//            thread.start();
//        }
    }

    /**
     * 创建日志标题
     *
     * @param sign 验证签名
     */
    public void setLogTitle(String sign) {
        this.sign = sign;
    }

    private String setLogInfos(String string) {
        //sign算法:game serverId sendTime 方法签名(中间用空格隔开)进行MD5加密.
        long sendTime = System.currentTimeMillis();
        //原来的发送格式
//        String md5Encode = MD5Util.md5Encode(GlobalUtil.GameID + "", GlobalUtil.PlatformId + "", GlobalUtil.ServerID + "", sendTime + "", sign);
        //现在
//        int random = RandomUtils.random(10001, 10002);
        String text = GlobalUtil.GameID + "" + GlobalUtil.PlatformId + "" + GlobalUtil.getServerID() + "" + sendTime + "" + sign;

        String md5Encode = MD5Util.md5Encode(text);

        return "{\"gameId\":" + GlobalUtil.GameID + ",\"sign\":\"" + md5Encode + "\",\"platformId\":" + GlobalUtil.PlatformId + ",\"sendTime\":" + sendTime + ",\"serverId\":" + GlobalUtil.getServerID() + ",\"infos\":[" + string + "]}";
    }

    public int getLogSize() {
        return logsQueue.size();
    }

    public void setUrlString(String UrlString) {
        this.UrlString = UrlString;
        log.error("UrlString=" + UrlString);
    }

    //<editor-fold desc="增加日志 public void addLog(String paramData)">
    /**
     *
     * @param paramData json 格式字符串，这里会把字符串做base64位处理
     */
    public void addLog(String paramData) {
//        paramData = "{\"identify\":\"" + GlobalUtil.getId() + "\"," + "\"createTime\":\"" + System.currentTimeMillis() + "\"," + paramData + "}";
//        paramData = StringUtil.getBase64(paramData);
//        log.error(paramData);
//        StringUtil..(paramData);
//        addLogQueue(paramData);
    }
    //</editor-fold>

    private long lastSendMailTime = 0;

    void addLogQueue(String paramData) {
        if (logsQueue.size() < 50000) {
            logsQueue.add(paramData);
            synchronized (logsQueue) {
                logsQueue.notify();
            }
        } else if (System.currentTimeMillis() - lastSendMailTime > 5 * 60 * 1000) {
            lastSendMailTime = System.currentTimeMillis();
            MailUtil.sendMail("日志服务器是否无法连接", "日志类型量超过：" + logsQueue.size());
        }
    }

    private final class LogRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                while (logsQueue.size() <= 0) {
                    synchronized (logsQueue) {
                        try {
                            logsQueue.wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
                long begin = System.currentTimeMillis();
                ArrayList<String> tmpLogList = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    String poll = logsQueue.poll();
                    if (StringUtil.isNullOrEmpty(poll)) {
                        break;
                    }
                    tmpLogList.add(poll);
                }
                if (tmpLogList.size() > 0) {
                    String paramData = "";
                    for (String tmpLog : tmpLogList) {
                        if (paramData.length() > 0) {
                            paramData += ",";
                        }
                        paramData += "\"" + tmpLog + "\"";
                    }
                    String setLogInfos = setLogInfos(paramData);
//                    log.error(setLogInfos);
                    //修改秒数
                    String urlGet = HttpUtil.urlPost(UrlString, setLogInfos, 3000);
                    if (!"ok".equals(urlGet)) {
//                        for (String tmpLog : tmpLogList) {
//                            addLogQueue(tmpLog);
//                        }
//                        log.error("日志服务器暂时无法连接");
//                        log.debug(urlGet);
                    }
                }
                long end = System.currentTimeMillis();
                long tmp = end - begin;
                if (tmp > 2) {
                    log.error("日志提交耗时：" + tmp);
                }
            }
        }
    }

}
