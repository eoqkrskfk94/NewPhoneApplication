package com.mj.newphoneapplication.Items;

public class MessageItem {
    private String address;
    private String msg;
    private String time;
    private long diff_date;

    public MessageItem(String address, String msg, String time, long diff_date) {
        this.address = address;
        this.msg = msg;
        this.time = time;
        this.diff_date = diff_date;
    }

    public MessageItem() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDiff_date() {
        return diff_date;
    }

    public void setDiff_date(long diff_date) {
        this.diff_date = diff_date;
    }

}
