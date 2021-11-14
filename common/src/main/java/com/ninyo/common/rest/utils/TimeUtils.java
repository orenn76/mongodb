package com.ninyo.common.rest.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TimeUtils {

    public static LocalDate toLocalDate(Date date) {
        LocalDate ld = null;
        if (date != null) {
            ld = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
        }
        return ld;
    }

    public static Date toDate(LocalDate ld) {
        Date date = null;
        if (ld != null) {
            date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return date;
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        LocalDateTime ldt = null;
        if (date != null) {
            ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return ldt;
    }

    public static Date toDate(LocalDateTime ldt) {
        Date date = null;
        if (ldt != null) {
            date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
        return date;
    }
}
