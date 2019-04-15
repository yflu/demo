package com.example.demo.web.core.exception;

import com.example.demo.core.exception.AbstractBaseExceptionEnum;

public enum BizExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 账户问题
     */
    USER_ALREADY_REG(1000, "该用户已经注册"),
    USER_NOT_EXISTED(1001, "没有此用户"),
    ACCOUNT_FREEZED(1002, "账号被冻结"),
    OLD_PWD_NOT_RIGHT(1003, "原密码不正确"),
    TWO_PWD_NOT_MATCH(1004, "两次输入密码不一致");

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
