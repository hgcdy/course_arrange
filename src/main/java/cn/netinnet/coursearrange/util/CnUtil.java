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
        String str;
        switch (week) {
            case 1:
                str = "星期一";
                break;
            case 2:
                str = "星期二";
                break;
            case 3:
                str = "星期三";
                break;
            case 4:
                str = "星期四";
                break;
            case 5:
                str = "星期五";
                break;
            case 6:
                str = "星期六";
                break;
            case 7:
                str = "星期日";
                break;
            default:
                str = "";
        }
        return str;
    }

    public static String cnPitchNum(int pitchNum) {
        String str;
        switch (pitchNum) {
            case 1:
                str = "第一节";
                break;
            case 2:
                str = "第二节";
                break;
            case 3:
                str = "第三节";
                break;
            case 4:
                str = "第四节";
                break;
            case 5:
                str = "第五节";
                break;
            case 6:
                str = "第六节";
                break;
            case 7:
                str = "第七节";
                break;
            case 8:
                str = "第八节";
                break;
            case 9:
                str = "第九节";
                break;
            default:
                str = "";
        }
        return str;
    }

    public static String cnNum(int num) {
        String str;
        switch (num) {
            case 0:
                str = "零";
                break;
            case 1:
                str = "一";
                break;
            case 2:
                str = "二";
                break;
            case 3:
                str = "三";
                break;
            case 4:
                str = "四";
                break;
            case 5:
                str = "五";
                break;
            case 6:
                str = "六";
                break;
            case 7:
                str = "七";
                break;
            case 8:
                str = "八";
                break;
            case 9:
                str = "九";
                break;
            default:
                str = "";
        }
        return str;
    }
}
