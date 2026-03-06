package com.bizflow.pos;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FoodDao_Impl implements FoodDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FoodItem> __insertionAdapterOfFoodItem;

  private final EntityDeletionOrUpdateAdapter<FoodItem> __deletionAdapterOfFoodItem;

  private final EntityDeletionOrUpdateAdapter<FoodItem> __updateAdapterOfFoodItem;

  public FoodDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFoodItem = new EntityInsertionAdapter<FoodItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `food_items` (`id`,`name`,`price`,`category`,`available`,`imagePath`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final FoodItem entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        statement.bindDouble(3, entity.price);
        if (entity.category == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.category);
        }
        final int _tmp = entity.available ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.imagePath == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.imagePath);
        }
      }
    };
    this.__deletionAdapterOfFoodItem = new EntityDeletionOrUpdateAdapter<FoodItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `food_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final FoodItem entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfFoodItem = new EntityDeletionOrUpdateAdapter<FoodItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `food_items` SET `id` = ?,`name` = ?,`price` = ?,`category` = ?,`available` = ?,`imagePath` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final FoodItem entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        statement.bindDouble(3, entity.price);
        if (entity.category == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.category);
        }
        final int _tmp = entity.available ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.imagePath == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.imagePath);
        }
        statement.bindLong(7, entity.id);
      }
    };
  }

  @Override
  public void insertFoodItem(final FoodItem foodItem) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfFoodItem.insert(foodItem);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteFoodItem(final FoodItem foodItem) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfFoodItem.handle(foodItem);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateFoodItem(final FoodItem foodItem) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfFoodItem.handle(foodItem);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<FoodItem> getAllFoodItems() {
    final String _sql = "SELECT * FROM food_items";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "available");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
      final List<FoodItem> _result = new ArrayList<FoodItem>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final FoodItem _item;
        _item = new FoodItem();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        _item.price = _cursor.getDouble(_cursorIndexOfPrice);
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfAvailable);
        _item.available = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfImagePath)) {
          _item.imagePath = null;
        } else {
          _item.imagePath = _cursor.getString(_cursorIndexOfImagePath);
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
  public List<FoodItem> getAvailableFoodItems() {
    final String _sql = "SELECT * FROM food_items WHERE available = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "available");
      final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
      final List<FoodItem> _result = new ArrayList<FoodItem>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final FoodItem _item;
        _item = new FoodItem();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        _item.price = _cursor.getDouble(_cursorIndexOfPrice);
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfAvailable);
        _item.available = _tmp != 0;
        if (_cursor.isNull(_cursorIndexOfImagePath)) {
          _item.imagePath = null;
        } else {
          _item.imagePath = _cursor.getString(_cursorIndexOfImagePath);
        }
        _result.add(_item);
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
