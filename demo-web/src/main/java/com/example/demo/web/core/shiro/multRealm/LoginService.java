package com.example.demo.web.core.shiro.multRealm;

import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.service.sys.ISysUserService;
import com.example.demo.core.constant.SessionConstant;
import com.example.demo.core.constant.enums.DataStatus;
import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.core.exception.enums.CoreExceptionEnum;
import com.example.demo.core.exception.factory.ServiceExceptionFactory;
import com.example.demo.core.util.AssertUtil;
import com.example.demo.core.util.SpringContextHolder;
import com.example.demo.web.core.exception.InvalidKaptchaException;
import com.example.demo.web.core.log.LogManager;
import com.example.demo.web.core.log.factory.LogTaskFactory;
import com.example.demo.web.core.shiro.model.ShiroUser;
import com.example.demo.web.core.shiro.util.ShiroKit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.demo.core.util.HttpContext.getIp;


@Slf4j
@Data
@Service("loginService")
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
            SysUser sysUser = this.getUser(LoginUtils.getLoginTokenFromRequest());
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
     * 登录前做事情
     * @param token
     * @param request
     */
    public final void beforeLogin(MyUsernamePasswordToken token, HttpServletRequest request){
        log.info("prepare to login {}", token.getPrincipal());
        this.doBeforeLogin(token, request);
    }

    protected void doBeforeLogin(MyUsernamePasswordToken token, HttpServletRequest request){

    }

    /**
     * 登录失败后做的事情，记录日志、删除相关数据
     * @param token
     * @param e
     * @param request
     * @param response
     */
    public final void afterLoginFailure(MyUsernamePasswordToken token, AuthenticationException e, HttpServletRequest request, HttpServletResponse response){
       log.error("login failure: {}", e.getMessage());
        log.info("clear authenticationInfo cache for {}", token.getPrincipal());
        doAfterLoginFailure(token, e, request, response);
    }

    protected void doAfterLoginFailure(MyUsernamePasswordToken token, AuthenticationException e, HttpServletRequest request, HttpServletResponse response){

    }

    /**
     * 登录成功后做的事情，可用于记录日志、加载初始化数据
     * 默认删除authenticationInfo cache，因为后面用不到了
     * 提前加载权限信息，因为
     *
     * @param token
     * @param subject  登录成功的subject，注意这个时候SecurityUtil.getSubject()还是未登录状态
     * @param request
     * @param response
     */
    public final void afterLoginSuccess(MyUsernamePasswordToken token, Subject subject, HttpServletRequest request, HttpServletResponse response) {
        log.info("login success {}", token.getPrincipal());
        log.info("clear authenticationInfo cache for {}", token.getPrincipal());
        Realm realm = this.getCandidateRealm(token);
        //登录成功后尝试情况 authenticationInfoCache & authorizationInfoCache
        if (realm instanceof AbstractAuthorizingRealm) {
            ((AbstractAuthorizingRealm) realm).clearCachedAuthenticationInfo(token);
            ((AbstractAuthorizingRealm) realm).clearCacheAuthorizationInfo(token.getPrincipal());
        }
        //如果是手机号+验证码登录，清空验证码
        if (token.getLoginType() == LoginType.MOBILE_VERIFY_CODE) {
            //SessionUtil.removeMobileVerifyCode(token.getPrincipal());
        }
        //登录方式存储到session中
        subject.getSession().setAttribute(SessionConstant.SESSION_KEY_LOGIN_TYPE, token.getLoginType().name());

        //token改写成真正的用户名，不然后面获取权限都拿不到
        this.convertToSysUser(token, subject);
        if (!token.getPrincipal().equals(subject.getPrincipal())) {
            log.info("{} runAs {}", token.getPrincipal(), subject.getPrincipal());
        }

        doAfterLoginSuccess(token, subject, request, response);
    }

    /**
     * @param token
     * @param subject  不能用SecurityUtils.getSubject()
     * @param request
     * @param response
     */
    protected void doAfterLoginSuccess(MyUsernamePasswordToken token, Subject subject, HttpServletRequest request, HttpServletResponse response) {
        ShiroUser shiroUser = (ShiroUser) subject.getPrincipals().getPrimaryPrincipal();
        LogManager.getInstance().executeLog(LogTaskFactory.loginLog(shiroUser.getUserId(), getIp()));
        ShiroKit.getSession().setAttribute("sessionFlag", true);
    }


    public final void beforeLogout(Subject subject, HttpServletRequest request, HttpServletResponse response){
        if(subject.isAuthenticated()){
            log.info("{} prepare to logout", subject.getPrincipal().toString());
            this.doBeforeLogout(subject, request, response);
        }
    }

    protected void doBeforeLogout(Subject subject, HttpServletRequest request, HttpServletResponse response){

    }

    public final void afterLogout(HttpServletRequest request, HttpServletResponse response){
        log.info("after logout");
        this.doAfterLogout(request, response);
    }

    protected void doAfterLogout(HttpServletRequest request, HttpServletResponse response){

    }


    private Realm getCandidateRealm(MyUsernamePasswordToken token) {
        RealmSecurityManager realmSecurityManager = SpringContextHolder.getBean(RealmSecurityManager.class);
        for (Realm realm : realmSecurityManager.getRealms()) {
            if (realm.supports(token)) {
                return realm;
            }
        }
        return null;
    }

    /**
     * 手机号+验证码、微信登录方式转换成系统username
     * 调用subject.runAs()
     * <p>
     * 注意：这里
     *
     * @param token
     * @param subject 不能用SecurityUtils.getSubject()
     */
    protected void convertToSysUser(MyUsernamePasswordToken token, Subject subject) {

    }


    public SysUser getUser(MyUsernamePasswordToken token) {
        LoginType loginType = token.getLoginType();
        if (loginType == LoginType.USERNAME_PASSWORD) {
            return sysUserService.getByAccount(token.getPrincipal());
        } else {
            //小商不支持手机、微信登录
            throw new AuthenticationException("not supported");
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
