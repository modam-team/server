package com.example.modam.global.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RedisRepository {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setToRedis(String key, Object value, long ttl) {
        Duration duration = Duration.ofSeconds(ttl);
        log.info("save to Redis:" + "key:" + key + ", value: " + value + " , ttl: " + ttl);
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public Object getRedisData(String key) {
        Object data = redisTemplate.opsForValue().get(key);
        log.info("read to Redis:" + key + " : " + data);
        return data;
    }
}
