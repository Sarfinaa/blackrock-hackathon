package com.blackrock.challenge.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateParser() {
    }

    /**
     * Parses a date string in "yyyy-MM-dd HH:mm:ss" format.
     */
    public static LocalDateTime parse(String dateStr) throws DateTimeParseException {
        return LocalDateTime.parse(dateStr, FORMATTER);
    }

    /**
     * Formats a LocalDateTime to "yyyy-MM-dd HH:mm:ss" string.
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
