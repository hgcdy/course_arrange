package cn.netinnet.coursearrange.entity;

import cn.netinnet.coursearrange.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("nin_student_course")
public class NinStudentCourse extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 学生id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("student_id")
    private Long studentId;

    /**
     * 学生选修课程id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("course_id")
    private Long courseId;

    /**
     * 学生选修课程的班级id
     */
    @TableField("take_class_id")
    private Long takeClassId;


}
