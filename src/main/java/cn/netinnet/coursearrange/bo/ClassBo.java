package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinClass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ClassBo extends NinClass {
    /**
     * 学院
     */
    private String college;

    /**
     * 专业名称
     */
    private String careerName;
}
