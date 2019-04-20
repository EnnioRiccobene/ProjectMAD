package com.madgroup.appbikers;

import java.io.Serializable;

public class RiderProfile implements Serializable {
    private String name;
    private String email;
    private String phoneNumber;
    private String additionalInformation;

    public RiderProfile() {

    }

    public RiderProfile(String name, String email, String phoneNumber, String additionalInformation) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.additionalInformation = additionalInformation;
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
