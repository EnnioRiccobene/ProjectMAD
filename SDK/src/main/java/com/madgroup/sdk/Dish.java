package com.madgroup.sdk;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class Dish {
    private String id;
    private String name;
    private String price;
    private String availableQuantity;
    private String description;
    private String orderedQuantityTot;

    public Dish() {
    }

    public Dish(String id, String name, String price, String availableQuantity, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.description = description;
        this.orderedQuantityTot = "0";
    }

    public Dish(String id, String name, String price, String availableQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.orderedQuantityTot = "0";
    }

    public String getOrderedQuantityTot() {
        return orderedQuantityTot;
    }

    public void setOrderedQuantityTot(String orderedQuantityTot) {
        this.orderedQuantityTot = orderedQuantityTot;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dish dish = (Dish) obj;
        if (name != dish.getName()) return true;
        return false;
    }
}