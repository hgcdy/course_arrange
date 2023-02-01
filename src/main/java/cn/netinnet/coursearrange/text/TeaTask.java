package cn.netinnet.coursearrange.text;

import lombok.Data;

import java.util.List;

@Data
public class TeaTask {
    private Long careerId;
    private Long courseId;
    private Long teachClassId;
    private List<Long> classIdList;
    private int code;

    private int peopleNum;
    private int houseType;

    private Long teacherId;

}
