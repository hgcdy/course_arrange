package cn.netinnet.coursearrange.text.impl;

import cn.netinnet.coursearrange.text.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneticAlgorithmImpl implements GeneticAlgorithm {

    private List<Chromosome> population = new ArrayList<Chromosome>();
    private int popSize = 20;//种群数量
    private int geneSize;//基因最大长度
    private int maxIterNum = 500;//最大迭代次数
    private double mutationRate = 0.01;//基因变异的概率

    private int generation = 1;//当前遗传到第几代

    private double bestScore;//最好得分
    private double worstScore;//最坏得分
    private double totalScore;//总得分
    private double averageScore;//平均得分

    private double x; //记录历史种群中最好的X值
    private double y; //记录历史种群中最好的Y值
    private int geneI;//x y所在代数

    @Autowired
    private ArrangeService arrangeService;


    @Override
    public void caculte() {
        //初始化种群
        generation = 1;
        init();
        while (generation < maxIterNum) {
            //种群遗传
            evolve();
            print();
            generation++;
        }
    }

    /**
     * 输出
     */
    private void print() {
        System.out.println("--------------------------------");
        System.out.println("the generation is:" + generation);
        System.out.println("the best y is:" + bestScore);
        System.out.println("the worst fitness is:" + worstScore);
        System.out.println("the average fitness is:" + averageScore);
        System.out.println("the total fitness is:" + totalScore);
        System.out.println("geneI:" + geneI + "\tx:" + x + "\ty:" + y);
    }

    /**
     * 初始化
     */
    private void init() {
        for (int i = 0; i < popSize; i++) {
            population = new ArrayList<Chromosome>();
            List<TaskRecord> taskRecords = arrangeService.generateChromosome();
            Chromosome chro = new Chromosome(taskRecords);
            population.add(chro);
        }
        caculteScore();
    }

    /**
     * 遗传
     */
    private void evolve() {
        List<Chromosome> childPopulation = new ArrayList<Chromosome>();
        //生成下一代种群
        while (childPopulation.size() < popSize) {
            Chromosome p1 = getParentChromosome();
            Chromosome p2 = getParentChromosome();
            List<Chromosome> children = genetic(p1, p2);
            if (children != null) {
                for (Chromosome chro : children) {
                    childPopulation.add(chro);
                }
            }
        }
        //新种群替换旧种群
        List<Chromosome> t = population;
        population = childPopulation;
        t.clear();
        t = null;
        //基因突变
        mutation();
        //计算新种群的适应度
        caculteScore();
    }

    /**
     * 轮盘赌选择
     */
    private Chromosome getParentChromosome (){
        double slice = Math.random() * totalScore;
        double sum = 0;
        for (Chromosome chro : population) {
            sum += chro.getScore();
            if (sum > slice && chro.getScore() >= averageScore) {
                return chro;
            }
        }
        return null;
    }

    /**
     * 计算种群适应度
     */
    private void caculteScore() {
        //todo
        setChromosomeScore(population.get(0));
        bestScore = population.get(0).getScore();
        worstScore = population.get(0).getScore();
        totalScore = 0;
        for (Chromosome chro : population) {
            setChromosomeScore(chro);
            if (chro.getScore() > bestScore) { //设置最好基因值
                bestScore = chro.getScore();
                if (y < bestScore) {

                    y = bestScore;
                    geneI = generation;
                }
            }
            if (chro.getScore() < worstScore) { //设置最坏基因值
                worstScore = chro.getScore();
            }
            totalScore += chro.getScore();
        }
        averageScore = totalScore / popSize;
        //因为精度问题导致的平均值大于最好值，将平均值设置成最好值
        averageScore = averageScore > bestScore ? bestScore : averageScore;



    }

    /**
     * 设置染色体得分
     * @param chro
     */
    private void setChromosomeScore(Chromosome chro) {
        if (chro == null) {
            return;
        }
        /**
         * 没有冲突
         * 教师班级方差最小
         * 课程在同一天
         */

        double score = 0;
        int clashNum = 0;//冲突次数
        Map<Long, int[]> idVarianceMap = new HashMap();

        List<TaskRecord> taskRecordList = chro.getTaskRecordList();


        Map<Integer, Map<Integer, List<TaskRecord>>> map = taskRecordList.stream().collect(Collectors.groupingBy(TaskRecord::getWeek, Collectors.groupingBy(TaskRecord::getPitchNum)));

        for (Map.Entry<Integer, Map<Integer, List<TaskRecord>>> map1: map.entrySet()) {
            Integer week = map1.getKey();
            Map<Integer, List<TaskRecord>> value = map1.getValue();

            for (Map.Entry<Integer, List<TaskRecord>> map2 : value.entrySet()) {
                Integer pitchNum = map2.getKey();
                List<TaskRecord> recordList = map2.getValue();

                int len = recordList.size();
                for (int i = 0; i < len; i++) {
                    TaskRecord taskRecord = recordList.get(i);
                    if (i + 1 != len) {
                        boolean b = arrangeService.verifyClash(recordList.subList(i + 1, len), taskRecord);
                        if (!b) {
                            clashNum++;
                        }
                    }
                    TeaTask teaTask = taskRecord.getTeaTask();

                    List<Long> longs = new ArrayList<>();
                    longs.add(teaTask.getTeacherId());
                    longs.addAll(teaTask.getClassIdList());

                    for (Long id : longs) {
                        if (null == idVarianceMap.get(id)) {
                            int[] ints = new int[5];
                            ints[week - 1] = 1;
                            idVarianceMap.put(id, ints);
                        } else {
                            idVarianceMap.get(id)[week - 1] += 1;
                        }
                    }
                }
            }
        }

        Map<TeaTask, List<TaskRecord>> teaTaskListMap = taskRecordList.stream().collect(Collectors.groupingBy(TaskRecord::getTeaTask));
        for (Map.Entry<TeaTask, List<TaskRecord>> map1: teaTaskListMap.entrySet()) {
            TeaTask task = map1.getKey();
            List<TaskRecord> recordList = map1.getValue();



        }
        chro.setScore(score);
    }

    /**
     * 基因突变
     */
    private void mutation() {
        for (Chromosome chro : population) {
            if (Math.random() < mutationRate) { //发生基因突变
                int i = (int) (Math.random() * 5);
                int j = (int) (Math.random() * 5);
                while (i == j) {
                    j = (int) (Math.random() * 5);
                }

                List<TaskRecord> taskRecordList = chro.getTaskRecordList();
                for (TaskRecord record : taskRecordList) {
                    if (record.getWeek() == i) {
                        record.setWeek(j);
                    }
                    if (record.getWeek() == j) {
                        record.setWeek(i);
                    }
                }

            }
        }
    }

    /**
     * 克隆
     * @param c
     * @return
     */
    public Chromosome clone(final Chromosome c) {
        if (c == null || c.getTaskRecordList() == null) {
            return null;
        }
        List<TaskRecord> recordList = new ArrayList<>();
        Chromosome copy = new Chromosome(recordList);
        for (TaskRecord record : c.getTaskRecordList()) {
            TaskRecord newTaskRecord = new TaskRecord(record.getTeaTask());
            BeanUtils.copyProperties(record, newTaskRecord);
            copy.getTaskRecordList().add(newTaskRecord);
        }
        return copy;
    }

    /**
     * 遗传产生下一代
     * @param p1
     * @param p2
     * @return
     */
    public List<Chromosome> genetic(Chromosome p1, Chromosome p2) {
        if (p1 == null || p2 == null) { //染色体有一个为空，不产生下一代
            return null;
        }
        if (p1.getTaskRecordList() == null || p2.getTaskRecordList() == null) { //染色体有一个没有基因序列，不产生下一代
            return null;
        }

        Chromosome c1 = clone(p1);
        Chromosome c2 = clone(p2);

        //随机产生交叉互换时间
        int size = c1.getTaskRecordList().size();
        int a = (int) (Math.random() * size);


        List<TaskRecord> taskRecordList1 = c1.getTaskRecordList();
        TaskRecord taskRecord1 = taskRecordList1.get(a);

        List<TaskRecord> taskRecordList2 = c2.getTaskRecordList();
        TaskRecord taskRecord2 = taskRecordList2.get(a);

        //交换时间
        int week1 = taskRecord1.getWeek();
        int week2 = taskRecord2.getWeek();
        taskRecord1.setWeek(week2);
        taskRecord2.setWeek(week1);

        int pitchNum1 = taskRecord1.getPitchNum();
        int pitchNum2 = taskRecord2.getPitchNum();
        taskRecord1.setPitchNum(pitchNum2);
        taskRecord2.setPitchNum(pitchNum1);


        //校验并解决冲突
        boolean b1 = arrangeService.verifyClash(taskRecordList1, taskRecord1);
        if (!b1) {
            arrangeService.solveClash(taskRecordList1, taskRecord1);
        }
        boolean b2 = arrangeService.verifyClash(taskRecordList2, taskRecord2);
        if (!b2) {
            arrangeService.solveClash(taskRecordList2, taskRecord2);
        }

        List<Chromosome> list = new ArrayList<Chromosome>();
        list.add(c1);
        list.add(c2);
        return list;
    }

}
