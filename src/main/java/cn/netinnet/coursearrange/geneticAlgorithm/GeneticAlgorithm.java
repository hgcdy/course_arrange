package cn.netinnet.coursearrange.geneticAlgorithm;

import java.util.List;

public interface GeneticAlgorithm {
    List<TaskRecord> start();

    /**
     * 校验一组解里是否存在冲突
     * @param taskRecordList
     * @return 冲突个数
     */
    int verifyClashAll(List<TaskRecord> taskRecordList);
}
