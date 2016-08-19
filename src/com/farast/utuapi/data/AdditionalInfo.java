package com.farast.utuapi.data;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class AdditionalInfo implements Identifiable, Selectable {
    private int id;
    private String name;
    private String url;
    private Subject subject;

    AdditionalInfo(int id, String name, String url, Subject subject) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return getName();
    }
}
