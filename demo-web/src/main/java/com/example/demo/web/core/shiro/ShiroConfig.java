package com.example.demo.web.core.shiro;

import com.example.demo.web.core.properties.SysProperties;
import com.example.demo.web.core.shiro.filter.MyFormAuthenticationFilter;
import com.example.demo.web.core.shiro.filter.TokenAuthenticationFilter;
import com.example.demo.web.core.shiro.filter.WebUserFilter;
import com.example.demo.web.core.shiro.multRealm.AbstractAuthorizingRealm;
import com.example.demo.web.core.shiro.multRealm.MyModularRealmAuthenticator;
import com.example.demo.web.core.shiro.multRealm.MyModularRealmAuthorizer;
import com.example.demo.web.core.shiro.multRealm.realms.MobileVerifyCodeRealm;
import com.example.demo.web.core.shiro.multRealm.realms.UsernamePasswordRealm;
import com.example.demo.web.core.shiro.multRealm.realms.WeixinOpenIdRealm;
import com.example.demo.web.core.shiro.redis.RedisSessionDao;
import com.example.demo.web.core.shiro.util.ShiroKit;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.*;

import static com.example.demo.web.core.common.Const.NONE_PERMISSION_RES;

/**
 * shiro权限管理的配置
 */
@Configuration
public class ShiroConfig {

    @Bean
    public RedisSessionDao getRedisSessionDAO() {
        return new RedisSessionDao();
    }

    /**
     * 项目自定义的Realm
     */
    @Bean("userRealm")
    public UserRealm userRealm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher matcher) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(matcher);
        return userRealm;
    }

    @Bean("usernamePasswordRealm")
    public AbstractAuthorizingRealm getUsernamePasswordRealm(@Qualifier("hashedCredentialsMatcher") HashedCredentialsMatcher matcher) {
        UsernamePasswordRealm realm = new UsernamePasswordRealm();
        realm.setCredentialsMatcher(matcher);
        return realm;
    }

    @Bean
    public AbstractAuthorizingRealm getWeixinOpenIdRealm() {
        return new WeixinOpenIdRealm();
    }

    @Bean
    public AbstractAuthorizingRealm getMobileVerifyCodeRealm() {
        return new MobileVerifyCodeRealm();
    }

    @Bean
    public ModularRealmAuthenticator getModularRealmAuthenticator() {
        return new MyModularRealmAuthenticator();
    }

    @Bean
    public ModularRealmAuthorizer getModularRealmAuthorizer() {
        return new MyModularRealmAuthorizer();
    }

    /**
     * 安全管理器
     */
    @Bean
    public DefaultWebSecurityManager securityManager(@Qualifier("usernamePasswordRealm") AuthorizingRealm usernamePasswordRealm, CookieRememberMeManager rememberMeManager, SessionManager sessionManager) {
        MyWebSecurityManager securityManager = new MyWebSecurityManager();
        securityManager.setAuthenticator(getModularRealmAuthenticator());
        securityManager.setAuthorizer(getModularRealmAuthorizer());
        List<Realm> realms = new ArrayList<>();
        realms.add(usernamePasswordRealm);
        realms.add(getMobileVerifyCodeRealm());
        realms.add(getWeixinOpenIdRealm());
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(rememberMeManager);
        return securityManager;
    }

    /**
     * 缓存管理器 使用Ehcache实现
     */
    @Bean("shiroEhcacheCacheManager")
    public CacheManager getCacheShiroManager(EhCacheManagerFactoryBean ehcache) {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManager(ehcache.getObject());
        return ehCacheManager;
    }

    /**
     * 密码匹配凭证管理器
     *
     * @return
     */
    @Bean(name = "hashedCredentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher(@Qualifier("shiroRedisCacheManager") CacheManager cacheShiroManager) {
        RetryLimitHashedCredentialsMatcher md5CredentialsMatcher = new RetryLimitHashedCredentialsMatcher(cacheShiroManager);
        md5CredentialsMatcher.setHashAlgorithmName(ShiroKit.hashAlgorithmName);
        md5CredentialsMatcher.setHashIterations(ShiroKit.hashIterations);
        return md5CredentialsMatcher;
    }

    /**
     * rememberMe管理器, cipherKey生成见{@code Base64Test.java}
     */
    @Bean
    public CookieRememberMeManager rememberMeManager(SimpleCookie rememberMeCookie) {
        CookieRememberMeManager manager = new CookieRememberMeManager();
        manager.setCipherKey(Base64.decode("Z3VucwAAAAAAAAAAAAAAAA=="));
        manager.setCookie(rememberMeCookie);
        return manager;
    }

    /**
     * 记住密码Cookie
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(7 * 24 * 60 * 60);//7天
        return simpleCookie;
    }

    /**
     * session管理器
     *
     * @return
     */
    @Bean
    public SessionManager sessionManager(@Qualifier("shiroRedisCacheManager") CacheManager cacheManager, SysProperties sysProperties) {
        ShiroSessionManager sessionManager = new ShiroSessionManager();
        sessionManager.setCacheManager(cacheManager);
        sessionManager.setSessionDAO(getRedisSessionDAO());
        //验证是否过期
        sessionManager.setSessionValidationInterval(sysProperties.getSessionValidationInterval() * 1000);
        //超时时间
        sessionManager.setGlobalSessionTimeout(sysProperties.getSessionInvalidateTime() * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        //去掉URL中的JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        //cookie设置
        Cookie cookie = new SimpleCookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
        cookie.setName("shiroCookie");
        cookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(cookie);
        return sessionManager;
    }

    @Bean
    public AuthenticatingFilter getFormAuthenticationFilter() {
        return new MyFormAuthenticationFilter();
    }

    @Bean
    public AuthenticatingFilter getTokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    /**
     * Shiro的过滤器链
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SessionsSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        /**
         * 默认的登陆访问url
         */
        shiroFilter.setLoginUrl("/login");
        /**
         * 登陆成功后跳转的url
         */
        shiroFilter.setSuccessUrl("/");
        /**
         * 没有权限跳转的url
         */
        shiroFilter.setUnauthorizedUrl("/global/error/403");

        /**
         * 覆盖默认的user拦截器(默认拦截器解决不了ajax请求 session超时的问题)
         */
        HashMap<String, Filter> myFilters = new HashMap<>();
        myFilters.put("user", new WebUserFilter());
        myFilters.put("authc", getFormAuthenticationFilter());
        myFilters.put("token", getTokenAuthenticationFilter());
        shiroFilter.setFilters(myFilters);

        /**
         * 配置shiro拦截器链
         *
         * anon  不需要认证
         * authc 需要认证
         * user  验证通过或RememberMe登录的都可以
         *
         * 当应用开启了rememberMe时,用户下次访问时可以是一个user,但不会是authc,因为authc是需要重新认证的
         *
         * 顺序从上到下,优先级依次降低
         *
         * api开头的接口，走rest api鉴权，不走shiro鉴权
         *
         */
        Map<String, String> hashMap = new LinkedHashMap<>();
        for (String nonePermissionRe : NONE_PERMISSION_RES) {
            hashMap.put(nonePermissionRe, "anon");
        }

        //配置退出过滤器
        hashMap.put("/logout", "logout");
        hashMap.put("/token/**", "token");
        hashMap.put("/login", "authc");
        hashMap.put("/**", "user");
        shiroFilter.setFilterChainDefinitionMap(hashMap);
        return shiroFilter;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }

    /**
     * 在方法中 注入 securityManager,进行代理控制
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(SessionsSecurityManager securityManager) {
        MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
        bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        bean.setArguments(securityManager);
        return bean;
    }

    /**
     * Shiro生命周期处理器:
     * 用于在实现了Initializable接口的Shiro bean初始化时调用Initializable接口回调(例如:UserRealm)
     * 在实现了Destroyable接口的Shiro bean销毁时调用 Destroyable接口回调(例如:DefaultSecurityManager)
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 启用shiro授权注解拦截方式，AOP式方法级权限检查
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SessionsSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
