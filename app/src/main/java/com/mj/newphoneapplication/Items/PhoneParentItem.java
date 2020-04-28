package com.mj.newphoneapplication.Items;

import java.util.ArrayList;

public class PhoneParentItem {

    private String itemDate;
    private ArrayList<PhoneSubItem> phoneSubItems;

    public PhoneParentItem(String itemDate, ArrayList<PhoneSubItem> phoneSubItems) {
        this.itemDate = itemDate;
        this.phoneSubItems = phoneSubItems;
    }

    public String getItemDate() {
        return itemDate;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public ArrayList<PhoneSubItem> getPhoneSubItems() {
        return phoneSubItems;
    }

    public void setPhoneSubItems(ArrayList<PhoneSubItem> phoneSubItems) {
        this.phoneSubItems = phoneSubItems;
    }






}
