package com.example.demo.core.exception.enums;

import com.example.demo.core.exception.AbstractBaseExceptionEnum;

public enum CoreExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 基础
     */
    SUCCESS(0, "请求成功"),
    SERVER_ERROR(1, "未知错误"),

    REQUEST_REPEAT(100, "重复请求"),
    REQUEST_PARAM_ERROR(101, "请求参数异常"),

    DATA_NOT_EXIST(110, "数据不存在"),
    DATA_ALREADY_EXIST(111, "数据已存在"),
    ASYNC_ERROR(112, "数据在被别人修改，请稍后重试"),

    /**
     * 鉴权
     */
    TOKEN_ERROR(200, "无效的token"),
    TOKEN_EXPIRED(201, "token过期"),
    SIGN_ERROR(202, "无效签名"),

    /**
     * 权限
     */
    AUTH_ERROR(300, "账号密码错误"),
    NO_CURRENT_USER(301, "当前没有登录的用户"),
    NO_PERMITION(302, "权限不足"),

    /**
     * 文件上传
     */
    UPLOAD_ERROR(400, "上传出错"),
    FILE_BIG(401, "文件大小超出上限"),
    FILE_READING_ERROR(403, "FILE_READING_ERROR!"),
    FILE_NOT_FOUND(404, "FILE_NOT_FOUND!"),

    /**
     * 其他
     */
    WRITE_ERROR(500, "渲染界面错误"),
    ENCRYPT_ERROR(501, "加解密错误");

    private Integer code;
    private String message;

    private CoreExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
