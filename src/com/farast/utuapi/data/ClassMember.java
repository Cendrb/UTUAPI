package com.farast.utuapi.data;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class ClassMember implements Identifiable {
    private int id;
    private String firstName;
    private String lastName;

    ClassMember(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
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
}
