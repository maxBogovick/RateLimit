package com.example.ratelimit.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class RequestHelper {
  private static final String LOCALHOST_IPV4 = "127.0.0.1";
  private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
  public static final String UNKNOWN = "unknown";

  private RequestHelper() {
  }

  public static Optional<HttpServletRequest> getRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    return Optional.ofNullable(request);

  }

  public static String getClientIp(final HttpServletRequest request) {
    Assert.notNull(request, "request must be a set");
    String ipAddress = request.getHeader("X-Forwarded-For");
    if (Objects.isNull(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }

    if (Objects.isNull(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }

    if (Objects.isNull(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
      if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
        try {
          InetAddress inetAddress = InetAddress.getLocalHost();
          ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
      }
    }

    if (!Objects.isNull(ipAddress)
        && ipAddress.length() > 15
        && ipAddress.indexOf(",") > 0) {
      ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
    }

    return ipAddress;
  }

}
