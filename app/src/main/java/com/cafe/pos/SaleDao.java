package com.bizflow.pos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY date DESC")
    List<Sale> getAllSales();

    @Insert
    void insertSale(Sale sale);

    @Query("SELECT COALESCE(SUM(totalAmount), 0) FROM sales WHERE date LIKE :date")
    double getDailySales(String date);

    @Query("SELECT * FROM sales WHERE date LIKE :date || '%' ORDER BY date DESC")
    List<Sale> getSalesByDate(String date);
}