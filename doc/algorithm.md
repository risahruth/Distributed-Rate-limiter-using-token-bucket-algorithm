# Rate Limiting Algorithm

## Overview

This project implements a **distributed Token Bucket rate limiting algorithm** to control API traffic on a per-user basis.

The system ensures:

- Fair usage of API resources
- Controlled burst handling
- Horizontal scalability
- Consistent rate limiting across multiple application instances

Rate limiting is enforced **after JWT-based authentication**, allowing per-user throttling.

The system uses Redis as a centralized in-memory datastore to maintain shared token state across distributed instances.

---

## Why Token Bucket?

The Token Bucket algorithm was chosen because it:

- Allows controlled bursts of traffic
- Provides smoother request handling compared to fixed window methods
- Is efficient and suitable for distributed systems
- Can be implemented atomically using Redis Lua scripts

---

## Token Bucket Logic

Each authenticated user is assigned:

- `capacity` → Maximum number of tokens
- `tokens` → Current available tokens
- `lastRefillTimestamp` → Time of last refill
- `refillRate` → Tokens added per second

Each incoming request:

1. Calculates elapsed time since last refill
2. Refills tokens based on refill rate
3. Caps tokens at maximum capacity
4. Deducts one token if available
5. Rejects request if no tokens remain

---

## Pseudocode

```
function allowRequest(userId):

    currentTime = getCurrentTime()

    bucket = getBucketFromRedis(userId)

    if bucket does not exist:
        create new bucket:
            tokens = capacity - 1
            lastRefillTimestamp = currentTime
        save bucket
        return true

    timeElapsed = currentTime - bucket.lastRefillTimestamp

    tokensToAdd = timeElapsed * refillRate

    bucket.tokens = min(capacity, bucket.tokens + tokensToAdd)

    bucket.lastRefillTimestamp = currentTime

    if bucket.tokens > 0:
        bucket.tokens -= 1
        save bucket
        return true
    else:
        return false
```

---

## Distributed Implementation Using Redis

To support multiple Spring Boot instances, token state is stored in Redis instead of application memory.

Why Redis?

- Shared state across instances
- Extremely fast in-memory operations
- Supports atomic execution
- Prevents race conditions under high concurrency

However, using separate `GET` and `SET` operations can cause race conditions.

To ensure atomicity, the entire token update logic is executed using a Redis Lua script.

---

## Redis Lua Script Strategy

Instead of:

- GET tokens
- Calculate refill
- SET updated values

We execute a single Lua script that:

1. Fetches existing token data
2. Performs refill calculation
3. Deducts token if available
4. Updates Redis state
5. Returns allow/deny decision

All steps execute atomically inside Redis.

---

## Lua Script (Conceptual)

```
local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local currentTime = tonumber(ARGV[3])

local data = redis.call("HMGET", key, "tokens", "lastRefill")

local tokens = tonumber(data[1])
local lastRefill = tonumber(data[2])

if tokens == nil then
    tokens = capacity
    lastRefill = currentTime
end

local timeElapsed = currentTime - lastRefill
local tokensToAdd = timeElapsed * refillRate

tokens = math.min(capacity, tokens + tokensToAdd)

if tokens > 0 then
    tokens = tokens - 1
    redis.call("HMSET", key, "tokens", tokens, "lastRefill", currentTime)
    return 1
else
    return 0
end
```

Return values:

- `1` → Request allowed
- `0` → Rate limit exceeded (HTTP 429)

---

## Execution Flow in Application

1. Request enters Spring Boot application
2. JWT is validated using Spring Security
3. User ID is extracted from token
4. Lua script is executed in Redis
5. Redis returns allow/deny result
6. Application:
   - Proceeds to controller if allowed
   - Returns HTTP 429 if denied

---

## Advantages of This Approach

- Fully distributed
- Race-condition free
- Horizontally scalable
- Burst-friendly traffic control
- Production-grade design

---

## Future Improvements

- Tier-based dynamic rate limits (Free, Pro, Admin)
- Configurable limits from database
- Redis fallback strategy
- Monitoring and metrics integration
- Sliding window alternative implementation
