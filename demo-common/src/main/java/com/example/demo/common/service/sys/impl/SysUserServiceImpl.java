package com.example.demo.common.service.sys.impl;

import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.dao.sys.SysUserMapper;
import com.example.demo.common.service.sys.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Eric
 * @since 2019-04-10
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
