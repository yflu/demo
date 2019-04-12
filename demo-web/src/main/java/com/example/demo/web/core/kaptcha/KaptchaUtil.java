package com.example.demo.web.core.kaptcha;

import com.example.demo.core.util.SpringContextHolder;
import com.example.demo.web.core.properties.SysProperties;

/**
 * 验证码工具类
 */
public class KaptchaUtil {

    /**
     * 获取验证码开关
     */
    public static Boolean getKaptchaOnOff() {
        return SpringContextHolder.getBean(SysProperties.class).getKaptchaOpen();
    }
}