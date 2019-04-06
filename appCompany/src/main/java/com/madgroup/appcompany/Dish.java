package com.madgroup.appcompany;

import android.graphics.Bitmap;

public class Dish {

    private int id;
    private String name;
    private float price;
    private int availableQuantity;
    private String description;
    private Bitmap photo;

    public Dish(int id, String name, float price, int availableQuantity, String description, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.description = description;
        this.photo = photo;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


}
