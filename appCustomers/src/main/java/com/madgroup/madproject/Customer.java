package com.madgroup.madproject;

import java.io.Serializable;

public class Customer implements Serializable {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String info;
    private String imageUri;

    public Customer(){}

    public Customer(String name, String email, String phone, String address, String info, String imageUri) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.info = info;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
