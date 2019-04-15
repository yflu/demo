package com.example.demo.core.common.model.response;

import com.example.demo.core.exception.enums.CoreExceptionEnum;

public class SuccessResponseData extends ResponseData {

    public SuccessResponseData() {
        super(true, CoreExceptionEnum.SUCCESS.getCode(), CoreExceptionEnum.SUCCESS.getMessage(), null);
    }

    public SuccessResponseData(Object object) {
        super(true, CoreExceptionEnum.SUCCESS.getCode(), CoreExceptionEnum.SUCCESS.getMessage(), object);
    }

    public SuccessResponseData(Integer code, String message, Object object) {
        super(true, code, message, object);
    }
}
