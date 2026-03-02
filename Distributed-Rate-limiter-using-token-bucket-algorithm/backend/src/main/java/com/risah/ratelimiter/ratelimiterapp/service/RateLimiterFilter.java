package com.risah.ratelimiter.ratelimiterapp.service;
import com.risah.ratelimiter.ratelimiterapp.service.RateLimiterService;
import com.risah.ratelimiter.ratelimiterapp.service.RateLimiterLoggingService;
import com.risah.ratelimiter.ratelimiterapp.model.RateLimiterResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RateLimiterService service;
    private final RateLimiterLoggingService loggingService;

    public RateLimiterFilter(RateLimiterService service,RateLimiterLoggingService loggingService){
        this.service = service;
        this.loggingService = loggingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("RateLimiterFilter triggered");

        String userId = request.getHeader("X-USER-ID");
        String endpoint = request.getRequestURI();

        if (userId == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Missing X-USER-ID header");
            return;
        }
        RateLimiterResult result = service.allowRequest(userId);

        loggingService.logRequest(
                userId,
                endpoint,
                result.isAllowed(),
                result.getTokensRemaining(),
                result.getCapacity(),
                result.getRefillRate()
        );

        if (!result.isAllowed()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate Limit Exceeded");
            return;
        }

        filterChain.doFilter(request, response);
    }
}