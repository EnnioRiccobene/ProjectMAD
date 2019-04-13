package com.madgroup.appcompany;

public class Reservation {
    private int mImageResource;
    private String address;
    private String delivery_time;
    private String price;

    public Reservation(int imageResource, String text1, String text2, String text3){
        mImageResource = imageResource;
        address = text1;
        delivery_time = text2;
        price = text3;

    }
    public void changeText1(String text){
        address = text;
    }

    public int getmImageResource(){
        return mImageResource;
    }

    public String getAddress(){
        return address;
    }

    public String getDelivery_time(){
        return delivery_time;
    }
        public String getPrice() {
        return price;
    }
}

