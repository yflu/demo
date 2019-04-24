package com.example.demo.web.controller;

import com.example.demo.common.base.BaseController;
import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.service.sys.ISysUserService;
import com.example.demo.core.common.model.response.ResponseUtil;
import com.example.demo.web.core.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口控制器提供
 *
 * @author eric
 * @Date 2019/4/20 23:39
 */
@RestController
@RequestMapping("/token")
public class TokenController extends BaseController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * api登录接口，通过账号密码获取token
     */
    @PostMapping("/get")
    public Object getToken(SysUser user) {
        return ResponseUtil.getSuccess(JwtUtil.generateToken(String.valueOf(user.getUserId())));
    }

    /**
     * 测试接口是否走鉴权
     */
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Object test() {
        return SUCCESS;
    }
}

