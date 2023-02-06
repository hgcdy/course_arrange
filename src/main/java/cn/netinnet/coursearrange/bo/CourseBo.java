package cn.netinnet.coursearrange.bo;

import cn.netinnet.coursearrange.entity.NinCourse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseBo extends NinCourse {

    /**
     * 课程需要的教室类型.0-教室，1-机房，2-实验室
     */
    private String cnHouseType;

    /**
     * 是否必修，0-选修，1-必修
     */
    private String cnMust;

    /**
     * 课程状态
     */
    private String start;
}
