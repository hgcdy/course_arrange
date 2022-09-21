package cn.netinnet.coursearrange.util;

import java.util.List;
import java.util.Map;

public class Utils {
    public static void conversion(List<Map<String, Object>> list){
        list.stream().forEach(i -> {
            if (i.get("houseType") != null){
                int houseType = (int) i.get("houseType");
                if (houseType == 0) {
                    i.put("houseType", "梯形教室");
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
            if (i.get("must") != null){
                int must = (int) i.get("must");
                if (must == 0){
                    i.put("must", "选修");
                } else if (must == 1){
                    i.put("must", "必修");
                }
            }
            if (i.get("id") != null){
                i.put("id", String.valueOf(i.get("id")));
            }
            if (i.get("careerId") != null){
                i.put("careerId", String.valueOf(i.get("careerId")));
            }
            if (i.get("classId") != null){
                i.put("classId", String.valueOf(i.get("classId")));
            }
            if (i.get("courseId") != null){
                i.put("courseId", String.valueOf(i.get("courseId")));
            }
            if (i.get("studentId") != null){
                i.put("studentId", String.valueOf(i.get("studentId")));
            }
            if (i.get("teacherId") != null){
                i.put("teacherId", String.valueOf(i.get("teacherId")));
            }
        });
    }
}
