package com.example.ratelimit;

import com.example.ratelimit.property.RateLimitProperty;
import com.example.ratelimit.util.RequestHelper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({RateLimitProperty.class})
@RequiredArgsConstructor
public class RateLimitter {

  private final RateLimitProperty rateLimitProperty;
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final ConcurrentMap<String, RateLimitData> cache = new ConcurrentHashMap<>();

  public boolean canCallMethod(final String method) {
    final HttpServletRequest httpServletRequest =
        RequestHelper.getRequest().orElseThrow(() -> new RuntimeException("http servlet not found"));
    final String ip = RequestHelper.getClientIp(httpServletRequest);
    final String key = String.join(":", ip, method);
    final RateLimitData cached = cache.get(key);
    final int quotasCount = rateLimitProperty.getQuotasCount();
    if (cached != null) {
      readWriteLock.readLock().lock();
      try {
        final AtomicInteger availableQuota = cached.getAvailableQuota();
        if (availableQuota.get() < quotasCount) {
          return availableQuota.incrementAndGet() <= quotasCount;
        } else {
          if (cached.getDateCreate() + rateLimitProperty.getLimitRequestPerMilliSecond()
              < System.currentTimeMillis()) {
            return availableQuota.get() <= quotasCount;
          } else {
            return availableQuota.get() < quotasCount;
          }
        }
      } finally {
        readWriteLock.readLock().unlock();
      }
    } else {
      readWriteLock.writeLock().lock();
      try {
        final RateLimitData value = new RateLimitData();
        cache.put(key, value);
        return value.getAvailableQuota().get() <= quotasCount;
      } finally {
        readWriteLock.writeLock().unlock();
      }
    }
  }

  public void clearCache() {
    if (cache.size() >= rateLimitProperty.getMaxLoadedCacheSize()) {
      cache.entrySet().removeIf(entry -> entry.getValue().getAvailableQuota().get() >= rateLimitProperty.getQuotasCount()
          && System.currentTimeMillis() - entry.getValue().getDateCreate() > rateLimitProperty.getLimitRequestPerMilliSecond());
    }
  }
}
