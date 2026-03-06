package com.bizflow.pos;

import java.io.Serializable;

public class OrderItem implements Serializable {
    public FoodItem foodItem;
    public int quantity;
    public double totalPrice;

    public OrderItem(FoodItem foodItem, int quantity) {
        if (foodItem == null) {
            throw new IllegalArgumentException("FoodItem cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.totalPrice = foodItem.price * quantity;
    }
}