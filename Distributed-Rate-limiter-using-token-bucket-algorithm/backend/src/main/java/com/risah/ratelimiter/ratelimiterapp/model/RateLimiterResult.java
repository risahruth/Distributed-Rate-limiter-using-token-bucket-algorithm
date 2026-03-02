package com.risah.ratelimiter.ratelimiterapp.model;

public class RateLimiterResult {

    private final boolean allowed;
    private final long tokensRemaining;
    private final long capacity;
    private final long refillRate;

    public RateLimiterResult(boolean allowed,
                             long tokensRemaining,
                             long capacity,
                             long refillRate) {
        this.allowed = allowed;
        this.tokensRemaining = tokensRemaining;
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getTokensRemaining() {
        return tokensRemaining;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getRefillRate() {
        return refillRate;
    }
}