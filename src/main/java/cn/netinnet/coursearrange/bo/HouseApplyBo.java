package cn.netinnet.coursearrange.bo;

import lombok.Data;

@Data
public class HouseApplyBo {
    private String classIdList;
    private Long houseId;
    private Long teacherId;
    private Long courseId;
    private String weeklyList;
    private String weekList;


    private Integer weekly;
    private Integer week;
    private Integer pitchNum;
}
