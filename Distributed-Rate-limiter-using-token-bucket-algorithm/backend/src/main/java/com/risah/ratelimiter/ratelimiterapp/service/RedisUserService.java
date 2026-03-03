package com.risah.ratelimiter.ratelimiterapp.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
@Component
public class RedisUserService {

    private final RedisTemplate<String, String> redisTemplate;
    private final String USER_KEY_PREFIX = "users:";

    public RedisUserService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveUser(String username, String password) {
        redisTemplate.opsForValue().set(USER_KEY_PREFIX + username, password);
    }

    public boolean validateUser(String username, String password) {
        String storedPassword = redisTemplate.opsForValue().get(USER_KEY_PREFIX + username);
        return storedPassword != null && storedPassword.equals(password);
    }
}