package com.learn.saas.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.benmanes.caffeine.cache.Caffeine;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
public class AppConfig {

    // ✅ Step 1: Define PasswordEncoder as a Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .findAndAddModules() // Auto-loads JavaTimeModule etc.
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }

    // @Bean //only when default redis cache manager is not sufficient and we want to customize it
    public RedisCacheConfiguration cacheConfiguration() {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(
                                        new GenericJacksonJsonRedisSerializer(jsonMapper())));
    }

    @Bean // only when we want to switch between Redis and Caffeine based on application properties
    @ConditionalOnProperty(
            name = "spring.cache.provider",
            havingValue = "redis")
    public RedisCacheManager getRedisCacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder()
                .cacheDefaults(cacheConfiguration())
                .build();
    }

    @Bean // only when we want to switch between Redis and Caffeine based on application properties
    @ConditionalOnProperty(
            name = "spring.cache.provider",
            havingValue = "caffeine",
            matchIfMissing = true)
    public CaffeineCacheManager getCaffeineCacheManager() {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "tenantById",
                "allTenants",
                "usersByTenant",
                "userByUsername");

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(10)));

        return cacheManager;
    }


    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

    // @Bean //used when we want to switch between Redis and Caffeine based on application properties manually
    // public CacheManager cacheManagerBean(@Value("${spring.cache.type:caffeine}") String cacheType) {
    //     if ("redis".equalsIgnoreCase(cacheType)) {
    //         // RedisCacheManager bean configuration
    //         return getRedisCacheManager();
    //     } else {
    //         // Default to CaffeineCacheManager
    //         return getCaffeineCacheManager();
    //     }
    // }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}