package lamdba.date;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

public class LocalDateTest {

    @Test
    public void test1(){
        LocalDate date = LocalDate.of(2014, 3, 18);
        date = date.with(ChronoField.MONTH_OF_YEAR, 9);
        date = date.plusYears(2).minusDays(10);
        date = date.withYear(2011);
        System.out.println(date);

    }

    /**
     * 自定义日期实现，获取工作日
     */
    @Test
    public void test2(){
        LocalDate date = LocalDate.now();
        date = date.with(temporal -> {
            DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
            else if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;
            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });
        System.out.println(date);
    }
    @Test
    public void test(){
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        Month month = date.getMonth();
        int monthValue = date.getMonthValue();
        int dayOfMonth = date.getDayOfMonth();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfYear = date.getDayOfYear();
        int i = date.lengthOfMonth();
        boolean leapYear = date.isLeapYear();

    }


    /**
     * 时区
     */
    @Test
    public void test3(){
        ZoneId romeZone = TimeZone.getDefault().toZoneId();
        LocalDate date = LocalDate.of(2014, Month.MARCH, 18);
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);

        System.out.println(zdt1);

        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
        ZonedDateTime zonedDateTime = dateTime.atZone(romeZone);

        System.out.println(zonedDateTime);

        Instant instant = Instant.now();
        ZonedDateTime zonedDateTime1 = instant.atZone(romeZone);
        System.out.println(zonedDateTime1);


    }
}
