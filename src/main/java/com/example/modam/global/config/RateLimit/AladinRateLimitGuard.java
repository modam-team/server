package com.example.modam.global.config.RateLimit;

import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Component;

@Component
public class AladinRateLimitGuard {

    // Rate Limit 초과 확인
    @RateLimiter(name = "aladinApi", fallbackMethod = "rateLimitFallback")
    public void check() {

    }

    // Rate Limit 초과 시 callback
    private void rateLimitFallback(RequestNotPermitted ex) {
        throw new ApiException(ErrorDefine.MANY_REQUEST);
    }
}
