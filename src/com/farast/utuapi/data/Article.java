package com.farast.utuapi.data;

import com.farast.utuapi.util.FormData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Article extends Updatable implements Identifiable, Titleable {
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
        this.additionalInfos = new ArrayList<>(additionalInfos);
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

    @Override
    FormData getFormData() {
        FormData formData = new FormData();
        if (id != -1)
            formData.put("id", id);
        formData.put("title", title);
        formData.put("description", description);
        formData.put("published_on", publishedOn);
        formData.put("sgroup_id", sgroup);
        formData.put("additional_info_ids", additionalInfos);
        return formData;
    }

    @Override
    String getTypeString() {
        return "article";
    }
}
