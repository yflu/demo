package com.example.demo.web;

import com.example.demo.common.dao.sys.SysUserMapper;
import com.example.demo.common.entity.sys.SysUser;
import com.example.demo.web.core.cache.redis.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisService redisService;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void contextLoads() {
        SysUser user = sysUserMapper.selectById(3);
        String key = "user:" + user.getUserId();
        if (!redisService.exists(key)) {
            redisService.set(key, user);
        } else {
            SysUser t = (SysUser) redisService.get(key);
            System.out.println(t);
        }
    }

    @Test
    public void testString() {
        String key = "hello1";
        if (!redisService.exists(key)) {
            redisService.set(key, "joy");
        } else {
            System.out.println("-----------"+redisService.get(key));
        }
    }
}
