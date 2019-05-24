package com.madgroup.sdk;

import java.io.Serializable;

public class RiderProfile implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String additionalInformation;
    private boolean status;
    private Position position;
    private String ratingAvg;
    private String ratingCounter;
    private String deliveryNumber;
    private String totDistance;

    public RiderProfile() {
    }

    public RiderProfile(String id, String name, String email, String phoneNumber, String additionalInformation, boolean status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.additionalInformation = additionalInformation;
        this.status = status;
        this.ratingAvg = "0";
        this.ratingCounter = "0";
        this.deliveryNumber = "0";
        this.totDistance = "0";
    }

    public String getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public String getTotDistance() {
        return totDistance;
    }

    public void setTotDistance(String totDistance) {
        this.totDistance = totDistance;
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

