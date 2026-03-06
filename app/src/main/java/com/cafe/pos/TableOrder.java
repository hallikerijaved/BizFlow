package com.bizflow.pos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_orders")
public class TableOrder {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public int tableId;
    public int foodId;
    public String foodName;
    public double foodPrice;
    public int quantity;
    public double totalPrice;
    public long orderTime;
    public String status; // "running", "completed"
    
    public TableOrder(int tableId, int foodId, String foodName, double foodPrice, int quantity) {
        this.tableId = tableId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.quantity = quantity;
        this.totalPrice = foodPrice * quantity;
        this.orderTime = System.currentTimeMillis();
        this.status = "running";
    }
}