package com.madgroup.madproject;

import java.io.Serializable;

public class OrderedDish implements Serializable {
    private String name;
    private String quantity;
    private String price;

    public OrderedDish() {
    }

    public OrderedDish(String name, String quantity, String price) {
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
