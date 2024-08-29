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
        RedisTemplate redisTemplate = new RedisTemplate();

        //设置redis的连接工厂对象
        //stringRedisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setConnectionFactory(connectionFactory);

        //设置redis key的序列化器
        //stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        //return stringRedisTemplate;
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
