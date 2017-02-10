package com.farast.utuapi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public final class DateUtil {
    private DateUtil() {

    }

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat CZ_SHORT_DATE_FORMAT = new SimpleDateFormat("dd. MM.");
    public static final SimpleDateFormat CZ_DATE_FORMAT = new SimpleDateFormat("dd. MM. yyyy");
    public static final SimpleDateFormat CZ_WEEK_DATE_FORMAT = new SimpleDateFormat("EEE dd. MM.");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static Date getZeroTimeCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
