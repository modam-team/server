package com.example.modam.global.config.Semaphore;

import com.example.modam.global.exception.ApiException;
import com.example.modam.global.exception.ErrorDefine;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
public class AladinSemaphore {

    private static final int PERMITS = 40;
    private static final long TIMEOUT_MS = 300;

    private final Semaphore semaphore = new Semaphore(PERMITS);

    public void acquire() {
        try {
            if (!semaphore.tryAcquire(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                throw new ApiException(ErrorDefine.MANY_REQUEST);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(ErrorDefine.MANY_REQUEST);
        }
    }

    public void release() {
        semaphore.release();
    }
}
