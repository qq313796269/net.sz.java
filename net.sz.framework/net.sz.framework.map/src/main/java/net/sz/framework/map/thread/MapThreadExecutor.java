package net.sz.framework.map.thread;

import io.netty.channel.ChannelHandlerContext;
import net.sz.framework.map.manager.AbsMapManager;
import net.sz.framework.map.spirit.Person;
import net.sz.framework.nio.tcp.NettyCoder;
import net.sz.framework.nio.tcp.NettyTcpHandler;
import net.sz.framework.thread.BaseTask;
import net.sz.framework.thread.BaseThread;
import net.sz.framework.thread.ExecutorFactory;
import net.sz.framework.thread.ServerExecutorQueue2Timer;
import net.sz.framework.thread.ExecutorType;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.thread.ExecutorKey;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapThreadExecutor extends ServerExecutorQueue2Timer {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 3590373543324122407L;

    public static final ThreadGroup THREAD_GROUP = new ThreadGroup(ExecutorFactory.GlobalThreadGroup.getParent(), "Map-THREAD-GROUP");
    public static final String LOGINPLAYER = "LOGINPLAYER";
    public static final String LOGINPLAYERID = "LOGINPLAYERID";
    public static final ServerExecutorQueue2Timer MapAllExcutor;

    static {
        MapAllExcutor = ExecutorFactory.newServerExecutorQueue2Timer(ExecutorType.User, ExecutorKey.valueOf("Map-Info-Excutor"), THREAD_GROUP, 5, 200000);
    }

    public MapThreadExecutor() {
        super(ExecutorType.User, ExecutorKey.valueOf("Map-GLOBAL-Excutor"), THREAD_GROUP, 1, 2000000);
    }

    @Override
    protected void runTask(BaseThread baseThread, BaseTask task) {
        if (task == null) {
            return;
        }
        if (task.isCancel()) {
            /* 如果任务已经取消 */
            return;
        }

        if (task instanceof NettyTcpHandler) {
            /*找到对应地图线程*/
            NettyTcpHandler reqHandler = (NettyTcpHandler) task;
            /*获得网络链接*/
            ChannelHandlerContext session = reqHandler.getSession();
            if (session == null) {
                log.error("执行分发command到地图服务器时,IoSession为NULL!" + task.getClass().getName());
                return;
            }
            /*验证Session中的Player是否为空*/
            Person person = NettyCoder.getSessionAttr(session, LOGINPLAYER, Person.class);

            if (person == null) {
                log.error("执行分发command到地图服务器时,IoSession尚未赋值player! reqHandler = " + task.getClass().getName());
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug("收到消息：" + reqHandler.getMessage().getClass().getName() + " 对象：" + person.showString());
            }

            AbsMapManager.addPersonTask(person, reqHandler);
        }
    }
}
