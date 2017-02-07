package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 25.07.2016.
 */
public class GroupCategory implements Identifiable {
    private int id;
    private String name;
    private List<Sgroup> sgroups;

    GroupCategory(int id, String name, List<Sgroup> sgroups) {
        this.id = id;
        this.name = name;
        this.sgroups = new ArrayList<>(sgroups);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Sgroup> getSgroups() {
        return new ArrayList<>(sgroups);
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.GROUP_CATEGORY;
    }
}
