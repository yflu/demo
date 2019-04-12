package com.example.demo.web.core.cache.redis.config;

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
public class SpringRedisConfig {

    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.lettuce.pool.max-wait}")
    private long maxWait;
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.timeout}")
    private long timeout;

    @Bean("redisConfiguration")
    public RedisStandaloneConfiguration standaloneConfiguration() {
        return RedisConfigUtil.redisStandaloneConfiguration(database, host, port, password);
    }

    @Bean("redisConnectionFactory")
    public RedisConnectionFactory lettuceConnectionFactory(@Qualifier("redisConfiguration") RedisStandaloneConfiguration configuration) {
        return RedisConfigUtil.redisConnectionFactory(configuration, maxActive, maxWait, maxIdle, minIdle, timeout);
    }

    @Bean("redisCacheManager")
    public RedisCacheManager redisCacheManager(@Qualifier("redisConnectionFactory") RedisConnectionFactory factory) {
        return RedisConfigUtil.redisCacheManager(factory);
    }

    @Bean("redisTemplate")
    public RedisTemplate sessionRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory factory) {
        return RedisConfigUtil.createRedisTemplate(factory);
    }
}
