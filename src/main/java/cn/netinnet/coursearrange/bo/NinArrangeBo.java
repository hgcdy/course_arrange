package cn.netinnet.coursearrange.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NinArrangeBo{

    /**
     * 排课记录id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 专业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long careerId;

    /**
     * 专业名称
     */
    private String careerName;

    /**
     * 班级id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;

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

    /**
     * 人数
     */
    private Integer peopleNum;

}
