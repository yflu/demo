package com.example.demo.web.core.shiro.multRealm.realms;

import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.web.core.shiro.multRealm.AbstractAuthorizingRealm;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;

/**
 * @author cheguangai
 * @date 2018/10/28 0028
 */
public class WeixinOpenIdRealm extends AbstractAuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        MyUsernamePasswordToken t = (MyUsernamePasswordToken) token;
        return t.getLoginType() == LoginType.WEIXIN_OPENID;
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.WEIXIN_OPENID;
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        this.loginService.assertCredentialsMatch(this, (MyUsernamePasswordToken) token, (SimpleAuthenticationInfo) info);
    }
}
