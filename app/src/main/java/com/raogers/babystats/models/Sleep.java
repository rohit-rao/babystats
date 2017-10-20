package com.raogers.babystats.models;

import com.google.firebase.database.Exclude;

import java.util.GregorianCalendar;

public class Sleep {

    public Long startTimeInNegativeMillis;
    public Long durationInMillis;
    public String comments;

    public Sleep() {
        startTimeInNegativeMillis = -1 * GregorianCalendar.getInstance().getTimeInMillis();
        durationInMillis = 0L;
        comments = "";
    }

    @Exclude
    public long getStartTimeInMillis() {
        return -1 * startTimeInNegativeMillis;
    }

    @Exclude
    public void setStartTimeInMillis(long millis) {
        startTimeInNegativeMillis = -1 * millis;
    }
}
