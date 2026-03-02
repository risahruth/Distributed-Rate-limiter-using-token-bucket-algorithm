package com.risah.ratelimiter.ratelimiterapp.service;
import com.risah.ratelimiter.ratelimiterapp.model.TokenBucket;
import com.risah.ratelimiter.ratelimiterapp.model.RateLimiterResult;
import com.risah.ratelimiter.ratelimiterapp.service.RateLimiterService;
import com.risah.ratelimiter.config.RateLimiterProperties;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import java.util.Map;
@Service
@Profile("dev")
public class InMemoryRateLimiterService implements RateLimiterService {
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final RateLimiterProperties rateLimiterProperties;

    public InMemoryRateLimiterService(RateLimiterProperties properties) {
        this.rateLimiterProperties = properties;
    }

    @Override
    public RateLimiterResult allowRequest(String userId) {

        TokenBucket bucket = buckets.computeIfAbsent(
                userId,
                id -> new TokenBucket(
                        rateLimiterProperties.getCapacity(),
                        rateLimiterProperties.getRefillRate()
                )
        );

        boolean allowed = bucket.tryConsume();

        return new RateLimiterResult(
                allowed,
                bucket.getToken(),
                rateLimiterProperties.getCapacity(),
                rateLimiterProperties.getRefillRate()
        );
    }
}