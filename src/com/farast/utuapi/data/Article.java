package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Article implements Identifiable {
    private int id;
    private String title;
    private String description;
    private Date publishedOn;
    private Sgroup sgroup;
    private List<AdditionalInfo> additionalInfos;

    Article(int id, String title, String description, Date publishedOn, Sgroup sgroup, List<AdditionalInfo> additionalInfos) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.publishedOn = new Date(publishedOn.getTime());
        this.sgroup = sgroup;
        this.additionalInfos = additionalInfos;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getPublishedOn() {
        return new Date(publishedOn.getTime());
    }

    public Sgroup getSgroup() {
        return sgroup;
    }

    public List<AdditionalInfo> getAdditionalInfos() {
        return new ArrayList<>(additionalInfos);
    }
}
