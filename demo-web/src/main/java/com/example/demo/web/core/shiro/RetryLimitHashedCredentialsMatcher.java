package com.example.demo.web.core.shiro;

import com.example.demo.core.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录失败次数太多,默认冻结30分钟
 */
@Slf4j
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        incrRetryCount(passwordRetryCache, username, SessionConstant.LOGIN_RETRY_LIMIT);
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            passwordRetryCache.remove(username);
        }
        return matches;
    }

    /**
     * 尝试次数+1，超过retryLimit 抛异常
     *
     * @param retryCache
     * @param retryCountKey
     */
    private void incrRetryCount(Cache<String, AtomicInteger> retryCache, String retryCountKey, int retryLimit) {
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
}
