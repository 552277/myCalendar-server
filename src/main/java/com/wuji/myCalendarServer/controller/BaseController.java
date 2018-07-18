/**
 * 
 */
package com.wuji.myCalendarServer.controller;


import com.wuji.myCalendarServer.bean.User;
import com.wuji.myCalendarServer.dto.ResponseResult;
import com.wuji.myCalendarServer.dto.ReturnCode;
import com.wuji.myCalendarServer.exception.InternalErrorException;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;

/**
 * controller基类
 * 
 * @author yuchongjun
 */
public abstract class BaseController {

    protected User sessionUser = null;

    protected ResponseResult getResponseResult(HttpServletRequest request, ResultDelegate delegate,
                                               BindingResult bindingResult) {
        ResponseResult result = new ResponseResult();
        sessionUser = (User) request.getSession().getAttribute("user");
        // if (sessionUser == null) {
        //     MMLogger.info("the saleUser not login");
        //     throw new ClientException("请先登陆");
        // }
        // 参数检查失败
        if (bindingResult != null && bindingResult.hasErrors()) {
            result.setCode(ReturnCode.PARAM_ERROR.getCode());
            result.setMsg(bindingResult.getFieldError().getDefaultMessage());
            return result;
        }

        try {
            Object resultObject = delegate.getResultObject();
            result.setCode(ReturnCode.SUCCESS.getCode());
            result.setMsg(ReturnCode.SUCCESS.getMsg());
            result.setResult(resultObject);
        } catch (InternalErrorException e) {
            result.setCode(ReturnCode.SERVER_ERROR.getCode());
            result.setMsg(e.getMessage());
            result.setResult(e.getMessage());
        } catch (Exception e) {
            result.setCode(ReturnCode.PARAM_ERROR.getCode());
            result.setMsg(e.getMessage());
            result.setResult(e.getMessage());
        }
        return result;
    }

    protected ResponseResult getResponseResult(HttpServletRequest request, ResultDelegate delegate) {
        return getResponseResult(request, delegate, null);
    }

    protected interface ResultDelegate {

        Object getResultObject() throws Exception;
    }

}
