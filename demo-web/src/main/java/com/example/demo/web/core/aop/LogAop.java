package com.example.demo.web.core.aop;

import com.alibaba.fastjson.JSON;
import com.example.demo.core.common.annotion.BussinessLog;
import com.example.demo.core.config.filter.BodyReaderRequestWrapper;
import com.example.demo.core.util.Contrast;
import com.example.demo.core.util.HttpContext;
import com.example.demo.web.core.log.LogManager;
import com.example.demo.web.core.log.LogObjectHolder;
import com.example.demo.web.core.log.factory.LogTaskFactory;
import com.example.demo.web.core.shiro.model.ShiroUser;
import com.example.demo.web.core.shiro.util.ShiroKit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 日志记录
 */
@Aspect
@Component
@Slf4j
public class LogAop {

    @Pointcut(value = "@annotation(com.example.demo.core.common.annotion.BussinessLog)")
    public void cutService() {
    }

    @Around("cutService()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        handle(point, time, result);
        return result;
    }

    private void handle(ProceedingJoinPoint point, long time, Object result) throws Exception {
        //获取拦截的方法名
        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();

        //如果当前用户未登录，不做日志
        ShiroUser user = ShiroKit.getUser();
        if (null == user) {
            return;
        }

        //获取拦截方法的参数
        String className = point.getTarget().getClass().getName();

        //获取操作名称
        BussinessLog annotation = currentMethod.getAnnotation(BussinessLog.class);
        String bussinessName = annotation.value();
        Class cls = annotation.cls();
        String key = annotation.key();

        HttpServletRequest request = HttpContext.getRequest();
        Object requestParamObj = null;
        String requestParams = null;
        String contrast = null;
        //请求的参数
        String contentType = request.getHeader("Content-Type");
        if (!contentType.startsWith("multipart/form-data;")) {
            if (contentType.startsWith("application/json")) {
                BodyReaderRequestWrapper servletRequestWrapper = new BodyReaderRequestWrapper(request);
                requestParams = servletRequestWrapper.getBodyString(request);
                if (cls != null && !ObjectUtils.isEmpty(requestParams)) {
                    requestParamObj = JSON.parseObject(requestParams, cls);
                }
            } else {
                Map<String, String> paramMap = HttpContext.getRequestParameters();
                requestParams = paramMap.toString();
                if (cls != null && paramMap != null && paramMap.size() > 0) {
                    requestParamObj = Contrast.mapToObject(paramMap, cls);
                }
            }

            //如果涉及到修改,比对变化
            if (cls != null && (bussinessName.contains("修改") || bussinessName.contains("编辑") || bussinessName.contains("更新"))) {
                Object obj1 = LogObjectHolder.getInstance().get();
                contrast = Contrast.contrastObj(obj1, requestParamObj, key);
            }
        }
        LogManager.getInstance().executeLog(LogTaskFactory.bussinessLog(user.getUserId(), request.getServletPath(), request.getMethod(), requestParams, bussinessName, className, methodName, HttpContext.getIp(), time, result, contrast));
    }
}