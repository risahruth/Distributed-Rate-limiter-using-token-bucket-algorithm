package com.risah.ratelimiter.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
@Profile("redis")
public class LuaScriptConfig{
    @Bean
    public RedisScript<Long> rateLimiterScript(){
        String script= """
        local tokens_key = KEYS[1]
        local timestamp_key = KEYS[2]

        local capacity = tonumber(ARGV[1])
        local refill_rate = tonumber(ARGV[2])
        local current_time = tonumber(ARGV[3])

        local tokens = tonumber(redis.call("GET", tokens_key))
        if tokens == nil then
            tokens = capacity
        end

        local last_refill = tonumber(redis.call("GET", timestamp_key))
        if last_refill == nil then
            last_refill = current_time
        end

        local delta = math.floor((current_time - last_refill) / 1000)
        local refill = delta * refill_rate

        if refill > 0 then
            tokens = math.min(capacity, tokens + refill)
            last_refill = current_time
        end

        if tokens <= 0 then
                        redis.call("SET", tokens_key, tokens)
                        redis.call("SET", timestamp_key, last_refill)
                        return {0, tokens}
        end
                
        tokens = tokens - 1
        redis.call("SET", tokens_key, tokens)
        redis.call("SET", timestamp_key, last_refill)
                
         return {1, tokens}
    """;

        return RedisScript.of(script, Long.class);
    }
}