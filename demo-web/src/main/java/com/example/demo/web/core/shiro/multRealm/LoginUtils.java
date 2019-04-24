package com.example.demo.web.core.shiro.multRealm;

import com.example.demo.core.util.HttpContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoginUtils {

    //暂存的request body
    public static final String Key_Stored_Login_Request_Body = "_REQUEST_BODY_BY_SHIRO_ACCESSTOKENFILTER_";
    public static final String Key_Stored_Login_Token = "__LOGIN_TOKEN___";
    public static final String Key_Stored_Login_Exception = "__LOGIN_EXCEPTION___";

    public static final String loginTypeParam = "loginType";
    public static final String usernameParam = "account";
    public static final String passwordParam = "password";
    public static final String mobileParam = "mobile";
    public static final String verifyCodeParam = "verifyCode";
    public static final String weixinCodeParam = "weixinCode";
    public static final String rememberMeParam = "rememberMe";

    /**
     * 尝试次数+1，超过retryLimit 抛异常
     *
     * @param retryCache
     * @param retryCountKey
     */
    public static void incrRetryCount(Cache<String, AtomicInteger> retryCache, String retryCountKey, int retryLimit) {
        AtomicInteger retryCount = retryCache.get(retryCountKey);
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
        }
        if (retryCount.incrementAndGet() > retryLimit) {
            log.warn("username: " + retryCountKey + " tried to login more than " + retryLimit + " times in period");
            throw new ExcessiveAttemptsException("username: " + retryCountKey + " tried to login more than " + retryLimit + " times in period");
        }
        retryCache.put(retryCountKey, retryCount);
    }

    /**
     * 去掉重试缓存
     *
     * @param retryCache
     * @param retryCountKey
     */
    public static void removeRetryCount(Cache<String, AtomicInteger> retryCache, String retryCountKey) {
        retryCache.remove(retryCountKey);
    }

    /**
     * 存储请求json到request参数中，因为有的登录请求是用application/json方式传参，request中是没有parameter的
     * @param request
     * @param requestBody
     */
    public static void storeRequestBody(HttpServletRequest request, String requestBody){
        if(request == null || StringUtils.isBlank(requestBody)){
            return;
        }
        request.setAttribute(LoginUtils.Key_Stored_Login_Request_Body, requestBody);
    }

    /**
     * 获取token 的请求是application/json， 且input stream 已经被TokenAuthenticationFilter 消费掉了，同时放到request attribute 中了，这里从attribute 中读取
     * @param request
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getLoginRequestBody(HttpServletRequest request, Class<T> cls){
        if(request == null){
            return null;
        }
        String requestBody = (String)request.getAttribute(LoginUtils.Key_Stored_Login_Request_Body);
        if(StringUtils.isBlank(requestBody)){
            return null;
        }
        if(cls == null){
            return (T) requestBody;
        }
        /*if(ReflectionUtil.isSimpleType(cls)){
            return ConverterUtil.getConversionService().convert(requestBody, cls);
        }
        return JSON.parseObject(requestBody, cls);*/
        return null;
    }

    /**
     * 将token设置进request
     *
     * @param token
     */
    public static void storeLoginToken(MyUsernamePasswordToken token) {
        HttpServletRequest request = HttpContext.getRequest();
        if (request == null) {
            return;
        }
        request.setAttribute(Key_Stored_Login_Token, token);
    }

    /**
     * 从request中获取登录token
     *
     * @return
     */
    public static MyUsernamePasswordToken getLoginTokenFromRequest() {
        HttpServletRequest request = HttpContext.getRequest();
        if (request != null) {
            return (MyUsernamePasswordToken) request.getAttribute(Key_Stored_Login_Token);
        }
        return null;
    }

    public static void storeLoginException(Exception e) {
        HttpServletRequest request = HttpContext.getRequest();
        if (request == null) {
            return;
        }
        request.setAttribute(Key_Stored_Login_Exception, e);
    }

    /**
     * 从request中获取登录异常
     *
     * @return
     */
    public static Exception getLoginExceptionFromRequest() {
        HttpServletRequest request = HttpContext.getRequest();
        if (request != null) {
            return (Exception) request.getAttribute(Key_Stored_Login_Exception);
        }
        return null;
    }
}
