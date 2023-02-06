package cn.netinnet.coursearrange.util;

public class CnUtil {

    public static String cnWeek(Integer week) {
        if (null == week) {
            return null;
        } else if (week > 0 && week < 7) {
            return "星期" + cnNum(week);
        } else if (week == 7) {
            return "星期日";
        } else {
            return "";
        }
    }

    public static String cnPitchNum(Integer pitchNum) {
        if (null == pitchNum) {
            return null;
        } else if (pitchNum > 0){
            return "第" + cnNum(pitchNum) + "节";
        } else {
            return "";
        }

    }

    public static String cnNum(int num) {
        String[] arr = {"零","一","二","三","四","五","六","七","八","九","十"};
        if (num >= 0 && num <= 10) {
            return arr[num];
        } else if (num > 10 && num < 20) {
            return "十" + arr[num - 10];
        } else if (num < 100 && num % 10 == 0) {
            return arr[num / 10] + "十";
        } else if (num < 100) {
            return arr[num / 10] + "十" + arr[num % 10];
        } else {
            return "";
        }
    }
}
