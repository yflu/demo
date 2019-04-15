package com.example.demo.core.util;

import com.example.demo.core.constant.SessionConstant;
import com.example.demo.core.constant.enums.LoginType;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;

public class SessionUtil {

    static public void put(String key, Object value) {
        SecurityUtils.getSubject().getSession().setAttribute(key, value);
    }

    static public Object get(String key) {
        return SecurityUtils.getSubject().getSession().getAttribute(key);
    }

    public static LoginType getLoginType() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            return null;
        }
        String loginType = (String) get(SessionConstant.SESSION_KEY_LOGIN_TYPE);
        if (StringUtils.isBlank(loginType)) {
            return null;
        }
        return LoginType.valueOf(loginType);
    }
}
