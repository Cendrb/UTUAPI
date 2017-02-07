package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;
import com.farast.utuapi.data.interfaces.OnelineRepresentable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 28.07.2016.
 */
public class Timetable implements Identifiable, OnelineRepresentable {
    private int id;
    private String name;
    private List<Sgroup> validSgroups;
    private List<SchoolDay> schoolDays;

    Timetable(int id, String name, List<Sgroup> validSgroups, List<SchoolDay> schoolDays) {
        this.id = id;
        this.name = name;
        this.validSgroups = new ArrayList<>(validSgroups);
        this.schoolDays = new ArrayList<>(schoolDays);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Sgroup> getValidSgroups() {
        return new ArrayList<>(validSgroups);
    }

    public List<SchoolDay> getSchoolDays() {
        return new ArrayList<>(schoolDays);
    }

    @Override
    public String getOnelineRepresentation() {
        return name;
    }

    @Override
    public String toString() {
        return getOnelineRepresentation();
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.TIMETABLE;
    }
}
