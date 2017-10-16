package com.raogers.babystats.models;

import com.google.firebase.database.Exclude;

import java.util.GregorianCalendar;

public class Food {
    public static int SIDE_UNKNOWN = -1;
    public static int SIDE_LEFT = 1;
    public static int SIDE_MOSTLY_LEFT = 2;
    public static int SIDE_BOTH = 3;
    public static int SIDE_MOSTLY_RIGHT = 4;
    public static int SIDE_RIGHT = 5;

    public Long startTimeInNegativeMillis;
    public Long nursingTimeInMillis;
    public Long milkOzInMillis;
    public Long formulaOzInMillis;
    public Integer nursingSide;
    public String comments;

    public Food() {
        startTimeInNegativeMillis = -1L * GregorianCalendar.getInstance().getTimeInMillis();
        nursingTimeInMillis = 0L;
        milkOzInMillis = 0L;
        formulaOzInMillis = 0L;
        nursingSide = SIDE_UNKNOWN;
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
