package com.example.modam.global.utils.redis;

public interface RedisClient<T> {
    void set(String key, T value, long time);

    T get(String key);

    void delete(String key);

    boolean exists(String key);
}
