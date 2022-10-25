package cn.netinnet.coursearrange.util;


import cn.netinnet.coursearrange.model.ResultModel;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发送post请求
 */
@Component
public class SendPostUtil {
    public static String sendPost(String url, Map<String,String> map) throws Exception{
        // 使用默认配置创建httpclient的实例
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for( String key : map.keySet()){
            params.add(new BasicNameValuePair(key, map.get(key)));
        }
        UrlEncodedFormEntity e = new UrlEncodedFormEntity(params, "UTF-8");
        post.setEntity(e);
        CloseableHttpResponse response = client.execute(post);

        String respStr = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            respStr = EntityUtils.toString(entity, "UTF-8");
        }
        // 释放资源
        EntityUtils.consume(entity);
        return respStr;
    }

    /**
     *  访问子平台 获取结果。
     * @param map
     * @param baseUrl
     * @return
     */
    public static ResultModel getSubSystemResultModel(Map<String, String> map, String baseUrl) {
        try {
            String dataResult = SendPostUtil.sendPost(baseUrl , map);
            JSONObject data = JSONObject.parseObject(dataResult);
            ResultModel resultModel = new ResultModel(data.getInteger("code"),data.getString("msg"),data.getJSONObject("data"));
            return resultModel;
        } catch (Exception e) {
            return ResultModel.error(500,"同步子平台错误");
        }
    }
}
