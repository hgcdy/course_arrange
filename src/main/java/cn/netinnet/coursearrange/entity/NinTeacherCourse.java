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
@TableName("nin_teacher_course")
public class NinTeacherCourse extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


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

}
