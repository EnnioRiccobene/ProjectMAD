package com.madgroup.sdk;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class RestaurantProfile implements Parcelable {

    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    private String email;
    private String foodCategory;
    private String minOrder;
    private String deliveryCost;
    private String mondayOpeningHours;
    private String tuesdayOpeningHours;
    private String wednesdayOpeningHours;
    private String thursdayOpeningHours;
    private String fridayOpeningHours;
    private String saturdayOpeningHours;
    private String sundayOpeningHours;
    private String additionalInformation;
    private String fCategoryANDdCost;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RestaurantProfile() {}

    public RestaurantProfile(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = "";
        this.address = "";
        this.foodCategory = "";
        this.minOrder = "0,00 €";
        this.deliveryCost = "0,00 €";
        this.mondayOpeningHours = "Closed";
        this.tuesdayOpeningHours = "Closed";
        this.wednesdayOpeningHours = "Closed";
        this.thursdayOpeningHours = "Closed";
        this.fridayOpeningHours = "Closed";
        this.saturdayOpeningHours = "Closed";
        this.sundayOpeningHours = "Closed";
        this.additionalInformation = "Closed";
        this.fCategoryANDdCost = foodCategory + "_" + deliveryCost.replace(".", "").replace(",", "");
    }

    public RestaurantProfile(String id, String name, String phoneNumber, String address, String email, String foodCategory, String minOrder, String deliveryCost, String mondayOpeningHours, String tuesdayOpeningHours, String wednesdayOpeningHours, String thursdayOpeningHours, String fridayOpeningHours, String saturdayOpeningHours, String sundayOpeningHours, String additionalInformation) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.foodCategory = foodCategory;
        this.minOrder = minOrder;
        this.deliveryCost = deliveryCost;
        this.mondayOpeningHours = mondayOpeningHours;
        this.tuesdayOpeningHours = tuesdayOpeningHours;
        this.wednesdayOpeningHours = wednesdayOpeningHours;
        this.thursdayOpeningHours = thursdayOpeningHours;
        this.fridayOpeningHours = fridayOpeningHours;
        this.saturdayOpeningHours = saturdayOpeningHours;
        this.sundayOpeningHours = sundayOpeningHours;
        this.additionalInformation = additionalInformation;
        this.fCategoryANDdCost = foodCategory + "_" + deliveryCost.replace(".", "").replace(",", "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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



    public String getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public String getMinOrder() {
        return minOrder;
    }

    public String getfCategoryANDdCost() {
        return fCategoryANDdCost;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.address);
        dest.writeString(this.foodCategory);
        dest.writeString(this.minOrder);
        dest.writeString(this.deliveryCost);
        dest.writeString(this.mondayOpeningHours);
        dest.writeString(this.tuesdayOpeningHours);
        dest.writeString(this.wednesdayOpeningHours);
        dest.writeString(this.thursdayOpeningHours);
        dest.writeString(this.fridayOpeningHours);
        dest.writeString(this.saturdayOpeningHours);
        dest.writeString(this.sundayOpeningHours);
    }

    protected RestaurantProfile(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.phoneNumber = in.readString();
        this.address = in.readString();
        this.foodCategory = in.readString();
        this.minOrder = in.readString();
        this.deliveryCost = in.readString();
        this.mondayOpeningHours = in.readString();
        this.tuesdayOpeningHours = in.readString();
        this.wednesdayOpeningHours = in.readString();
        this.thursdayOpeningHours = in.readString();
        this.fridayOpeningHours = in.readString();
        this.saturdayOpeningHours = in.readString();
        this.sundayOpeningHours = in.readString();
    }

    public static final Parcelable.Creator<RestaurantProfile> CREATOR = new Parcelable.Creator<RestaurantProfile>() {
        @Override
        public RestaurantProfile createFromParcel(Parcel source) {
            return new RestaurantProfile(source);
        }

        @Override
        public RestaurantProfile[] newArray(int size) {
            return new RestaurantProfile[size];
        }
    };
}