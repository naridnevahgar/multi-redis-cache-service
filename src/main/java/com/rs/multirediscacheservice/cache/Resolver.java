package com.rs.multirediscacheservice.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@Slf4j
public class Resolver extends CachingConfigurerSupport {

    private final CacheManager cacheManager;

    public Resolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return (context) -> {
            log.info("Cache Hit - Resolving from Cache");
            RedisCacheManager activeRedisCacheManager = cacheManager.getPrimaryCacheManager();

            if (activeRedisCacheManager != null) {
                return Arrays.asList(cacheManager.getPrimaryCacheManager().getCache("SCVPOL"));
            } else {
                return Collections.EMPTY_LIST;
            }
        };
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) { }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) { }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) { }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) { }
        };
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            return StringUtils.arrayToDelimitedString(params, ":");
        };
    }
}
