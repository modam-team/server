package com.example.modam.global.utils;

import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
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
        try {
            Duration duration = Duration.ofSeconds(ttl);
            log.info("save to Redis:" + "key:" + key + ", value: " + value + " , ttl: " + ttl);
            redisTemplate.opsForValue().set(key, value, duration);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(ErrorDefine.INVALID_ACCESS_TO_REDIS);
        }
    }

    public Object getRedisData(String key) {
        try {
            Object data = redisTemplate.opsForValue().get(key);
            log.info("read to Redis:" + key + " : " + data);
            return data;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(ErrorDefine.INVALID_ACCESS_TO_REDIS);
        }
    }
}
