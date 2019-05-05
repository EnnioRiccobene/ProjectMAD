package com.madgroup.appcompany;

import java.io.Serializable;

public class OrderedDish implements Serializable {
    private String name;
    private Integer quantity;
    private float price;

    public OrderedDish() {
    }

    public OrderedDish(String name, Integer quantity, float price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
