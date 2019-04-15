package com.example.demo.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.service.sys.ISysUserService;
import com.example.demo.core.common.annotion.BussinessLog;
import com.example.demo.core.common.model.response.SuccessResponseData;
import com.example.demo.core.exception.ServiceException;
import com.example.demo.core.exception.enums.CoreExceptionEnum;
import com.example.demo.core.exception.factory.ServiceExceptionFactory;
import com.example.demo.web.core.log.LogObjectHolder;
import com.example.demo.web.core.shiro.util.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ISysUserService sysUserService;

    @RequestMapping("/get/{id}")
    @BussinessLog(value = "查询用户", key = "userId", cls = SysUser.class)
    public String getUser(@PathVariable Integer id) {
        sysUserService.getById(id);
        System.out.println("---------------" + id);
        return "success";
    }

    @RequestMapping("/list")
    @BussinessLog(value = "用户列表", key = "userId", cls = SysUser.class)
    @ResponseBody
    public Object list() {
        Page<SysUser> page = new Page<SysUser>(0, 2);
        QueryWrapper queryWrapper = new QueryWrapper<SysUser>().eq("role_id", 1).orderByDesc("user_id");
        IPage<SysUser> list = sysUserService.page(page, queryWrapper);
        return list;
    }

    @RequestMapping("/add")
    @BussinessLog(value = "新增用户", key = "userId", cls = SysUser.class)
    @ResponseBody
    public Object insert2(SysUser user) {
        // 完善账号信息
        String salt = ShiroKit.getRandomSalt(5);
        String password = ShiroKit.md5(user.getPassword(), salt);
        user.setSalt(salt);
        user.setPassword(password);
        sysUserService.save(user);
        return user;
    }

    @RequestMapping("/add2")
    @BussinessLog(value = "新增用户", key = "userId", cls = SysUser.class)
    @ResponseBody
    public Object insert(@RequestBody SysUser user) {
        // 完善账号信息
        String salt = ShiroKit.getRandomSalt(5);
        String password = ShiroKit.md5(user.getPassword(), salt);
        user.setSalt(salt);
        user.setPassword(password);
        sysUserService.save(user);
        return user;
    }


    @RequestMapping("/update")
    @BussinessLog(value = "更新用户", key = "userId", cls = SysUser.class)
    @ResponseBody
    public Object update(SysUser user) {
        SysUser old = sysUserService.getById(user.getUserId());
        if (old == null) {
            throw ServiceExceptionFactory.createException(CoreExceptionEnum.DATA_NOT_EXIST);
        }
        LogObjectHolder.getInstance().set(old);
        user.setVersion(old.getVersion());
        if (sysUserService.updateById(user))
            return user;
        throw ServiceExceptionFactory.createException(CoreExceptionEnum.ASYNC_ERROR);
    }

    @RequestMapping("/update2")
    @BussinessLog(value = "更新用户", key = "userId", cls = SysUser.class)
    @ResponseBody
    public Object update2(@RequestBody SysUser user) {
        SysUser old = sysUserService.getById(user.getUserId());
        if (old == null) {
            throw new ServiceException(CoreExceptionEnum.NO_PERMITION.getCode(), "用户不存在");
        }
        LogObjectHolder.getInstance().set(old);
        user.setVersion(old.getVersion());
        if (sysUserService.updateById(user))
            return user;
        throw new ServiceException(CoreExceptionEnum.TOKEN_EXPIRED.getCode(), "版本号不一致");
    }

    @RequestMapping("/delete")
    @BussinessLog(value = "删除用户")
    @ResponseBody
    public Object delete(Long userId) {
        if (sysUserService.removeById(userId))
            return new SuccessResponseData();
        throw new ServiceException(CoreExceptionEnum.TOKEN_EXPIRED.getCode(), "版本号不一致");
    }
}
