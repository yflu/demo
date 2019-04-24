package com.example.demo.web.core.shiro;

import com.example.demo.core.util.HttpContext;
import com.example.demo.web.core.shiro.multRealm.LoginService;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MyWebSecurityManager extends DefaultWebSecurityManager {

    @Autowired
    private LoginService loginService;

    @Override
    protected void onSuccessfulLogin(AuthenticationToken token, AuthenticationInfo info, Subject subject) {
        log.warn("session onStart {}, {}", token.getPrincipal(), subject.getSession(false).getId());
        super.onSuccessfulLogin(token, info, subject);
        this.loginService.afterLoginSuccess((MyUsernamePasswordToken) token, subject, HttpContext.getRequest(), HttpContext.getResponse());
        //放在loginService.afterLoginSuccess后面，有可能在里面改principal
        //SessionUtil.saveSession(String.valueOf(subject.getPrincipal()), String.valueOf(subject.getSession(false).getId()));
    }

    @Override
    public Subject login(Subject subject, AuthenticationToken token) throws AuthenticationException {
        this.loginService.beforeLogin((MyUsernamePasswordToken) token, HttpContext.getRequest());
        return super.login(subject, token);
    }

    @Override
    protected void onFailedLogin(AuthenticationToken token, AuthenticationException ae, Subject subject) {
        super.onFailedLogin(token, ae, subject);
        this.loginService.afterLoginFailure((MyUsernamePasswordToken) token, ae, HttpContext.getRequest(), HttpContext.getResponse());
    }

    @Override
    protected void beforeLogout(Subject subject) {
        super.beforeLogout(subject);
        this.loginService.beforeLogout(subject, HttpContext.getRequest(), HttpContext.getResponse());
    }
}
