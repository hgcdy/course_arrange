package cn.netinnet.coursearrange;

import cn.netinnet.coursearrange.entity.NinArrange;
import cn.netinnet.coursearrange.entity.NinClass;
import cn.netinnet.coursearrange.mapper.NinArrangeMapper;
import cn.netinnet.coursearrange.mapper.NinClassMapper;
import cn.netinnet.coursearrange.mapper.NinCourseMapper;
import cn.netinnet.coursearrange.model.ResultModel;
import cn.netinnet.coursearrange.service.INinArrangeService;
import cn.netinnet.coursearrange.service.INinStudentCourseService;
import cn.netinnet.coursearrange.util.GenSecretUtil;
import cn.netinnet.coursearrange.util.HttpUtil;
import cn.netinnet.coursearrange.util.SendPostUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.poi.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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







        System.out.println("");
    }
//
//    @ResponseBody
//    @RequestMapping(value = "export")
//    public AjaxJson exportFile(Office office, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
//        AjaxJson j = new AjaxJson();
//        try {
//            String fileName = "机构"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
//            Page<Office> page = officeService.findPage(new Page<Office>(request, response, -1), office);
//
//            for(int i=0;i<page.getList().size();i++){
//                System.out.println(page.getList().get(i));
//            }
//            //new ExportExcel("机构", Office.class).setDataList(page.getList()).write(response, fileName).dispose();
//            String path = System.getProperty("catalina.home");
//            createExcel(response,page.getList(),fileName,path);
//
//            downloadExcel(response,path+fileName);
//
//            j.setSuccess(true);
//            j.setMsg("导出成功！");
//            return j;
//        } catch (Exception e) {
//            e.printStackTrace();
//            j.setSuccess(false);
//            j.setMsg("导出测试记录失败！失败信息："+e.getMessage());
//        }
//        return j;
//    }
//
//    public void downloadExcel(HttpServletResponse response,String path) throws IOException{
//
//        //1、设置响应的头文件，会自动识别文件内容
//        response.setContentType("multipart/form-data");
//
//        //2、设置Content-Disposition
//        response.setHeader("Content-Disposition", "attachment;filename=test.xls");
//
//        OutputStream out = null;
//        InputStream in = null;
//        try {
//            //3、输出流
//            out = response.getOutputStream();
//
//            //4、获取服务端生成的excel文件，这里的path等于4.8中的path
//            in = new FileInputStream(new File(path));
//
//            //5、输出文件
//            int b;
//            while((b=in.read())!=-1){
//                out.write(b);
//            }
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        finally{
//            in.close();
//            out.close();
//
//        }
//    }
//
//    public void createExcel(HttpServletResponse response,List<Office> list,String filename,String path) throws IOException{
//
//        //1、创建workbook,对应一个Excel
//        HSSFWorkbook wb = new HSSFWorkbook();
//
//        //2、创建一个sheet,参数为sheet的名称
//        HSSFSheet sheet = wb.createSheet(filename);
//
//        //3、创建第一行
//        HSSFRow row = sheet.createRow(0);
//
//        //4、创建第一行的列信息，也就是列名
//        HSSFCell cell = row.createCell(0);
//        cell.setCellValue("机构名称");
//
//        cell = row.createCell(1);
//        cell.setCellValue("机构编号");
//
//        //5、写入数据
//        for(int i=1;i<=list.size();i++){
//            row = sheet.createRow(i);
//            cell = row.createCell(0);
//            cell.setCellValue(list.get(i-1).getName());
//            cell = row.createCell(1);
//            cell.setCellValue(list.get(i-1).getCode());
//        }
//
//        //6、生成文件
//        //String path = System.getProperty("catalina.home");
//
//
//        FileOutputStream os = new FileOutputStream(path+filename);;
//        try {
//            wb.write(os);
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        finally{
//            os.close();
//        }}
//
////  前端
//    $("#export").click(function(){//导出Excel文件
//        alert("sasasas");
//        window.location.href = '${ctx}/sys/office/export';
//    });
//
//
}


