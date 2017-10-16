package com.raogers.babystats.models;

import com.google.firebase.database.Exclude;

import java.util.GregorianCalendar;

public class Diaper {

    public Long timeInNegativeMillis;
    public Boolean hasPoop;
    public Boolean hasPee;
    public String comments;

    public Diaper() {
        // Default values.
        timeInNegativeMillis = -(GregorianCalendar.getInstance().getTimeInMillis());
        hasPoop = false;
        hasPee = false;
        comments = "";
    }

    @Exclude
    public long getTimeInMillis() {
        return -1 * timeInNegativeMillis;
    }

    @Exclude
    public void setTimeInMillis(long timeInMillis) {
        timeInNegativeMillis = -1 * timeInMillis;
    }

}
