package com.risah.ratelimiter.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix="ratelimiter")
public class RateLimiterConfig{
    private int capacity;
    private int refillrate;
    private String strategy;
}