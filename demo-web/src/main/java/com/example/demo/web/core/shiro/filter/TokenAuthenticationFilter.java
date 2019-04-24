package com.example.demo.web.core.shiro.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.core.constant.enums.LoginType;
import com.example.demo.core.util.AssertUtil;
import com.example.demo.web.core.shiro.multRealm.LoginService;
import com.example.demo.web.core.shiro.multRealm.LoginUtils;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.shiro.authc.*;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenAuthenticationFilter extends AuthenticatingFilter {

    @Autowired
    private LoginService loginService;


    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String jsonPostData = "";
        try {
            jsonPostData = receivePost(request);
            log.debug("jsonPostData = {}", jsonPostData);
        } catch (Exception e) {
            log.error("request :jsonPostData = {}, {}", jsonPostData, getExMsgOrClzName(e), e);
            String msg = "system.token.authentication.request.invalid";//"请求无效"
            sendError(req, resp, 400, "400", msg);
            return null;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(jsonPostData);
        } catch (Exception e) {
            log.error("request: failed get username/password from body: [{}], {}", jsonPostData, getExMsgOrClzName(e), e);
            String msg = "system.token.authentication.request.invalid";//"请求无效"
            sendError(req, resp, 400, "400", msg);
            return null;
        }
        String username = jsonObject.getString(this.loginService.getUsernameParam());
        String password = jsonObject.getString(this.loginService.getPasswordParam());
        String loginTypeValue = jsonObject.getString(this.loginService.getLoginTypeParam());

        LoginType loginType = null;
        MyUsernamePasswordToken token = null;
        if (StringUtils.isBlank(loginTypeValue) || LoginType.USERNAME_PASSWORD.name().equalsIgnoreCase(loginTypeValue)) {
            loginType = LoginType.USERNAME_PASSWORD;
            token = this.loginService.createToken(isRememberMe(request), getHost(request), username, password, loginType, req);
        } else if (LoginType.MOBILE_VERIFY_CODE.name().equalsIgnoreCase(loginTypeValue)) {
            loginType = LoginType.MOBILE_VERIFY_CODE;
            token = this.loginService.createToken(isRememberMe(request), getHost(request), jsonObject.getString(loginService.getMobileParam()), jsonObject.getString(loginService.getVerifyCodeParam()), loginType, req);
        } else if (LoginType.WEIXIN_OPENID.name().equalsIgnoreCase(loginTypeValue)) {
            loginType = LoginType.WEIXIN_OPENID;
            token = this.loginService.createToken(isRememberMe(request), getHost(request), jsonObject.getString(loginService.getWeixinCodeParam()), null, loginType, req);
        }
        AssertUtil.notNull(loginType);
        LoginUtils.storeLoginToken(token);
        return token;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false;
        if (isLoginAttempt(request, response)) {
            try {
                loggedIn = executeLogin(request, response);
            } catch (Exception e) {
                //AuthenticationException不会被捕获的
                log.error("TokenAuthenticationFilter尝试登录发生异常", e);
                request.setAttribute(LoginUtils.Key_Stored_Login_Exception, e);
            }
        }
        if (!loggedIn) {
            sendChallenge((HttpServletRequest) request, (HttpServletResponse) response);
        }
        return loggedIn;
    }

    @Override
    protected final boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        return this.isLoginAttempt(request, response);
    }

    protected String getExMsgOrClzName(Exception e) {
        String msg = e.getMessage();
        return msg == null || msg.trim().isEmpty() ? e.getClass().getCanonicalName() : msg;
    }

    /**
     * 是否登录请求
     *
     * @param request
     * @param response
     * @return
     */
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = WebUtils.toHttp(request);
        String pi = req.getPathInfo();
        if (pi == null) {
            pi = req.getServletPath().toLowerCase();
        }
        //post请求，url中含有/token
        return request instanceof HttpServletRequest && req.getMethod().equalsIgnoreCase(POST_METHOD) && (pi.contains("token"));
    }

    /**
     * 消费input stream并将请求内容放入request
     *
     * @param request
     * @return
     * @throws Exception
     */
    private String receivePost(ServletRequest request) throws Exception {
        StringWriter sw = new StringWriter(256);
        IOUtils.copy(request.getInputStream(), sw, Charset.forName("utf-8"));
        LoginUtils.storeRequestBody((HttpServletRequest) request, sw.toString());
        return sw.toString();
    }

    /**
     * 登录失败，返回信息给前端
     *
     * @param req
     * @param resp
     * @return
     */
    private boolean sendChallenge(HttpServletRequest req, HttpServletResponse resp) {
        Exception exception = (Exception) req.getAttribute(LoginUtils.Key_Stored_Login_Exception);
        if (exception == null) {
            if (this.isLoginRequest(req, resp)) {
                //是登录请求，但是在登录过程中发生了错误，但是没有异常，这种情况不太可能发生
                log.error("this is a failed login attempt but exception is null, this should never happen, why this occur??? req path={}", req.getPathInfo());
                sendError(req, resp, 500, "500", "system.token.authentication.sys.error");
                return false;
            } else {
                //不是登录请求，返回401给前端
                log.error("TokenAuthenticationFilter exception is null, it is not a login attempt, should not send request req path={}", req.getPathInfo());
                sendError(req, resp, 401, "401", "system.token.authentication.sys.error");
            }
            return false;
        }

        //获得底层exception
        Exception rootCause = (Exception) getRootCause(exception);
        Map<String, Object> processMap = this.processException(rootCause, req, resp);
        int httpStatusCode = MapUtils.getInteger(processMap, "httpStatusCode");
        String errorCode = MapUtils.getString(processMap, "errorCode");
        String errorMsg = MapUtils.getString(processMap, "errorMsg");
        log.error("request : failed when login, {}", getExMsgOrClzName(rootCause), rootCause);
        MyUsernamePasswordToken loginTokenFromRequest = LoginUtils.getLoginTokenFromRequest();
        loggerError(rootCause, loginTokenFromRequest == null ? "null" : loginTokenFromRequest.getPrincipal(), WebUtils.getCleanParam(req, "host"), errorMsg, errorCode);
        sendError(req, resp, httpStatusCode, errorCode, errorMsg);
        return false;
    }

    private void loggerError(Object exception, String username, String host, String errorMsg, String errorCode) {
        String msg = String.format("access denied;host:%s,username:%s,errorCode:%s,errorMsg:%s", host, username, errorCode, errorMsg);
        log.error(msg, exception);
    }

    public static Throwable getRootCause(Throwable e) {
        if (e == null) {
            return null;
        }
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return e;
    }

    /**
     * 处理登录异常，子类可以重写
     * 返回errorCode, errorMsg, httpStatusCode
     *
     * @param exception 不为null
     * @param request
     * @param response
     * @return
     */
    protected Map<String, Object> processException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        String errorCode = "401";
        String errorMsg = null;
        int httpStatusCode = HttpServletResponse.SC_UNAUTHORIZED;

        if (exception instanceof AuthenticationException) {
            if (exception instanceof LockedAccountException) {
                errorCode = "401.1";
                errorMsg = "system.token.authentication.account.lock";//"该账号锁定，不允许登陆!";
            } else if (exception instanceof DisabledAccountException) {
                errorCode = "401.2";
                errorMsg = "system.token.authentication.account.not.allow.login";//"账号不允许登陆!";
            } else if (exception instanceof UnknownAccountException) {
                errorCode = "401.3";
                errorMsg = "system.token.authentication.account.not.exist";//"账号不存在!";
            } else if (exception instanceof IncorrectCredentialsException) {
                errorCode = "401.4";
                errorMsg = "system.token.authentication.account.password.error";//"密码错误!";
            } else if (exception instanceof ExcessiveAttemptsException) {
                errorCode = "401.6";
                errorMsg = "system.token.authentication.account.too.many.logon.failures";//"登录失败次数过多，请稍后再试!";
            } else {
                errorCode = "401.5";
                errorMsg = exception.getMessage();
                if (StringUtils.isBlank(errorMsg)) {
                    errorMsg = "system.token.authentication.account.login.error";//"登录失败：";
                    errorMsg = errorMsg + exception.getClass().getSimpleName();
                }
            }
        } else {
            httpStatusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            errorCode = "500";
            errorMsg = exception.getMessage();
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("errorCode", errorCode);
        resultMap.put("httpStatusCode", httpStatusCode);
        resultMap.put("errorMsg", errorMsg);
        return resultMap;
    }

    /**
     * 错误信息
     *
     * @param req
     * @param resp
     * @param statusCode
     * @param errorCode
     * @param errorMsg
     */
    protected void sendError(HttpServletRequest req, HttpServletResponse resp, int statusCode, final String errorCode, final String errorMsg) {
        resp.setStatus(statusCode);
        resp.setContentType(ContentType.APPLICATION_JSON.toString());
        PrintWriter pw;
        try {
            pw = resp.getWriter();
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("request : failed when login, {}", getExMsgOrClzName(e), e);
            throw new RuntimeException("failed to get inputstream", e);
        }
        pw.print(JSON.toJSONString(new HashMap<String, String>() {{
            put("errorCode", errorCode);
            put("errorMsg", errorMsg);
        }}));
        pw.flush();
        pw.close();
    }
}
