package com.madgroup.madproject;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable {

    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    private String info;
    private String foodCategory;
    private String photo;
    private String minOrder;
    private String deliveryCost;
    private String mondayOpeningHours;
    private String tuesdayOpeningHours;
    private String wednesdayOpeningHours;
    private String thursdayOpeningHours;
    private String fridayOpeningHours;
    private String saturdayOpeningHours;
    private String sundayOpeningHours;
    private String fCategoryANDdCost;
    private String ratingAvg;
    private String ratingCounter;
    private String foodRatingAvg;

    //campi di supporto per le query al db
    private String catPizza;
    private String catSandwiches;
    private String catKebab;
    private String catItalian;
    private String catAmerican;
    private String catDesserts;
    private String catFry;
    private String catVegetarian;
    private String catAsian;
    private String catMediterranean;
    private String catSouthAmerican;

    private String catPizzaDel;
    private String catSandwichesDel;
    private String catKebabDel;
    private String catItalianDel;
    private String catAmericanDel;
    private String catDessertsDel;
    private String catFryDel;
    private String catVegetarianDel;
    private String catAsianDel;
    private String catMediterraneanDel;
    private String catSouthAmericanDel;

    public Restaurant() {
    }

    public Restaurant(String id, String name, String phoneNumber, String address, String info,
                      String foodCategory, String photo, String minOrder, String deliveryCost,
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
        this.fCategoryANDdCost = foodCategory + "_" + deliveryCost.replace(".", "").replace(",", "");
        this.ratingAvg = "0";
        this.foodRatingAvg = "0";
        this.ratingCounter = "0";

        if(foodCategory.contains("Pizza"))
            this.catPizza = "true";
        else
            this.catPizza = "false";
        this.catPizzaDel = catPizza + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Sandwiches") || foodCategory.contains("Panini"))
            this.catSandwiches = "true";
        else
            this.catSandwiches = "false";
        this.catSandwichesDel = catSandwiches + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Kebab"))
            this.catKebab = "true";
        else
            this.catKebab = "false";
        this.catKebabDel = catKebab + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Italian") || foodCategory.contains("Italiano"))
            this.catItalian = "true";
        else
            this.catItalian = "false";
        this.catItalianDel = catItalian + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("American") || foodCategory.contains("Americano"))
            this.catAmerican = "true";
        else
            this.catAmerican = "false";
        this.catAmericanDel = catAmerican + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Desserts") || foodCategory.contains("Dolci"))
            this.catDesserts = "true";
        else
            this.catDesserts = "false";
        this.catDessertsDel = catDesserts + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Fry") || foodCategory.contains("Fritti"))
            this.catFry = "true";
        else
            this.catFry = "false";
        this.catFryDel = catFry + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Vegetarian") || foodCategory.contains("Vegetariano"))
            this.catVegetarian = "true";
        else
            this.catVegetarian = "false";
        this.catVegetarianDel = catVegetarian + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Asian") || foodCategory.contains("Asiatico"))
            this.catAsian = "true";
        else
            this.catAsian = "false";
        this.catAsianDel = catAsian + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Mediterranean") || foodCategory.contains("Mediterraneo"))
            this.catMediterranean = "true";
        else
            this.catMediterranean = "false";
        this.catMediterraneanDel = catMediterranean + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("South American") || foodCategory.contains("Sud Americano"))
            this.catSouthAmerican = "true";
        else
            this.catSouthAmerican = "false";
        this.catSouthAmericanDel = catSouthAmerican + "_" + deliveryCost.replace(".", "").replace(",", "");
    }

    public Restaurant(String id, String name, String phoneNumber, String address, String foodCategory,
                      String photo, String minOrder, String deliveryCost, String mondayOpeningHours,
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
        this.fCategoryANDdCost = foodCategory + "_" + deliveryCost;
        this.ratingAvg = "0";
        this.foodRatingAvg = "0";
        this.ratingCounter = "0";

        if(foodCategory.contains("Pizza"))
            this.catPizza = "true";
        else
            this.catPizza = "false";
        this.catPizzaDel = catPizza + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Sandwiches") || foodCategory.contains("Panini"))
            this.catSandwiches = "true";
        else
            this.catSandwiches = "false";
        this.catSandwichesDel = catSandwiches + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Kebab"))
            this.catKebab = "true";
        else
            this.catKebab = "false";
        this.catKebabDel = catKebab + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Italian") || foodCategory.contains("Italiano"))
            this.catItalian = "true";
        else
            this.catItalian = "false";
        this.catItalianDel = catItalian + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("American") || foodCategory.contains("Americano"))
            catAmerican = "true";
        else
            catAmerican = "false";
        catAmericanDel = catAmerican + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Desserts") || foodCategory.contains("Dolci"))
            catDesserts = "true";
        else
            catDesserts = "false";
        catDessertsDel = catDesserts + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Fry") || foodCategory.contains("Fritti"))
            catFry = "true";
        else
            catFry = "false";
        catFryDel = catFry + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Vegetarian") || foodCategory.contains("Vegetariano"))
            catVegetarian = "true";
        else
            catVegetarian = "false";
        catVegetarianDel = catVegetarian + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Asian") || foodCategory.contains("Asiatico"))
            catAsian = "true";
        else
            catAsian = "false";
        catAsianDel = catAsian + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("Mediterranean") || foodCategory.contains("Mediterraneo"))
            catMediterranean = "true";
        else
            catMediterranean = "false";
        catMediterraneanDel = catMediterranean + "_" + deliveryCost.replace(".", "").replace(",", "");

        if(foodCategory.contains("South American") || foodCategory.contains("Sud Americano"))
            catSouthAmerican = "true";
        else
            catSouthAmerican = "false";
        catSouthAmericanDel = catSouthAmerican + "_" + deliveryCost.replace(".", "").replace(",", "");
    }

    public String getfCategoryANDdCost() {
        return fCategoryANDdCost;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public String getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(String ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public String getRatingCounter() {
        return ratingCounter;
    }

    public void setRatingCounter(String ratingCounter) {
        this.ratingCounter = ratingCounter;
    }

    public String getFoodRatingAvg() {
        return foodRatingAvg;
    }

    public void setFoodRatingAvg(String foodRatingAvg) {
        this.foodRatingAvg = foodRatingAvg;
    }

    public void setFoodCategory(String foodCategory) {
        this.foodCategory = foodCategory;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
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
        dest.writeString(this.info);
        dest.writeString(this.foodCategory);
//        dest.writeParcelable(this.photo, flags);
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

    protected Restaurant(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.phoneNumber = in.readString();
        this.address = in.readString();
        this.info = in.readString();
        this.foodCategory = in.readString();
        this.photo = in.readParcelable(Bitmap.class.getClassLoader());
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

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
