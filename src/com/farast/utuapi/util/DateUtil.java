package com.farast.utuapi.util;

import java.text.SimpleDateFormat;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public final class DateUtil {
    private DateUtil() {

    }

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss zzz");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-DD");
    public static final SimpleDateFormat CZ_SHORT_DATE_FORMAT = new SimpleDateFormat("DD. MM.");
    public static final SimpleDateFormat CZ_DATE_FORMAT = new SimpleDateFormat("DD. MM. yyyy");
    public static final SimpleDateFormat CZ_WEEK_DATE_FORMAT = new SimpleDateFormat("EEE DD. MM.");
}
