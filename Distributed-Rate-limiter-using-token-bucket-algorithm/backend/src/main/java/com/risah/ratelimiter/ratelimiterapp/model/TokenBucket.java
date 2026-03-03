package com.risah.ratelimiter.ratelimiterapp.model;
import java.util.concurrent.TimeUnit;

public class TokenBucket {

    private final long capacity;
    private final long refillRate;

    private long tokens;
    private long lastRefillTimestamp;

    public TokenBucket(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.nanoTime();
    }

    public synchronized boolean tryConsume() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }
    public long getToken(){
        return tokens;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastRefillTimestamp;

        long tokensToAdd = (elapsedNanos / 1_000_000_000L) * refillRate;

        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}