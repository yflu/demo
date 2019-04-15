package com.example.demo.core.common.model.response;

import lombok.Data;

@Data
public class ResponseData {

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应对象
     */
    private Object data;

    public ResponseData() {
    }

    public ResponseData(Boolean success, Integer code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static SuccessResponseData success() {
        return new SuccessResponseData();
    }

    public static SuccessResponseData success(Object object) {
        return new SuccessResponseData(object);
    }

    public static SuccessResponseData success(Integer code, String message, Object object) {
        return new SuccessResponseData(code, message, object);
    }

    public static ErrorResponseData error(String message) {
        return new ErrorResponseData(message);
    }

    public static ErrorResponseData error(Integer code, String message) {
        return new ErrorResponseData(code, message);
    }

    public static ErrorResponseData error(Integer code, String message, Object object) {
        return new ErrorResponseData(code, message, object);
    }
}
