package com.farast.utuapi.data.interfaces;

import com.farast.utuapi.data.AdditionalInfo;

import java.util.List;

/**
 * Created by cendr_000 on 19.08.2016.
 */
public interface Infoable extends Identifiable {
    List<AdditionalInfo> getAdditionalInfos();
}
