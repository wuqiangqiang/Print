package com.zrodo.demo.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.text.format.DateFormat;

public class ComUtils {
    /**
     * 控制台打印object
     */
    public static void print(Object obj) {
        if (null == obj) {
            System.out.println("NULL MESSAGE");
        } else {
            System.out.println(obj.toString());
        }

    }

    /**
     * 修改：防止重复点击
     *
     * @author wujn
     */
    public static boolean isFastDoubleClick(long ago) {
        long now = System.currentTimeMillis();

        if (ago == 0) {
            return true;
        }
        if (now - ago > 1000) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 计算数列的平均值：
     * 去掉一个最高值，去掉一个最低值，然后剩余的求和再平均
     *
     * @param list or array
     * @author wujn
     */
    public static float caculateAvg(float[] ar) {
        int len = ar.length;
        float min, max;
        max = ar[0];
        min = ar[0];

        // 求出N个数中间输入的最大值和最小值
        for (int i = 0; i < len; i++) {
            if (ar[i] > max) {
                max = ar[i];
            }
            if (ar[i] < min) {
                min = ar[i];
            }
        }

        // 对N个数去除最大值和最小值后求和
        float sum = 0;
        for (int i = 0; i < len - 2; i++) {
            if (ar[i] > min && ar[i] < max) {
                sum += ar[i];
            }
        }

        return sum / (len - 2);

    }

    /**
     * 得到一个唯一标识的UUID
     *
     * @author kevin.xia
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    /**
     * 四舍五入保留两位小数
     * use in pie
     *
     * @param num
     * @return
     * @author kevin.xia
     */
    public static double numberRound(double num) {
        if (num > 0) {
            num += 0.0000001;
        } else {
            num -= 0.0000001;
        }
        BigDecimal b = new BigDecimal(num);
        double f = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f;
    }


    /**
     * 转批成指定的格式
     *
     * @param timeInMillis
     * @return
     */
    public static String formatTime(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        return DateFormat.format("yyyy-MM-dd hh:mm:ss", c).toString();
    }


    public static String trimTime(String time) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-mm-dd");
        Date d = new Date(System.currentTimeMillis());
        try {
            d = f.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
        }
        return f.format(d);
    }


    /**
     * 获取当前日期 时间
     *
     * @return
     */
    public static String getNow() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String date = sDateFormat.format(new Date());
        return date;
    }

    public static String getDay() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = sDateFormat.format(new Date());
        return date;
    }

    public static String getChsWeek() {
        Calendar c = Calendar.getInstance();
        int weeks = c.get(Calendar.DAY_OF_WEEK);
        if (weeks == 1) {
            return "星期日";
        } else if (weeks == 2) {
            return "星期一";
        } else if (weeks == 3) {
            return "星期二";
        } else if (weeks == 4) {
            return "星期三";
        } else if (weeks == 5) {
            return "星期四";
        } else if (weeks == 6) {
            return "星期五";
        } else if (weeks == 7) {
            return "星期六";
        } else {
            return "";
        }
    }

    /**
     * 获取当前日期 时间
     *
     * @return
     */
    public static String getTimes(String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String date = sDateFormat.format(new Date());
        return date;
    }


    /**
     * 把秒转成时分秒
     */
    public static String second2Time(int s) {
        String time = "";
        int hour = 0;
        int minute = 0;
        int second = 0;

        if (s <= 0) {
            time = "00:00:00";
        } else {
            hour = (int) (s / (60 * 60));
            minute = (int) (s / 60) % 60;
            second = (int) s % 60;

            if (hour >= 0) {
                time += hour + ":";
            }

            if (minute >= 0) {
                if (((int) minute / 10) == 0) {
                    time += "0" + minute + ":";
                } else {
                    time += minute + ":";
                }

            } else {
                time += "00:";
            }

            if (second >= 0) {
                if (((int) second / 10) == 0) {
                    time += "0" + second + "";
                } else {
                    time += second + "";
                }
            } else {
                time += "00";
            }

        }

        return time;
    }


    /**
     * 把秒转成时分秒
     */
    public static String second2TimeNoHour(int s) {
        String time = "";
        int minute = 0;
        int second = 0;

        if (s <= 0) {
            time = "00:00";
        } else {
            minute = (int) (s / 60) % 60;
            second = (int) s % 60;

            if (minute >= 0) {
                if (((int) minute / 10) == 0) {
                    time += "0" + minute + ":";
                } else {
                    time += minute + ":";
                }

            } else {
                time += "00:";
            }

            if (second >= 0) {
                if (((int) second / 10) == 0) {
                    time += "0" + second + "";
                } else {
                    time += second + "";
                }
            } else {
                time += "00";
            }

        }

        return time;
    }

    /**
     * 获得4位的float
     */
    public static float getFloat4(float f) {
        float f4 = ((float) (Math.round(f * 10000))) / 10000;
        return f4;
    }

    /**
     * 获取异常的堆栈信息
     *
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     *           from Log.w(TAG, "Unable to open content: " + mUri, ex);
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     *           from Log.w(TAG, "Unable to open content: " + mUri, ex);
     */
    public static String getStackTraceString(Exception tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }
}
