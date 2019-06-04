package com.madgroup.appcompany;

public class Review {
    String customerId;
    String customerName;
    String restaurantRating;
    String orderID;
    String comment;
    String foodRating;

    public Review(){

    }

    public Review(String customerId, String restaurantRating, String orderID, String comment, String foodRating) {
        this.customerId = customerId;
        this.restaurantRating = restaurantRating;
        this.orderID = orderID;
        this.comment = comment;
        this.foodRating = foodRating;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(String restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(String foodRating) {
        this.foodRating = foodRating;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
