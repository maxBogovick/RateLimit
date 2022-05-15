package com.example.ratelimit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(classes = RateLimitApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RateLimitApplicationTests {

  public static final String URL_TEMPLATE = UriComponentsBuilder.fromPath("/sample").toUriString();
  public static final ResultMatcher STATUS_OK = status().isOk();
  public static final ResultMatcher STATUS_BAD_GATEWAY = status().isBadGateway();
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RateLimitter rateLimitter;

  private final RequestPostProcessor defaultRequestPostProcessor = request -> {
    request.setRemoteAddr("192.168.1.10");
    return request;
  };

  @Test
  void expect_success_one_request_per_one_seconds_test() throws Exception {
    Assertions.assertTrue(executeRequest(STATUS_OK));
    TimeUnit.SECONDS.sleep(2L);
    Assertions.assertTrue(executeRequest(STATUS_OK));
  }

  @Test
  void expect_success_one_request_and_bad_gateway_second_test() throws Exception {
    rateLimitter.clearCache();
    Assertions.assertTrue(executeRequest(STATUS_OK));
    Assertions.assertTrue(executeRequest(STATUS_BAD_GATEWAY));
  }

  @Test
  void expect_success_multiple_ip_request_test() throws Exception {
    rateLimitter.clearCache();
    final RequestPostProcessor requestPostProcessor1 = request -> {
      request.setRemoteAddr("192.168.1.1");
      return request;
    };
    final RequestPostProcessor requestPostProcessor2 = request -> {
      request.setRemoteAddr("192.168.1.2");
      return request;
    };
    final RequestPostProcessor requestPostProcessor3 = request -> {
      request.setRemoteAddr("192.168.1.3");
      return request;
    };
    final ExecutorService executorService = Executors.newFixedThreadPool(4);
    List<Future<Boolean>> requests = new ArrayList<>(4);
    requests.add(executorService.submit(() -> {
      try {
        executeRequest(STATUS_OK, requestPostProcessor1);
        executeRequest(STATUS_BAD_GATEWAY, requestPostProcessor1);
        return true;
      } catch (Exception e) {
        return false;
      }
    }));
    requests.add(executorService.submit(() -> {
      try {
        executeRequest(STATUS_OK, requestPostProcessor2);
        executeRequest(STATUS_BAD_GATEWAY, requestPostProcessor2);
        return true;
      } catch (Exception e) {
        return false;
      }
    }));
    requests.add(executorService.submit(() -> {
      try {
        executeRequest(STATUS_OK, requestPostProcessor3);
        executeRequest(STATUS_BAD_GATEWAY, requestPostProcessor3);
        return true;
      } catch (Exception e) {
        return false;
      }
    }));
    for (Future<Boolean> item : requests) {
      while (true) {
        if (item.isDone()) {
          final Boolean obj = item.get();
          Assertions.assertTrue(obj);
          break;
        }
      }
    }
  }

  private boolean executeRequest(final ResultMatcher expectedResultStatus) {
    return executeRequest(expectedResultStatus, defaultRequestPostProcessor);
  }

  private boolean executeRequest(final ResultMatcher expectedResultStatus,
                                 final RequestPostProcessor requestPostProcessor) {
    try {
      mockMvc.perform(
              get(URL_TEMPLATE).with(requestPostProcessor))
          .andDo(print())
          .andExpect(expectedResultStatus);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
