package com.risah.ratelimiter.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.risah.ratelimiter.ratelimiterapp.service.RateLimiterService;
import com.risah.ratelimiter.ratelimiterapp.service.InMemoryRateLimiterService;
import com.risah.ratelimiter.ratelimiterapp.service.RedisRateLimiterService;
@Configuration
public class RateLimitConfig{
    public RateLimiterService rateLimiterService(RateLimiterProperties properties,InMemoryRateLimiterService inMemory,RedisRateLimiterService redis){
        if("redis".equalsIgnoreCase(properties.getStrategy())){
            return redis;
        }
        return inMemory;
    }
}