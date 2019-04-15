package com.example.demo.web.core.log.factory;

import com.alibaba.fastjson.JSON;
import com.example.demo.common.entity.sys.SysLoginLog;
import com.example.demo.common.entity.sys.SysOperationLog;
import com.example.demo.core.constant.enums.LogSucceed;
import com.example.demo.core.constant.enums.LogType;

import java.util.Date;

public class LogFactory {

    /**
     * 创建操作日志
     */
    public static SysOperationLog createOperationLog(LogType logType, LogSucceed succeed, Long userId, String uri, String requestType, String params, String bussinessName, String clazzName, String methodName, String ip, Long time, Object result, String contrast) {
        SysOperationLog operationLog = new SysOperationLog();
        operationLog.setLogType(logType.getMessage());
        operationLog.setLogName(bussinessName);
        operationLog.setCreateUser(userId);
        operationLog.setClassName(clazzName);
        operationLog.setMethod(methodName);
        operationLog.setCreateTime(new Date());
        operationLog.setSucceed(succeed.getMessage());
        operationLog.setResult(JSON.toJSONString(result));
        operationLog.setContrast(contrast);
        operationLog.setCostTime(time);
        operationLog.setParams(params);
        operationLog.setIp(ip);
        operationLog.setUri(uri);
        operationLog.setRequestType(requestType);
        return operationLog;
    }

    /**
     * 创建登录日志
     */
    public static SysLoginLog createLoginLog(LogType logType, Long userId, String msg, String ip) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setLogName(logType.getMessage());
        loginLog.setCreateUser(userId);
        loginLog.setCreateTime(new Date());
        loginLog.setSucceed(LogSucceed.SUCCESS.getMessage());
        loginLog.setIp(ip);
        loginLog.setMessage(msg);
        return loginLog;
    }
}
