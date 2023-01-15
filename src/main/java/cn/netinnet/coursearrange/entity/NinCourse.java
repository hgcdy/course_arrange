package cn.netinnet.coursearrange.entity;

import cn.netinnet.coursearrange.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjs
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_course")
public class NinCourse extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 课程名称
     */
    @TableField("course_name")
    private String courseName;

    /**
     * 课程需要的教室类型.0-教室，1-机房，2-实验室，3-课外，4-网课
     */
    @TableField("house_type")
    private Integer houseType;

    /**
     * 是否必修，0-选修，1-必修
     */
    @TableField("must")
    private Integer must;

    /**
     * 课时
     */
    @TableField("course_time")
    private Integer courseTime;

    /**
     * 最早开课时间
     */
    @TableField("start_time")
    private Integer startTime;

    /**
     * 最晚结课时间
     */
    @TableField("end_time")
    private Integer endTime;

    /**
     * 几周内结课
     */
    @TableField("week_time")
    private Integer weekTime;

    /**
     * 最多上课班级
     */
    @TableField("max_class_num")
    private Integer maxClassNum;

}
