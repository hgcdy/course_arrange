package cn.netinnet.coursearrange.entity;

import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
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
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_teach_class")
public class NinTeachClass extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 教学班id
     */
    @TableField("teach_class_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long teachClassId;

    /**
     * 班级id
     */
    @TableField("class_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId;

    /**
     * 班级名称
     */
    @TableField("class_name")
    private String className;

}
