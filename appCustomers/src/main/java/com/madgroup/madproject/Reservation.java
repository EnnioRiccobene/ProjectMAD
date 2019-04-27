package com.madgroup.madproject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Reservation implements Serializable {

    // TODO: Aggiungere successivamente classe Customer con tutte le informazioni necessarie dell'utente (nome, cognome, telefono)
    private String orderID;
    private ArrayList<OrderedDish> orderedDishList;
    private String address;
    private String deliveryTime;
    private String price;
    private Integer status;
    private String notes;

    public Reservation() {
    }

    public Reservation(String address, String deliveryTime, String price) {
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.price = price;
        this.status = 0;
    }

    public Reservation(ArrayList<OrderedDish> orderedDishList, String address, String deliveryTime) {
        this.orderedDishList = orderedDishList;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.status = 0;
        // Compute total Price
        float x = 0;
        for (OrderedDish element : orderedDishList)
            x += element.getPrice() * element.getQuantity();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        price = df.format(x);
    }

    public Reservation(ArrayList<OrderedDish> orderedDishList, String address, String deliveryTime, String notes) {
        this.orderedDishList = orderedDishList;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.notes = notes;
        this.status = 0;
        // Compute total Price
        float x = 0;
        for (OrderedDish element : orderedDishList)
            x += element.getPrice() * element.getQuantity();
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

    public String getNotes() {
        return notes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}