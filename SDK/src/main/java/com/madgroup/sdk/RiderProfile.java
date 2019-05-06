package com.madgroup.sdk;

import java.io.Serializable;

public class RiderProfile implements Serializable {
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

    public RiderProfile(String name, String email, String phoneNumber, boolean active) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = active;
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
}

