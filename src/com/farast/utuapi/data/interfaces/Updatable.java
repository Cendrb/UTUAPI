package com.farast.utuapi.data.interfaces;

import com.farast.utuapi.util.FormData;

/**
 * Created by cendr_000 on 17.08.2016.
 */
public interface Updatable extends Identifiable, OnelineRepresentable {
    FormData getFormData();
    String getTypeString();
}
