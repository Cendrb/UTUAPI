package com.farast.utuapi.data;

import com.farast.utuapi.util.FormData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Exam extends Updatable implements Identifiable, TEItem, Titleable, Infoable {

    private int id;
    private String title;
    private String description;
    private Date date;
    private Subject subject;
    private Sgroup sgroup;
    private List<AdditionalInfo> additionalInfos;
    private boolean done;
    private List<Lesson> lessons;
    private Type type;
    Exam(int id, String title, String description, Date date, Subject subject, Sgroup sgroup, List<AdditionalInfo> additionalInfos, boolean done, List<Lesson> lessons, Type type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = new Date(date.getTime());
        this.subject = subject;
        this.sgroup = sgroup;
        this.additionalInfos = new ArrayList<>(additionalInfos);
        this.done = done;
        this.lessons = lessons;
        this.type = type;
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
    FormData getFormData() {
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

    public Type getType() {
        return type;
    }

    @Override
    String getTypeString() {
        switch (type)
        {
            case written:
                return "written_exam";
            case raking:
                return "raking_exam";
            default:
                return "this type of Exam is not supported";
        }
    }

    public enum Type {raking, written}
}
