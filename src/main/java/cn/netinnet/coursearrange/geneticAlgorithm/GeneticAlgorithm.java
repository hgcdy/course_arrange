package cn.netinnet.coursearrange.geneticAlgorithm;

import java.util.List;

public interface GeneticAlgorithm {
    List<TaskRecord> start();

    //冲突校验所有
    int verifyClashAll(List<TaskRecord> taskRecordList);
}
