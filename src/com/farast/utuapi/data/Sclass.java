package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class Sclass implements Identifiable, Selectable {
    private int id;
    private String name;
    private List<ClassMember> classMembers;

    Sclass(int id, String name, List<ClassMember> classMembers) {
        this.id = id;
        this.name = name;
        this.classMembers = new ArrayList<ClassMember>(classMembers);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ClassMember> getClassMembers() {
        return new ArrayList<ClassMember>(classMembers);
    }

    @Override
    public String toString() {
        return name;
    }
}
