package com.risah.ratelimiter.ratelimiterapp.service;
import com.risah.ratelimiter.ratelimiterapp.service.RateLimiterService;
import com.risah.ratelimiter.ratelimiterapp.service.RateLimiterLoggingService;
import com.risah.ratelimiter.ratelimiterapp.model.RateLimiterResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String path = request.getRequestURI();

        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized");
            return;
        }

        String userId = authentication.getName();
        String endpoint = request.getRequestURI();

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