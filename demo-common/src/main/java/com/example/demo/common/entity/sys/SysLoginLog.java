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
 * 登录记录
 * </p>
 *
 * @author Eric
 * @since 2019-04-10
 */
@ApiModel(value="SysLoginLog对象", description="登录记录")
public class SysLoginLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日志ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @ApiModelProperty(value = "日志名称")
    @TableField("log_name")
    private String logName;

    @ApiModelProperty(value = "是否执行成功")
    @TableField("succeed")
    private String succeed;

    @ApiModelProperty(value = "具体消息")
    @TableField("message")
    private String message;

    @ApiModelProperty(value = "登录ip")
    @TableField("ip")
    private String ip;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }
    public String getSucceed() {
        return succeed;
    }

    public void setSucceed(String succeed) {
        this.succeed = succeed;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "SysLoginLog{" +
        "logId=" + logId +
        ", logName=" + logName +
        ", succeed=" + succeed +
        ", message=" + message +
        ", ip=" + ip +
        "}";
    }
}
