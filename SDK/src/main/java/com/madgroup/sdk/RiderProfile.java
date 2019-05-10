package com.madgroup.sdk;

import java.io.Serializable;

public class RiderProfile implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String additionalInformation;
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public RiderProfile() {
    }

    public RiderProfile(String id, String name, String email, String phoneNumber, String additionalInformation, boolean status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.additionalInformation = additionalInformation;
        this.status = status;
    }

    public RiderProfile(String id, String name, String email, String phoneNumber, String additionalInformation) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.additionalInformation = additionalInformation;
        this.status = status;
    }

    public RiderProfile(String id, String name, String email, String phoneNumber, boolean status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public RiderProfile(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
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

