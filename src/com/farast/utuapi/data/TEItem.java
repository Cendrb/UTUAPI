package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 27.07.2016.
 */
public interface TEItem {

    int getId();

    String getTitle();

    String getDescription();

    Date getDate();

    Subject getSubject();

    Sgroup getSgroup();

    List<AdditionalInfo> getAdditionalInfos();

    boolean isDone();

    List<Lesson> getLessons();
}
