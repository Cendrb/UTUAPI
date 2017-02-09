package com.farast.utuapi.data;

import com.farast.utuapi.data.common.UtuType;
import com.farast.utuapi.data.interfaces.Identifiable;
import com.farast.utuapi.data.interfaces.OnelineRepresentable;

import java.util.Date;

/**
 * Created by cendr on 09/02/2017.
 */
public class Service implements Identifiable, OnelineRepresentable {

    private int id;
    private Date start;
    private Date end;
    private ClassMember firstMember;
    private ClassMember secondMember;

    public Service(int id, Date start, Date end, ClassMember firstMember, ClassMember secondMember) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.firstMember = firstMember;
        this.secondMember = secondMember;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public ClassMember getFirstMember() {
        return firstMember;
    }

    public ClassMember getSecondMember() {
        return secondMember;
    }

    @Override
    public String getOnelineRepresentation() {
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public UtuType getUtuType() {
        return UtuType.SERVICE;
    }
}
