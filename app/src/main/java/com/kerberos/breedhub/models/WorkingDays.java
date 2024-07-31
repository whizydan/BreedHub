package com.kerberos.breedhub.models;

public class WorkingDays {

    public WorkingDays(String day, String time){
        this.day = day;
        this.time = time;
    }
    private String day, time;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
