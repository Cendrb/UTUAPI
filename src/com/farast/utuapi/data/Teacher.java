package com.farast.utuapi.data;

/**
 * Created by cendr_000 on 28.07.2016.
 */
public class Teacher implements Identifiable {
    private int id;
    private String name;
    private String abbr;

    Teacher(int id, String name, String abbr) {
        this.id = id;
        this.name = name;
        this.abbr = abbr;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }
}
