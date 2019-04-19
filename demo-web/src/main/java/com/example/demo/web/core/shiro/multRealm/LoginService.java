package com.example.demo.web.core.shiro.multRealm;

import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.service.sys.ISysUserService;
import com.example.demo.core.constant.SessionConstant;
import com.example.demo.core.constant.enums.DataStatus;
import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.core.exception.enums.CoreExceptionEnum;
import com.example.demo.core.exception.factory.ServiceExceptionFactory;
import com.example.demo.core.util.AssertUtil;
import com.example.demo.web.core.exception.InvalidKaptchaException;
import com.example.demo.web.core.shiro.model.ShiroUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private ISysUserService sysUserService;

    //表单input name
    protected String loginTypeParam = LoginUtils.loginTypeParam;
    protected String usernameParam = LoginUtils.usernameParam;
    protected String passwordParam = LoginUtils.passwordParam;
    protected String mobileParam = LoginUtils.mobileParam;
    protected String verifyCodeParam = LoginUtils.verifyCodeParam;
    protected String weixinCodeParam = LoginUtils.weixinCodeParam;
    protected String rememberMeParam = LoginUtils.rememberMeParam;

    public SimpleAuthenticationInfo getAuthenticationInfo(String principal, LoginType loginType, String realmName, MyUsernamePasswordToken token) {
        if (loginType == LoginType.USERNAME_PASSWORD) {
            SysUser sysUser = sysUserService.getByAccount(principal);
            if (sysUser == null) {
                throw new UnknownAccountException("login.user.not.exist");
            }
            if (DataStatus.DISABLE.getCode() == sysUser.getStatus()) {
                throw new LockedAccountException("login.user.locked");
            }

            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(createShiroUser(sysUser),
                    sysUser.getPassword(),
                    new Md5Hash(sysUser.getSalt()), realmName);
            return authenticationInfo;
        } else if (loginType == LoginType.MOBILE_VERIFY_CODE) {
            throw new UnsupportedTokenException("mobile verify code not supported");
        } else if (loginType == LoginType.WEIXIN_OPENID) {
            throw new UnsupportedTokenException("weixin openId not supported");
        }
        throw new UnknownAccountException("login.user.not.exist");
    }

    public AuthorizationInfo getAuthorizationInfo(String principal) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //加入角色
        authorizationInfo.setRoles(Collections.EMPTY_SET);
        // 设置功能权限
        authorizationInfo.setStringPermissions(Collections.EMPTY_SET);
        return authorizationInfo;
    }

    public MyUsernamePasswordToken createToken(HttpServletRequest request, HttpServletResponse response) {
        boolean rememberMe = WebUtils.isTrue(request, this.rememberMeParam);
        String host = request.getRemoteHost();
        String loginTypeParam = StringUtils.upperCase(WebUtils.getCleanParam(request, this.loginTypeParam));
        String principal = null;
        String credentials = null;
        LoginType loginType = LoginType.USERNAME_PASSWORD;
        if (StringUtils.isBlank(loginTypeParam) || loginTypeParam.equals(LoginType.USERNAME_PASSWORD.name())) {
            loginType = LoginType.USERNAME_PASSWORD;
            principal = WebUtils.getCleanParam(request, this.usernameParam);
            credentials = WebUtils.getCleanParam(request, this.passwordParam);
        } else if (loginTypeParam.equals(LoginType.MOBILE_VERIFY_CODE.name())) {
            loginType = LoginType.MOBILE_VERIFY_CODE;
            principal = WebUtils.getCleanParam(request, this.mobileParam);
            credentials = WebUtils.getCleanParam(request, this.verifyCodeParam);
        } else if (loginTypeParam.equals(LoginType.WEIXIN_OPENID.name())) {
            loginType = LoginType.WEIXIN_OPENID;
            principal = WebUtils.getCleanParam(request, this.weixinCodeParam);
        }
        return this.createToken(rememberMe, host, principal, credentials, loginType, request);
    }

    /**
     * 创建token
     *
     * @param rememberMe
     * @param host
     * @param principal
     * @param credentials
     * @param loginType
     * @param request
     * @return
     */
    public MyUsernamePasswordToken createToken(boolean rememberMe, String host, String principal, String credentials, LoginType loginType, HttpServletRequest request) {
        if (loginType == LoginType.USERNAME_PASSWORD) {
            AssertUtil.isTrue(StringUtils.isNotBlank(principal) && StringUtils.isNotBlank(credentials), "sys.getToken.username.password.isnull");
        } else if (loginType == LoginType.MOBILE_VERIFY_CODE) {
            AssertUtil.isTrue(StringUtils.isNotBlank(principal) && StringUtils.isNotBlank(credentials), "sys.getToken.mobile.verifyCode.isnull");
        } else if (loginType == LoginType.WEIXIN_OPENID) {
            AssertUtil.isTrue(StringUtils.isNotBlank(principal), "sys.getToken.weixinCode.isnull");
        }
        MyUsernamePasswordToken token = new MyUsernamePasswordToken();
        token.setHost(host);
        token.setRememberMe(rememberMe);
        token.setPrincipal(principal);
        token.setCredentials(credentials);
        token.setLoginType(loginType);
        //return this.decorateToken(token, request);
        return token;
    }

    public void assertCredentialsMatch(AbstractAuthorizingRealm realm, MyUsernamePasswordToken token, SimpleAuthenticationInfo info) {
        LoginType loginType = token.getLoginType();
        if (loginType == LoginType.USERNAME_PASSWORD) {
            throw ServiceExceptionFactory.createException(CoreExceptionEnum.LOGIN_ERROR.getCode(), "not supported, please use RetryLimitHashedCredentialsMatcher");
        } else if (loginType == LoginType.MOBILE_VERIFY_CODE) {
            Cache<String, AtomicInteger> passwordRetryCache = realm.getCacheManager().getCache("passwordRetryCache");
            String retryCountKey = token.getPrincipal() + "_retryCount";
            LoginUtils.incrRetryCount(passwordRetryCache, retryCountKey, SessionConstant.LOGIN_RETRY_LIMIT);
            //手机号和区号都匹配才行
            if (token.getCredentials().equalsIgnoreCase(info.getCredentials().toString())) {
                LoginUtils.removeRetryCount(passwordRetryCache, retryCountKey);
            } else {
                log.warn("短信验证码不匹配 手机号={},真实验证码={}，用户输入验证码={}", token.getPrincipal(), info.getCredentials(), token.getCredentials());
                throw new InvalidKaptchaException();
            }
        } else if (loginType == LoginType.WEIXIN_OPENID) {
            if (StringUtils.isBlank((String) info.getCredentials())) {
                throw ServiceExceptionFactory.createException(CoreExceptionEnum.LOGIN_ERROR);
            }
        }
    }

    /**
     * 通过用户表的信息创建一个shiroUser对象
     */
    public static ShiroUser createShiroUser(SysUser user) {
        ShiroUser shiroUser = new ShiroUser();
        if (user == null) {
            return shiroUser;
        }
        shiroUser.setUserId(user.getUserId());
        shiroUser.setAccount(user.getAccount());
        shiroUser.setName(user.getName());
        shiroUser.setAvatar(user.getAvatar());
        return shiroUser;
    }
}
