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

    public String getCatPizza() {
        return catPizza;
    }

    public String getCatSandwiches() {
        return catSandwiches;
    }

    public String getCatKebab() {
        return catKebab;
    }

    public String getCatItalian() {
        return catItalian;
    }

    public String getCatAmerican() {
        return catAmerican;
    }

    public String getCatDesserts() {
        return catDesserts;
    }

    public String getCatFry() {
        return catFry;
    }

    public String getCatVegetarian() {
        return catVegetarian;
    }

    public String getCatAsian() {
        return catAsian;
    }

    public String getCatMediterranean() {
        return catMediterranean;
    }

    public String getCatSouthAmerican() {
        return catSouthAmerican;
    }

    public String getCatPizzaDel() {
        return catPizzaDel;
    }

    public String getCatSandwichesDel() {
        return catSandwichesDel;
    }

    public String getCatKebabDel() {
        return catKebabDel;
    }

    public String getCatItalianDel() {
        return catItalianDel;
    }

    public String getCatAmericanDel() {
        return catAmericanDel;
    }

    public String getCatDessertsDel() {
        return catDessertsDel;
    }

    public String getCatFryDel() {
        return catFryDel;
    }

    public String getCatVegetarianDel() {
        return catVegetarianDel;
    }

    public String getCatAsianDel() {
        return catAsianDel;
    }

    public String getCatMediterraneanDel() {
        return catMediterraneanDel;
    }

    public String getCatSouthAmericanDel() {
        return catSouthAmericanDel;
    }

    public void setfCategoryANDdCost(String fCategoryANDdCost) {
        this.fCategoryANDdCost = fCategoryANDdCost;
    }

    public void setCatPizza(String catPizza) {
        this.catPizza = catPizza;
    }

    public void setCatSandwiches(String catSandwiches) {
        this.catSandwiches = catSandwiches;
    }

    public void setCatKebab(String catKebab) {
        this.catKebab = catKebab;
    }

    public void setCatItalian(String catItalian) {
        this.catItalian = catItalian;
    }

    public void setCatAmerican(String catAmerican) {
        this.catAmerican = catAmerican;
    }

    public void setCatDesserts(String catDesserts) {
        this.catDesserts = catDesserts;
    }

    public void setCatFry(String catFry) {
        this.catFry = catFry;
    }

    public void setCatVegetarian(String catVegetarian) {
        this.catVegetarian = catVegetarian;
    }

    public void setCatAsian(String catAsian) {
        this.catAsian = catAsian;
    }

    public void setCatMediterranean(String catMediterranean) {
        this.catMediterranean = catMediterranean;
    }

    public void setCatSouthAmerican(String catSouthAmerican) {
        this.catSouthAmerican = catSouthAmerican;
    }

    public void setCatPizzaDel(String catPizzaDel) {
        this.catPizzaDel = catPizzaDel;
    }

    public void setCatSandwichesDel(String catSandwichesDel) {
        this.catSandwichesDel = catSandwichesDel;
    }

    public void setCatKebabDel(String catKebabDel) {
        this.catKebabDel = catKebabDel;
    }

    public void setCatItalianDel(String catItalianDel) {
        this.catItalianDel = catItalianDel;
    }

    public void setCatAmericanDel(String catAmericanDel) {
        this.catAmericanDel = catAmericanDel;
    }

    public void setCatDessertsDel(String catDessertsDel) {
        this.catDessertsDel = catDessertsDel;
    }

    public void setCatFryDel(String catFryDel) {
        this.catFryDel = catFryDel;
    }

    public void setCatVegetarianDel(String catVegetarianDel) {
        this.catVegetarianDel = catVegetarianDel;
    }

    public void setCatAsianDel(String catAsianDel) {
        this.catAsianDel = catAsianDel;
    }

    public void setCatMediterraneanDel(String catMediterraneanDel) {
        this.catMediterraneanDel = catMediterraneanDel;
    }

    public void setCatSouthAmericanDel(String catSouthAmericanDel) {
        this.catSouthAmericanDel = catSouthAmericanDel;
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