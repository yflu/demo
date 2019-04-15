package com.example.demo.core.common.model.response;

import com.example.demo.core.exception.AbstractBaseExceptionEnum;
import com.example.demo.core.exception.enums.CoreExceptionEnum;

/**
 * response返回工具类
 */
public class ResponseUtil {

    public static SuccessResponseData SUCCESS = new SuccessResponseData();

    public static ErrorResponseData FAIL = new ErrorResponseData();

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static ResponseData getSuccess(Object data) {
        return new SuccessResponseData(data);
    }

    /**
     * 参数异常
     *
     * @return
     */
    public static ResponseData getParamError() {
        return new ErrorResponseData(CoreExceptionEnum.REQUEST_PARAM_ERROR.getCode(), CoreExceptionEnum.REQUEST_PARAM_ERROR.getMessage());
    }

    /**
     * 参数异常
     *
     * @return
     */
    public static ResponseData getParamError(String msg) {
        return new ErrorResponseData(CoreExceptionEnum.REQUEST_PARAM_ERROR.getCode(), msg);
    }

    /**
     * 返回错误码
     *
     * @param e
     * @return
     */
    public static ErrorResponseData getFail(AbstractBaseExceptionEnum e) {
        return new ErrorResponseData(e.getCode(), e.getMessage());
    }

    /**
     * 返回错误码
     *
     * @param e
     * @param data
     * @return
     */
    public static ErrorResponseData getFail(AbstractBaseExceptionEnum e, Object data) {
        return new ErrorResponseData(e.getCode(), e.getMessage(), data);
    }

    /**
     * 返回错误码
     *
     * @param code
     * @param message
     * @return
     */
    public static ErrorResponseData getFail(Integer code, String message) {
        return new ErrorResponseData(code, message);
    }

    /**
     * 返回错误码
     *
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static ErrorResponseData getFail(Integer code, String message, Object data) {
        return new ErrorResponseData(code, message, data);
    }
}
