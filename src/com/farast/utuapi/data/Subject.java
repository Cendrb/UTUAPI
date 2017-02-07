package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;
import com.farast.utuapi.data.interfaces.OnelineRepresentable;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class Subject implements Identifiable, OnelineRepresentable {
    private int id;
    private String name;

    Subject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getOnelineRepresentation() {
        return name;
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.SUBJECT;
    }
}
