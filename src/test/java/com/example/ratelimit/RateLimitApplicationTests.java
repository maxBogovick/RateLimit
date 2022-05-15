package com.example.ratelimit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(classes = RateLimitApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitApplicationTests {

  public static final String URL_TEMPLATE = UriComponentsBuilder.fromPath("/sample").toUriString();
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RateLimitter rateLimitter;

  @Test
  void expect_success_one_request_per_one_seconds_test() throws Exception {
    //rateLimitter.clearCache();
    mockMvc.perform(
            get(URL_TEMPLATE))
        .andDo(print())
        .andExpect(status().isOk());
    TimeUnit.SECONDS.sleep(2L);
    mockMvc.perform(
            get(URL_TEMPLATE))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void expect_success_one_request_and_bad_gateway_second_test() throws Exception {
    rateLimitter.clearCache();
    mockMvc.perform(
            get(URL_TEMPLATE))
        .andDo(print())
        .andExpect(status().isOk());
    mockMvc.perform(
            get(URL_TEMPLATE))
        .andDo(print())
        .andExpect(status().isBadGateway());
  }
}
