package store.xiaolan.spring.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class TimeZoneUtils {

    private static final Logger logger = LoggerFactory.getLogger(TimeZoneUtils.class);

    public static String toGmt(String time,String format,String zone){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        ZoneId zoneId = timeZone.toZoneId();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
        ZonedDateTime gmt = offsetDateTime.atZoneSameInstant(ZoneId.of("GMT"));
        return gmt.format(formatter);
    }

    public static String toGMTTime(HttpServletRequest request, String time, String format) throws ServletRequestBindingException {
        if (request == null || time == null) {
            return time;
        }
        Map<String, String> timezoneMap = getTimezoneMap();
        String timeZoneStr = ServletRequestUtils.getStringParameter(request, "timezone");
        TimeZone timeZone = null;
        if (!StringUtils.isEmpty(timeZoneStr) && timezoneMap.containsKey(timeZoneStr)) {
            timeZoneStr = timezoneMap.get(timeZoneStr);
            timeZone = TimeZone.getTimeZone(timeZoneStr);
        } else {
            timeZone = RequestContextUtils.getTimeZone(request);
        }

        if (timeZone == null ) {
            timeZone = TimeZone.getTimeZone("GMT");
        }
        return toGmt(time,format,timeZone.getID());
    }


    public static Map<String,String> getTimezoneMap() {
        return null;
    }


}
