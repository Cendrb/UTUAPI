package com.farast.utuapi.util;

import java.text.DecimalFormat;

/**
 * Created by cendr on 10/02/2017.
 */
public class NumberFormatUtil {
    public static String formatWithLeadingZeros(int number, int total) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < total; x++) {
            builder.append("0");
        }
        DecimalFormat decimalFormat = new DecimalFormat(builder.toString());
        return decimalFormat.format(number);
    }
}
