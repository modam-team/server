package com.example.modam.global.utils.redis;

import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class RedisStringClient implements RedisClient<String> {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisStringClient(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value, long time) {
        try {
            Duration ttl = Duration.ofSeconds(time);
            log.info("save to redis : " + value);
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            log.error("Redis String set failed. key={}", key, e);
            throw new ApiException(ErrorDefine.INVALID_ACCESS_TO_REDIS);
        }
    }

    @Override
    public String get(String key) {
        try {
            String data = redisTemplate.opsForValue().get(key);
            log.info("get to redis: " + key);
            return data;
        } catch (Exception e) {
            log.error("Redis String get failed. key={}", key, e);
            throw new ApiException(ErrorDefine.INVALID_ACCESS_TO_REDIS);
        }
    }

    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Redis String increment failed. key={}", key, e);
            throw new ApiException(ErrorDefine.INVALID_ACCESS_TO_REDIS);
        }
    }


    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
