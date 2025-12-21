package com.group.repairbackend.model;

import lombok.Data;

@Data
public class Result {
    private Integer code; //1是成功，0是失败；
    private String msg; //成功消息或者失败消息；
    private Object data; //放数据；

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success(Object data) {
        return new Result(1, "success", data);
    }
    public static Result success() {
        return new Result(1, "success", null);
    }
    public static Result success(String msg) {
        return new Result(1,msg,null);
    }
    public static Result success(String msg, Object data) {
        return new Result(1, msg, data);
    }

    public static Result error( String msg) {
        return new Result(0, msg,null);
    }

}
