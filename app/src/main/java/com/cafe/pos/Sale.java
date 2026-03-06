package com.bizflow.pos;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "sales")
public class Sale {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public double totalAmount;
    public String items;
    public String invoiceNumber;
    public String paymentMethod; // Cash, Card, UPI
    public double discountPercent;
    public double discountAmount;
    public double taxAmount;
    public double finalAmount;
    public String tableName;

    @Ignore
    public Sale(String date, double totalAmount, String items, String invoiceNumber) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.items = items;
        this.invoiceNumber = invoiceNumber;
        this.paymentMethod = "Cash";
        this.discountPercent = 0;
        this.discountAmount = 0;
        this.taxAmount = 0;
        this.finalAmount = totalAmount;
    }

    public Sale() {}
}