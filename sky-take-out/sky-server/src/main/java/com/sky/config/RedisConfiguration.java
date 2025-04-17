package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("开始创建redis模板对象...");
        //StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        //redis客户端显示正常
        /*
        RedisTemplate redisTemplate = new RedisTemplate();

        //设置redis的连接工厂对象
        //stringRedisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setConnectionFactory(connectionFactory);

        //设置redis key的序列化器
        //stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        //return stringRedisTemplate;
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
        */

        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);

        // 设置 Key 的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 设置 Value 的序列化器，防止中文乱码
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // 如果使用 Hash 类型，还需要单独设置 HashKey 和 HashValue 的序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 初始化配置
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
