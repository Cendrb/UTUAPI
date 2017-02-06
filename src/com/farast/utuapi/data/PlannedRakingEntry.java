package com.farast.utuapi.data;

/**
 * Created by cendr_000 on 26.08.2016.
 */
public class PlannedRakingEntry implements Identifiable {

    private int id;
    private String description;
    private boolean finished;
    private String grade;
    private int sortingOrder;
    private ClassMember classMember;

    public PlannedRakingEntry(int id, String description, boolean finished, String grade, int sortingOrder, ClassMember classMember) {
        this.id = id;
        this.description = description;
        this.finished = finished;
        this.grade = grade;
        this.sortingOrder = sortingOrder;
        this.classMember = classMember;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getGrade() {
        return grade;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public ClassMember getClassMember() {
        return classMember;
    }
}
