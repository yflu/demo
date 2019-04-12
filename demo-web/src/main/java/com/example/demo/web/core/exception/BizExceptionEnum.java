package com.example.demo.web.core.exception;

import com.example.demo.core.exception.AbstractBaseExceptionEnum;

public enum BizExceptionEnum implements AbstractBaseExceptionEnum {

    NOT_LOGIN(401, "当前用户未登录"),

    /**
     * token异常
     */
    TOKEN_EXPIRED(700, "token过期"),
    TOKEN_ERROR(700, "token验证失败"),

    NO_PERMITION(405, "权限异常"),
    SERVER_ERROR(500, "服务器异常");

    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
