package com.farast.utuapi.data.interfaces;

import com.farast.utuapi.data.AdditionalInfo;
import com.farast.utuapi.data.Lesson;
import com.farast.utuapi.data.Sgroup;
import com.farast.utuapi.data.Subject;

import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 27.07.2016.
 */
public interface TEItem extends Identifiable {

    String getTitle();

    String getDescription();

    Date getDate();

    Subject getSubject();

    Sgroup getSgroup();

    List<AdditionalInfo> getAdditionalInfos();

    boolean isDone();

    List<Lesson> getLessons();
}
