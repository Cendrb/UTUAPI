package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Event implements Identifiable {
    private int id;
    private String title;
    private String description;
    private String location;
    private int price;
    private Date start;
    private Date end;
    private Date payDate;
    private Sgroup sgroup;
    private List<AdditionalInfo> additionalInfos;
    private boolean done;

    Event(int id, String title, String description, String location, int price, Date start, Date end, Date payDate, Sgroup sgroup, List<AdditionalInfo> additionalInfos, boolean done) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        this.payDate = new Date(payDate.getTime());
        this.sgroup = sgroup;
        this.additionalInfos = additionalInfos;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }

    public Date getStart() {
        return new Date(start.getTime());
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }

    public Date getPayDate() {
        return new Date(payDate.getTime());
    }

    public Sgroup getSgroup() {
        return sgroup;
    }

    public List<AdditionalInfo> getAdditionalInfos() {
        return new ArrayList<>(additionalInfos);
    }

    public boolean isDone() {
        return done;
    }
}
