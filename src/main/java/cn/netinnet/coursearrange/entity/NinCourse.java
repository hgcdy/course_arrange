package cn.netinnet.coursearrange.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
public class NinCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("course_name")
    private String courseName;

    /**
     * 课程需要的教室类型.0-普通教室，1-机房，2-实验室
     */
    @TableField("house_type")
    private Integer houseType;

    /**
     * 是否必修，0-选修，1-必修
     */
    private Integer must;

    /**
     * 课时
     */
    private Integer courseTime;

    /**
     * 最早开课时间
     */
    private Integer startTime;

    /**
     * 最晚结课时间
     */
    private Integer endTime;

    /**
     * 几周内结课
     */
    private Integer weekTime;

    /**
     * 最多上课班级
     */
    private Integer maxClassNum;

    @TableField("del_flag")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 更新时间
     */
    @TableField("modify_time")
    private LocalDateTime modifyTime;

    @TableField("modify_user_id")
    private Long modifyUserId;


}
