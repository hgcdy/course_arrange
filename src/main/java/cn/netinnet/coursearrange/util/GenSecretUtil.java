package cn.netinnet.coursearrange.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * 秘钥验证
 * Created by Administrator on 2020/2/26.
 */
public class GenSecretUtil {
    public  static  String APP_KEY="nin_case";//appkey  取常量值
    private static  String CHARSET = "UTF-8";
    private static  String PUBLIC_KEY = "ZBY";//公钥
    private static  Integer TIMEOUT = 30;//超时时间
    private final static Logger _LOG = LoggerFactory.getLogger(GenSecretUtil.class);
    /**
     * 功能：将一个Map按照Key字母升序构成一个QueryString. 并且加入时间戳，公钥，私钥，生成对应的字符串
     * @param queryMap 请求参数
     * @param appKey    私钥
     * @return
     */

    public static String creatSecretKey(Map<String, String> queryMap, String appKey) {
        if (queryMap.size()==0) {
            return null;
        }
        JSONObject obj = encryption(queryMap,appKey);
        return String.format("%s&secretkey=%s", obj.getString("sqs"), obj.getString("qsmd"));
    }
    private static JSONObject encryption(Map<String, String> queryMap, String appKey){
        JSONObject obj = new JSONObject();
        //自动排序
        Map<String, String> map = new TreeMap<String, String>(queryMap);
        long timestamp = System.currentTimeMillis();
        map.put("timestamp",Long.toString(timestamp));
        //传进来的参数加上时间戳，然后拼接起来
        String sqs = createQueryString(map);
        //传进来的参数加上时间戳,再加上秘钥，拼接起来
        map.put("appKey",appKey);
        map.put("publicKey",PUBLIC_KEY);
        String qs = createQueryString(map); // 生成queryString方法可自己编写
        //带秘钥的串加密
        String qsmd = MD5.getMD5Encode(qs);
        qsmd = qsmd.toUpperCase();
        obj.put("sqs",sqs);
        obj.put("qsmd",qsmd);
        return obj;
    }
    private static JSONObject uncryption(Map<String, String> queryMap, String appKey){
        JSONObject obj = new JSONObject();
        //自动排序
        Map<String, String> map = new TreeMap<String, String>(queryMap);
        long timestamp = System.currentTimeMillis();
        map.put("timestamp",queryMap.get("timestamp"));
        //传进来的参数加上时间戳，然后拼接起来
        String sqs = createQueryString(map);
        //传进来的参数加上时间戳,再加上秘钥，拼接起来
        map.put("appKey",appKey);
        map.put("publicKey",PUBLIC_KEY);
        String qs = createQueryString(map); // 生成queryString方法可自己编写
        //带秘钥的串加密
        String qsmd = MD5.getMD5Encode(qs);
        qsmd = qsmd.toUpperCase();
        obj.put("sqs",sqs);
        obj.put("qsmd",qsmd);
        return obj;
    }
    /**************************************************
     *@Function: createQueryString
     *@Description: 对于queryMap中的每个键值对按照键的字母顺序升序排序，得到排序后的请求字符串qs;
     *@param queryMap
     *@return
     *@return:String
     **************************************************/
    public static String createQueryString(Map<String, String> queryMap){
        StringBuffer queryStr = new StringBuffer();
        for (Iterator<Map.Entry<String, String>> iterator = queryMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, String> entry = iterator.next();
            try {
                if(null == entry.getValue()){
                    continue;
                }
                String value = URLEncoder.encode(entry.getValue(), CHARSET);
                value = value.replaceAll("\\+", "%2B");
                if(queryStr.length()==0){
                    queryStr.append(entry.getKey()+"=" + value);
                }else{
                    queryStr.append("&"+entry.getKey()+"=" + value);
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        return queryStr.toString();
    }

    /**
     * 校验秘钥
     * @param apiSecret 项目私钥
     * @param request
     * @return
     */
    public static boolean checkSecret(String apiSecret, HttpServletRequest request){
        Map<String, String> queryMap = new HashMap<>();
        Enumeration enu = request.getParameterNames();
        String value = "";
        while(enu.hasMoreElements()){
            String paraName = (String)enu.nextElement();
            try {
                String objStr = request.getParameter(paraName);
                objStr = objStr.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                objStr = objStr.replaceAll("%2B", "\\+");
                value = URLDecoder.decode(objStr, CHARSET);
            }catch (Exception e){
                value = request.getParameter(paraName);
            }

            queryMap.put(paraName,value);
        }
        JSONObject result = checkSecretKey(queryMap,apiSecret);
        if(1 == result.getInteger("checked")){
            return true;
        }else {
            return false;
        }
    }

    public static JSONObject checkSecretKey(Map<String, String> queryMap, String appKey){
        JSONObject obj = new JSONObject();
        if (queryMap.size()==0) {
            obj.put("checked", "0");
            obj.put("msg", "验证失败，参数queryMap为空");
            _LOG.debug("------------------------> 验证失败，没有传参数<------------------------");
            return obj;
        }
        if(queryMap.get("secretkey")==null){
            obj.put("checked", "0");
            obj.put("msg", "验证失败，没有秘钥secretkey");
            _LOG.debug("------------------------> 验证失败，没有秘钥secretkey<------------------------");
            return obj;
        }
        String secretkey = queryMap.get("secretkey");
        queryMap.remove("secretkey");
        //万能秘钥
        if("Netinnet".equals(secretkey)){
            obj.put("checked", "1");
            obj.put("msg", "万能秘钥验证通过");
            _LOG.debug("------------------------> 万能秘钥验证通过<------------------------");
            return obj;
        }
        Long timestamp = Long.valueOf(queryMap.get("timestamp"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -TIMEOUT);
        if(timestamp<cal.getTimeInMillis()){
            _LOG.debug("传过来的时间戳为："+timestamp);
            obj.put("checked", "0");
            obj.put("msg", "时间戳超时");
            _LOG.error("-------------时间戳超时----------------"+timestamp);
            return obj;
        }
        if(secretkey.equals(uncryption(queryMap,appKey).getString("qsmd"))){
            obj.put("checked", "1");
            obj.put("msg", "验证通过");
            _LOG.debug("------------------------> 验证通过<------------------------");
            return obj;
        }else{
            obj.put("checked", "0");
            obj.put("msg", "加密验证失败");
            _LOG.debug("------------------------> 加密验证失败<------------------------");
            return obj;
        }
    }

    public static void main(String[] args) {
        Map<String, String> queryMap= new HashMap<>();
        queryMap.put("fff","fff");
        queryMap.put("aaa","aaa");
        queryMap.put("bbb","bbb");
        queryMap.put("ccc","ccc");
        //生成秘钥字符串
        String jm =creatSecretKey(queryMap,APP_KEY);
        String[] jmlist = jm.split("&");
        String secretkey="";
        String timestamp="";

        for (int i = 0 ; i <jmlist.length ; i++ ) {
                if(jmlist[i].indexOf("secretkey")>=0){
                    secretkey=jmlist[i].split("=")[1];
                    System.out.println("秘钥："+secretkey);
                    queryMap.put("secretkey",secretkey);
                }
            if(jmlist[i].indexOf("timestamp")>=0){
                timestamp=jmlist[i].split("=")[1];
                System.out.println("时间戳："+timestamp);
                queryMap.put("timestamp",timestamp);
            }
        }
        System.out.println("生成的秘钥："+jm);
        System.out.println("参数："+queryMap.toString());


        //验证秘钥字符串
        System.out.println(checkSecretKey(queryMap,APP_KEY));
    }
}
