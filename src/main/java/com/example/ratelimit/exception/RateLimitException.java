package com.example.ratelimit.exception;

public class RateLimitException extends RuntimeException {
  public RateLimitException() {
  }

  public RateLimitException(String message) {
    super(message);
  }
}
