package cn.netinnet.coursearrange.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NinArrangeBo{


    /**
     * 专业id
     */
    private Long careerId;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 教学班id列表
     */
    private List<Long> teachClassIdList;

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
    private Integer must;

    /**
     * 周次(0-每周，1-单周，2-双周)
     */
    private Integer weekly;

    /**
     * 开始周次
     */
    private Integer startTime;

    /**
     * 结束周次
     */
    private Integer endTime;

    /**
     * 星期
     */
    private Integer week;

    /**
     * 节数
     */
    private Integer pitchNum;

}
