package com.madgroup.sdk;

public class Delivery {

    private String orderID;
    private String restaurantID;
    private String customerID;
    private String customerName;
    private String restaurantName;
    private String restaurantAddress;
    private String customerAddress;
    private String paymentMethod;
    private String restaurantCustomerDistance;
    private float price;
    private String customerPhone;
    private String restaurantPhone;
    private String deliveryTime;
    private boolean seen;


    public Delivery() {
    }

    public Delivery(String restaurantName, String restaurantAddress, String customerAddress, String paymentMethod, String deliveryTime) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.customerAddress = customerAddress;
        this.paymentMethod = paymentMethod;
        this.deliveryTime = deliveryTime;
        this.seen = false;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int calculateDistance(String customerAddress, String restaurantAddress) {

        int n = (int) (Math.random() * 100);

        return n;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getRestaurantCustomerDistance() {
        return restaurantCustomerDistance;
    }

    public void setRestaurantCustomerDistance(String restaurantCustomerDistance) {
        this.restaurantCustomerDistance = restaurantCustomerDistance;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
