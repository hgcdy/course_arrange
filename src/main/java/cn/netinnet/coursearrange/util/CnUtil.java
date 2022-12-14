package cn.netinnet.coursearrange.util;

public class CnUtil {

    public static String cnMust(int must) {
        if (must == 0) {
            return "选修";
        } else if (must == 1) {
            return "必修";
        } else {
            return "";
        }
    }

    public static String cnWeekly(int weekly) {
        if (weekly == 0) {
            return "单双周";
        } else if (weekly == 1) {
            return "单周";
        } else if (weekly == 2) {
            return "双周";
        } else {
            return "";
        }
    }

    public static String cnHouse(int houseType) {
        String str;
        switch (houseType) {
            case 0:
                str = "教室";
                break;
            case 1:
                str = "机房";
                break;
            case 2:
                str = "实验室";
                break;
            case 3:
                str = "网课";
                break;
            case 4:
                str = "课外";
                break;
            default:
                str = "";
        }
        return str;
    }

    public static String cnWeek(int week) {
        if (week > 0 && week < 7) {
            return "星期" + cnNum(week);
        } else if (week == 7) {
            return "星期日";
        } else {
            return "";
        }
    }

    public static String cnPitchNum(int pitchNum) {
        if (pitchNum > 0){
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
