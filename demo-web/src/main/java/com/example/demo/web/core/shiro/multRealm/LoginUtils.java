package com.example.demo.web.core.shiro.multRealm;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.cache.Cache;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoginUtils {

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
}
