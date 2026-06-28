package RateLimiter.Bucket.filter;

import RateLimiter.Bucket.model.UserTier;
import RateLimiter.Bucket.services.TokenBucketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final TokenBucketService tokenBucketService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String clientId = request.getHeader("X-Client-ID");
        String tierStr = request.getHeader("X-User-Tier");

        if (clientId == null) clientId = request.getRemoteAddr();

        UserTier tier = (tierStr != null && tierStr.equalsIgnoreCase("PREMIUM"))
                ? UserTier.PREMIUM
                : UserTier.FREE;

        boolean allowed = tokenBucketService.allowRequest(clientId, tier);

        if (!allowed) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Try again later.");
            return false;
        }

        return true;
    }
}
