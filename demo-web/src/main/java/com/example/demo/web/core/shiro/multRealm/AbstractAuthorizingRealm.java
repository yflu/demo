package com.example.demo.web.core.shiro.multRealm;

import com.example.demo.core.constant.enums.LoginType;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractAuthorizingRealm extends AuthorizingRealm implements BeanNameAware {

    protected String realmName;

    @Autowired
    protected LoginService loginService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return this.loginService.getAuthorizationInfo((String) principals.getPrimaryPrincipal());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        MyUsernamePasswordToken t = (MyUsernamePasswordToken) token;
        return this.loginService.getAuthenticationInfo(t.getPrincipal(), t.getLoginType(), this.realmName, t);
    }

    @Override
    public void setBeanName(String name) {
        this.realmName = name;
    }

    /**
     * 清空authentication info，在登录成功后清除
     *
     * @param token
     */
    public void clearCachedAuthenticationInfo(MyUsernamePasswordToken token) {
        super.clearCachedAuthenticationInfo(new SimplePrincipalCollection(token.getPrincipal(), this.realmName));
    }

    public void clearCachedAuthenticationInfo(String principal) {
        super.clearCachedAuthenticationInfo(new SimplePrincipalCollection(principal, this.realmName));
    }

    public void clearCacheAuthorizationInfo(String principal) {
        super.clearCachedAuthorizationInfo(new SimplePrincipalCollection(principal, this.realmName));
    }

    @Override
    public abstract boolean supports(AuthenticationToken token);

    @Override
    protected Object getAuthenticationCacheKey(AuthenticationToken token) {
        StringBuilder sb = new StringBuilder(32);
        sb.append("authentication:").append(this.getLoginType().name()).append(":").append(token.getPrincipal().toString());
        return sb.toString();
    }

    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        StringBuilder sb = new StringBuilder(32);
        sb.append("authorization:").append(this.getLoginType().name()).append(":").append(principals.getPrimaryPrincipal().toString());
        return sb.toString();
    }

    @Override
    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
        StringBuilder sb = new StringBuilder(32);
        sb.append("authentication:").append(this.getLoginType().name()).append(":").append(principals.getPrimaryPrincipal().toString());
        return sb.toString();
    }

    public abstract LoginType getLoginType();
}
