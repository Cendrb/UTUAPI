package com.farast.utuapi.data;

import com.farast.utuapi.util.CollectionUtil;
import com.farast.utuapi.util.functional_interfaces.Predicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cendr_000 on 26.08.2016.
 */
public class PlannedRakingRound implements Identifiable, OnelineRepresentable {

    public static final Comparator<PlannedRakingRound> COMPARATOR = new Comparator<PlannedRakingRound>() {
        @Override
        public int compare(PlannedRakingRound o1, PlannedRakingRound o2) {
            if (o1.getRoundNumber() == o2.getRoundNumber())
                return 0;
            else if (o1.getRoundNumber() > o2.getRoundNumber())
                return 1;
            else
                return -1;
        }
    };
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


    public int getAlreadyRektCount() {
        return CollectionUtil.filter(plannedRakingEntries, new Predicate<PlannedRakingEntry>() {
            @Override
            public boolean test(PlannedRakingEntry object) {
                return object.isFinished();
            }
        }).size();
    }

    public int getSignedUpRemainingCount() {
        return CollectionUtil.filter(plannedRakingEntries, new Predicate<PlannedRakingEntry>() {
            @Override
            public boolean test(PlannedRakingEntry object) {
                return !object.isFinished();
            }
        }).size();
    }
}
