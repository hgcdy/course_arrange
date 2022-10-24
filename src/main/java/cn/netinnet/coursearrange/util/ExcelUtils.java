package cn.netinnet.coursearrange.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chen.wb
 * @version V1.0
 * @Description: excel工具类
 * @Date 2017-11-30
 **/
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * @param is            根据excel扩展名创建workBook
     * @param excelFileName excel文件名
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/8/23 9:20
     * @Description
     */
    private static Workbook createWorkbook(InputStream is, String excelFileName) throws IOException {
        if (StringUtil.isNullOrEmpty(excelFileName)) {
            return null;
        }
        String extName = excelFileName.substring(excelFileName.indexOf(".")).toLowerCase();
        if (extName.endsWith(".xls")) {
            return new HSSFWorkbook(is);
        } else if (extName.endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        }
        return null;
    }

    /**
     * @param workbook   excel工作簿
     * @param sheetIndex 工作簿中的sheet索引
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/8/23 9:22
     * @Description 获取工作簿中指定的sheet内容
     */
    private static Sheet getSheet(Workbook workbook, int sheetIndex) {
        return workbook.getSheetAt(sheetIndex);
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            return cell.getCellFormula();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }

    /**
     * @param file
     * @param params
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/10/10 10:08
     * @Description 读取Excel数据，有合并单元格的情况也可以读取
     */
    public static JSONArray readExcel4Array(File file, Map<String, String> params) {
        try {
            Workbook wb = createWorkbook(new FileInputStream(file), file.getName());
            return readExcel(wb, params, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray readExcel4Array(InputStream inputStream, String fileName, Map<String, String> params) {
        try {
            Workbook wb = createWorkbook(inputStream, fileName);
            return readExcel(wb, params, 0, 0, 0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取excel文件
     *
     * @param wb
     * @param sheetIndex    sheet页下标：从0开始
     * @param startReadLine 开始读取的行:从0开始
     * @param tailLine      去除最后读取的行
     */
    public static JSONArray readExcel(Workbook wb, Map<String, String> params, int sheetIndex, int startReadLine, int tailLine) throws InvocationTargetException {
        Sheet sheet = wb.getSheetAt(sheetIndex);
        Row row;
        String[] fields = new String[params.size()];
        JSONArray list = new JSONArray();
        DecimalFormat decimalFormat = new DecimalFormat("000000");
        flag:for (int i = startReadLine; i < sheet.getLastRowNum() - tailLine + 1; i++) {
            int j = 0;
            row = sheet.getRow(i);
            if (row == null) {
                continue ;
            }
            JSONObject obj = new JSONObject();
            obj.put("rows", i + 1);
            int blankCellNum = 0;
            for (int k = 0; k < params.size(); k++) {
                Cell c = row.getCell(k);
                if (c != null && c.getCellType() == Cell.CELL_TYPE_BLANK){
                    blankCellNum ++;
                }
                if(("tel".equals(fields[j])||"email".equals(fields[j])||"position".equals(fields[j])) && c == null){
                        obj.put(fields[j++],"");
                        continue;
                }else if(c == null){
                        blankCellNum ++;
                        j ++;
                        continue;
                }else{
                    c.setCellType(1);
                }
                if (i == 0) {
                    //先验证读取的表头是否符合，不符合则跳出外层循环
                    if(params.containsKey(c.getStringCellValue())){
                        fields[j++] = params.get(c.getRichStringCellValue().getString().trim());
                    }else{
                        break flag;
                    }
                } else {
                        boolean isMerge = isMergedRegion(sheet, i, c.getColumnIndex());
                        //判断是否具有合并单元格
                        if (isMerge) {
                            String rs = getMergedRegionValue(sheet, row.getRowNum(), c.getColumnIndex());
                            obj.put(fields[j++], rs.trim());
                        } else {
                            String value = c.getStringCellValue().trim();
                            if ("sex".equals(fields[j])) {
                                if ("女".equals(value)) {
                                    value = "0";
                                } else if ("男".equals(value)) {
                                    value = "1";
                                } else {
                                    value = "2";
                                }
                            }
                            if("jobNumber".equals(fields[j]) && value != null && !value.trim().equals("")){
                                value = decimalFormat.format(Integer.parseInt(value));
                            }

                            obj.put(fields[j++], value);
                        }
                }
            }
            if (i != 0 && blankCellNum != params.size()) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    private static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }
        return null;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param object 实体对象
     * @param fields 字段名
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/8/22 13:46
     * @Description
     */
    private static boolean isHasValues(Object object, String[] fields) {
        boolean flag = false;
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i];
            String methodName = "get" /* + StringUtil.firstCharToUpperCase(fieldName)*/;//todo firstCharToUpperCases首字母大写
            Method getMethod;
            try {
                getMethod = object.getClass().getMethod(methodName);
                Object obj = getMethod.invoke(object);
                if (null != obj && !"".equals(obj)) {
                    // 有一个为空
                    flag = true;
                    break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return flag;
    }

    /**
     * @param renderData
     * @param title
     * @param os
     * @param headers
     * @param fields
     * @param rule       自己定义合并单元格的规则
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/8/25 14:20
     * @Description 导出，支持多个sheet导出
     */
    private static void export(JSONObject renderData, String[] title, OutputStream os, List<String[]> headers, List<String[]> fields, String rule) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        if (renderData.size() > 0) {
            for (int i = 0; i < renderData.size(); i++) {
                JSONArray array = renderData.getJSONArray("list_" + i);
                //生成一个表格
                HSSFSheet sheet = workbook.createSheet(title[i]);

                //设置表格默认列宽15个字节
                sheet.setDefaultColumnWidth(20);
                //生成一个样式
                HSSFCellStyle style = getCellStyle(workbook);

                //生成一个字体
                HSSFFont font = getFont(workbook);
                //把字体应用到当前样式
                style.setFont(font);
                style.setWrapText(true);
                //生成表格标题
                HSSFRow row = sheet.createRow(0);
                row.setHeight((short) 300);


                HSSFCell cell = null;
                for (int j = 0; j < headers.get(i).length; j++) {
                    cell = row.createCell(j);
                    cell.setCellStyle(style);
                    HSSFRichTextString text = new HSSFRichTextString(headers.get(i)[j]);
                    cell.setCellValue(text);
                }
                final HSSFCellStyle cessStyle = setCellStyle(workbook);
                //将数据放入sheet中
                for (int j = 0; j < array.size(); j++) {
                    row = sheet.createRow(j + 1);
                    JSONObject obj = array.getJSONObject(j);
                    Integer index = 0;
                    //利用反射，根据JavaBean属性的先后顺序，动态调用get方法得到属性的值
                    try {
                        for (int k = 0; k < fields.get(i).length; k++) {
                            cell = row.createCell(k);
                            row.setHeightInPoints((short) 75);
                            cell.setCellStyle(cessStyle);
                            cell.setCellValue(obj.getString(fields.get(i)[k]));
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    if ("trial".equals(rule) && j != 0 && (j + 1) % 4 == 0) {
                        sheet.addMergedRegion(new CellRangeAddress(j + 1 - 3, j + 1, 0, 0));
                    }
                }
            }
            close(workbook, os);
        }
    }

    private static void close(HSSFWorkbook workbook, OutputStream os) {
        try {
            workbook.write(os);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }


    /**
     * @param array
     * @param title
     * @param os
     * @param header
     * @param field  与标题的顺序要一致,fields遵循驼峰命名规则
     * @param rule   自己定义合并单元格的规则
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/8/25 14:19
     * @Description 导出JsonArray数据
     */
    public static void exportJsonArrayToExcel(JSONArray array, String title, OutputStream os, String[] header, String[] field, String rule) {
        JSONObject renderData = new JSONObject();
        renderData.put("list_0", array);
        ArrayList<String[]> headers = new ArrayList<>();
        headers.add(header);
        ArrayList<String[]> fields = new ArrayList<>();
        fields.add(field);
        export(renderData, new String[]{title}, os, headers, fields, rule);
    }

    /**
     * @param arrays  导出多个sheet的数据集合
     * @param titles  每个sheet的名称
     * @param os
     * @param headers 每个sheet的第一行显示的标题
     * @param fields  每个sheet的显示字段，与标题的顺序要一致,fields遵循驼峰命名规则
     * @param rule    自己定义合并单元格的规则
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/8/25 14:40
     * @Description
     */
    public static void exportMultiJsonArrayToExcel(List<JSONArray> arrays, String[] titles, OutputStream os, List<String[]> headers, List<String[]> fields, String rule) {
        JSONObject renderData = new JSONObject();
        for (int i = 0; i < arrays.size(); i++) {
            renderData.put("list_" + i, arrays.get(i));
        }
        export(renderData, titles, os, headers, fields, rule);
    }

    /**
     * @param list    导出的实体数据
     * @param headers 每一列的属性标题
     * @param title   excel的tab
     * @param os      输出流
     * @param fields  要导出的字段，和header一一对应
     * @param rule    自己定义合并单元格的规则
     * @return
     * @Author liyq liyq@netinnet.cn
     * @Date 2017/8/22 13:43
     * @Description
     */
    public static <T> void exportListToExcel(List<T> list, String title, OutputStream os, String[] headers, String[] fields, String rule) {
        JSONArray renderData = JSONArray.parseArray(JSON.toJSONString(list));
        exportJsonArrayToExcel(renderData, title, os, headers, fields, rule);
    }

    /**
     * @param @param  workbook
     * @param @return
     * @return HSSFCellStyle
     * @throws
     * @Title: getCellStyle
     * @Description: 获取单元格格式
     */
    private static HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        return style;
    }

    /**
     * @param @param  workbook
     * @param @return
     * @return HSSFFont
     * @throws
     * @Title: getFont
     * @Description: 生成字体样式
     */
    private static HSSFFont getFont(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.WHITE.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        return font;
    }

    public boolean isIE(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 ? true : false;
    }

    /**
     * @ Author chenkl
     * @ Date 2017/10/16 18:16
     */
    private static HSSFCellStyle setCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平
        style.setWrapText(true);//todo 修改
        return style;
    }

    /***
    * 设置数据校验
    * @param sheet      工作簿
    * @param dataList   数据项
    * @param firstRow   起始行
    * @param lastRow    结束行
    * @param col        列
    * @author ousp
    * @date 2021/4/19
    * @return void
    */
    public static void setValidationData(HSSFSheet sheet, List<String> dataList, int firstRow, int lastRow, int col) {
        //设置数据有效性
        DVConstraint gradeConstraint = DVConstraint.createExplicitListConstraint(dataList.toArray((new String[dataList.size()])));
        //设置第col列前lastRow行都为下拉框（设置数据有效性加载在哪些单元格上）
        CellRangeAddressList gradeRegion = new CellRangeAddressList(firstRow, lastRow, col, col);
        //数据有效性对象
        HSSFDataValidation gradeDataValidation = new HSSFDataValidation(gradeRegion, gradeConstraint);
        sheet.addValidationData(gradeDataValidation);
    }

    /** 方法描述
     * @description 设置单元格style
     * @param workbook
     * @param fontSize
     * @param isBlod
     * @param isAlign
     * @return org.apache.poi.hssf.usermodel.HSSFCellStyle
     * @author Caicm
     * @date 2020/5/9 17:38
     */
    public static HSSFCellStyle setCellStyle(HSSFWorkbook workbook, short fontSize, boolean isBlod, boolean isAlign) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        //是否水平居中
        if (isAlign) {
            //设置水平居中
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        }
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        HSSFFont font = workbook.createFont();
        if (isBlod) {
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        }
        font.setFontHeightInPoints(fontSize);
        cellStyle.setFont(font);
        return cellStyle;
    }
}
