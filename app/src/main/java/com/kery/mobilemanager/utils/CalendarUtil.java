package com.kery.mobilemanager.utils;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/5/17.
 */

public class CalendarUtil {

    private static final SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");//年月日
    private static final SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");

    public synchronized static String today() {
        return DateFormat.format(format1.toPattern(), Calendar.getInstance(Locale.CHINA)).toString();
    }


    /**
     * @param now    想要处理的时间,格式是yyyy-MM-dd
     * @param months 想要添加或者减去的月,正负数都可以
     */
    public static String addMonth(@NonNull CharSequence now, int months) {
        try {
            Date date = format1.parse(String.valueOf(now));
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, months);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return DateFormat.format(format1.toPattern(), calendar).toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log("添加月失败");
        }
        return null;
    }

    /**
     * @param now  想要处理的时间,格式是yyyy-MM-dd
     * @param days 想要添加或者减去的天数,正负数都可以
     */
    public static String addDay(@NonNull CharSequence now, int days) {
        try {
            Date date = format1.parse(String.valueOf(now));
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return DateFormat.format(format1.toPattern(), calendar).toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log("添加月失败");
        }
        return null;
    }

    /**
     * @param days 想要添加或者减去的天数,正负数都可以
     */
    public static String getDay(int days) {
        try {
            Date date = format1.parse(String.valueOf(today()));
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return DateFormat.format(format1.toPattern(), calendar).toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log("添加月失败");
        }
        return null;
    }

    /**
     * @param date 想要处理的时间,格式是yyyy-MM-dd
     */
    public static int[] getYmd(CharSequence date) {
        int[] arr = new int[3];
        try {
            Date d = format1.parse(String.valueOf(date));
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.setTime(d);
            arr[0] = calendar.get(Calendar.YEAR);
            arr[1] = calendar.get(Calendar.MONTH);
            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
            LogUtil.log(Arrays.toString(arr));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage());
        }
        return arr;
    }


    /**
     * @return 根据年月日来获取时间字符串
     */
    public static String getdate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, dayOfMonth);
        return format1.format(calendar.getTime());
    }

    /**
     * @param date 想要处理的时间,格式是yyyy-MM-dd
     */
    public static long getTime(CharSequence date) {
        try {
            return format1.parse(String.valueOf(date)).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.log(e.getMessage(), "获取时间Long失败");
        }
        return 0;
    }

    /**
     * @return 获取两者之间的时间
     */
    public static CharSequence compare(CharSequence startTime, CharSequence endTime) {
        String f = "(共%d月%d天)";
        int[] arr = comparemd(startTime, endTime);
        return String.format(Locale.CHINA, f, arr[0], arr[1]);
    }


    /**
     * @return 获取两者之间的时间, 如果相等为0，开始时间大于结束时间，-1,否则返回最少是两天
     */
    public static int compareInt(CharSequence endTime, CharSequence startTime) {
        int[] start = getYmd(startTime);
        int[] end = getYmd(endTime);
        int count = -1;
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.clear();
        calendar.set(start[0], start[1], start[2]);
        Calendar endCalendar = Calendar.getInstance(Locale.CHINA);
        endCalendar.clear();
        endCalendar.set(end[0], end[1], end[2]);
        if (calendar.compareTo(endCalendar) == 0)
            return 0;
        //如果开始时间比结束时间大，返回-1
        if (calendar.compareTo(endCalendar) == 1)
            return count;
        count = 0;
        //这里只能比较年月日，不然会无限循环
        while (calendar.compareTo(endCalendar) != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            count++;
        }
        //相等之后还要再加上一天
//        count++;
        return count;
    }


    public static int[] comparemd(CharSequence startDate, CharSequence endDate) {
        int[] arr = new int[2];
        long st = getTime(startDate);
        long et = getTime(endDate);
        if (st > et) {
            LogUtil.log("开始时间大于结束时间");
            return arr;
        }
        Calendar a = Calendar.getInstance();
        a.clear();
        a.setTimeInMillis(st);
        Calendar b = Calendar.getInstance();
        b.clear();
        b.setTimeInMillis(et);
        if (a.compareTo(b) == 0) {
            arr[1] = 1;
            return arr;
        }
        Calendar privous = Calendar.getInstance();
        privous.clear();
        Calendar next = Calendar.getInstance();
        next.clear();
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            privous.clear();
            privous.setTimeInMillis(st);
            //添加一个月,5-20添加一个月变成6-20,
            privous.add(Calendar.MONTH, i - 1);
            privous.add(Calendar.DAY_OF_MONTH, -1);
            next.clear();
            next.setTimeInMillis(st);
            next.add(Calendar.MONTH, i);
            next.add(Calendar.DAY_OF_MONTH, -1);
            if (next.compareTo(b) == 0) {
                arr[0] = i;
                arr[1] = 0;
                break;
            }
            if (privous.compareTo(b) == 0) {
                arr[0] = i - 1;
                arr[1] = 0;
                break;
            }
            if (privous.compareTo(b) < 0 && next.compareTo(b) > 0) {
                arr[0] = i - 1;
                int d = 0;
                while (privous.compareTo(b) != 0) {
                    privous.add(Calendar.DAY_OF_MONTH, 1);
                    d++;
                }
                arr[1] = d;
                break;
            }
        }
        //因为是计算天数，所以把开始时间也计算进去了
        LogUtil.log("获取时间差", Arrays.toString(arr));
        return arr;
    }


    public static int contains(Pair<String, String> p, List<Pair<String, String>> pairs) {
        if (p == null || pairs == null || pairs.isEmpty())
            return -1;
        Calendar a = Calendar.getInstance();
        a.clear();
        a.setTimeInMillis(getTime(p.first));
        Calendar b = Calendar.getInstance();
        b.clear();
        b.setTimeInMillis(getTime(p.second));
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        for (int i = 0; i < pairs.size(); i++) {
            Pair<String, String> temp = pairs.get(i);
            c.clear();
            d.clear();
            c.setTimeInMillis(getTime(temp.first));
            d.setTimeInMillis(getTime(temp.second));
            //当你的开始时间大于比较对象的结束时间或者结束时间小于比较对象的开始时间,判断为不重复
            if (a.compareTo(d) > 0 || b.compareTo(c) < 0)
                continue;
            return i;
        }
        return -1;
    }

    public static boolean containAll(List<Pair<String, String>> pairs, @NonNull Pair<String, String> p) {
        if (pairs == null || pairs.isEmpty())
            return true;
        Calendar a = Calendar.getInstance();
        a.clear();
        a.setTimeInMillis(getTime(p.first));
        Calendar b = Calendar.getInstance();
        b.clear();
        b.setTimeInMillis(getTime(p.second));
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        for (int i = 0; i < pairs.size(); i++) {
            Pair<String, String> temp = pairs.get(i);
            c.clear();
            d.clear();
            c.setTimeInMillis(getTime(temp.first));
            d.setTimeInMillis(getTime(temp.second));
            if (c.compareTo(d) > 0)//开始时间比结束时间大
                return false;
            //当你的开始时间小于比较对象的开始时间或者结束时间大于比较对象的结束时间,判断已经越界
            boolean contain = c.compareTo(a) >= 0 && d.compareTo(b) <= 0;
            if (!contain) return false;
        }
        return true;
    }
}
