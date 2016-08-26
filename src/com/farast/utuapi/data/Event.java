package com.farast.utuapi.data;

import com.farast.utuapi.util.FormData;

import java.util.*;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Event implements Updatable, Identifiable, Infoable {
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
        this.additionalInfos = new ArrayList<>(additionalInfos);
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

    @Override
    public FormData getFormData() {
       FormData formData = new FormData();
        if (id != -1)
            formData.put("id", id);
        formData.put("title", title);
        formData.put("description", description);
        formData.put("location", location);
        formData.put("price", price);
        formData.put("event_start", start);
        formData.put("event_end", end);
        formData.put("pay_date", payDate);
        formData.put("sgroup_id", sgroup.getId());
        formData.put("additional_info_ids", additionalInfos);
        return formData;
    }

    @Override
    public String getTypeString() {
        return "event";
    }

    @Override
    public String getOnelineRepresentation() {
        return title;
    }
}
