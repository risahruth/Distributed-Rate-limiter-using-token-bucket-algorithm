package com.risah.ratelimiter.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.context.annotation.Profile;
@Profile("redis")
@Configuration
public class RedisScriptLoader {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> rateLimiterScript;

    public RedisScriptLoader(RedisTemplate<String, String> redisTemplate,
                             RedisScript<Long> rateLimiterScript) {
        this.redisTemplate = redisTemplate;
        this.rateLimiterScript = rateLimiterScript;
    }

    @Bean
    public String preloadRateLimiterScript() {
        String sha = redisTemplate.getConnectionFactory()
                .getConnection()
                .scriptLoad(rateLimiterScript.getScriptAsString().getBytes());
        System.out.println("Rate limiter script loaded with SHA: " + sha);
        return sha;
    }
}
