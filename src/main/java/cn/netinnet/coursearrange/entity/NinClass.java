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
@TableName("nin_class")
public class NinClass extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 专业id
     */
    @TableField("career_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long careerId;

    /**
     * 班级名称
     */
    @TableField("class_name")
    private String className;

    /**
     * 班级人数
     */
    @TableField("people_num")
    private Integer peopleNum;

    /**
     * 已有课程数
     */
    @TableField("course_num")
    private Integer courseNum;


}
