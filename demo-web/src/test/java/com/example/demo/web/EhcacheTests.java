package com.example.demo.web;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EhcacheTests {

    @Resource(name = "cacheShiroManager")
    private CacheManager cacheManager;

    @Test
    public void test() {
        String username = "1554885042130";
        Cache<String, AtomicInteger> passwordRetryCache = cacheManager.getCache("passwordRetryCache");
        passwordRetryCache.get(username);
    }
}
