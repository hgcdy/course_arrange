package cn.netinnet.coursearrange.global;


import cn.netinnet.coursearrange.enums.ResultEnum;

import java.io.Serializable;

public class ResultEntry<T> implements Serializable {

    public static final int SUCCESS = 200;
    public static final int FAILURE = 412;

    private int code;
    private String msg;
    private T data;

    public ResultEntry(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultEntry(ResultEnum resultEnum, T data) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
        this.data = data;
    }

    public ResultEntry(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultEntry(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultEntry{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static <T> ResultEntry<T> ok(){
        return new ResultEntry<>(ResultEnum.SUCCESS);
    }

    public static <T> ResultEntry<T> ok(String msg){
        return new ResultEntry<>(SUCCESS, msg);
    }

    public static <T> ResultEntry<T> ok(T data){
        return new ResultEntry<>(ResultEnum.SUCCESS, data);
    }

    public static <T> ResultEntry<T> ok(String msg, T data){
        return new ResultEntry<>(SUCCESS, msg, data);
    }

    public static <T> ResultEntry<T> error(){
        return new ResultEntry<>(ResultEnum.FAILURE);
    }

    public static <T> ResultEntry<T> error(String msg){
        return new ResultEntry<>(FAILURE, msg);
    }

    public static <T> ResultEntry<T> error(int code, String msg){
        return new ResultEntry<>(code, msg);
    }

    public static <T> ResultEntry<T> error(ResultEnum resultEnum){
        return new ResultEntry<>(resultEnum);
    }

    public static <T> ResultEntry<T> error(int code, String msg, T data){
        return new ResultEntry<>(code, msg, data);
    }

    public static <T> ResultEntry<T> error(ResultEnum resultEnum, T data){
        return new ResultEntry<>(resultEnum, data);
    }
}
