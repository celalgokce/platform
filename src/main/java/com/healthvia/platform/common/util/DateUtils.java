// common/util/DateUtils.java
package com.healthvia.platform.common.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {
    
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Istanbul");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    
    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }
    
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE);
    }
    
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }
    
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
    
    public static LocalDateTime startOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY).atStartOfDay();
    }
    
    public static LocalDateTime endOfWeek(LocalDate date) {
        return date.with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX);
    }
    
    public static LocalDateTime startOfMonth(LocalDate date) {
        return date.withDayOfMonth(1).atStartOfDay();
    }
    
    public static LocalDateTime endOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
    }
    
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
    
    public static boolean isWorkingDay(LocalDate date) {
        return !isWeekend(date);
    }
    
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }
    
    public static List<LocalDate> getDatesBetween(LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = start;
        
        while (!current.isAfter(end)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        
        return dates;
    }
    
    public static String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(now());
    }
    
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(now());
    }
    
    public static boolean isToday(LocalDate date) {
        return date.equals(today());
    }
    
    public static boolean isTomorrow(LocalDate date) {
        return date.equals(today().plusDays(1));
    }
    
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, DEFAULT_ZONE);
    }
    
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toInstant();
    }
}
