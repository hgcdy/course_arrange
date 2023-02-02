package cn.netinnet.coursearrange.geneticAlgorithm;

import lombok.Data;

import java.util.List;

@Data
public class TeaTask {
    /**
     * 教学任务 将教师,班级,课程绑定
     */

    private Long careerId;
    private Long courseId;
    private Long teachClassId;
    private List<Long> classIdList;
    private int code;

    private int peopleNum;
    private int houseType;

    private Long teacherId;

}
