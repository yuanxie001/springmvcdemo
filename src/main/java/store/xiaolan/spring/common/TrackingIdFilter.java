package store.xiaolan.spring.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class TrackingIdFilter extends OncePerRequestFilter {
    private static final String TRACK_PREFIX = "WEB_";
    public static final String TRACKING_ID = "tracking-id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String trackingId = MDC.get(TRACKING_ID);
        if (StringUtils.isEmpty(trackingId)) {
            byte[] bytes = UUID.randomUUID().toString().substring(0,32).getBytes();
            String s = Base64.encodeBase64URLSafeString(bytes);
            s= s.split("=")[0];
            MDC.put(TRACKING_ID,TRACK_PREFIX+s);
        }
        filterChain.doFilter(request,response);
    }
}
