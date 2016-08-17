package com.farast.utuapi.util;

import com.farast.utuapi.data.Identifiable;

import java.util.*;

/**
 * Created by cendr_000 on 17.08.2016.
 */
public class FormData extends HashMap<String, String> {
    public String put(String key, int value) {
        return super.put(key, String.valueOf(value));
    }

    public String put(String key, boolean value) {
        return super.put(key, String.valueOf(value));
    }

    public String put(String key, String[] data) {
        return super.put(key, "[" + ArrayUtil.join(data, 0, ", ") + "]");
    }

    public String put(String key, Date date) {
        return super.put(key, DateUtil.DATE_FORMAT.format(date));
    }

    public String put(String key, List<? extends Identifiable> data) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for(int i = 0; i < data.size(); i++)
        {
            if(i != 0)
                builder.append(", ");
            builder.append(data.get(i).getId());
        }
        builder.append("]");
        return super.put(key, builder.toString());
    }

    public String put(String key, Object[] data) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for(int i = 0; i < data.length; i++)
        {
            if(i != 0)
                builder.append(", ");
            builder.append(data[i]);
        }
        builder.append("]");
        return super.put(key, builder.toString());
    }

    public String put(String key, Identifiable item) {
        return put(key, item.getId());
    }
}
