package com.example.demo.common.service.sys.impl;

import com.example.demo.common.entity.sys.SysOperationLog;
import com.example.demo.common.dao.sys.SysOperationLogMapper;
import com.example.demo.common.service.sys.ISysOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author Eric
 * @since 2019-04-12
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements ISysOperationLogService {

}
