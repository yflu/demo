package com.example.demo.core.common.model.response;

import com.example.demo.core.exception.enums.CoreExceptionEnum;
import lombok.Data;

@Data
public class ErrorResponseData extends ResponseData {

    public ErrorResponseData() {
        super(false, CoreExceptionEnum.SERVER_ERROR.getCode(), CoreExceptionEnum.SERVER_ERROR.getMessage(), null);
    }

    public ErrorResponseData(String message) {
        super(false, CoreExceptionEnum.SERVER_ERROR.getCode(), message, null);
    }

    public ErrorResponseData(Integer code, String message) {
        super(false, code, message, null);
    }

    public ErrorResponseData(Integer code, String message, Object object) {
        super(false, code, message, object);
    }
}
