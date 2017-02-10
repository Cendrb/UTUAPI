package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;

/**
 * Created by cendr_000 on 28.07.2016.
 */
public class Lesson implements Identifiable {
    private int id;
    private int serialNumber;
    private String room;
    private boolean notNormal;
    private String notNormalComment;
    private String eventName;
    private Subject subject;
    private Teacher teacher;
    private LessonTiming lessonTiming;

    Lesson(int id, int serialNumber, String room, boolean notNormal, String notNormalComment, String eventName, Subject subject, Teacher teacher, LessonTiming lessonTiming) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.room = room;
        this.notNormal = notNormal;
        this.notNormalComment = notNormalComment;
        this.eventName = eventName;
        this.subject = subject;
        this.teacher = teacher;
        this.lessonTiming = lessonTiming;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getRoom() {
        return room;
    }

    public boolean isNotNormal() {
        return notNormal;
    }

    public String getNotNormalComment() {
        return notNormalComment;
    }

    public String getEventName() {
        return eventName;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public LessonTiming getLessonTiming() {
        return lessonTiming;
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.LESSON;
    }
}
