package com.farast.utuapi.data;

import com.farast.utuapi.util.NumberFormatUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by cendr on 10/02/2017.
 */
public class AbsoluteTime {
    private int hours;
    private int minutes;
    private int seconds;

    public static AbsoluteTime parse(int totalSeconds) {
        return new AbsoluteTime(totalSeconds / 3600, (totalSeconds % 3600) / 60, (totalSeconds % 3600) % 60);
    }

    public AbsoluteTime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public static AbsoluteTime add(AbsoluteTime first, AbsoluteTime second) {
        return AbsoluteTime.parse(first.getTotalSeconds() + second.getTotalSeconds());
    }

    public int getTotalSeconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    public String getHoursMinutesString() {
        return NumberFormatUtil.formatWithLeadingZeros(hours, 2) + ":" +
                NumberFormatUtil.formatWithLeadingZeros(minutes, 2);
    }

    public String getHoursMinutesSecondsString() {
        return NumberFormatUtil.formatWithLeadingZeros(hours, 2) + ":" +
                NumberFormatUtil.formatWithLeadingZeros(minutes, 2) + ":" +
                NumberFormatUtil.formatWithLeadingZeros(seconds, 2);
    }

    public Date getOffsetDate(Date base) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(Calendar.HOUR, hours);
        calendar.add(Calendar.MINUTE, minutes);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }
}
