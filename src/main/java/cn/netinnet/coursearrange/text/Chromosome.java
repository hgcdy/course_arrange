package cn.netinnet.coursearrange.text;

import lombok.Data;

import java.util.List;

@Data
public class Chromosome {

    private List<TaskRecord> taskRecordList;
    private double score;

    public Chromosome(List<TaskRecord> taskRecordList) {
        this.taskRecordList = taskRecordList;
    }
}
