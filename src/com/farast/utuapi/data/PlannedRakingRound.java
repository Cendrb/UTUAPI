package com.farast.utuapi.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cendr_000 on 26.08.2016.
 */
public class PlannedRakingRound implements Identifiable, OnelineRepresentable {

    private int id;
    private int roundNumber;
    private List<PlannedRakingEntry> plannedRakingEntries;


    public PlannedRakingRound(int id, int roundNumber, List<PlannedRakingEntry> plannedRakingEntries) {
        this.id = id;
        this.roundNumber = roundNumber;
        this.plannedRakingEntries = new ArrayList<>(plannedRakingEntries);
    }

    @Override
    public int getId() {
        return id;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public List<PlannedRakingEntry> getPlannedRakingEntries() {
        return new ArrayList<>(plannedRakingEntries);
    }

    @Override
    public String getOnelineRepresentation() {
        return String.valueOf(roundNumber) + ". round";
    }
}
