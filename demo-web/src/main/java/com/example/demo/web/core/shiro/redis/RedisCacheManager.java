package com.example.demo.web.core.shiro.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("shiroRedisCacheManager")
public class RedisCacheManager implements CacheManager {

    @Resource(name = "redisTemplate_shiro")
    private RedisTemplate redisTemplate;
    //过期时间(默认半小时)，单位秒
    private long expire = 1800;

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return new ShiroRedisCache<>(name, redisTemplate, expire);
    }
}
