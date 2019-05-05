package com.madgroup.madproject;

import android.graphics.Bitmap;

public class Dish {

    private String id;
    private String name;
    private String price;
    private String availableQuantity;
    private String description;
    private Bitmap photo;

    public Dish() {
    }

    public Dish(String id, String name, String price, String availableQuantity, String description, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.description = description;
        this.photo = photo;
    }

    public Dish(String id, String name, String price, String availableQuantity, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.description = description;
    }

    public Dish(String id, String name, String price, String availableQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}