package com.example.demo.web.controller;

import com.example.demo.common.base.BaseController;
import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.service.sys.ISysUserService;
import com.example.demo.core.common.annotion.BussinessLog;
import com.example.demo.web.core.log.LogManager;
import com.example.demo.web.core.log.factory.LogTaskFactory;
import com.example.demo.web.core.shiro.model.ShiroUser;
import com.example.demo.web.core.shiro.multRealm.LoginService;
import com.example.demo.web.core.shiro.multRealm.MyUsernamePasswordToken;
import com.example.demo.web.core.shiro.util.ShiroKit;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.demo.core.util.HttpContext.getIp;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    @BussinessLog(value = "login", key = "userId", cls = SysUser.class)
    public Object loginSubmit(HttpServletRequest request, HttpServletResponse response) {
        Subject currentUser = ShiroKit.getSubject();

        //MyUsernamePasswordToken token = new MyUsernamePasswordToken(user.getAccount(), user.getPassword(), "on".equals(remember), ToolUtil.getIP(), LoginType.USERNAME_PASSWORD);
        MyUsernamePasswordToken token = loginService.createToken(request, response);

        //执行shiro登录操作
        currentUser.login(token);

        //登录成功，记录登录日志
        ShiroUser shiroUser = ShiroKit.getUserNotNull();
        LogManager.getInstance().executeLog(LogTaskFactory.loginLog(shiroUser.getUserId(), getIp()));

        ShiroKit.getSession().setAttribute("sessionFlag", true);
        return REDIRECT + "/";
    }

    /**
     * 退出登录
     *
     * @author fengshuonan
     * @Date 2018/12/23 5:42 PM
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut() {
        LogManager.getInstance().executeLog(LogTaskFactory.exitLog(ShiroKit.getUserNotNull().getUserId(), getIp()));
        ShiroKit.getSubject().logout();
        deleteAllCookie();
        return REDIRECT + "/login";
    }
}
