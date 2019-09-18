package com.rs.multirediscacheservice.cache;

import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheManager {

    private final RedisCacheManager redisOneCacheManager;
    private final RedisCacheManager redisTwoCacheManager;
    private final RedisTemplate<String, String> redisOneTemplate;
    private final RedisTemplate<String, String> redisTwoTemplate;

    public CacheManager(@Qualifier("redisOneCacheManager") RedisCacheManager redisOneCacheManager,
                        @Qualifier("redisTwoCacheManager") RedisCacheManager redisTwoCacheManager,
                        @Qualifier("redisOneTemplate") RedisTemplate<String, String> redisOneTemplate,
                        @Qualifier("redisTwoTemplate") RedisTemplate<String, String> redisTwoTemplate) {
        this.redisOneCacheManager = redisOneCacheManager;
        this.redisTwoCacheManager = redisTwoCacheManager;
        this.redisOneTemplate = redisOneTemplate;
        this.redisTwoTemplate = redisTwoTemplate;
    }

    public RedisCacheManager getPrimaryCacheManager() {

        try {
            if (redisOneTemplate.opsForValue().get("primary").equalsIgnoreCase("yes")) {
                return redisOneCacheManager;
            } else if (redisTwoTemplate.opsForValue().get("primary").equalsIgnoreCase("yes")) {
                return redisTwoCacheManager;
            }
        } catch (Exception e) {
            return redisOneCacheManager;
        }

        return null;
    }

    public Quartet<RedisCacheManager, RedisCacheManager, RedisTemplate, RedisTemplate> currentState() {
        if (redisOneTemplate.opsForValue().get("primary").equalsIgnoreCase("yes")) {
            return new Quartet<>(redisOneCacheManager, redisTwoCacheManager, redisOneTemplate, redisTwoTemplate);
        } else if (redisTwoTemplate.opsForValue().get("primary").equalsIgnoreCase("yes")) {
            return new Quartet<>(redisTwoCacheManager, redisOneCacheManager, redisTwoTemplate, redisOneTemplate);
        }

        return null;
    }
}
