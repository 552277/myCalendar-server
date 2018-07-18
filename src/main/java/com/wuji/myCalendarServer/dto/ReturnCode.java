package com.wuji.myCalendarServer.dto;

/**
 * @author zhongweichang
 * @email 15090552277@163.com
 * @date 2018/7/18 下午8:39
 */
public enum ReturnCode {
    SUCCESS(200, "成功"), PARAM_ERROR(400, "参数错误"), SESSION_INVALID(407, "session过期"), SERVER_ERROR(500, "服务器忙，请稍后再试");

    private int code;
    private String msg;

    private ReturnCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public ReturnCode setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ReturnCode setMsg(String msg) {
        this.msg = msg;
        return this;
    }

}