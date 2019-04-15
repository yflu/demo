package com.example.demo.web.core.shiro.multRealm;

import org.apache.shiro.authc.UsernamePasswordToken;

public class CustomizedToken extends UsernamePasswordToken {

    //登录类型
    private String loginType;

    public CustomizedToken(final String username, final String password, String loginType) {
        super(username, password);
        this.loginType = loginType;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
