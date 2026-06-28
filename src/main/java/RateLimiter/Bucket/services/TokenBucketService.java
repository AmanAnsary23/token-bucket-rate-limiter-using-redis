package RateLimiter.Bucket.services;

import RateLimiter.Bucket.model.UserTier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBucketService {

    private final StringRedisTemplate redisTemplate;

    @Value("${rate.limit.free.capacity}")
    private int freeCapacity;

    @Value("${rate.limit.free.refill.interval.seconds}")
    private long freeRefillInterval;

    @Value("${rate.limit.premium.capacity}")
    private int premiumCapacity;

    @Value("${rate.limit.premium.refill.interval.seconds}")
    private long premiumRefillInterval;

    public boolean allowRequest(String clientId, UserTier tier) {
        int capacity = (tier == UserTier.PREMIUM) ? premiumCapacity : freeCapacity;
        long refillInterval = (tier == UserTier.PREMIUM) ? premiumRefillInterval : freeRefillInterval;

        String tokenKey = "token_bucket:" + clientId;
        String lastRefillKey = "last_refill:" + clientId;

        long now = System.currentTimeMillis() / 1000;

        String tokensStr = redisTemplate.opsForValue().get(tokenKey);
        String lastRefillStr = redisTemplate.opsForValue().get(lastRefillKey);

        int tokens = (tokensStr != null) ? Integer.parseInt(tokensStr) : capacity;
        long lastRefill = (lastRefillStr != null) ? Long.parseLong(lastRefillStr) : now;

        // Refill logic — 1 token per interval, max capacity
        long elapsed = now - lastRefill;
        long tokensToAdd = elapsed / refillInterval;

        if (tokensToAdd > 0) {
            tokens = (int) Math.min(capacity, tokens + tokensToAdd);
            lastRefill = now;
            redisTemplate.opsForValue().set(lastRefillKey, String.valueOf(lastRefill));
        }

        if (tokens > 0) {
            tokens--;
            redisTemplate.opsForValue().set(tokenKey, String.valueOf(tokens));
            return true;
        }

        return false;
    }
}
