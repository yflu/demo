package com.example.demo.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.dao.sys.SysUserMapper;
import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.common.vo.SysUserVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageTests {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void contextLoads() {
        Page<SysUser> page = new Page<SysUser>(0, 2);
        QueryWrapper queryWrapper = new QueryWrapper<SysUser>().eq("role_id", 1).orderByDesc("user_id");
        IPage<SysUser> userIPage = sysUserMapper.selectPage(page, queryWrapper);
        System.out.println("总条数 ------> " + userIPage.getTotal());
        System.out.println("当前页数 ------> " + userIPage.getCurrent());
        System.out.println("当前每页显示数 ------> " + userIPage.getSize());
        print(userIPage.getRecords());

        Page<SysUserVo> page1 = new Page<SysUserVo>(0, 2);
        QueryWrapper queryWrapper1 = new QueryWrapper<SysUserVo>().eq("r.name", "admin").orderByDesc("user_id");
    }

    private <T> void print(List<T> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(System.out::println);
        }
    }
}
