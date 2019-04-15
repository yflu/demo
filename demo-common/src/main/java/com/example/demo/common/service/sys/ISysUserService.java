package com.example.demo.common.service.sys;

import com.example.demo.common.entity.sys.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Eric
 * @since 2019-04-10
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据account获取用户
     *
     * @param account
     * @return
     */
    SysUser getByAccount(String account);

}
