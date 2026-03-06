package com.bizflow.pos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface FoodDao {
    @Query("SELECT * FROM food_items")
    List<FoodItem> getAllFoodItems();

    @Query("SELECT * FROM food_items WHERE available = 1")
    List<FoodItem> getAvailableFoodItems();

    @Insert
    void insertFoodItem(FoodItem foodItem);

    @Update
    void updateFoodItem(FoodItem foodItem);

    @Delete
    void deleteFoodItem(FoodItem foodItem);
}