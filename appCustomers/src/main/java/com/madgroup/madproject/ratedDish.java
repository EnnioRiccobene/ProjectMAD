package com.madgroup.madproject;

public class ratedDish{
    private String id;
    private String name;
    private String restaurant;
    private String restaurantId;
    private Float foodRating;
    private int orderCount;
    private String description;
    private String price;

    public ratedDish(String id, String name, String restaurant,
                     String restaurantId, Float foodRating, int orderCount,
                     String description, String price) {
        this.id = id;
        this.name = name;
        this.restaurant = restaurant;
        this.restaurantId = restaurantId;
        this.foodRating = foodRating;
        this.orderCount = orderCount;
        this.description = description;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Float getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(Float foodRating) {
        this.foodRating = foodRating;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
