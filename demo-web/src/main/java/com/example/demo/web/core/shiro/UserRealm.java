package com.example.demo.web.core.shiro;

import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.service.sys.ISysUserService;
import com.example.demo.core.constant.enums.DataStatus;
import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.core.util.SpringContextHolder;
import com.example.demo.web.core.shiro.model.ShiroUser;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import com.example.demo.web.core.shiro.util.ShiroKit;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;

public class UserRealm extends AuthorizingRealm {

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        SysUser user = SpringContextHolder.getBean(ISysUserService.class).getByAccount(token.getUsername());
        if (null == user) {
            throw new UnknownAccountException();
        }
        if (DataStatus.DISABLE.getCode() == user.getStatus()) {
            throw new LockedAccountException();
        }
        ShiroUser shiroUser = ShiroKit.createShiroUser(user);
        return getAuthInfo(shiroUser, user, super.getName());
    }

    /**
     * 权限认证
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();

        Set<String> permissionSet = new HashSet<>();
        Set<String> roleNameSet = new HashSet<>();

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermissions(permissionSet);
        info.addRoles(roleNameSet);
        return info;
    }

    /**
     * 获取shiro的认证信息
     */
    public SimpleAuthenticationInfo getAuthInfo(ShiroUser shiroUser, SysUser user, String realmName) {
        String credentials = user.getPassword();
        // 密码加盐处理
        String source = user.getSalt();
        ByteSource credentialsSalt = new Md5Hash(source);
        return new SimpleAuthenticationInfo(shiroUser, credentials, credentialsSalt, realmName);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof MyUsernamePasswordToken) {
            MyUsernamePasswordToken usernamePasswordToken = (MyUsernamePasswordToken) token;
            return LoginType.USERNAME_PASSWORD == usernamePasswordToken.getLoginType();
        }
        return false;
    }

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        /*SimpleAuthenticationInfo simpleAuthenticationInfo = (SimpleAuthenticationInfo) info;
        throw new LockedAccountException();*/
        super.assertCredentialsMatch(token, info);
    }
}
