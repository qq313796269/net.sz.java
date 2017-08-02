package net.sz.framework.map.thread;

import io.netty.channel.ChannelHandlerContext;
import net.sz.framework.map.manager.AbsMapManager;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.nio.tcp.NettyCoder;
import net.sz.framework.nio.tcp.NettyTcpHandler;
import net.sz.framework.struct.thread.BaseThreadRunnable;
import net.sz.framework.szthread.TaskModel;
import net.sz.framework.struct.thread.ThreadType;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.szthread.SzQueueThread;
import net.sz.framework.szthread.ThreadPool;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapThreadExcutor extends SzQueueThread {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 3590373543324122407L;

    public static final ThreadGroup THREAD_GROUP = new ThreadGroup(BaseThreadRunnable.GlobalThreadGroup.getParent(), "Map-THREAD-GROUP");
    public static final String LOGINPLAYER = "LOGINPLAYER";
    public static final String LOGINPLAYERID = "LOGINPLAYERID";
    public static final MapThread MapAllExcutor;

    static {
        MapAllExcutor = new MapThread("Map-Info-Excutor", 5);
        ThreadPool.addThread(MapAllExcutor);
    }

    public MapThreadExcutor() {
        super(ThreadType.User, THREAD_GROUP, "Map-GLOBAL-Excutor", 1);
    }

    @Override
    public void run(TaskModel taskModel) {
        if (taskModel == null) {
            return;
        }
        if (taskModel.isCancel()) {
            /* 如果任务已经取消 */
            return;
        }

        if (taskModel instanceof NettyTcpHandler) {
            /*找到对应地图线程*/
            NettyTcpHandler reqHandler = (NettyTcpHandler) taskModel;
            /*获得网络链接*/
            ChannelHandlerContext session = reqHandler.getSession();
            if (session == null) {
                log.error("执行分发command到地图服务器时,IoSession为NULL!" + taskModel.getClass().getName());
                return;
            }
            /*验证Session中的Player是否为空*/
            Person person = NettyCoder.getSessionAttr(session, LOGINPLAYER, Person.class);

            if (person == null) {
                log.error("执行分发command到地图服务器时,IoSession尚未赋值player! reqHandler = " + taskModel.getClass().getName());
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("收到消息：" + reqHandler.getMessage().getClass().getName() + " 对象：" + person.showString());
            }

            AbsMapManager.addPersonTask(person, reqHandler);
        }
    }
}
