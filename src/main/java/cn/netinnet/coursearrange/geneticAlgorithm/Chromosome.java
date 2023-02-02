package cn.netinnet.coursearrange.geneticAlgorithm;

import lombok.Data;

import java.util.List;

@Data
public class Chromosome {

    private List<TaskRecord> taskRecordList;
    private double score;

}
