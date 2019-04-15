package com.example.demo.web.core.cache.redis.config;

import com.example.demo.core.serializers.session.ObjectSerializer;
import com.example.demo.web.core.cache.redis.util.RedisConfigUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisConfig
 *
 * @author eric
 * @date 2018/4/30 09:32
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class ShiroRedisConfig {

    @Value("${session.redis.database}")
    private int database;
    @Value("${session.redis.host}")
    private String host;
    @Value("${session.redis.port}")
    private int port;
    @Value("${session.redis.password}")
    private String password;
    @Value("${session.redis.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${session.redis.lettuce.pool.max-wait}")
    private long maxWait;
    @Value("${session.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${session.redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${session.redis.timeout}")
    private long timeout;

    @Bean("redisConfiguration_shiro")
    public RedisStandaloneConfiguration standaloneConfiguration() {
        return RedisConfigUtil.redisStandaloneConfiguration(database, host, port, password);
    }

    @Bean("redisConnectionFactory_shiro")
    public RedisConnectionFactory lettuceConnectionFactory(@Qualifier("redisConfiguration_shiro") RedisStandaloneConfiguration configuration) {
        return RedisConfigUtil.redisConnectionFactory(configuration, maxActive, maxWait, maxIdle, minIdle, timeout);
    }

    @Bean("redisCacheManager_shiro")
    public RedisCacheManager redisCacheManager(@Qualifier("redisConnectionFactory_shiro") RedisConnectionFactory factory) {
        return RedisConfigUtil.redisCacheManager(factory);
    }

    @Bean("redisTemplate_shiro")
    public RedisTemplate sessionRedisTemplate(@Qualifier("redisConnectionFactory_shiro") RedisConnectionFactory factory) {
        return RedisConfigUtil.createRedisTemplate(factory, new ObjectSerializer());
    }
}
