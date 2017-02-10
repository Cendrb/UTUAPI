package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;

/**
 * Created by cendr on 10/02/2017.
 */
public class LessonTiming implements Identifiable {

    private int id;
    private int serialNumber;
    private AbsoluteTime start;
    private AbsoluteTime duration;

    public LessonTiming(int id, int serialNumber, AbsoluteTime start, AbsoluteTime duration) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.start = start;
        this.duration = duration;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public AbsoluteTime getStart() {
        return start;
    }

    public AbsoluteTime getDuration() {
        return duration;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.LESSON_TIMING;
    }
}
