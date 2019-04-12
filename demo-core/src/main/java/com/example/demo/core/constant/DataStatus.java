package com.example.demo.core.constant;

/**
 * 数据状态
 */
public enum DataStatus {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用");

    Integer code;
    String message;

    DataStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getDescription(String status) {
        if (status == null) {
            return "";
        } else {
            for (DataStatus s : DataStatus.values()) {
                if (s.getCode().equals(status)) {
                    return s.getMessage();
                }
            }
            return "";
        }
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
