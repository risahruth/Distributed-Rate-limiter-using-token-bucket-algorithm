package com.risah.ratelimiter.ratelimiterapp.service;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import com.risah.ratelimiter.ratelimiterapp.model.TokenBucket;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RateLimiterLoggingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final long LOG_TTL_MILLIS = 24 * 60 * 60 * 1000L;
    private final ObjectMapper mapper = new ObjectMapper();

    public RateLimiterLoggingService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void logRequest(String userId, String endpoint, boolean allowed, TokenBucket bucket) {
        String key = "rate_limit:logs:" + userId;

        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("status", allowed ? "ALLOWED" : "BLOCKED");
        logEntry.put("endpoint", endpoint);
        logEntry.put("tokensRemaining", bucket.getToken());
        logEntry.put("capacity", bucket.getCapacity());
        logEntry.put("refillRate", bucket.getRefillRate());
        logEntry.put("timestamp", Instant.now().toEpochMilli());

        try {
            String json = mapper.writeValueAsString(logEntry);
            long timestamp = Instant.now().toEpochMilli();
            redisTemplate.opsForZSet().add(key, json, timestamp);
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, timestamp - LOG_TTL_MILLIS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}