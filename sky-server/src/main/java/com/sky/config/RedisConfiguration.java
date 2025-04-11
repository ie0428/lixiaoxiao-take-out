package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) { // 1. 明确泛型类型
        log.info("开始创建redis模板对象...");
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        
        // 设置key序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 新增value序列化配置 ▼▼▼
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        // 设置hash结构序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}