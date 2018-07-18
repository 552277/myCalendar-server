package com.wuji.myCalendarServer.dto;

/**
 * 返回的响应结果包装对象
 *
 * @author zhongweichang
 * @email 15090552277@163.com
 * @date 2018/7/18 下午8:37
 */
public class ResponseResult {

    private int code;
    private String msg;
    private Object result;

    public ResponseResult(){
    }

    public ResponseResult(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(int code, String msg, Object result){
        this(code, msg);
        this.result = result;
    }

    public ResponseResult(ReturnCode retCode){
        this.code = retCode.getCode();
        this.msg = retCode.getMsg();
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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}