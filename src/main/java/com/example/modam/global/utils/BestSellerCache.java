package com.example.modam.global.utils;

import com.example.modam.domain.book.Presentation.BookInfoResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class BestSellerCache {
    private volatile CompletableFuture<List<BookInfoResponse>> cachedFuture;
    private volatile long expireAt = 0L;

    final long ttlMillis = 24 * 60 * 60 * 1000L;

    public synchronized void saveFuture(CompletableFuture<List<BookInfoResponse>> future) {
        this.cachedFuture = future;
        this.expireAt = System.currentTimeMillis() + ttlMillis;
    }

    public boolean isExist() {
        CompletableFuture<List<BookInfoResponse>> f = this.cachedFuture;
        if (f != null && System.currentTimeMillis() < expireAt) {
            return true;
        } else {
            return false;
        }
    }

    public CompletableFuture<List<BookInfoResponse>> get() {
        if (!isExist()) {
            return null;
        }

        return this.cachedFuture;
    }
}
