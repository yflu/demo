package com.example.demo.web.core.shiro.multRealm.realms;

import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.web.core.shiro.multRealm.AbstractAuthorizingRealm;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;

/**
 * 手机验证码认证
 */
public class MobileVerifyCodeRealm extends AbstractAuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        MyUsernamePasswordToken t = (MyUsernamePasswordToken) token;
        return t.getLoginType() == LoginType.MOBILE_VERIFY_CODE;
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.MOBILE_VERIFY_CODE;
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        this.loginService.assertCredentialsMatch(this, (MyUsernamePasswordToken) token, (SimpleAuthenticationInfo) info);
    }
}
