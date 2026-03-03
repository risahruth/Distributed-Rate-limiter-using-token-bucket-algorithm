package com.risah.ratelimiter.ratelimiterapp.service;
import com.risah.ratelimiter.config.RateLimiterProperties;
import com.risah.ratelimiter.ratelimiterapp.service.RateLimiterService;
import com.risah.ratelimiter.ratelimiterapp.model.RateLimiterResult;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.RedisTemplate;
import com.risah.ratelimiter.config.RedisScriptLoader;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.List;
@Service
@Profile("redis")
public class RedisRateLimiterService implements RateLimiterService{
    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimiterProperties properties;
    private final String scriptSha;

    public RedisRateLimiterService(RedisTemplate<String, String> redisTemplate,
                                   RateLimiterProperties properties,
                                   @Qualifier("preloadRateLimiterScript")String preloadRateLimiterScript) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.scriptSha = preloadRateLimiterScript;
    }

    public RateLimiterResult allowRequest(String userId) {
        System.out.println("Checking rate limit for user: " + userId);
        String tokensKey = "rate_limit:" + userId + ":tokens";
        String timestampKey = "rate_limit:" + userId + ":timestamp";

        List<Long> result = (List<Long>) redisTemplate
                .getConnectionFactory()
                .getConnection()
                .evalSha(
                        scriptSha,
                        ReturnType.MULTI,
                        2,
                        tokensKey.getBytes(),
                        timestampKey.getBytes(),
                        String.valueOf(properties.getCapacity()).getBytes(),
                        String.valueOf(properties.getRefillRate()).getBytes(),
                        String.valueOf(System.currentTimeMillis()).getBytes()
                );
        System.out.println("Lua result: " + result);

        boolean allowed = result.get(0) == 1;
        long tokensRemaining = result.get(1);

        return new RateLimiterResult(
                allowed,
                tokensRemaining,
                properties.getCapacity(),
                properties.getRefillRate()
        );
    }
}
