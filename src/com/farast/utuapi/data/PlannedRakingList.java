package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 26.08.2016.
 */
public class PlannedRakingList implements Identifiable, Infoable, OnelineRepresentable  {

    private int id;
    private String title;
    private Subject subject;
    private Sgroup sgroup;
    private int rektPerRound;
    private List<AdditionalInfo> additionalInfos;
    private List<PlannedRakingRound> plannedRakingRounds;

    PlannedRakingList(int id, String title, Subject subject, Sgroup sgroup, int rektPerRound, List<AdditionalInfo> additionalInfos, List<PlannedRakingRound> plannedRakingRounds) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.sgroup = sgroup;
        this.rektPerRound = rektPerRound;
        this.additionalInfos = new ArrayList<>(additionalInfos);
        this.plannedRakingRounds = new ArrayList<>(plannedRakingRounds);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Subject getSubject() {
        return subject;
    }

    public Sgroup getSgroup() {
        return sgroup;
    }

    public int getRektPerRound() {
        return rektPerRound;
    }

    public List<AdditionalInfo> getAdditionalInfos() {
        return new ArrayList<>(additionalInfos);
    }

    public List<PlannedRakingRound> getPlannedRakingRounds() {
        return new ArrayList<>(plannedRakingRounds);
    }

    @Override
    public String getOnelineRepresentation() {
        return title;
    }
}
