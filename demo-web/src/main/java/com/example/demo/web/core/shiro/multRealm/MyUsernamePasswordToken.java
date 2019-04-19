package com.example.demo.web.core.shiro.multRealm;

import com.example.demo.core.constant.enums.LoginType;
import org.apache.shiro.authc.UsernamePasswordToken;

public class MyUsernamePasswordToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名、手机号、weixinCode
     */
    private String principal;
    /**
     * 密码、手机验证码、weixin openId
     */
    private String credentials;
    /**
     * 验证码
     */
    private String captcha;
    /**
     * 登录方式
     */
    private LoginType loginType;

    public MyUsernamePasswordToken() {
    }

    public MyUsernamePasswordToken(String principal, String credentials, boolean rememberMe, String host, LoginType loginType) {
        super(principal, credentials, rememberMe, host);
        this.loginType = loginType;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }
}
