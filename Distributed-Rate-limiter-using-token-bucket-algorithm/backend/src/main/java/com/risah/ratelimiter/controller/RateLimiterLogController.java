package com.risah.ratelimiter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@RestController
@RequestMapping("/logs")
public class RateLimiterLogController {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public RateLimiterLogController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/{username}")
    public List<Map<String, Object>> getUserLogs(@PathVariable String username) throws Exception {
        String key = "rate_limit:logs:" + username;
        Set<String> logs = redisTemplate.opsForZSet().range(key, 0, -1);
        List<Map<String,Object>> logList = new ArrayList<>();

        if (logs != null) {
            for(String log : logs) {
                logList.add(mapper.readValue(log, Map.class));
            }
        }

        return logList;
    }
}