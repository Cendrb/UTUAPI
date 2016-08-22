package com.farast.utuapi.data;

import java.util.List;

/**
 * Created by cendr_000 on 19.08.2016.
 */
public interface Infoable extends Identifiable {
    List<AdditionalInfo> getAdditionalInfos();
}
