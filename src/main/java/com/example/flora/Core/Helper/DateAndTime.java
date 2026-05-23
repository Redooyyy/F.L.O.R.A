package com.example.flora.Core.Helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel.DATE_FMT;

public class DateAndTime {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateAndTime() {}

    public static String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public static LocalDateTime parse(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        return LocalDateTime.parse(dateStr, FORMATTER);
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(FORMATTER);
    }

    public static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s, DATE_FMT); }
        catch (Exception e) { return null; }
    }
}
