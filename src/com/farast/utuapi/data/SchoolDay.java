package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 28.07.2016.
 */
public class SchoolDay implements Identifiable {
    private int id;
    private Date date;
    private List<Lesson> lessons;

    SchoolDay(int id, Date date, List<Lesson> lessons) {
        this.id = id;
        this.date = new Date(date.getTime());
        this.lessons = new ArrayList<>(lessons);
    }

    @Override
    public int getId() {
        return id;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.SCHOOL_DAY;
    }
}
