package net.sz.game.engine.map.thread;

import io.netty.channel.ChannelHandlerContext;
import net.sz.game.engine.nio.nettys.tcp.NettyCoder;
import net.sz.game.engine.nio.nettys.tcp.NettyTcpHandler;
import net.sz.game.engine.map.manager.AbsMapManager;
import net.sz.game.engine.map.spirit.Person;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.SzThread;
import net.sz.game.engine.thread.ThreadType;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapThreadExcutor extends SzThread {

    private static SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 3590373543324122407L;

    public static final ThreadGroup THREAD_GROUP = new ThreadGroup(ThreadPool.GlobalThreadGroup.getParent(), "Map-THREAD-GROUP");
    public static final String LOGINPLAYER = "LOGINPLAYER";
    public static final String LOGINPLAYERID = "LOGINPLAYERID";
    public static final MapThread MapExcutor;

    static {
        MapExcutor = new MapThread("Map-Action-Excutor", 5);
        MapExcutor.addTimerTask(new MapHertTimerTask());
        ThreadPool.addThread(MapExcutor);
    }

    public MapThreadExcutor() {
        super(ThreadType.User, THREAD_GROUP, "Map-GLOBAL-Excutor", 1);
    }

    @Override
    public void run() {
        ThreadModel currentThread = (ThreadModel) Thread.currentThread();
        while (runing) {
            currentThread.setLastCommand(null);
            currentThread.setLastExecuteTime(0);
            while (taskQueue.isEmpty() && runing) {
                try {
                    /* 任务队列为空，则等待有新任务加入从而被唤醒 */
                    synchronized (taskQueue) {
                        taskQueue.wait();
                    }
                } catch (InterruptedException ie) {
                    log.error(ie);
                }
            }
            /* 取出任务执行 */
            if (runing) {
                currentThread.setLastCommand(taskQueue.poll());
            }
            if (currentThread.getLastCommand() != null) {
                /* 执行任务 */
                currentThread.setLastExecuteTime(System.currentTimeMillis());
                try {
                    if (currentThread.getLastCommand() instanceof NettyTcpHandler) {
                        /*找到对应地图线程*/
                        NettyTcpHandler reqHandler = (NettyTcpHandler) currentThread.getLastCommand();
                        /*获得网络链接*/
                        ChannelHandlerContext session = reqHandler.getSession();
                        if (session == null) {
                            log.error("执行分发command到地图服务器时,IoSession为NULL!" + currentThread.getLastCommand().getClass().getName());
                            continue;
                        }

                        /*验证Session中的Player是否为空*/
                        //Person person = reqHandler.getTmpParameter().get(LOGINPLAYER, Person.class);
                        Person person = NettyCoder.getSessionAttr(session, LOGINPLAYER, Person.class);
                        if (person == null) {
                            log.error("执行分发command到地图服务器时,IoSession尚未赋值player! reqHandler = " + currentThread.getLastCommand().getClass().getName());
                            continue;
                        }
                        MapThread mapThread = AbsMapManager.getMAPTHREAD_MAP().get(person.getMapThreadId());
                        mapThread.addTask(reqHandler);
                        //AbsMapManager.addTask(person, reqHandler);

//                        MapInfo map = AbsMapManager.getMapInfo(person);
//                        MapServer mapServer = mapServers.get(getKey(person.getLineId(), person.getMapId()));
//                        if (mapServer == null) {
//                            log.error("未找到玩家所在地图服务器!!!" + person.getLineId() + " " + person.getMapId());
//                            // 断开链接
//                            NettyPool.getInstance().closeSession(session, "未找到玩家所在地图服务器!!!" + person.getLineId() + " " + person.getMapId());
//                            continue;
//                        }
//
//                        mapServer.addTask(reqHandler);
                    }
                } catch (Throwable e) {
                    log.error("工人<“" + currentThread.getName() + "”> 执行任务<" + currentThread.getLastCommand().getClass().getName() + "> 遇到错误: ", e);
                }
                long timeL1 = System.currentTimeMillis() - currentThread.getLastExecuteTime();
                if (timeL1 > 20) {
                    log.error("工人<“" + currentThread.getName() + "”> 完成了任务：" + currentThread.getLastCommand().getClass().getName() + " 执行耗时：" + timeL1);
                }
            }
        }
        log.error("线程结束, 工人<“" + Thread.currentThread().getName() + "”>退出");
    }
}
