package com.bizflow.pos;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {FoodItem.class, Sale.class, Table.class, TableOrder.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FoodDao foodDao();
    public abstract SaleDao saleDao();
    public abstract TableDao tableDao();
    public abstract TableOrderDao tableOrderDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "cafe_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}