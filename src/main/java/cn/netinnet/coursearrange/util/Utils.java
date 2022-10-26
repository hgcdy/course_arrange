package cn.netinnet.coursearrange.util;

import java.util.List;
import java.util.Map;

public class Utils {
    public static void conversion(List<Map<String, Object>> list) {
        list.stream().forEach(i -> {
            if (i.get("houseType") != null) {
                int houseType = (int) i.get("houseType");
                if (houseType == 0) {
                    i.put("houseType", "教室");
                } else if (houseType == 1) {
                    i.put("houseType", "机房");
                } else if (houseType == 2) {
                    i.put("houseType", "实验室");
                } else if (houseType == 3) {
                    i.put("houseType", "课外");
                } else if (houseType == 4) {
                    i.put("houseType", "网课");
                }
            }
            if (i.get("must") != null) {
                int must = (int) i.get("must");
                if (must == 0) {
                    i.put("must", "选修");
                } else if (must == 1) {
                    i.put("must", "必修");
                }
            }
            if (i.get("weekly") != null) {
                int weekly = (int) i.get("weekly");
                if (weekly == 0) {
                    i.put("weekly", "单双周");
                } else if (weekly == 1) {
                    i.put("weekly", "单周");
                } else if (weekly == 2) {
                    i.put("weekly", "双周");
                }
            }
            if (i.get("week") != null) {
                int week = (int) i.get("week");
                i.put("week", cnWeek(week));
            }
            if (i.get("pitchNum") != null) {
                int pitchNum = (int) i.get("pitchNum");
                i.put("pitchNum", cnPitchNum(pitchNum));
            }
            if (i.get("id") != null) {
                i.put("id", String.valueOf(i.get("id")));
            }
            if (i.get("careerId") != null) {
                i.put("careerId", String.valueOf(i.get("careerId")));
            }
            if (i.get("classId") != null) {
                i.put("classId", String.valueOf(i.get("classId")));
            }
            if (i.get("courseId") != null) {
                i.put("courseId", String.valueOf(i.get("courseId")));
            }
            if (i.get("studentId") != null) {
                i.put("studentId", String.valueOf(i.get("studentId")));
            }
            if (i.get("teacherId") != null) {
                i.put("teacherId", String.valueOf(i.get("teacherId")));
            }
        });
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
