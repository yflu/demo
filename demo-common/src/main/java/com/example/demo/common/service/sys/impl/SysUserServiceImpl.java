package com.example.demo.common.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.dao.sys.SysUserMapper;
import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.service.sys.ISysUserService;
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

    @Override
    public SysUser getByAccount(String account) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("account", account);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
