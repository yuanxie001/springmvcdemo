package store.xiaolan.spring.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

@Slf4j
public class TrackingIdFilter extends OncePerRequestFilter implements OrderedFilter {
    private static final String TRACK_PREFIX = "WEB_";
    public static final String TRACKING_ID = "tracking-id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String trackingId = MDC.get(TRACKING_ID);
        if (StringUtils.isEmpty(trackingId)) {
            trackingId = setTrackingId();
        }
        response.addHeader("X-WEB-TRACKING_ID",trackingId);
        filterChain.doFilter(request,response);
        Object bestUrl = request.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE);
        log.info("request url:{}",bestUrl);
    }

    public static String setTrackingId() {
        String trackingId;
        byte[] bytes = UUID.randomUUID().toString().substring(0,32).getBytes();
        String s = Base64.encodeBase64URLSafeString(bytes);
        s= s.split("=")[0];
        trackingId = TRACK_PREFIX+s;
        MDC.put(TRACKING_ID,trackingId);
        return trackingId;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }
}
