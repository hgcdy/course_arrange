package cn.netinnet.coursearrange.text;

import lombok.Data;

@Data
public class TaskRecord {
    private TeaTask teaTask;

    private Long houseId;
    private Integer seat;

    private int weekly;
    private int week;
    private int pitchNum;

    public TaskRecord(TeaTask teaTask) {
        this.teaTask = teaTask;
    }

    public TaskRecord() {
    }
}
