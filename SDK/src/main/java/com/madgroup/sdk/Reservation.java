package com.madgroup.sdk;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Reservation implements Serializable {

    private String orderID;
    private String customerID;
    private String restaurantID;
    private String bikerID;
    private ArrayList<OrderedDish> orderedDishList;
    private String restaurantName;
    private String customerName;
    private String address;
    private String deliveryTime;
    private String deliveryCost;
    private String price;
    private Integer status;
    private String notes;
    //private boolean seen;

//    Status for Company
//    0: Da confermare/rifiutare        Tab1
//    1: Confermato. Da preparare       Tab2
//    2: Rider chiamato. In attesa      Tab2
//    3: Consegnato e concluso          Tab3
//    4: Rifiutato                      Tab3

//    Status for Biker
//    0: Accettato                      Tab1
//    1: Concluso                       Tab2

//    Status for Customer
//    0: Da confermare/rifiutare        Tab1
//    1: Confermato. Da preparare       Tab1
//    2: Rider chiamato
//    3: Consegnato e concluso          Tab2
//    4: Rifiutato                      Tab2

    public Reservation() {
    }

    public Reservation(String restaurantName, ArrayList<OrderedDish> orderedDishList, String address,
                       String deliveryTime, String notes, String restaurantID, String deliveryCost, String customerName) {
        this.restaurantName = restaurantName;
        this.customerName = customerName;
        this.orderedDishList = orderedDishList;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.notes = notes;
        this.status = 0;
        this.restaurantID = restaurantID;
        this.deliveryCost = deliveryCost;
        this.bikerID = "";
        //this.seen = false;
        // Compute total Price
        float x = 0;
        for (OrderedDish element : orderedDishList) {
            float elementPrice = Float.valueOf(element.getPrice().replace(",", ".").replace("£", "").replace("$", "").replace("€", "").replaceAll("\\s", ""));
            int elementQuantity = Integer.valueOf(element.getQuantity());
            x += elementPrice * elementQuantity;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        price = df.format(x);
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Reservation(String text1, String text2, String text3) {
        address = text1;
        deliveryTime = text2;
        price = text3;
    }

    public ArrayList<OrderedDish> getOrderedDishList() {
        return orderedDishList;
    }

    public void setOrderedDishList(ArrayList<OrderedDish> orderedDishList) {
        this.orderedDishList = orderedDishList;
    }

    public Reservation(String text1, String text2, String text3, boolean confirmed) {
        address = text1;
        deliveryTime = text2;
        price = text3;
    }

    public void changeText1(String text) {
        address = text;
    }

    public String getAddress() {
        return address;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public String getPrice() {
        return price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(String deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getBikerID() {
        return bikerID;
    }

    public void setBikerID(String bikerID) {
        this.bikerID = bikerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}

