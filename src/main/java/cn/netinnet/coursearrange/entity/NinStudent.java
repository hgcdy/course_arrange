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
@TableName("nin_student")
public class NinStudent extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 学生名称
     */
    @TableField("student_name")
    private String studentName;

    /**
     * 学生账号
     */
    @TableField("student_code")
    private String studentCode;

    /**
     * 学生用户密码
     */
    @TableField("student_password")
    private String studentPassword;

    /**
     * 行政班级id
     */
    @TableField("class_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;


}
