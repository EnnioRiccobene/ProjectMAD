package com.madgroup.madproject;

import android.graphics.Bitmap;

public class Restaurant {

    private int id;
    private String name;
    private String phoneNumber;
    private String address;
    private String info;
    private String foodCategory;
    private Bitmap photo;
    private String minOrder;  //todo aggiungerlo al profilo company
    private String deliveryCost; //todo aggiungerlo al profilo company
    private String mondayOpeningHours;
    private String tuesdayOpeningHours;
    private String wednesdayOpeningHours;
    private String thursdayOpeningHours;
    private String fridayOpeningHours;
    private String saturdayOpeningHours;
    private String sundayOpeningHours;

    public Restaurant(int id, String name, String phoneNumber, String address, String info,
                      String foodCategory, Bitmap photo, String minOrder, String deliveryCost,
                      String mondayOpeningHours, String tuesdayOpeningHours,
                      String wednesdayOpeningHours, String thursdayOpeningHours,
                      String fridayOpeningHours, String saturdayOpeningHours,
                      String sundayOpeningHours) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.info = info;
        this.foodCategory = foodCategory;
        this.photo = photo;
        this.minOrder = minOrder;
        this.deliveryCost = deliveryCost;
        this.mondayOpeningHours = mondayOpeningHours;
        this.tuesdayOpeningHours = tuesdayOpeningHours;
        this.wednesdayOpeningHours = wednesdayOpeningHours;
        this.thursdayOpeningHours = thursdayOpeningHours;
        this.fridayOpeningHours = fridayOpeningHours;
        this.saturdayOpeningHours = saturdayOpeningHours;
        this.sundayOpeningHours = sundayOpeningHours;
    }

    public Restaurant(int id, String name, String phoneNumber, String address, String foodCategory,
                      Bitmap photo, String minOrder, String deliveryCost, String mondayOpeningHours,
                      String tuesdayOpeningHours, String wednesdayOpeningHours,
                      String thursdayOpeningHours, String fridayOpeningHours,
                      String saturdayOpeningHours, String sundayOpeningHours) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.foodCategory = foodCategory;
        this.photo = photo;
        this.minOrder = minOrder;
        this.deliveryCost = deliveryCost;
        this.mondayOpeningHours = mondayOpeningHours;
        this.tuesdayOpeningHours = tuesdayOpeningHours;
        this.wednesdayOpeningHours = wednesdayOpeningHours;
        this.thursdayOpeningHours = thursdayOpeningHours;
        this.fridayOpeningHours = fridayOpeningHours;
        this.saturdayOpeningHours = saturdayOpeningHours;
        this.sundayOpeningHours = sundayOpeningHours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(String minOrder) {
        this.minOrder = minOrder;
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(String deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String getMondayOpeningHours() {
        return mondayOpeningHours;
    }

    public void setMondayOpeningHours(String mondayOpeningHours) {
        this.mondayOpeningHours = mondayOpeningHours;
    }

    public String getTuesdayOpeningHours() {
        return tuesdayOpeningHours;
    }

    public void setTuesdayOpeningHours(String tuesdayOpeningHours) {
        this.tuesdayOpeningHours = tuesdayOpeningHours;
    }

    public String getWednesdayOpeningHours() {
        return wednesdayOpeningHours;
    }

    public void setWednesdayOpeningHours(String wednesdayOpeningHours) {
        this.wednesdayOpeningHours = wednesdayOpeningHours;
    }

    public String getThursdayOpeningHours() {
        return thursdayOpeningHours;
    }

    public void setThursdayOpeningHours(String thursdayOpeningHours) {
        this.thursdayOpeningHours = thursdayOpeningHours;
    }

    public String getFridayOpeningHours() {
        return fridayOpeningHours;
    }

    public void setFridayOpeningHours(String fridayOpeningHours) {
        this.fridayOpeningHours = fridayOpeningHours;
    }

    public String getSaturdayOpeningHours() {
        return saturdayOpeningHours;
    }

    public void setSaturdayOpeningHours(String saturdayOpeningHours) {
        this.saturdayOpeningHours = saturdayOpeningHours;
    }

    public String getSundayOpeningHours() {
        return sundayOpeningHours;
    }

    public void setSundayOpeningHours(String sundayOpeningHours) {
        this.sundayOpeningHours = sundayOpeningHours;
    }
}
