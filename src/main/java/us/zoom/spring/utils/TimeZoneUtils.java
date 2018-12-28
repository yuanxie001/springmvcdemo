package us.zoom.spring.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        ZoneId zoneId = timeZone.toZoneId();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();
        ZonedDateTime gmt = offsetDateTime.atZoneSameInstant(ZoneId.of("GMT"));
        return gmt.format(formatter);
    }
    
    public static void main(String[] args) {
        String date;
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        date=fmt.format(new Date());
        logger.info("转化前的时间："+date);
        String s = toGmt(date, format, "GMT+8:00");

        logger.info("转化后的时间："+ s);
    }

    public static Map<String,String> getTimezoneMap() {
        return null;
    }


}
