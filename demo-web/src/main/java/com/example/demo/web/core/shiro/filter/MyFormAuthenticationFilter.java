package com.example.demo.web.core.shiro.filter;

import com.example.demo.web.core.shiro.multRealm.LoginService;
import com.example.demo.web.core.shiro.multRealm.LoginUtils;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFormAuthenticationFilter extends FormAuthenticationFilter {

    public static final String EXCEPTION_KEY_NAME = "__shiroLoginException___";

    @Autowired
    private LoginService loginService;

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        MyUsernamePasswordToken token = this.loginService.createToken((HttpServletRequest) request, (HttpServletResponse) response);
        LoginUtils.storeLoginToken(token);
        return token;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                     ServletRequest request, ServletResponse response) {
        request.setAttribute(EXCEPTION_KEY_NAME, e);
        return super.onLoginFailure(token, e, request, response);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        return super.onLoginSuccess(token, subject, request, response);
    }
}
