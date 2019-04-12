package com.example.demo.common.entity.sys;

import com.example.demo.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 操作日志
 * </p>
 *
 * @author Eric
 * @since 2019-04-12
 */
@ApiModel(value="SysOperationLog对象", description="操作日志")
public class SysOperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日志ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @ApiModelProperty(value = "日志类型")
    @TableField("log_type")
    private String logType;

    @ApiModelProperty(value = "日志名称")
    @TableField("log_name")
    private String logName;

    @ApiModelProperty(value = "请求路径")
    @TableField("uri")
    private String uri;

    @ApiModelProperty(value = "请求类型")
    @TableField("request_type")
    private String requestType;

    @ApiModelProperty(value = "类名称")
    @TableField("class_name")
    private String className;

    @ApiModelProperty(value = "方法名称")
    @TableField("method")
    private String method;

    @ApiModelProperty(value = "是否成功")
    @TableField("succeed")
    private String succeed;

    @ApiModelProperty(value = "备注")
    @TableField("params")
    private String params;

    @ApiModelProperty(value = "返回值")
    @TableField("result")
    private String result;

    @ApiModelProperty(value = "对比")
    @TableField("contrast")
    private String contrast;

    @ApiModelProperty(value = "ip")
    @TableField("ip")
    private String ip;

    @ApiModelProperty(value = "耗时")
    @TableField("cost_time")
    private Long costTime;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }
    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
    public String getSucceed() {
        return succeed;
    }

    public void setSucceed(String succeed) {
        this.succeed = succeed;
    }
    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public String getContrast() {
        return contrast;
    }

    public void setContrast(String contrast) {
        this.contrast = contrast;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    @Override
    public String toString() {
        return "SysOperationLog{" +
        "logId=" + logId +
        ", logType=" + logType +
        ", logName=" + logName +
        ", uri=" + uri +
        ", requestType=" + requestType +
        ", className=" + className +
        ", method=" + method +
        ", succeed=" + succeed +
        ", params=" + params +
        ", result=" + result +
        ", contrast=" + contrast +
        ", ip=" + ip +
        ", costTime=" + costTime +
        "}";
    }
}
