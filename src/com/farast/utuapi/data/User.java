package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;

/**
 * Created by cendr_000 on 16.08.2016.
 */
public class User implements Identifiable {
    private int id;
    private boolean admin;
    private int sclassId;
    private String email;
    private ClassMember classMember;

    User(int id, boolean admin, int sclassId, String email, ClassMember classMember) {
        this.id = id;
        this.admin = admin;
        this.sclassId = sclassId;
        this.email = email;
        this.classMember = classMember;
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public int getSclassId() {
        return sclassId;
    }

    public String getEmail() {
        return email;
    }

    public ClassMember getClassMember() {
        return classMember;
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.USER;
    }
}
