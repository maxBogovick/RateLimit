package com.example.ratelimit.scheduler;

import com.example.ratelimit.RateLimitter;
import com.example.ratelimit.property.RateLimitProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClearScheduler {

  private final RateLimitter rateLimitter;

  @Scheduled(cron = "${app.scheduler.clear-rate-limit:0/1 0/5 * * * *}")
  public void clearRateLimitCache() {
    rateLimitter.clearCache();
  }
}
