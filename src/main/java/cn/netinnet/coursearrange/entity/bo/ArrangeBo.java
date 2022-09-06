package cn.netinnet.coursearrange.entity.bo;

import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.util.IDUtil;
import cn.netinnet.coursearrange.util.UserUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ArrangeBo {


    /**
     * 专业
     */
    private String career;

    /**
     * 班级id列表（同专业多个班级一起上课）
     */
    private List<Long> classIdList;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 教师id
     */
    private Long teacherId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 教室id
     */
    private Long houseId;

    /**
     * 教室类型
     */
    private Integer houseType;

    /**
     * 人数
     */
    private Integer peopleNum;

    /**
     * 课时
     */
    private Integer num;

    /**
     * 是否选修，0-选修，1-必修
     */
    private Integer must;

    /**
     * 周次
     */
    private Integer weekly;

    /**
     * 星期
     */
    private Integer week;

    /**
     * 节数
     */
    private Integer pitchNum;

    /**
     * 课时8的周次
     */
    private Integer weekly_8;

}
