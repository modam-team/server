package com.example.modam.global.utils.redis;

import com.example.modam.domain.book.Domain.BookEntity;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class RedisBookDataClient implements RedisClient<List<BookEntity>> {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisBookDataClient(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, List<BookEntity> value, long time) {
        try {
            Duration ttl = Duration.ofSeconds(time);
            log.info("save to redis : " + value);
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            log.error("Redis String set failed. key={}", key, e);
            throw new ApiException(ErrorDefine.INVALID_ACCESS_TO_REDIS);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BookEntity> get(String key) {
        try {
            List<BookEntity> data = (List<BookEntity>) redisTemplate.opsForValue().get(key);
            log.info("get to redis: " + key);
            return data;
        } catch (Exception e) {
            log.error("Redis String get failed. key={}", key, e);
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
