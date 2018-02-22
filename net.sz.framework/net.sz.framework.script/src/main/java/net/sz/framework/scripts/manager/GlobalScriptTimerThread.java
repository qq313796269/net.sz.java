package net.sz.framework.scripts.manager;

import java.util.ArrayList;
import net.sz.framework.scripts.timer.iscript.IDayZeroEventTimerScript;
import net.sz.framework.scripts.timer.iscript.IHourEventTimerScript;
import net.sz.framework.scripts.timer.iscript.IMinuteEventTimerScript;
import net.sz.framework.scripts.timer.iscript.ISecondsEventTimerScript;
import net.sz.framework.szlog.SzLogger;
import net.sz.framework.utils.GlobalUtil;
import net.sz.framework.utils.TimeUtil;

/**
 * 脚本定时器执行线程
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
class GlobalScriptTimerThread extends Thread {

    private static final SzLogger log = SzLogger.getLogger();
    private static final Object SYN_OBJECT = new Object();

    int second = -1;
    int minute = -1;
    int hour = -1;
    int day = -1;

    /**
     * 默认一秒执行一次
     *
     */
    public GlobalScriptTimerThread() {
        super("Global-Script-Timer-Thread");
        this.start();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {

                synchronized (SYN_OBJECT) {
                    SYN_OBJECT.wait(998);
                }

                if (!GlobalUtil.SERVERSTARTEND) {
                    continue;
                }

                long currentTimeMillis = TimeUtil.currentTimeMillis();
                /*获取当前时间的秒*/
                int sec = TimeUtil.getDayOfSecond(currentTimeMillis);
                /*获取当前时间 的分钟*/
                int min = TimeUtil.getDayOfMin(currentTimeMillis);
                /*获取当前时间的小时*/
                int h = TimeUtil.getDayOfHour(currentTimeMillis);
                /*获取当前时间运行周期*/
                int _day = TimeUtil.getDays(currentTimeMillis);

                if (this.second != sec) {
                    this.second = sec;
                    ArrayList<ISecondsEventTimerScript> evts = ScriptManager.getInstance().getBaseScriptEntry().getEvts(ISecondsEventTimerScript.class);
                    for (int i = 0; i < evts.size(); i++) {
                        ISecondsEventTimerScript get = evts.get(i);
                        try {
                            get.run(sec);
                        } catch (Throwable e) {
                            log.error("执行每秒钟运行脚本定时器任务：" + get.getClass().getName(), e);
                        }
                    }
                }

                if (this.minute != min) {
                    this.minute = min;
                    ArrayList<IMinuteEventTimerScript> evtsMin = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IMinuteEventTimerScript.class);
                    for (int i = 0; i < evtsMin.size(); i++) {
                        IMinuteEventTimerScript getMin = evtsMin.get(i);
                        try {
                            getMin.run(minute);
                        } catch (Throwable e) {
                            log.error("执行每分钟运行脚本定时器任务：" + getMin.getClass().getName(), e);
                        }
                    }
                }

                if (this.hour != h) {
                    this.hour = h;
                    ArrayList<IHourEventTimerScript> evtsHour = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IHourEventTimerScript.class);
                    for (int i = 0; i < evtsHour.size(); i++) {
                        IHourEventTimerScript getHour = evtsHour.get(i);
                        try {
                            getHour.run(this.hour);
                        } catch (Throwable e) {
                            log.error("执行每小时运行脚本定时器任务：" + getHour.getClass().getName(), e);
                        }
                    }
                }

                if (this.day != _day && (this.hour == 0 || this.hour == 24)) {
                    this.day = _day;
                    ArrayList<IDayZeroEventTimerScript> evtsDay = ScriptManager.getInstance().getBaseScriptEntry().getEvts(IDayZeroEventTimerScript.class);
                    for (int i = 0; i < evtsDay.size(); i++) {
                        IDayZeroEventTimerScript getDay = evtsDay.get(i);
                        try {
                            getDay.run(this.day);
                        } catch (Throwable e) {
                            log.error("执行每天凌晨运行脚本定时器任务：" + getDay.getClass().getName(), e);
                        }
                    }
                }
            } catch (Throwable e) {
            }
        }
    }
}
