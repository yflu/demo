package com.example.demo.web;

import com.example.demo.common.entity.sys.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource(name = "redisTemplate_shiro")
    private RedisTemplate bizRedisTemplate;

    @Test
    public void set() {
        redisTemplate.opsForValue().set("admin", "123456");
        System.out.println(redisTemplate.opsForValue().get("admin"));
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setAccount("eric");
        user.setCreateTime(new Date());
        redisTemplate.opsForValue().set("user", user);
        System.out.println(redisTemplate.opsForValue().get("user"));
    }

    @Test
    public void set2() {
        bizRedisTemplate.opsForValue().set("admin", "123456");
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setAccount("eric");
        user.setCreateTime(new Date());
        bizRedisTemplate.opsForValue().set("user", user);
        System.out.println(bizRedisTemplate.opsForValue().get("admin"));
        System.out.println(bizRedisTemplate.opsForValue().get("user"));
    }
}
