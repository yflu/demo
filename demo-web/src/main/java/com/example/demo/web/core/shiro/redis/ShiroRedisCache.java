package com.example.demo.web.core.shiro.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * shiro redis缓存
 *
 * @param <K>
 * @param <V>
 */
@Slf4j
public class ShiroRedisCache<K, V> implements Cache<K, V> {

    private static final String REDIS_SHIRO_CACHE = "shiro-cache:";
    private String cacheKey;
    private RedisTemplate<K, V> redisTemplate;
    private long expire = 1800;

    public ShiroRedisCache(String name, RedisTemplate<K, V> client, long expire) {
        this.cacheKey = REDIS_SHIRO_CACHE + name + ":";
        this.redisTemplate = client;
        this.expire = expire;
    }

    @Override
    public V get(K key) throws CacheException {
        redisTemplate.boundValueOps(getCacheKey(key)).expire(expire, TimeUnit.SECONDS);
        return redisTemplate.boundValueOps(getCacheKey(key)).get();
    }

    @Override
    public V put(K key, V value) throws CacheException {
        V old = get(key);
        redisTemplate.boundValueOps(getCacheKey(key)).set(value);
        return old;
    }

    @Override
    public V remove(K key) throws CacheException {
        V old = get(key);
        redisTemplate.delete(getCacheKey(key));
        return old;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.delete(keys());
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.keys(getCacheKey("*"));
    }

    @Override
    public Collection<V> values() {
        Set<K> set = keys();
        List<V> list = new ArrayList<>();
        for (K s : set) {
            list.add(get(s));
        }
        return list;
    }

    private K getCacheKey(Object k) {
        return (K) (this.cacheKey + k);
    }

    /**
     * 自增
     *
     * @param key
     * @return
     * @throws CacheException
     */
    public long incr(K key) throws CacheException {
        return redisTemplate.boundValueOps(getCacheKey(key)).increment(1);
    }
}
