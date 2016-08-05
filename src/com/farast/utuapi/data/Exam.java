package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Exam implements Identifiable, TEItem {
    private int id;
    private String title;
    private String description;
    private Date date;
    private Subject subject;
    private Sgroup sgroup;
    private List<AdditionalInfo> additionalInfos;
    private boolean done;
    private List<Lesson> lessons;

    Exam(int id, String title, String description, Date date, Subject subject, Sgroup sgroup, List<AdditionalInfo> additionalInfos, boolean done, List<Lesson> lessons) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = new Date(date.getTime());
        this.subject = subject;
        this.sgroup = sgroup;
        this.additionalInfos = additionalInfos;
        this.done = done;
        this.lessons = lessons;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    public Subject getSubject() {
        return subject;
    }

    public Sgroup getSgroup() {
        return sgroup;
    }

    public List<AdditionalInfo> getAdditionalInfos() {
        return new ArrayList<>(additionalInfos);
    }

    public boolean isDone() {
        return done;
    }

    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }
}
