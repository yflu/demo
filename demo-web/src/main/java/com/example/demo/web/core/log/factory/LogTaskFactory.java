package com.example.demo.web.core.log.factory;

import com.example.demo.common.dao.sys.SysLoginLogMapper;
import com.example.demo.common.dao.sys.SysOperationLogMapper;
import com.example.demo.common.entity.sys.SysLoginLog;
import com.example.demo.common.entity.sys.SysOperationLog;
import com.example.demo.core.constant.LogSucceed;
import com.example.demo.core.constant.LogType;
import com.example.demo.core.util.SpringContextHolder;
import com.example.demo.core.util.ToolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

/**
 * 日志操作任务创建工厂
 */
@Slf4j
public class LogTaskFactory {

    private static SysLoginLogMapper loginLogMapper = SpringContextHolder.getBean(SysLoginLogMapper.class);
    private static SysOperationLogMapper operationLogMapper = SpringContextHolder.getBean(SysOperationLogMapper.class);

    public static TimerTask loginLog(final Long userId, final String ip) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    SysLoginLog loginLog = LogFactory.createLoginLog(LogType.LOGIN, userId, null, ip);
                    loginLogMapper.insert(loginLog);
                } catch (Exception e) {
                    log.error("创建登录日志异常!", e);
                }
            }
        };
    }

    public static TimerTask loginLog(final String username, final String msg, final String ip) {
        return new TimerTask() {
            @Override
            public void run() {
                SysLoginLog loginLog = LogFactory.createLoginLog(
                        LogType.LOGIN_FAIL, null, "账号:" + username + "," + msg, ip);
                try {
                    loginLogMapper.insert(loginLog);
                } catch (Exception e) {
                    log.error("创建登录失败异常!", e);
                }
            }
        };
    }

    public static TimerTask exitLog(final Long userId, final String ip) {
        return new TimerTask() {
            @Override
            public void run() {
                SysLoginLog loginLog = LogFactory.createLoginLog(LogType.EXIT, userId, null, ip);
                try {
                    loginLogMapper.insert(loginLog);
                } catch (Exception e) {
                    log.error("创建退出日志异常!", e);
                }
            }
        };
    }

    public static TimerTask bussinessLog(final Long userId, final String uri, final String requestType, final String params, final String bussinessName, final String clazzName, final String methodName, final String ip, final Long time, final Object result, final String contrast) {
        return new TimerTask() {
            @Override
            public void run() {
                SysOperationLog operationLog = LogFactory.createOperationLog(
                        LogType.BUSSINESS, LogSucceed.SUCCESS, userId, uri, requestType, params, bussinessName, clazzName, methodName, ip, time, result, contrast);
                try {
                    operationLogMapper.insert(operationLog);
                } catch (Exception e) {
                    log.error("创建业务日志异常!", e);
                }
            }
        };
    }

    public static TimerTask exceptionLog(final Long userId, final String uri, final String requestType, final String params, final String ip, final Exception exception) {
        return new TimerTask() {
            @Override
            public void run() {
                String result = ToolUtil.getExceptionMsg(exception);
                SysOperationLog operationLog = LogFactory.createOperationLog(
                        LogType.EXCEPTION, LogSucceed.FAIL, userId, uri, requestType, params, null, null, null, ip, null, result, null);
                try {
                    operationLogMapper.insert(operationLog);
                } catch (Exception e) {
                    log.error("创建异常日志异常!", e);
                }
            }
        };
    }
}
