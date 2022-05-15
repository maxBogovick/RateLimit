package com.example.ratelimit.controller;

import com.example.ratelimit.RateLimit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

  @GetMapping("/sample")
  @RateLimit
  public ResponseEntity<Object> getSample() {
    return ResponseEntity.ok(null);
  }
}
