package com.raogers.babystats.models;

import com.google.firebase.database.Exclude;

import java.util.GregorianCalendar;

public class Sleep {
    public static long END_TIME_UNSET = -1L;

    public Long startTimeInNegativeMillis;
    public Long endTimeInMillis;
    public String comments;

    public Sleep() {
        startTimeInNegativeMillis = -1 * GregorianCalendar.getInstance().getTimeInMillis();
        endTimeInMillis = END_TIME_UNSET;
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
