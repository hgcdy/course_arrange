package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinStudent;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class StudentBo extends NinStudent {

    /**
     * 学院
     */
    private String college;

    /**
     * 专业id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long careerId;

    /**
     * 专业名称
     */
    private String careerName;

    /**
     * 班级名称
     */
    private String className;
}
