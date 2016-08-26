package com.farast.utuapi.data;

import com.farast.utuapi.util.FormData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cendr_000 on 26.07.2016.
 */
public class Article implements Updatable, Identifiable, Infoable {
    private int id;
    private String title;
    private String description;
    private Date publishedOn;
    private Date showInDetailsUntil;
    private Sgroup sgroup;
    private List<AdditionalInfo> additionalInfos;

    Article(int id, String title, String description, Date publishedOn, Date showInDetailsUntil, Sgroup sgroup, List<AdditionalInfo> additionalInfos) {
        this.id = id;
        this.title = title;
        this.description = description;
        if (publishedOn != null)
            this.publishedOn = new Date(publishedOn.getTime());
        else
            publishedOn = null;
        this.sgroup = sgroup;
        if (showInDetailsUntil != null)
            this.showInDetailsUntil = new Date(showInDetailsUntil.getTime());
        else
            showInDetailsUntil = null;
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
        if (isPublished())
            return new Date(publishedOn.getTime());
        else
            return null;
    }

    public Date getShowInDetailsUntil()
    {
        if (isShowInDetails())
            return new Date(showInDetailsUntil.getTime());
        else
            return null;
    }

    public boolean isPublished() {
        return publishedOn != null;
    }

    public boolean isShowInDetails() {
        return showInDetailsUntil != null;
    }

    public Sgroup getSgroup() {
        return sgroup;
    }

    public List<AdditionalInfo> getAdditionalInfos() {
        return new ArrayList<>(additionalInfos);
    }

    @Override
    public FormData getFormData() {
        FormData formData = new FormData();
        if (id != -1)
            formData.put("id", id);
        formData.put("title", title);
        formData.put("text", description);
        if (publishedOn != null) {
            formData.put("published_on", publishedOn);
            formData.put("published", true);
        } else
            formData.put("published", false);
        if (showInDetailsUntil != null) {
            formData.put("show_in_details_until", showInDetailsUntil);
            formData.put("show_in_details", true);
        } else
            formData.put("show_in_details", false);
        formData.put("sgroup_id", sgroup);
        formData.put("additional_info_ids", additionalInfos);
        return formData;
    }

    @Override
    public String getTypeString() {
        return "article";
    }

    @Override
    public String getOnelineRepresentation() {
        return title;
    }
}
