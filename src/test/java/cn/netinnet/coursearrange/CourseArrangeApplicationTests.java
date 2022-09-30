package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.mapper.NinClassCourseMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinClassCourseService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.GenSecretUtil;
import cn.netinnet.coursearrange.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootTest
class CourseArrangeApplicationTests {
    @Autowired
    private NinClassCourseMapper ninClassCourseMapper;
    @Autowired
    private INinClassCourseService ninClassCourseService;
    @Autowired
    private NinCourseMapper ninCourseMapper;
    @Autowired
    private INinStudentCourseService ninStudentCourseService;
    @Autowired
    private INinArrangeService ninArrangeService;
    @Autowired
    private NinClassMapper ninClassMapper;
    @Autowired
    private NinArrangeMapper ninArrangeMapper;


    @Test
    void contextLoads() {
//     ninArrangeService.arrange();
//        Map<String, String> info = ninArrangeService.getInfo(null, null, 1L, null);
//        System.out.println(info);
//        List<Map<String, Object>> info = ninArrangeMapper.getInfo(null, null, 3L);
//        ArrayList<Long> classIdList = new ArrayList<>();
//        classIdList.add(100L);
//        ArrayList<Long> teachClassIdList = new ArrayList<>();
//        teachClassIdList.add(812322783474005866L);
//
//        List<Map<String, Object>> info1 = ninArrangeMapper.getInfo(classIdList, null, null);
//        List<Map<String, Object>> info2 = ninArrangeMapper.getInfo(null, teachClassIdList, null);
//        List<Map<String, Object>> info3 = ninArrangeMapper.getInfo(classIdList, teachClassIdList, null);

//        List<NinClass> cds = ninClassMapper.getSelectList("新工科产业学院", null, null);

//        List<Map<String, Object>> maps = ninClassMapper.collegeCareerClassList();
//        Map<String, Map<String, List<Map<String, Object>>>> collect = maps.stream().collect(Collectors.groupingBy(i -> (String) (i.get("college")), Collectors.groupingBy(i -> (String) i.get("careerName"))));

//        String str = "[]";
//        List<Long> longs = JSON.parseArray(str, Long.class);

//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("defd");
//        strings.add("dfg");
//        ArrayList<String> strings1 = new ArrayList<>();
//        strings1.addAll(strings);
//        strings1.add("dfvb");

//        ArrayList<Map<String, Object>> maps = new ArrayList<>();
//        Map<String, Object> longIntegerMap = new HashMap<>();
//        longIntegerMap.put("careerId", 101L);
//        longIntegerMap.put("courseNum", -10);
//        maps.add(longIntegerMap);
//
//
//        Map<String, Object> longIntegerMap2 = new HashMap<>();
//        longIntegerMap2.put("careerId", 102L);
//        longIntegerMap2.put("courseNum", -8);
//        maps.add(longIntegerMap2);
//
//
//        ninClassMapper.alterBatchCourseNum(maps);
//
//        BigDecimal score = BigDecimal.ZERO;
//        System.out.println(score);
//        for (int i = 0; i < 10; i++) {
//            score = score.add(new BigDecimal("1." + i));
//            System.out.println(score);
//        }
        String subSystemUrl = "";
        JSONObject obj = null;


        try {
            //apiCode是定死在数据字典里面的
            subSystemUrl = "http://172.30.6.162:8095/nin_case_platform?";
            HashMap<String, String> hashMap = new HashMap<>();
            String str = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjQ2MTA1ODgsInVzZXIiOiJ7XCJjbGFzc0lkXCI6MCxcImNsYXNzTmFtZVwiOlwiXCIsXCJyb2xlQ29kZVwiOlwiYWRtaW5cIixcInNjaG9vbENvZGVcIjpcIk5FVElOTkVUXCIsXCJzY2hvb2xJZFwiOjAsXCJzY2hvb2xOYW1lXCI6XCJcIixcInVzZXJJZFwiOjcwMzMwNDUwNTk0OTAxMTk2OCxcInVzZXJMb2dpblwiOlwidGVzdEFkbWluXCIsXCJ1c2VyTmFtZVwiOlwidGVzdEFkbWluXCIsXCJ1c2VyVHlwZVwiOjAsXCJ2aXJ0dWFsTG9naW5cIjpmYWxzZX0iLCJjbGllbnRfaWQiOiJSUEEifQ.R1sbTxnUSsJIDtOCV_BGMOTseEwA1kXx9wHMzWr3EHM";

            hashMap.put("access_token", str);
            hashMap.put("taskTitle", "");
            String nin_case = GenSecretUtil.creatSecretKey(hashMap, "nin_case");
            subSystemUrl = subSystemUrl + nin_case;


        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = HttpUtil.getHttpInterface(subSystemUrl);
        obj = JSONObject.parseObject(result);


        System.out.println(obj);

    }

    public int[] grouping(int maxNum, int minNum){
        int[] ints = new int[minNum];
        for (int i = 0; i < maxNum; i++) {
            ints[i % ints.length]++;
        }
        return ints;
    }


}


