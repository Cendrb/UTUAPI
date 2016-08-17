package com.farast.utuapi.data;

/**
 * Created by cendr_000 on 16.08.2016.
 */
public class User implements Identifiable {
    private int id;
    private boolean admin;
    private int sclassId;

    User(int id, boolean admin, int sclassId) {
        this.id = id;
        this.admin = admin;
        this.sclassId = sclassId;
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
}
