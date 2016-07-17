package com.bmob.im.demo.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    public final static String FORMAT_YEAR = "yyyy";
    public final static String FORMAT_MONTH_DAY = "MM��dd��";

    public final static String FORMAT_DATE = "yyyy-MM-dd";
    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_MONTH_DAY_TIME = "MM��dd��  hh:mm";

    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";
    private static final int YEAR = 365 * 24 * 60 * 60;// ��
    private static final int MONTH = 30 * 24 * 60 * 60;// ��
    private static final int DAY = 24 * 60 * 60;// ��
    private static final int HOUR = 60 * 60;// Сʱ
    private static final int MINUTE = 60;// ����
    private static SimpleDateFormat sdf = new SimpleDateFormat();

    /**
     * ���ʱ�����ȡ������ʱ�䣬��3����ǰ��1��ǰ
     *
     * @param timestamp ʱ��� ��λΪ����
     * @return ʱ���ַ�
     */
    public static String getDescriptionTimeFromTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// ������ʱ���������
        System.out.println("timeGap: " + timeGap);
        String timeStr = null;
        if (timeGap > YEAR) {
            timeStr = timeGap / YEAR + "��ǰ";
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "����ǰ";
        } else if (timeGap > DAY) {// 1������
            timeStr = timeGap / DAY + "��ǰ";
        } else if (timeGap > HOUR) {// 1Сʱ-24Сʱ
            timeStr = timeGap / HOUR + "Сʱǰ";
        } else if (timeGap > MINUTE) {// 1����-59����
            timeStr = timeGap / MINUTE + "����ǰ";
        } else {// 1����-59����
            timeStr = "�ո�";
        }
        return timeStr;
    }

    /**
     * ��ȡ��ǰ���ڵ�ָ����ʽ���ַ�
     *
     * @param format ָ��������ʱ���ʽ����Ϊnull��""��ʹ��ָ���ĸ�ʽ"yyyy-MM-dd HH:MM"
     */
    public static String getCurrentTime(String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(new Date());
    }

    // date����ת��ΪString����
    // formatType��ʽΪyyyy-MM-dd HH:mm:ss//yyyy��MM��dd�� HHʱmm��ss��
    // data Date���͵�ʱ��
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // long����ת��ΪString����
    // currentTimeҪת����long���͵�ʱ��
    // formatTypeҪת����string���͵�ʱ���ʽ
    public static String longToString(long currentTime, String formatType) {
        String strTime = "";
        Date date = longToDate(currentTime, formatType);// long����ת��Date����
        strTime = dateToString(date, formatType); // date����ת��String
        return strTime;
    }

    // string����ת��Ϊdate����
    // strTimeҪת����string���͵�ʱ�䣬formatTypeҪת���ĸ�ʽyyyy-MM-dd HH:mm:ss//yyyy��MM��dd��
    // HHʱmm��ss�룬
    // strTime��ʱ���ʽ����Ҫ��formatType��ʱ���ʽ��ͬ
    public static Date stringToDate(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    // longת��ΪDate����
    // currentTimeҪת����long���͵�ʱ��
    // formatTypeҪת����ʱ���ʽyyyy-MM-dd HH:mm:ss//yyyy��MM��dd�� HHʱmm��ss��
    public static Date longToDate(long currentTime, String formatType) {
        Date dateOld = new Date(currentTime); // ���long���͵ĺ���������һ��date���͵�ʱ��
        String sDateTime = dateToString(dateOld, formatType); // ��date���͵�ʱ��ת��Ϊstring
        Date date = stringToDate(sDateTime, formatType); // ��String����ת��ΪDate����
        return date;
    }

    // string����ת��Ϊlong����
    // strTimeҪת����String���͵�ʱ��
    // formatTypeʱ���ʽ
    // strTime��ʱ���ʽ��formatType��ʱ���ʽ������ͬ
    public static long stringToLong(String strTime, String formatType) {
        Date date = stringToDate(strTime, formatType); // String����ת��date����
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date����ת��long����
            return currentTime;
        }
    }

    // date����ת��Ϊlong����
    // dateҪת����date���͵�ʱ��
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    /**
     * ��ȡ����ʱ�䣺��Ϊsdk��ʱ��Ĭ�ϵ����Ӧ�ó�1000
     *
     * @param @param timesamp
     * @return String
     * @Title: getChatTime
     * @Description: TODO
     */
    public static String getChatTime(long timesamp) {
        long clearTime = timesamp * 1000;
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(clearTime);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = "���� " + getHourAndMin(clearTime);
                break;
            case 1:
                result = "���� " + getHourAndMin(clearTime);
                break;
            case 2:
                result = "ǰ�� " + getHourAndMin(clearTime);
                break;

            default:
                result = getTime(clearTime);
                break;
        }

        return result;
    }
}