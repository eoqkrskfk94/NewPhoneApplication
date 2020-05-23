package com.mj.newphoneapplication.items;

public class PhoneSubItem {

    private String name;
    private String number;
    private String type;
    private String date;
    private int duration;
    private long diff_date;

    public PhoneSubItem() {

    }
    public PhoneSubItem(String name, String number, String type, String date, int duration, long diff_date) {
        this.name = name;
        this.number = number;
        this.type = type;
        this.date = date;
        this.duration = duration;
        this.diff_date = diff_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getDiff_date() {
        return diff_date;
    }

    public void setDiff_date(long diff_date) {
        this.diff_date = diff_date;
    }




}
