package com.learn.saas.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class CacheController {

    private final RedisTemplate<String, Object> redisTemplate;

    private final CacheManager cacheManager;

    @GetMapping("/cache")
    public Set<String> cacheKeys() {
        return redisTemplate.keys("*");
    }

    @GetMapping("/cache-info")
    public String info() {

        System.out.println(
            cacheManager.getClass().getName());

        System.out.println(
            cacheManager.getCacheNames());

        return "ok";
    }

    @GetMapping("/cache/{cacheName}")
    public Object cacheKeys(@PathVariable String cacheName) {

        Cache cache = cacheManager.getCache(cacheName);

        if (cache instanceof org.springframework.cache.caffeine.CaffeineCache caffeineCache) {
            return caffeineCache.getNativeCache().asMap().keySet();
        }

        if (redisTemplate != null) {
            return redisTemplate.keys(cacheName + "*");
        }

        return "Provider does not support key enumeration";
    }
}