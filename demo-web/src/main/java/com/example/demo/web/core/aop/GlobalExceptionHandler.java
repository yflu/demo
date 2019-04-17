package com.example.demo.web.core.aop;

import com.example.demo.core.common.model.response.ErrorResponseData;
import com.example.demo.core.common.model.response.ResponseUtil;
import com.example.demo.core.exception.ServiceException;
import com.example.demo.core.exception.enums.CoreExceptionEnum;
import com.example.demo.core.util.HttpContext;
import com.example.demo.web.core.exception.InvalidKaptchaException;
import com.example.demo.web.core.log.LogManager;
import com.example.demo.web.core.log.factory.LogTaskFactory;
import com.example.demo.web.core.shiro.util.ShiroKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import static com.example.demo.core.util.HttpContext.getIp;
import static com.example.demo.core.util.HttpContext.getRequest;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 */
@ControllerAdvice
@Order(-1)
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 拦截业务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponseData bussiness(HttpServletRequest request, ServiceException e) {
        LogManager.getInstance().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getUserId(), request.getServletPath(), request.getMethod(), HttpContext.getRequestParameters().toString(), HttpContext.getIp(), e));
        getRequest().setAttribute("tip", e.getMessage());
        log.error("业务异常:", e);
        return new ErrorResponseData(e.getCode(), e.getMessage());
    }

    /**
     * 用户未登录异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unAuth(AuthenticationException e) {
        log.error("用户未登陆：", e);
        return "login";
    }

    /**
     * 账号被冻结异常
     */
    @ExceptionHandler(LockedAccountException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountLocked(LockedAccountException e, Model model) {
        String account = getRequest().getParameter("account");
        LogManager.getInstance().executeLog(LogTaskFactory.loginLog(account, "账号被冻结", getIp()));
        model.addAttribute("tips", "账号被冻结");
        return "login";
    }

    /**
     * 账号密码错误异常
     */
    @ExceptionHandler({CredentialsException.class, UnknownAccountException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String credentials(CredentialsException e, Model model) {
        String account = getRequest().getParameter("account");
        LogManager.getInstance().executeLog(LogTaskFactory.loginLog(account, "账号密码错误", getIp()));
        model.addAttribute("tips", "账号密码错误");
        return "login";
    }

    /**
     * 登录失败次数过多
     */
    @ExceptionHandler(ExcessiveAttemptsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String excessiveAttempts(ExcessiveAttemptsException e, Model model) {
        String account = getRequest().getParameter("account");
        LogManager.getInstance().executeLog(LogTaskFactory.loginLog(account, "登录失败次数过多", getIp()));
        model.addAttribute("tips", "登录失败次数过多，请稍后重试");
        return "login";
    }

    /**
     * 验证码错误异常
     */
    @ExceptionHandler(InvalidKaptchaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String invalidKaptcha(InvalidKaptchaException e, Model model) {
        String account = getRequest().getParameter("account");
        LogManager.getInstance().executeLog(LogTaskFactory.loginLog(account, "验证码错误", getIp()));
        model.addAttribute("tips", "验证码错误");
        return "login";
    }

    /**
     * 无权访问该资源异常
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponseData credentials(UndeclaredThrowableException e) {
        getRequest().setAttribute("tip", "权限不足");
        log.error("权限不足!", e);
        return ResponseUtil.getFail(CoreExceptionEnum.NO_PERMITION);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponseData other(HttpServletRequest request, HttpServletResponse response, RuntimeException e) throws IOException {
        LogManager.getInstance().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getUserId(), request.getServletPath(), request.getMethod(), HttpContext.getRequestParameters().toString(), HttpContext.getIp(), e));
        getRequest().setAttribute("tip", "服务器未知运行时异常");
        log.error("运行时异常:", e);
        //如果是页面请求则跳500页面
        if (!isAjax(request)) {
            response.sendRedirect(request.getContextPath() + "/global/error/500");
        }
        return ResponseUtil.FAIL;
    }

    /**
     * 判断是否ajax请求
     *
     * @param request 请求对象
     * @return true:ajax请求  false:非ajax请求
     */
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}
