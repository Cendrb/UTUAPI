package com.farast.utuapi.data;

import com.farast.utuapi.util.FormData;

import java.util.HashMap;

/**
 * Created by cendr_000 on 17.08.2016.
 */
public interface Updatable extends Identifiable, Titleable {
    FormData getFormData();
    String getTypeString();
}
