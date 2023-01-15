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
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_career_course")
public class NinCareerCourse extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 专业id
     */
    @TableField("career_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long careerId;

    /**
     * 课程id
     */
    @TableField("course_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;

}
