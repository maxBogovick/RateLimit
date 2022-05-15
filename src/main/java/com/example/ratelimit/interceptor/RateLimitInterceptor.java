package com.example.ratelimit.interceptor;

import com.example.ratelimit.RateLimitter;
import com.example.ratelimit.exception.RateLimitException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class RateLimitInterceptor {

  private final RateLimitter rateLimitter;

  @Autowired
  public RateLimitInterceptor(final RateLimitter rateLimitter) {
    this.rateLimitter = rateLimitter;
  }

  @Around("@annotation(com.example.ratelimit.RateLimit)")
  public Object limitCall(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    if (rateLimitter.canCallMethod(proceedingJoinPoint.getSignature().getName())) {
      return proceedingJoinPoint.proceed();
    } else {
      throw new RateLimitException();
    }
  }
}