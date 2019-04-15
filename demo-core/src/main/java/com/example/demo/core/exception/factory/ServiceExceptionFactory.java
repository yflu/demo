package com.example.demo.core.exception.factory;

import com.example.demo.core.exception.AbstractBaseExceptionEnum;
import com.example.demo.core.exception.ServiceException;

/**
 * 异常工厂
 */
public class ServiceExceptionFactory {

    /**
     * 根据枚举生成异常信息
     *
     * @param e
     * @return
     */
    public static ServiceException createException(AbstractBaseExceptionEnum e) {
        return new ServiceException(e.getCode(), e.getMessage());
    }

    /**
     * 生成异常信息
     *
     * @param code
     * @param msg
     * @return
     */
    public static ServiceException createException(int code, String msg) {
        return new ServiceException(code, msg);
    }
}
