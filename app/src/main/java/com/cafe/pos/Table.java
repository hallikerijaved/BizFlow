package com.bizflow.pos;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tables")
public class Table {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int capacity;
    public String status; // "available", "occupied", "reserved"
    public String currentOrderId;
    public long lastUpdated;

    @Ignore
    public Table(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.status = "available";
        this.currentOrderId = null;
        this.lastUpdated = System.currentTimeMillis();
    }

    public Table() {}
}