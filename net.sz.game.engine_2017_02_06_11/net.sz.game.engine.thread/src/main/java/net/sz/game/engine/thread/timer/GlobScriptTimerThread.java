package net.sz.game.engine.thread.timer;

import java.util.ArrayList;
import java.util.Calendar;
import net.sz.game.engine.scripts.ScriptPool;
import net.sz.game.engine.thread.ThreadPool;
import net.sz.game.engine.thread.timer.iscript.IDayZeroEventTimerScript;
import net.sz.game.engine.thread.timer.iscript.IHourEventTimerScript;
import net.sz.game.engine.thread.timer.iscript.IMinuteEventTimerScript;
import net.sz.game.engine.thread.timer.iscript.ISecondsEventTimerScript;
import org.apache.log4j.Logger;

/**
 * 脚本定时器执行线程
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class GlobScriptTimerThread extends Thread {

    private static final Logger log = Logger.getLogger(GlobScriptTimerThread.class);
    private static final Object SYN_OBJECT = new Object();

    int second = -1;
    int minute = -1;
    int hour = -1;
    int day = -1;
    ScriptPool _ScriptPool;

    /**
     * 默认一秒执行一次
     *
     * @param scriptPool 脚本管理器
     */
    public GlobScriptTimerThread(ScriptPool scriptPool) {
        super(ThreadPool.GlobalThreadGroup, "Glob Script Timer Thread");
        _ScriptPool = scriptPool;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (SYN_OBJECT) {
                try {
                    SYN_OBJECT.wait(800);
                } catch (InterruptedException ex) {
                }
            }
            if (!ThreadPool.isStarEnd()) {
                continue;
            }

            Calendar calendar = Calendar.getInstance();

            int sec = calendar.get(Calendar.SECOND);
            int min = calendar.get(Calendar.MINUTE);
            int h = calendar.get(Calendar.HOUR);
            int _day = calendar.get(Calendar.DAY_OF_MONTH);

            if (this.second != sec) {
                this.second = sec;
                ArrayList<ISecondsEventTimerScript> evts = _ScriptPool.getEvts(ISecondsEventTimerScript.class);
                for (int i = 0; i < evts.size(); i++) {
                    ISecondsEventTimerScript get = evts.get(i);
                    try {
                        get.run(sec);
                    } catch (Exception e) {
                        log.error("执行任务错误：" + get.getClass().getName(), e);
                    }
                }
//                Iterator<ISecondsEventTimerScript> secondsScripts = _ScriptPool.getIterator(ISecondsEventTimerScript.class);
//                while (secondsScripts.hasNext()) {
//                    try {
//                        secondsScripts.next().run(sec);
//                    } catch (Exception e) {
//                        log.error("执行任务错误：" + secondsScripts.next().getClass().getName(), e);
//                    }
//                }
            }

            if (this.minute != min) {
                this.minute = min;
                ArrayList<IMinuteEventTimerScript> evtsMin = _ScriptPool.getEvts(IMinuteEventTimerScript.class);
                for (int i = 0; i < evtsMin.size(); i++) {
                    IMinuteEventTimerScript getMin = evtsMin.get(i);
                    try {
                        getMin.run(minute);
                    } catch (Exception e) {
                        log.error("执行任务错误：" + getMin.getClass().getName(), e);
                    }
                }
//                Iterator<IMinuteEventTimerScript> minuteScripts = _ScriptPool.getIterator(IMinuteEventTimerScript.class);
//                while (minuteScripts.hasNext()) {
//                    try {
//                        minuteScripts.next().run(minute);
//                    } catch (Exception e) {
//                        log.error("执行任务错误：" + minuteScripts.next().getClass().getName(), e);
//                    }
//                }
            }

            if (this.hour != h) {
                this.hour = h;
                ArrayList<IHourEventTimerScript> evtsHour = _ScriptPool.getEvts(IHourEventTimerScript.class);
                for (int i = 0; i < evtsHour.size(); i++) {
                    IHourEventTimerScript getHour = evtsHour.get(i);
                    try {
                        getHour.run(this.hour);
                    } catch (Exception e) {
                        log.error("执行任务错误：" + getHour.getClass().getName(), e);
                    }
                }
//                Iterator<IHourEventTimerScript> hoursScripts = _ScriptPool.getIterator(IHourEventTimerScript.class);
//                while (hoursScripts.hasNext()) {
//                    try {
//                        hoursScripts.next().run(this.hour);
//                    } catch (Exception e) {
//                        log.error("执行任务错误：" + hoursScripts.next().getClass().getName(), e);
//                    }
//                }
            }

            if (this.day != _day) {
                this.day = _day;
                ArrayList<IDayZeroEventTimerScript> evtsDay = _ScriptPool.getEvts(IDayZeroEventTimerScript.class);
                for (int i = 0; i < evtsDay.size(); i++) {
                    IDayZeroEventTimerScript getDay = evtsDay.get(i);
                    try {
                        getDay.run(this.day);
                    } catch (Exception e) {
                        log.error("执行任务错误：" + getDay.getClass().getName(), e);
                    }
                }
//                Iterator<IDayZeroEventTimerScript> hoursScripts = _ScriptPool.getIterator(IDayZeroEventTimerScript.class);
//                while (hoursScripts.hasNext()) {
//                    try {
//                        hoursScripts.next().run(this.day);
//                    } catch (Exception e) {
//                        log.error("执行任务错误：" + hoursScripts.next().getClass().getName(), e);
//                    }
//                }
            }
        }
    }
}
