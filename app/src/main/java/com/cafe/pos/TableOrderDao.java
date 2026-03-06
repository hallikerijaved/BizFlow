package com.bizflow.pos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TableOrderDao {
    @Insert
    void insertOrder(TableOrder order);
    
    @Query("SELECT * FROM table_orders WHERE tableId = :tableId AND status = 'running'")
    List<TableOrder> getRunningOrdersForTable(int tableId);
    
    @Query("SELECT SUM(totalPrice) FROM table_orders WHERE tableId = :tableId AND status = 'running'")
    Double getTableTotal(int tableId);
    
    @Query("UPDATE table_orders SET status = 'completed' WHERE tableId = :tableId AND status = 'running'")
    void completeTableOrders(int tableId);
    
    @Query("DELETE FROM table_orders WHERE tableId = :tableId AND status = 'running'")
    void clearRunningOrders(int tableId);
    
    @Update
    void updateOrder(TableOrder order);
}