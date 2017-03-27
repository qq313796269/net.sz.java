package net.sz.game.engine.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class TimeUtil {

    private static SzLogger log = SzLogger.getLogger();
    private static final SimpleDateFormat DF222 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    public static void main(String[] args) throws ParseException {
        ArrayList<Integer> list = new ArrayList<>(100000);
        for (int i = 0; i < 1; i++) {

            String time = "[*][*][*][*][02:00-02:00]";
            Date parse = DF222.parse("2016-12-28-23-59-58-000");
            long currentTimeMillis = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
//            boolean verifyConfigTimeStr = TimeUtil.verifyConfigTimeStr(calendar, time);
            long verifyConfigTimeStr = TimeUtil.verifyDateTime(calendar, time);
//            long verifyConfigTimeStr = TimeUtil.verifyDateTime(calendar, time);
//            long verifyConfigTimeStr = TimeUtil.verifyDateTime(time);
            long currentTimeMillis1 = System.currentTimeMillis();
            System.out.println("活动是否开启：" + verifyConfigTimeStr + "  " + (currentTimeMillis1 - currentTimeMillis));
        }
//        HashMap<Integer, Integer> parseObject = JsonUtil.parseObject("{0:70148,1:70136,2:70137,3:70142}", (new HashMap<Integer, Integer>()).getClass());
//        System.err.println(JsonUtil.toJSONString(parseObject));
//        System.out.println(getQuarterOfYear());
//        System.out.println("223717".indexOf("223717"));
    }

    /**
     * yyyy-MM-dd
     */
    public static final SimpleDateFormat DF1 = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * yyyy-MM-dd HH
     */
    public static final SimpleDateFormat DF2 = new SimpleDateFormat("yyyy-MM-dd HH");
    /**
     * yyyy-MM-dd-HH
     */
    public static final SimpleDateFormat DF21 = new SimpleDateFormat("yyyy-MM-dd-HH");
    /**
     * yyyy-MM-dd HH:mm
     */
    public static final SimpleDateFormat DF3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final SimpleDateFormat DF4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected static long execTime = 0l;

    public static long currentTimeMillis() {
        //return System.currentTimeMillis() + execTime;
        return System.currentTimeMillis();
    }

    /**
     * 返回指定天数以后的时间，凌晨
     *
     * @param currentTimeMillis
     * @param days
     * @return
     */
    public static long getAddDays(long currentTimeMillis, int days) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DF1.parse(DF1.format(new Date(currentTimeMillis))));
            calendar.add(Calendar.DATE, days);
            return calendar.getTime().getTime();
        } catch (Throwable ex) {
        }
        return 0;
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd")
     *
     * @param date
     * @return
     */
    public static String getDateFormat1(Date date) {
        return DF1.format(date);
    }

    public static Date getDateFormat1(String fmt) {
        try {
            return DF1.parse(fmt);
        } catch (Throwable e) {
        }
        return null;
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd")
     *
     * @return
     */
    public static String getDateFormat1() {
        return getDateFormat1(new Date());
    }

    /**
     * 获取当前时间的星期
     *
     * @return
     */
    public static int getWeekOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    /**
     * 获取昨天时间 SimpleDateFormat("yyyy-MM-dd")
     *
     * @return
     */
    public static String getUpDay() {
        return getUpDay(new Date());
    }

    /**
     * 获取昨天时间 SimpleDateFormat("yyyy-MM-dd")
     *
     * @param date
     * @return
     */
    public static String getUpDay(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.set(Calendar.HOUR_OF_DAY, -1);//日期减去一天
        return DF1.format(instance.getTime());
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH")
     * <pre> 篝火晚会使用的精确到小时</pre>
     *
     * @return
     */
    @Deprecated
    public static String getDateFormatHH() {
        return getDateFormat1(new Date());
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH")
     * <pre> 篝火晚会使用的精确到小时</pre>
     *
     * @param date
     * @return
     */
    @Deprecated
    public static String getDateFormatHH(Date date) {
        return DF3.format(date);
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *
     * @return
     */
    public static String getDateFormat2() {
        return getDateFormat2(new Date());
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *
     * @param date
     * @return
     */
    public static String getDateFormat2(Date date) {
        return DF2.format(date);
    }

    /**
     * SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     *
     * @param fmt
     * @return
     */
    public static Date getDateFormat2(String fmt) {
        try {
            return DF2.parse(fmt);
        } catch (Throwable e) {
        }
        return null;
    }

    /**
     * 获取日期
     *
     * @param time
     * @return
     */
    public static int getDayOfMonth(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取月份
     *
     * @param time
     * @return
     */
    public static int getMonth(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取小时
     *
     * @param time
     * @return
     */
    public static int getDayOfHour(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分钟
     *
     * @param time
     * @return
     */
    public static int getDayOfMin(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     *
     * @param time
     * @return
     */
    public static int getDayOfSecond(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.SECOND);
    }

    //<editor-fold defaultstate="collapsed" desc="获取当前日期的星期一时间 public String getDateWeekMonday()">
    /**
     * 获取星期几,周日为 7
     *
     * @param time
     * @return
     */
    public static int getDayOfWeek(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        if (w == 0) {
            w = 7;
        }
        return w;
    }

    /**
     * 获取当前日期的星期一时间，
     * <br>修正过的 如果是星期天，则计算为上一周
     * <br>国际时间 星期天 为 一周 的开始
     *
     * @return
     */
    public static String getDateWeekMonday() {
        Calendar cal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        if (w == 0) {
            //国际时间 星期天 为 一周 的开始
            //如果是星期天的话，计算为上一周
            cal.add(Calendar.DATE, -1);
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return DF1.format(cal.getTime());
    }
    //</editor-fold>

    /**
     * 获取每一个月的第一天的日期
     *
     * @return
     */
    public static String getDateMonthDayString() {
        Calendar cal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return DF1.format(cal.getTime());
    }

    /**
     * 获取每一年的第一天的日期
     *
     * @return
     */
    public static String getDateYearDayString() {
        Calendar cal = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return DF1.format(cal.getTime());
    }

    /**
     * 获取指定时间 是一月内的第几周
     *
     * @param time
     * @return
     */
    public static int getDayOfWeekInMonth(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获取一年内的第几天
     *
     * @param time
     * @return
     */
    public static int getDayOfYear(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 返回指定日期的季度第一天 yyyy-MM-dd
     *
     * @return
     */
    public static String getQuarterOfYear() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int newmonth = month % 3;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month - newmonth);
        return TimeUtil.DF1.format(calendar.getTime());
    }

    /**
     * 获取1970至今的时间, 1获取秒，2 分钟，3小时，4天数,5周数
     *
     * @param x
     * @return
     */
    public static long getCurTimeInMin(int x, long time) {
        TimeZone zone = TimeZone.getDefault();	//默认时区
        long s = time / 1000;
        if (zone.getRawOffset() != 0) {
            s = s + zone.getRawOffset() / 1000;
        }
        switch (x) {
            case 1:
                break;
            case 2:
                s = s / 60;
                break;
            case 3:
                s = s / 3600;
                break;
            case 4:
                s = s / 86400;
                break;
            case 5:
                s = s / 86400 + 3;// 补足天数，星期1到7算一周
                s = s / 7;
                break;
            default:
                break;
        }
        return s;
    }

//    /**
//     * 得到当前服务器开区天数
//     *
//     * @return
//     */
//    public static int getOpenAreaDay() {
//        Date time = Main.getOpenTime();
//        long zday = getCurTimeInMin(4, time.getTime());
//        long sday = getCurTimeInMin(4, System.currentTimeMillis());
//        int day = (int) (sday - zday) + 1;
//        return day;
//    }
    /**
     * 判断两个时间是否在同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameDay(long time1, long time2) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time1);
        int d1 = instance.get(Calendar.DAY_OF_YEAR);
        int y1 = instance.get(Calendar.YEAR);
        instance.setTimeInMillis(time2);
        int d2 = instance.get(Calendar.DAY_OF_YEAR);
        int y2 = instance.get(Calendar.YEAR);
        return d1 == d2 && y1 == y2;
    }

    /**
     * 星期替换
     *
     * @param week
     * @return
     */
    private static String replaceMonth(String week) {
        // 星期一 MON 星期二 TUE 星期三 WED 星期四 THU 星期五 FRI 星期六 SAT 星期天 SUN
        return week.replaceAll("w1", "MON").replaceAll("w2", "TUE").replaceAll("w3", "WED").replaceAll("w4", "THU").replaceAll("w5", "FRI").replaceAll("w6", "SAT").replaceAll("w7", "SUN");
    }

    //<editor-fold desc="获取倒计时，当前时间内，距离结束倒计时 static public long verifyDateEndTime(String timeStr)">
    /**
     * 获取倒计时，当前时间内，距离结束倒计时
     *
     * @param timeStr [*][*][20/22][*][10:00-11:59/16:00-17:59]
     * @return
     */
    static public long verifyDateEndTime(String timeStr) {
        Calendar calendar = Calendar.getInstance();
        return verifyDateEndTime(calendar, timeStr);
    }

    /**
     * 获取倒计时，当前时间内，距离结束倒计时
     *
     * @param calendar
     * @param timeStr [*][*][20/22][*][10:00-11:59/16:00-17:59]
     * @return
     */
    static public long verifyDateEndTime(Calendar calendar, String timeStr) {
        String[] items = timeStr.split(";|；");
        for (String item : items) {
            //验证时间匹配
            if (verifyConfigTimeStr(calendar, item)) {
                String[] timeStrs = item.replace("[", "").split("]");
                String times = timeStrs[4];
                String weeks = timeStrs[3];
                String days = timeStrs[2];
                String months = timeStrs[1];
                String years = timeStrs[0];

                int hour = 0, minute = 0, second = 0;

                ArrayList<Integer> tempYears = getConfigDate(calendar, calendar.get(Calendar.YEAR), years);
                ArrayList<Integer> tempMonths = getConfigDate(calendar, calendar.get(Calendar.MONTH) + 1, months);
                ArrayList<Integer> tempDays = getConfigDate(calendar, calendar.get(Calendar.DATE), days);
                //由于星期比较特殊所以获取与星期相关的日期的时候有点诡异。
                if (!"*".equals(weeks)) {
                    if (weeks.indexOf("-") > 0) {
                        //星期的间隔模式
                        String[] weeksplit = weeks.split("-");
                        int weekmin = Integer.parseInt(weeksplit[0]);
                        int weekmax = Integer.parseInt(weeksplit[1]);
                        actionWeekDay(weekmin, weekmax, tempDays, tempMonths, tempYears);
                    } else if (weeks.indexOf("/") > 0) {
                        //星期的或模式
                        String[] weekssplit = weeks.split("/");
                        int tempWeek;
                        for (String weekssplit1 : weekssplit) {
                            tempWeek = Integer.parseInt(weekssplit1);
                            if (0 <= tempWeek && tempWeek <= 7) {
                                actionWeekDay(tempWeek, tempWeek, tempDays, tempMonths, tempYears);
                            }
                        }
                    } else {
                        //特定星期的模式
                        int tempweek = Integer.parseInt(weeks);
                        actionWeekDay(tempweek, tempweek, tempDays, tempMonths, tempYears);
                    }
                } else {
                    //未指定星期的模式
//                    actionWeekDay(1, 7, tempDays, tempMonths, tempYears);
                }

                //获取结束时间倒计时
                ArrayList<String> tempHHMMs = getConfigEndTimeStr(times);

                //进行简单的排序
                Collections.sort(tempYears);
                Collections.sort(tempMonths);
                Collections.sort(tempDays);
                Collections.sort(tempHHMMs);

                //接下来这里是天坑，就是构造时间器比较，然后计算出倒计时
                for (int y = 0; y < tempYears.size(); y++) {
                    for (int m = 0; m < tempMonths.size(); m++) {
                        for (int d = 0; d < tempDays.size(); d++) {
                            if (tempYears.get(y) < calendar.get(Calendar.YEAR)) {
                                continue;
                            }
                            if (tempYears.get(y) == calendar.get(Calendar.YEAR) && tempMonths.get(m) - 1 < calendar.get(Calendar.MONTH)) {
                                continue;
                            }
                            if (tempYears.get(y) == calendar.get(Calendar.YEAR) && tempMonths.get(m) - 1 == calendar.get(Calendar.MONTH) && tempDays.get(d) < calendar.get(Calendar.DATE)) {
                                continue;
                            }
                            for (int h = 0; h < tempHHMMs.size(); h++) {
                                String[] hhmm = tempHHMMs.get(h).split(":|：");
                                hour = Integer.parseInt(hhmm[0]);
                                minute = Integer.parseInt(hhmm[1]);
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(new Date((tempYears.get(y) - 1900), (tempMonths.get(m) - 1), tempDays.get(d), hour, minute, second));
                                    if (hour == 23 && minute == 59) {//把时间加1分钟
                                        calendar1.add(Calendar.MINUTE, 1);
                                    }
                                } catch (Throwable ex) {
                                    log.error("", ex);
                                }
//                                calendar1.set(tempYears.get(y), (tempMonths.get(m) - 1), tempDays.get(d), hour, minute, second);
//                                System.out.println(DF6.format(calendar1.getTime()) + "   " + DF6.format(calendar.getTime()));
                                if (calendar1.getTimeInMillis() > calendar.getTimeInMillis()) {
                                    return calendar1.getTimeInMillis() - calendar.getTimeInMillis();
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }
    //</editor-fold>

    //<editor-fold desc="获取倒计时，距离开始时间倒计时 :[*][*][20/22][*][10:00-11:59/16:00-17:59] static public long verifyDateTime(String timeStr)">
    /**
     * 获取倒计时，距离开始时间倒计时，返回值 -1 表示永久过期，0 表示在时间规则内，大于 0 表示倒计时
     *
     * @param timeStr [*][*][20/22][*][10:00-11:59/16:00-17:59]
     * @return 返回值 -1 表示永久过期，0 表示在时间规则内，大于 0 表示倒计时
     */
    static public long verifyDateTime(String timeStr) {
        Calendar calendar = Calendar.getInstance();
        return verifyDateTime(calendar, timeStr);
    }

    /**
     *
     * @param calendar
     * @param timeStr
     * @return
     */
    static public long verifyDateTime(Calendar calendar, String timeStr) {
        long ret = -1;
        String[] items = timeStr.split(";|；");
        for (String item : items) {
            //验证时间匹配
            if (verifyConfigTimeStr(calendar, item)) {
                ret = 0;
                break;
            }
            //未通过时间匹配，检查返回剩余时间
            String[] timeStrs = item.replace("[", "").split("]");

            String times = timeStrs[4];
            String weeks = timeStrs[3];
            String days = timeStrs[2];
            String months = timeStrs[1];
            String years = timeStrs[0];

            int hour = 0, minute = 0, second = 0;

            ArrayList<Integer> tempYears = getConfigDate(calendar, calendar.get(Calendar.YEAR), years);
            ArrayList<Integer> tempMonths = getConfigDate(calendar, calendar.get(Calendar.MONTH) + 1, months);
            ArrayList<Integer> tempDays = getConfigDate(calendar, calendar.get(Calendar.DATE), days);
            //由于星期比较特殊所以获取与星期相关的日期的时候有点诡异。
            if (!"*".equals(weeks)) {
                if (weeks.indexOf("-") > 0) {
                    //星期的间隔模式
                    String[] weeksplit = weeks.split("-");
                    int weekmin = Integer.parseInt(weeksplit[0]);
                    int weekmax = Integer.parseInt(weeksplit[1]);
                    actionWeekDay(weekmin, weekmax, tempDays, tempMonths, tempYears);
                } else if (weeks.indexOf("/") > 0) {
                    //星期的或模式
                    String[] weekssplit = weeks.split("/");
                    int tempWeek;
                    for (String weekssplit1 : weekssplit) {
                        tempWeek = Integer.parseInt(weekssplit1);
                        if (0 <= tempWeek && tempWeek <= 7) {
                            actionWeekDay(tempWeek, tempWeek, tempDays, tempMonths, tempYears);
                        }
                    }
                } else {
                    //特定星期的模式
                    int tempweek = Integer.parseInt(weeks);
                    actionWeekDay(tempweek, tempweek, tempDays, tempMonths, tempYears);
                }
            } else {
                //未指定星期的模式
//                actionWeekDay(1, 7, tempDays, tempMonths, tempYears);
            }

            ArrayList<String> tempHHMMs = getConfigTimeStr(times);

            //进行简单的排序
            Collections.sort(tempYears);
            Collections.sort(tempMonths);
            Collections.sort(tempDays);
            Collections.sort(tempHHMMs);

            //接下来这里是天坑，就是构造时间器比较，然后计算出倒计时
            for (int y = 0; y < tempYears.size(); y++) {
                for (int m = 0; m < tempMonths.size(); m++) {
                    for (int d = 0; d < tempDays.size(); d++) {
                        if (tempYears.get(y) < calendar.get(Calendar.YEAR)) {
                            continue;
                        }
                        if (tempYears.get(y) == calendar.get(Calendar.YEAR) && tempMonths.get(m) - 1 < calendar.get(Calendar.MONTH)) {
                            continue;
                        }
                        if (tempYears.get(y) == calendar.get(Calendar.YEAR) && tempMonths.get(m) - 1 == calendar.get(Calendar.MONTH) && tempDays.get(d) < calendar.get(Calendar.DATE)) {
                            continue;
                        }
                        for (int h = 0; h < tempHHMMs.size(); h++) {
                            String[] hhmm = tempHHMMs.get(h).split(":|：");
                            hour = Integer.parseInt(hhmm[0]);
                            minute = Integer.parseInt(hhmm[1]);
                            Calendar calendar1 = Calendar.getInstance();
                            try {
                                calendar1.setTime(new Date((tempYears.get(y) - 1900), (tempMonths.get(m) - 1), tempDays.get(d), hour, minute, second));
                            } catch (Throwable ex) {
                                log.error("", ex);
                            }
//                            calendar1.set(tempYears.get(y), (tempMonths.get(m) - 1), tempDays.get(d), hour, minute, second);
//                            System.out.println(DF2.format(calendar1.getTime()) + "   " + DF2.format(calendar.getTime()));
//                            System.out.println(calendar1.getTimeInMillis() + "   " + calendar.getTimeInMillis());
                            long tmpRet = calendar1.getTimeInMillis() - calendar.getTimeInMillis();
//                            System.out.println(DF6.format(calendar1.getTime()) + "   " + DF6.format(calendar.getTime()));
                            if (tmpRet > 0) {
                                if (verifyConfigTimeStr(calendar1, item)) {
                                    return tmpRet;
//                                    long tmpRet = calendar1.getTimeInMillis() - calendar.getTimeInMillis();
//                                    if (ret == -1 || ret > tmpRet) {
//                                        return calendar1.getTimeInMillis() - calendar.getTimeInMillis();
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }
    //</editor-fold>

    //<editor-fold desc="处理星期包含的日期 日 static void actionWeekDay(int weekmin, int weekmax, ArrayList<Integer> days, ArrayList<Integer> months, ArrayList<Integer> years)">
    /**
     * 处理星期包含的日期 日
     *
     * @param weekmin
     * @param weekmax
     * @param days
     * @param months
     * @param years
     */
    static void actionWeekDay(int weekmin, int weekmax, ArrayList<Integer> days, ArrayList<Integer> months, ArrayList<Integer> years) {
        Calendar nowWeekDate = Calendar.getInstance();
        Integer[] tempMonths, tempYears;
        tempYears = years.toArray(new Integer[0]);
        tempMonths = months.toArray(new Integer[0]);
        for (int itemYear : tempYears) {
            for (int itemMonth : tempMonths) {
                int itemDay = 1;
                if (nowWeekDate.get(Calendar.MONTH) + 1 == itemMonth) {
                    itemDay = nowWeekDate.get(Calendar.DATE);
                }
                Calendar date = Calendar.getInstance();
                date.set(itemYear, itemMonth - 1, itemDay);
                for (int i = 0; i < 7; i++) {
                    int week = date.get(Calendar.DAY_OF_WEEK) - 1;
                    if (week < 1) {
                        week = 7;
                    }
                    if (weekmin <= week && week <= weekmax) {
                        if (!days.contains(date.get(Calendar.DATE))) {
                            days.add(date.get(Calendar.DATE));
                        }
                        if (!months.contains(date.get(Calendar.MONTH) + 1)) {
                            months.add(date.get(Calendar.MONTH) + 1);
                        }
                        if (!years.contains(date.get(Calendar.YEAR))) {
                            years.add(date.get(Calendar.YEAR));
                        }
                    }
                    date.add(Calendar.DATE, 1);
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="验证时间:[*][*][20/22][*][10:00-11:59/16:00-17:59] static public boolean verifyConfigTimeStr(String timeStr)">
    /**
     * 验证时间:[*][*][20/22][*][10:00-11:59/16:00-17:59];[*][*][20/22][*][10:00-11:59/16:00-17:59]
     * <br>第一个是年，，第二个是月，第三个是日期，第四个是星期，第五个是时间，
     * <br>每一个参数，"-" 表示 到 如：“2015-2017”表示 2015 到 2017, "/" 表示 或者 如：
     * “2015/2017”表示2015 或者 2017
     *
     */
    static public boolean verifyConfigTimeStr(String timeStr) {
        String[] items = timeStr.split(";|；");
        Calendar calendar = Calendar.getInstance();
        for (String item : items) {
            if (verifyConfigTimeStr(calendar, item)) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="验证时间:[*][*][20/22][*][10:00-11:59/16:00-17:59] static boolean verifyConfigTimeStr(Calendar date, String timeStr)">
    /**
     * 验证时间:[*][*][20/22][*][10:00-11:59/16:00-17:59]
     * <br>第一个是年，，第二个是月，第三个是日期，第四个是星期，第五个是时间
     * <br>每一个参数，"-" 表示 到 如：“2015-2017”表示 2015 到 2017, "/" 表示 或者 如：
     * <br>“2015/2017”表示2015 或者 2017
     *
     */
    static public boolean verifyConfigTimeStr(Calendar date, String timeStr) {
        String[] timeStrs = timeStr.replace("[", "").split("]");
        if (verifyDate(date.get(Calendar.YEAR), timeStrs[0])) {
            if (verifyDate(date.get(Calendar.MONTH) + 1, timeStrs[1])) {
                // {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                int week = date.get(Calendar.DAY_OF_WEEK) - 1;
                if (week < 1) {
                    week = 7;
                }//星期天
                if (verifyDate(week, timeStrs[3])) {
                    if (verifyDate(date.get(Calendar.DATE), timeStrs[2])) {
                        if (verifyTime(date, timeStrs[4])) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="验证时间:[*][*][20/22][*][10:00-11:59/16:00-17:59] static boolean verifyConfigEndTimeStr(Calendar date, String timeStr)">
    /**
     * 验证时间:[*][*][20/22][*][10:00-11:59/16:00-17:59] 配置的结束时间
     * <para>第一个是年，，第二个是月，第三个是日期，第四个是星期，第五个是时间，</para>
     * <para>每一个参数，"-" 表示 到 如：“2015-2017”表示 2015 到 2017, "/" 表示 或者 如：
     * “2015/2017”表示2015 或者 2017</para>
     *
     */
    static boolean verifyConfigEndTimeStr(Calendar date, String timeStr) {
        String[] timeStrs = timeStr.replace("[", "").split("]");
        if (verifyDate(date.get(Calendar.YEAR), timeStrs[0])) {
            if (verifyDate(date.get(Calendar.MONTH) + 1, timeStrs[1])) {
                // {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                int week = date.get(Calendar.DAY_OF_WEEK) - 1;
                if (week < 1) {
                    week = 7;
                }//星期天
                if (verifyDate(week, timeStrs[3])) {
                    if (verifyDate(date.get(Calendar.DATE), timeStrs[2])) {
                        if (verifyEndTime(date, timeStrs[4])) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc=" 验证当前时间 年，月，日，星期，是否符合 static boolean verifyDate(int nowItem, String items)">
    /**
     * 验证当前时间 年，月，日，星期，是否符合
     *
     * @param nowItem 参数
     * @param items 1-7;表示 1 到 7 , 1/7 表示 1 或者 7
     * @return
     */
    static boolean verifyDate(int nowItem, String items) {
        String nowItemStr = String.valueOf(nowItem);
        if ("*".equals(items) || nowItemStr.equals(items)) {
            return true;
        } else if (items.indexOf("-") > 0) {//区间划分
            String[] itemsplit = items.split("-");
            int item1 = Integer.parseInt(itemsplit[0]);
            int item2 = Integer.parseInt(itemsplit[1]);
            if (item1 <= nowItem && nowItem <= item2) {
                return true;
            }
        } else if (items.indexOf("/") > 0) {//或划分
            String[] weekssplit = items.split("/");
            for (String item : weekssplit) {
                if (nowItemStr.equals(item)) {
                    return true;
                }
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="验证当前时间格式 static boolean verifyTime(Calendar date, String itemTime)">
    /**
     * 验证当前时间格式
     *
     * @param date
     * @param itemTime 10:00-11:59/16:00-17:59
     * @return
     */
    static boolean verifyTime(Calendar date, String itemTime) {
        boolean ret = false;
        if (!"00:00-23:59".equals(itemTime) && !"*".equals(itemTime)) {
            String[] items = itemTime.split("/");
            for (String item : items) {
                String[] itemTimes = item.split("-");
                String[] hhmm = itemTimes[0].split(":|：");
                int hh = Integer.parseInt(hhmm[0]);
                int mm = Integer.parseInt(hhmm[1]);
                if (itemTimes[0].equals(itemTimes[1])) {
                    ret = date.get(Calendar.HOUR_OF_DAY) == hh && date.get(Calendar.MINUTE) == mm;
                } else if (date.get(Calendar.HOUR_OF_DAY) > hh || (date.get(Calendar.HOUR_OF_DAY) == hh && date.get(Calendar.MINUTE) >= mm)) {
                    String[] hhmm1 = itemTimes[1].split(":|：");
                    int hh1 = Integer.parseInt(hhmm1[0]);
                    int mm1 = Integer.parseInt(hhmm1[1]);
                    if ("23:59".equals(itemTimes[1]) || date.get(Calendar.HOUR_OF_DAY) < hh1 || (date.get(Calendar.HOUR_OF_DAY) == hh1 && date.get(Calendar.MINUTE) < mm1)) {
                        ret = true;
                    } else {
                        ret = false;
                    }
                } else {
                    ret = false;
                }
                if (ret) {
                    break;
                }
            }
        } else {
            ret = true;
        }
        return ret;
    }
    //</editor-fold>

    //<editor-fold desc="验证当前时间格式 static boolean verifyEndTime(Calendar date, String itemTime)">
    /**
     * 验证当前时间格式-配置的结束时间
     *
     * @param date
     * @param itemTime 10:00-11:59/16:00-17:59
     * @return
     */
    static boolean verifyEndTime(Calendar date, String itemTime) {
        boolean ret = false;
        if (!"00:00-23:59".equals(itemTime) && !"*".equals(itemTime)) {
            String[] items = itemTime.split("/");
            for (String item : items) {
                String[] itemTimes = item.split("-");
                String[] hhmm = itemTimes[1].split(":|：");
                int hh = Integer.parseInt(hhmm[0]);
                int mm = Integer.parseInt(hhmm[1]);
                if (("23:59".equals(itemTimes[1])) || date.get(Calendar.HOUR_OF_DAY) < hh || (date.get(Calendar.HOUR_OF_DAY) == hh && date.get(Calendar.MINUTE) <= mm)) {
                    ret = true;
                    break;
                } else {
                    ret = false;
                }
            }
        } else {
            ret = true;
        }
        return ret;
    }
    //</editor-fold>

    //<editor-fold desc="获取配置的年月日星期等信息  static ArrayList<Integer> getConfigDate(Calendar calendar, int p1, String p3)">
    static ArrayList<Integer> getConfigDate(Calendar calendar, int p1, String p3) {
        ArrayList<Integer> rets = new ArrayList<Integer>();
        String p1Str = String.valueOf(p1);
        if ("*".equals(p3)) {//|| p1Str.equals(p3)
            rets.add(p1);
            rets.add(p1 + 1);
        } else if (p3.indexOf("-") > 0) {
            String[] weeksplit = p3.split("-");
            int k1 = Integer.parseInt(weeksplit[0]);
            int k2 = Integer.parseInt(weeksplit[1]);
            for (int i = k1; i <= k2 + 1; i++) {
                rets.add(i);
            }
        } else if (p3.indexOf("/") > 0) {
            String[] weekssplit = p3.split("/");
            for (String item : weekssplit) {
                int temp = Integer.parseInt(item);
                rets.add(temp);
            }
        } else {
            rets.add(Integer.parseInt(p3));
        }
        return rets;
    }
    //</editor-fold>

    //<editor-fold desc="获取配置的时间字符串 static ArrayList<String> getConfigTimeStr(String itemTime)">
    /**
     * 必须类似的格式 单条 00:00-23:59 多条00:00-23:59/00:00-23:59
     *
     * @param itemTime
     * @return
     */
    static ArrayList<String> getConfigTimeStr(String itemTime) {
        ArrayList<String> retObjs = new ArrayList<>();
        // 00:00-23:59
        if (!"*".equals(itemTime)) {
            String[] items = itemTime.split("/");
            for (String item : items) {
                String[] itemTimes = item.split("-");
                retObjs.add(itemTimes[0]);
            }
        } else {
            retObjs.add("00:00");
        }
        return retObjs;
    }
    //</editor-fold>

    //<editor-fold desc="获取配置的时间字符串 结束时间 static ArrayList<String> getConfigEndTimeStr(String itemTime)">
    /**
     * 必须类似的格式 单条 00:00-23:59 多条00:00-23:59/00:00-23:59
     *
     * @param itemTime
     * @return
     */
    static ArrayList<String> getConfigEndTimeStr(String itemTime) {
        ArrayList<String> retObjs = new ArrayList<>();
        // 00:00-23:59
        if (!"*".equals(itemTime)) {
            String[] items = itemTime.split("/");
            for (String item : items) {
                String[] itemTimes = item.split("-");
                retObjs.add(itemTimes[1]);
            }
        } else {
            retObjs.add("23:59");
        }
        return retObjs;
    }
    //</editor-fold>

}
