package com.itsAadi.urlshortener.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter implements Filter {

    private final StringRedisTemplate redisTemplate;
    private static final int REQUEST_LIMIT=10;
    private static final Duration WINDOW=Duration.ofMinutes(1);
    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;

        if(!"/urls".equals(httpServletRequest.getRequestURI()) || !"POST".equals(httpServletRequest.getMethod())) {
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        String clientIp =
                httpServletRequest.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isBlank()) {
            clientIp = httpServletRequest.getRemoteAddr();
        }
         String key="rate_limit:"+ clientIp;
         Long requests=redisTemplate.opsForValue().increment(key);
         if(requests!=null && requests==1) {
             redisTemplate.expire(key,WINDOW);
         }
        if (requests != null && requests > REQUEST_LIMIT) {

            httpServletResponse.setHeader(
                    "Access-Control-Allow-Origin",
                        frontendUrl
            );

            httpServletResponse.setHeader(
                    "Access-Control-Expose-Headers",
                    "Retry-After"
            );

            httpServletResponse.setStatus(429);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            Long ttl=redisTemplate.getExpire(key, TimeUnit.SECONDS);
            long retryAfter = ttl != null && ttl > 0 ? ttl : 0;
            httpServletResponse.setHeader("Retry-After",String.valueOf(retryAfter));

            httpServletResponse.getWriter().write(
                    "{\"message\":\"Rate limit exceeded. Try again later.\"}"
            );

            return;
        }
             httpServletResponse.setHeader(
                     "X-Rate-Limit-Limit",
                     String.valueOf(REQUEST_LIMIT)
             );
            httpServletResponse.setHeader(
                    "X-Rate-Limit-Remaining",
                    String.valueOf(
                            Math.max(0,REQUEST_LIMIT-requests))
            );

         filterChain.doFilter(servletRequest,servletResponse);
        }
    }
