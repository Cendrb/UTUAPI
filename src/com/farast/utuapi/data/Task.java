package com.farast.utuapi.data;

import com.farast.utuapi.util.FormData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Task implements Updatable, Identifiable, TEItem, Titleable, Infoable {
    private int id;
    private String title;
    private String description;
    private Date date;
    private Subject subject;
    private Sgroup sgroup;
    private List<AdditionalInfo> additionalInfos;
    private boolean done;
    private List<Lesson> lessons;

    Task(int id, String title, String description, Date date, Subject subject, Sgroup sgroup, List<AdditionalInfo> additionalInfos, boolean done, List<Lesson> lessons) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = new Date(date.getTime());
        this.subject = subject;
        this.sgroup = sgroup;
        this.additionalInfos = new ArrayList<>(additionalInfos);
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

    @Override
    public FormData getFormData() {
        FormData formData = new FormData();
        if (id != -1)
            formData.put("id", id);
        formData.put("title", title);
        formData.put("description", description);
        formData.put("date", date);
        formData.put("subject_id", subject);
        formData.put("sgroup_id", sgroup);
        formData.put("additional_info_ids", additionalInfos);
        return formData;
    }

    @Override
    public String getTypeString() {
        return "task";
    }
}
