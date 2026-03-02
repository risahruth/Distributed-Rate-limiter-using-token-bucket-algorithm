package com.risah.ratelimiter.ratelimiterapp.service;
import com.risah.ratelimiter.ratelimiterapp.model.RateLimiterResult;
public interface RateLimiterService{
    RateLimiterResult allowRequest(String userId);
}