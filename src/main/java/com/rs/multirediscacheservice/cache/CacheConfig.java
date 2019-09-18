package com.rs.multirediscacheservice.cache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    private String redisOneHost = "localhost";
    private int redisOnePort = 9001;

    private String redisTwoHost = "localhost";
    private int redisTwoPort = 9002;

    private final RedisCacheConfiguration redisCacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

    @Bean("redisOneCacheManager")
    public RedisCacheManager redisOneCacheManager() {
        return RedisCacheManager.builder(jedisConnectionFactory(redisOneHost, redisOnePort))
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Bean("redisTwoCacheManager")
    public RedisCacheManager redisTwoCacheManager() {
        return RedisCacheManager.builder(jedisConnectionFactory(redisTwoHost, redisTwoPort))
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Bean
    @Qualifier("redisOneTemplate")
    public RedisTemplate<String, String> redisOneTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(redisOneHost, redisOnePort));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);

        return redisTemplate;
    }

    @Bean
    @Qualifier("redisTwoTemplate")
    public RedisTemplate<String, String> redisTwoTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory(redisTwoHost, redisTwoPort));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);

        return redisTemplate;
    }

    private JedisConnectionFactory jedisConnectionFactory(String host, int port) {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }
}
