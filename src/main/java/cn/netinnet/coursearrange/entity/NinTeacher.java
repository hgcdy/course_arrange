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
@TableName("nin_teacher")
public class NinTeacher extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 教师名称
     */
    @TableField("teacher_name")
    private String teacherName;

    /**
     * 教师账号
     */
    @TableField("teacher_code")
    private String teacherCode;

    /**
     * 教师用户密码
     */
    @TableField("teacher_password")
    private String teacherPassword;



}
