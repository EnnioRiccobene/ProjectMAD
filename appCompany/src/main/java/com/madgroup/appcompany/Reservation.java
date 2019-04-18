package com.madgroup.appcompany;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Reservation implements Serializable {


    // TODO: Aggiungere successivamente classe Customer con tutte le informazioni necessarie dell'utente (nome, cognome, telefono)
    private String orderID;
    private ArrayList<orderedDish> orderedDishList;
    private String address;
    private String deliveryTime;
    private String price;
    private Integer status;
    private String notes;

//    Status
//    0: Da confermare/rifiutare        Tab1
//    1: Confermato. Da consegnare      Tab2
//    2: Consegnato e concluso          Tab3
//    3: Rifiutato                      Tab3 or Nothing
//    ? Need to add "Rider Called" status ?


    public Reservation() {
    }

    public Reservation(String address, String deliveryTime, Integer status, String price) {
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.status = status;
        this.price = price;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Reservation(ArrayList<orderedDish> orderedDishList, String address, String deliveryTime, Integer status) {
        this.orderedDishList = orderedDishList;
        this.address = address;
        this.deliveryTime = deliveryTime;
        this.status= status;
        // Compute total Price
        float x = 0;
        for (orderedDish element : orderedDishList)
            x += element.getPrice() * element.getQuantity();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setMinimumFractionDigits(2);
        price = df.format(x);
    }

    public Reservation(String text1, String text2, String text3) {
        address = text1;
        deliveryTime = text2;
        price = text3;
    }



    public ArrayList<orderedDish> getOrderedDishList() {
        return orderedDishList;
    }

    public void setOrderedDishList(ArrayList<orderedDish> orderedDishList) {
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
}

