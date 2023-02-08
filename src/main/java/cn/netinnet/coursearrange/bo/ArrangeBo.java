package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinArrange;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
public class ArrangeBo extends NinArrange {

    /**
     * 专业名称
     */
    private String careerName;


    /**
     * 教学班id列表
     */
    private List<Long> teachClassIdList;

    /**
     * 班级名称（多个班级以;隔开）
     */
    private String className;

    /**
     * 教师名称
     */
    private String teacherName;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 教室名称
     */
    private String houseName;

    /**
     * 是否选修，0-选修，1-必修
     */
    private String cnMust;

    /**
     * 周次(0-每周，1-单周，2-双周)
     */
    private String cnWeekly;

    /**
     * 星期
     */
    private String cnWeek;

    /**
     * 节数
     */
    private String cnPitchNum;

    /**
     * 删除标记，但用于标记是否因为冲突导致未安排的课程
     */
//    private Integer delFlag;

}
