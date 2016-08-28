package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class ClassMember implements Identifiable {
    private int id;
    private String firstName;
    private String lastName;
    private List<Sgroup> sgroups;

    ClassMember(int id, String firstName, String lastName, List<Sgroup> sgroups) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sgroups = new ArrayList<>(sgroups);
    }

    public int getId() {
        return id;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Sgroup> getSgroups() {
        return new ArrayList<>(sgroups);
    }
}
