package com.example.demo.core.util;

import com.example.demo.core.exception.ServiceException;
import com.example.demo.core.exception.enums.CoreExceptionEnum;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类
 * Created by cheguangai on 2018/4/19 0019.
 */
public class AssertUtil {

    /**
     * 断言不为null
     *
     * @param object
     * @param codeAndArgs
     */
    public static void notNull(Object object, Object... codeAndArgs) {
        if (object == null) {
            throwOperationException(codeAndArgs);
        }
    }

    public static void isNull(Object object, Object... codeAndArgs) {
        if (object != null) {
            throwOperationException(codeAndArgs);
        }
    }

    /**
     * 断言Collection不为空
     *
     * @param collection
     * @param codeAndArgs
     */
    public static void notEmpty(Collection<?> collection, Object... codeAndArgs) {
        if (CollectionUtils.isEmpty(collection)) {
            throwOperationException(codeAndArgs);
        }
    }

    /**
     * 断言Map不为空
     *
     * @param map
     * @param codeAndArgs
     */
    public static void notEmpty(Map<?, ?> map, Object... codeAndArgs) {
        if (MapUtils.isEmpty(map)) {
            throwOperationException(codeAndArgs);
        }
    }

    /**
     * 断言数组不为空
     *
     * @param array
     * @param codeAndArgs
     */
    public static void notEmpty(Object[] array, Object... codeAndArgs) {
        if (ObjectUtils.isEmpty(array)) {
            throwOperationException(codeAndArgs);
        }
    }

    /**
     * 断言字符串不为空（不为null也不为""）
     *
     * @param text
     * @param codeAndArgs
     */
    public static void hasText(String text, Object... codeAndArgs) {
        if (StringUtils.isBlank(text)) {
            throwOperationException(codeAndArgs);
        }
    }

    /**
     * 判断条件b是否为true
     *
     * @param b
     * @param codeAndArgs
     */
    public static void isTrue(Boolean b, Object... codeAndArgs) {
        if (b == null || !b) {
            throwOperationException(codeAndArgs);
        }
    }

    public static void isFalse(Boolean b, Object... codeAndArgs) {
        if (b == null || b) {
            throwOperationException(codeAndArgs);
        }
    }

    /**
     * @param b
     * @param nullAsFalse null 是否看做 false
     * @param codeAndArgs
     */
    public static void isFalse(Boolean b, boolean nullAsFalse, Object... codeAndArgs) {
        if (nullAsFalse && b == null) {
            b = false;
        }
        if (b == null || b) {
            throwOperationException(codeAndArgs);
        }
    }

    public static void isEqual(Object o1, Object o2, Object... codeAndArgs) {
        if (!Objects.equals(o1, o2)) {
            throwOperationException(codeAndArgs);
        }
    }

    private static void throwOperationException(Object... codeAndArgs) {
        if (codeAndArgs.length <= 1) {
            throw new ServiceException(CoreExceptionEnum.SERVER_ERROR.getCode(), CoreExceptionEnum.SERVER_ERROR.getMessage());
        } else {
            throw new ServiceException(CoreExceptionEnum.SERVER_ERROR.getCode(), CoreExceptionEnum.SERVER_ERROR.getMessage());
        }
    }

}
