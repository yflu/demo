package com.example.demo.web.core.shiro.multRealm.realms;

import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.web.core.shiro.multRealm.AbstractAuthorizingRealm;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 账号密码认证
 */
public class UsernamePasswordRealm extends AbstractAuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        MyUsernamePasswordToken t = (MyUsernamePasswordToken) token;
        return t.getLoginType() == LoginType.USERNAME_PASSWORD;
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.USERNAME_PASSWORD;
    }
}
