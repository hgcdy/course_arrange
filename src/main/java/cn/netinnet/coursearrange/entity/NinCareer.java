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
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("nin_career")
public class NinCareer extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学院
     */
    @TableField("college")
    private String college;

    /**
     * 专业名称
     */
    @TableField("career_name")
    private String careerName;

    /**
     * 班级数量
     */
    @TableField("class_num")
    private Integer classNum;


}
