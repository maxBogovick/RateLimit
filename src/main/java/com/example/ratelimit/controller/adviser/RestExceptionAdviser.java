package com.example.ratelimit.controller.adviser;

import com.example.ratelimit.exception.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RestExceptionAdviser {

  @ExceptionHandler(RateLimitException.class)
  @ResponseStatus(HttpStatus.BAD_GATEWAY)
  public void catchRateLimitException() {
    log.error("Error during call method, cause exceed count request per time for this ip address");
  }
}
