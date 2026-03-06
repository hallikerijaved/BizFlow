package com.bizflow.pos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TableDao {
    @Query("SELECT * FROM tables ORDER BY name")
    List<Table> getAllTables();

    @Query("SELECT * FROM tables WHERE status = :status")
    List<Table> getTablesByStatus(String status);

    @Query("SELECT * FROM tables WHERE id = :id")
    Table getTableById(int id);

    @Insert
    void insertTable(Table table);

    @Update
    void updateTable(Table table);

    @Delete
    void deleteTable(Table table);

    @Query("UPDATE tables SET status = :status, currentOrderId = :orderId, lastUpdated = :timestamp WHERE id = :tableId")
    void updateTableStatus(int tableId, String status, String orderId, long timestamp);
}