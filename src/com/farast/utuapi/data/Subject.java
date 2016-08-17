package com.farast.utuapi.data;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class Subject implements Identifiable {
    private int id;
    private String name;

    Subject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
