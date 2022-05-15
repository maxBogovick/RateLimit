package com.example.ratelimit;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;

@Data
public class RateLimitData {
  private final AtomicInteger availableQuota = new AtomicInteger(1);
  private final long dateCreate = System.currentTimeMillis();
}
