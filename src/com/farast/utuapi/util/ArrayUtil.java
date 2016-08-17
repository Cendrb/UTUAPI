package com.farast.utuapi.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 30.07.2016.
 */
public final class ArrayUtil {
    private ArrayUtil() {

    }

    public static List<String> parseStringArray(String rubyArrayString) {
        ArrayList<String> result = new ArrayList<>();
        String sgroupsIdsString = rubyArrayString.substring(1, rubyArrayString.length() - 1).replaceAll("\\s", ""); // remoave first/last elements + spaces
        String[] split = sgroupsIdsString.split("\\,"); // split by periods
        for (String sgroupIdString : split) {
            if (sgroupIdString.length() > 0)
                result.add(sgroupIdString);
        }
        return result;
    }

    public static List<Integer> parseIntArray(String rubyArrayString) {
        ArrayList<Integer> result = new ArrayList<>();
        String sgroupsIdsString = rubyArrayString.substring(1, rubyArrayString.length() - 1).replaceAll("\\s", ""); // remoave first/last elements + spaces
        String[] split = sgroupsIdsString.split("\\,"); // split by periods
        for (String sgroupIdString : split) {
            if (sgroupIdString.length() > 0)
                result.add(Integer.parseInt(sgroupIdString));
        }
        return result;
    }

    public static String join(String[] strings, int startIndex, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i=startIndex; i < strings.length; i++) {
            if (i != startIndex) sb.append(separator);
            sb.append(strings[i]);
        }
        return sb.toString();
    }
}
