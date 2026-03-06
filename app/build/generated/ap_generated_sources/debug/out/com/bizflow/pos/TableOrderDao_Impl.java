package com.bizflow.pos;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TableOrderDao_Impl implements TableOrderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TableOrder> __insertionAdapterOfTableOrder;

  private final EntityDeletionOrUpdateAdapter<TableOrder> __updateAdapterOfTableOrder;

  private final SharedSQLiteStatement __preparedStmtOfCompleteTableOrders;

  private final SharedSQLiteStatement __preparedStmtOfClearRunningOrders;

  public TableOrderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTableOrder = new EntityInsertionAdapter<TableOrder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `table_orders` (`id`,`tableId`,`foodId`,`foodName`,`foodPrice`,`quantity`,`totalPrice`,`orderTime`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final TableOrder entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.tableId);
        statement.bindLong(3, entity.foodId);
        if (entity.foodName == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.foodName);
        }
        statement.bindDouble(5, entity.foodPrice);
        statement.bindLong(6, entity.quantity);
        statement.bindDouble(7, entity.totalPrice);
        statement.bindLong(8, entity.orderTime);
        if (entity.status == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.status);
        }
      }
    };
    this.__updateAdapterOfTableOrder = new EntityDeletionOrUpdateAdapter<TableOrder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `table_orders` SET `id` = ?,`tableId` = ?,`foodId` = ?,`foodName` = ?,`foodPrice` = ?,`quantity` = ?,`totalPrice` = ?,`orderTime` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final TableOrder entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.tableId);
        statement.bindLong(3, entity.foodId);
        if (entity.foodName == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.foodName);
        }
        statement.bindDouble(5, entity.foodPrice);
        statement.bindLong(6, entity.quantity);
        statement.bindDouble(7, entity.totalPrice);
        statement.bindLong(8, entity.orderTime);
        if (entity.status == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.status);
        }
        statement.bindLong(10, entity.id);
      }
    };
    this.__preparedStmtOfCompleteTableOrders = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE table_orders SET status = 'completed' WHERE tableId = ? AND status = 'running'";
        return _query;
      }
    };
    this.__preparedStmtOfClearRunningOrders = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM table_orders WHERE tableId = ? AND status = 'running'";
        return _query;
      }
    };
  }

  @Override
  public void insertOrder(final TableOrder order) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTableOrder.insert(order);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateOrder(final TableOrder order) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTableOrder.handle(order);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void completeTableOrders(final int tableId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfCompleteTableOrders.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, tableId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfCompleteTableOrders.release(_stmt);
    }
  }

  @Override
  public void clearRunningOrders(final int tableId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearRunningOrders.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, tableId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearRunningOrders.release(_stmt);
    }
  }

  @Override
  public List<TableOrder> getRunningOrdersForTable(final int tableId) {
    final String _sql = "SELECT * FROM table_orders WHERE tableId = ? AND status = 'running'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tableId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTableId = CursorUtil.getColumnIndexOrThrow(_cursor, "tableId");
      final int _cursorIndexOfFoodId = CursorUtil.getColumnIndexOrThrow(_cursor, "foodId");
      final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
      final int _cursorIndexOfFoodPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "foodPrice");
      final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
      final int _cursorIndexOfTotalPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPrice");
      final int _cursorIndexOfOrderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "orderTime");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<TableOrder> _result = new ArrayList<TableOrder>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TableOrder _item;
        final int _tmpTableId;
        _tmpTableId = _cursor.getInt(_cursorIndexOfTableId);
        final int _tmpFoodId;
        _tmpFoodId = _cursor.getInt(_cursorIndexOfFoodId);
        final String _tmpFoodName;
        if (_cursor.isNull(_cursorIndexOfFoodName)) {
          _tmpFoodName = null;
        } else {
          _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
        }
        final double _tmpFoodPrice;
        _tmpFoodPrice = _cursor.getDouble(_cursorIndexOfFoodPrice);
        final int _tmpQuantity;
        _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
        _item = new TableOrder(_tmpTableId,_tmpFoodId,_tmpFoodName,_tmpFoodPrice,_tmpQuantity);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.totalPrice = _cursor.getDouble(_cursorIndexOfTotalPrice);
        _item.orderTime = _cursor.getLong(_cursorIndexOfOrderTime);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Double getTableTotal(final int tableId) {
    final String _sql = "SELECT SUM(totalPrice) FROM table_orders WHERE tableId = ? AND status = 'running'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, tableId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final Double _result;
      if (_cursor.moveToFirst()) {
        final Double _tmp;
        if (_cursor.isNull(0)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getDouble(0);
        }
        _result = _tmp;
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
