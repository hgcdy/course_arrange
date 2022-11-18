package cn.netinnet.coursearrange.model;


import cn.netinnet.coursearrange.constant.IResultStatus;
import cn.netinnet.coursearrange.constant.impl.ResultStatus;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * ajax返回数据模型
 */
public class ResultModel implements Serializable {

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回结果描述
     */
    private String msg;

    /**
     * 返回内容
     */
    private Object data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public ResultModel() {
    }

    public ResultModel(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = new JSONObject();
    }

    public ResultModel(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultModel(IResultStatus status) {
        this.code = status.getCode();
        this.msg = status.getMsg();
        this.data = new JSONObject();
    }

    public ResultModel(IResultStatus status, Object data) {
        this.code = status.getCode();
        this.msg = status.getMsg();
        this.data = data;
    }

    public static ResultModel ok(Object data) {
        return new ResultModel(ResultStatus.SUCCESS, data);
    }

    public static ResultModel ok() {
        return new ResultModel(ResultStatus.SUCCESS);
    }

    public static ResultModel error(IResultStatus error) {
        return new ResultModel(error);
    }

    public static ResultModel error(IResultStatus error, Object data) {
        return new ResultModel(error, data);
    }

    public static ResultModel error(int code, String msg) {
        return new ResultModel(code, msg);
    }

    /**
     * 判断返回的信息
     *
     * @param index
     * @param result
     */
    public static JSONObject returnMessage(int index, JSONObject result) {
        String message = "操作成功！";
        Boolean code = true;
        if (index <= 0) {
            message = "操作失败!";
            code = false;
        }
        result.put("message", message);
        result.put("code", code);
        return result;
    }
}
