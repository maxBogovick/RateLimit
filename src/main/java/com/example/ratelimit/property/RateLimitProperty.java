package com.example.ratelimit.property;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit")
@NoArgsConstructor
@Data
public class RateLimitProperty {
  private int quotasCount;
  private long limitRequestPerMilliSecond;
  private int maxLoadedCacheSize;
}
