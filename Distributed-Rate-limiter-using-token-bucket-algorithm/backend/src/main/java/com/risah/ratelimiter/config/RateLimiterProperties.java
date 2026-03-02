package com.risah.ratelimiter.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix="ratelimiter")
public class RateLimiterProperties{
    private long capacity;
    private long refillRate;
    private String strategy;
}