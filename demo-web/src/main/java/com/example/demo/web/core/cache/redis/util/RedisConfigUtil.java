package com.example.demo.web.core.cache.redis.util;

import com.example.demo.core.serializers.FastJson2JsonRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ObjectUtils;

import java.time.Duration;

/**
 * RedisConfigUtil
 *
 * @author eric
 * @date 2019/4/30 09:32
 */
public class RedisConfigUtil {

    /**
     * configuration
     *
     * @param database
     * @param host
     * @param port
     * @param password
     * @return
     */
    public static RedisStandaloneConfiguration redisStandaloneConfiguration(int database, String host, int port, String password) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        if (!ObjectUtils.isEmpty(password))
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        return redisStandaloneConfiguration;
    }

    /**
     * redisConnectionFactory
     *
     * @param configuration
     * @param maxActive
     * @param maxWait
     * @param maxIdle
     * @param minIdle
     * @param timeout
     * @return
     */
    public static RedisConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration configuration, int maxActive, long maxWait, int maxIdle, int minIdle, long timeout) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(genericObjectPoolConfig);
        builder.commandTimeout(Duration.ofSeconds(timeout));
        return new LettuceConnectionFactory(configuration, builder.build());
    }

    /**
     * redisCacheManager
     *
     * @param factory
     * @return
     */
    public static RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(factory);
        builder.cacheDefaults(defaultConfiguration);
        return builder.build();
    }

    /**
     * 配置redisTemplate
     *
     * @param factory
     * @return
     */
    public static RedisTemplate sessionRedisTemplate(RedisConnectionFactory factory) {
        return createRedisTemplate(factory);
    }

    /**
     * json 实现 redisTemplate
     *
     * @param redisConnectionFactory
     * @return
     */
    public static RedisTemplate createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, new FastJson2JsonRedisSerializer<Object>(Object.class));
    }

    /**
     * json 实现 redisTemplate
     *
     * @param redisConnectionFactory
     * @param serializer
     * @return
     */
    public static RedisTemplate createRedisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer serializer) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringSerializer);//设置key的序列化器
        redisTemplate.setValueSerializer(serializer);//设置值的序列化器
        redisTemplate.setHashKeySerializer(stringSerializer); // Hash key序列化
        redisTemplate.setHashValueSerializer(serializer); // Hash value序列化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
