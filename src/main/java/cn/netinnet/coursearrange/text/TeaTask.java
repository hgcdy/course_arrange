package cn.netinnet.coursearrange.text;

import lombok.Data;

import java.util.List;

@Data
public class TeaTask {
    private Long courseId;
    private Long teachClassId;
    private List<Long> classIdList;
    private int code;

    private int num;
    private int houseType;
}
