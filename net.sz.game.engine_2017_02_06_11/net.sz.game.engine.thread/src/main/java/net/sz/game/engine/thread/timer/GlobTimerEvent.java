package net.sz.game.engine.thread.timer;

//package net.sz.engine.timer;
//
//import java.util.Calendar;
//import java.util.Iterator;
//import org.apache.log4j.Logger;
//import net.sz.engine.script.ScriptPool;
//import net.sz.engine.thread.ThreadPool;
//import net.sz.engine.thread.TimerTaskEvent;
//import net.sz.engine.timer.iscript.IHourEventTimerScript;
//import net.sz.engine.timer.iscript.IMinuteEventTimerScript;
//import net.sz.engine.timer.iscript.ISecondsEventTimerScript;
//
///**
// *
// *
// * <br>
// * author 失足程序员<br>
// * mail 492794628@qq.com<br>
// * phone 13882122019<br>
// */
//public class GlobTimerEvent extends TimerTaskEvent {
//
//    private static final Logger log = Logger.getLogger(GlobTimerEvent.class);
//    int second = -1;
//    int minute = -1;
//    int hour = -1;
//    ScriptPool _ScriptPool;
//
//    /**
//     * 默认一秒执行一次
//     *
//     * @param scriptPool 脚本管理器
//     */
//    public GlobTimerEvent(ScriptPool scriptPool) {
//        super(995);
//        _ScriptPool = scriptPool;
//    }
//
//    @Override
//    public void run() {
//        if (!ThreadPool.isStarEnd()) {
//            return;
//        }
//        Calendar calendar = Calendar.getInstance();
//        int sec = calendar.get(Calendar.SECOND);
//        if (this.second != sec) {
//            this.second = sec;
//            Iterator<ISecondsEventTimerScript> secondsScripts = _ScriptPool.getIterator(ISecondsEventTimerScript.class);
//            while (secondsScripts.hasNext()) {
//                ISecondsEventTimerScript next = secondsScripts.next();
//                try {
//                    next.run(sec);
//                } catch (Exception e) {
//                    log.error("执行任务错误：" + next.getClass().getName(), e);
//                }
//            }
//        }
//
//        int min = calendar.get(Calendar.MINUTE);
//        if (this.minute != min) {
//            this.minute = min;
//            Iterator<IMinuteEventTimerScript> minuteScripts = _ScriptPool.getIterator(IMinuteEventTimerScript.class);
//            while (minuteScripts.hasNext()) {
//                IMinuteEventTimerScript next = minuteScripts.next();
//                try {
//                    next.run(minute);
//                } catch (Exception e) {
//                    log.error("执行任务错误：" + next.getClass().getName(), e);
//                }
//            }
//        }
//
//        int h = calendar.get(Calendar.HOUR);
//        if (this.hour != h) {
//            this.hour = h;
//            Iterator<IHourEventTimerScript> hoursScripts = _ScriptPool.getIterator(IHourEventTimerScript.class);
//            while (hoursScripts.hasNext()) {
//                IHourEventTimerScript next = hoursScripts.next();
//                try {
//                    next.run(this.hour);
//                } catch (Exception e) {
//                    log.error("执行任务错误：" + next.getClass().getName(), e);
//                }
//            }
//        }
//    }
//
//}
