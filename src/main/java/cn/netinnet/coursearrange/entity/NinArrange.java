package cn.netinnet.coursearrange.entity;

import cn.netinnet.coursearrange.domain.BaseEntity;
import cn.netinnet.coursearrange.enums.CourseTypeEnum;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.TaskRecord;
import cn.netinnet.coursearrange.geneticAlgorithm.domain.TeaTask;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * <p>
 * 
 * </p>
 *
 * @author wangjs
 * @since 2022-09-6
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_arrange")
public class NinArrange extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 专业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("career_id")
    private Long careerId;

    /**
     * 班级id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("class_id")
    private Long classId;

    /**
     * 教学班id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("teach_class_id")
    private Long teachClassId;

    /**
     * 教师id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("teacher_id")
    private Long teacherId;

    /**
     * 课程id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("course_id")
    private Long courseId;

    /**
     * 教室id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("house_id")
    private Long houseId;

    /**
     * 是否选修，0-选修，1-必修
     */
    @TableField("must")
    private Integer must;

    /**
     * 周次(0-每周，1-单周，2-双周)
     */
    @TableField("weekly")
    private Integer weekly;

    /**
     * 开始周次
     */
    @TableField("start_time")
    private Integer startTime;

    /**
     * 结束周次
     */
    @TableField("end_time")
    private Integer endTime;

    /**
     * 星期
     */
    @TableField("week")
    private Integer week;

    /**
     * 节数
     */
    @TableField("pitch_num")
    private Integer pitchNum;

    /**
     * 人数
     */
    @TableField("people_num")
    private Integer peopleNum;


    public NinArrange() {
    }

    public NinArrange(TaskRecord taskRecord) {
        TeaTask teaTask = taskRecord.getTeaTask();
        this.careerId = teaTask.getCareerId();
        this.teacherId = teaTask.getTeacherId();
        this.courseId = teaTask.getCourseId();
        this.houseId = taskRecord.getHouseId();
        if (null == teaTask.getTeacherId()) {
            this.classId = teaTask.getClassIdList().get(0);
            this.must = CourseTypeEnum.OPTIONAL.getCode();
        } else {
            this.must = CourseTypeEnum.REQUIRED_COURSE.getCode();
        }
        this.teachClassId = teaTask.getTeachClassId();
        this.weekly = taskRecord.getWeekly();
//        this.startTime = startTime;
//        this.endTime = endTime;
        this.week = taskRecord.getWeek();
        this.pitchNum = taskRecord.getPitchNum();
        this.peopleNum = teaTask.getPeopleNum();
        if (null == houseId || null == week || null == pitchNum) {
            setDelFlag(-1);
        }

    }
}
