package com.bizflow.pos;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_items")
public class FoodItem implements java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double price;
    public String category;
    public boolean available;
    public String imagePath;

    @Ignore
    public FoodItem(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = true;
        this.imagePath = null;
    }

    public FoodItem() {}
}