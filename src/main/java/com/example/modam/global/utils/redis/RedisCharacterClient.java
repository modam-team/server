package com.example.modam.global.utils.redis;

import com.example.modam.domain.report.Presentation.dto.CharacterResponse;
import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@AllArgsConstructor
public class RedisCharacterClient implements RedisClient<CharacterResponse> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, CharacterResponse value, long time) {
        try {
            Duration ttl = Duration.ofSeconds(time);
            log.info("character save to redis : " + value);
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            log.error("Redis String set failed. key={}", key, e);
            throw new ApiException(ErrorDefine.INVALID_ACCESS_TO_REDIS);
        }
    }

    @Override
    public CharacterResponse get(String key) {
        CharacterResponse data = (CharacterResponse) redisTemplate.opsForValue().get(key);
        log.info("get to redis: " + key);
        return data;
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
