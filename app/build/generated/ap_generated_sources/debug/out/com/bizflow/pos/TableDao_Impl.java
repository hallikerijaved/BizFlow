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
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TableDao_Impl implements TableDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Table> __insertionAdapterOfTable;

  private final EntityDeletionOrUpdateAdapter<Table> __deletionAdapterOfTable;

  private final EntityDeletionOrUpdateAdapter<Table> __updateAdapterOfTable;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTableStatus;

  public TableDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTable = new EntityInsertionAdapter<Table>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `tables` (`id`,`name`,`capacity`,`status`,`currentOrderId`,`lastUpdated`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Table entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        statement.bindLong(3, entity.capacity);
        if (entity.status == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.status);
        }
        if (entity.currentOrderId == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.currentOrderId);
        }
        statement.bindLong(6, entity.lastUpdated);
      }
    };
    this.__deletionAdapterOfTable = new EntityDeletionOrUpdateAdapter<Table>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `tables` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Table entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfTable = new EntityDeletionOrUpdateAdapter<Table>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tables` SET `id` = ?,`name` = ?,`capacity` = ?,`status` = ?,`currentOrderId` = ?,`lastUpdated` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Table entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        statement.bindLong(3, entity.capacity);
        if (entity.status == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.status);
        }
        if (entity.currentOrderId == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.currentOrderId);
        }
        statement.bindLong(6, entity.lastUpdated);
        statement.bindLong(7, entity.id);
      }
    };
    this.__preparedStmtOfUpdateTableStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tables SET status = ?, currentOrderId = ?, lastUpdated = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public void insertTable(final Table table) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTable.insert(table);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteTable(final Table table) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfTable.handle(table);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateTable(final Table table) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTable.handle(table);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateTableStatus(final int tableId, final String status, final String orderId,
      final long timestamp) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTableStatus.acquire();
    int _argIndex = 1;
    if (status == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, status);
    }
    _argIndex = 2;
    if (orderId == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, orderId);
    }
    _argIndex = 3;
    _stmt.bindLong(_argIndex, timestamp);
    _argIndex = 4;
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
      __preparedStmtOfUpdateTableStatus.release(_stmt);
    }
  }

  @Override
  public List<Table> getAllTables() {
    final String _sql = "SELECT * FROM tables ORDER BY name";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "capacity");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfCurrentOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "currentOrderId");
      final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
      final List<Table> _result = new ArrayList<Table>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Table _item;
        _item = new Table();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        _item.capacity = _cursor.getInt(_cursorIndexOfCapacity);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfCurrentOrderId)) {
          _item.currentOrderId = null;
        } else {
          _item.currentOrderId = _cursor.getString(_cursorIndexOfCurrentOrderId);
        }
        _item.lastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Table> getTablesByStatus(final String status) {
    final String _sql = "SELECT * FROM tables WHERE status = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "capacity");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfCurrentOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "currentOrderId");
      final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
      final List<Table> _result = new ArrayList<Table>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Table _item;
        _item = new Table();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        _item.capacity = _cursor.getInt(_cursorIndexOfCapacity);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfCurrentOrderId)) {
          _item.currentOrderId = null;
        } else {
          _item.currentOrderId = _cursor.getString(_cursorIndexOfCurrentOrderId);
        }
        _item.lastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Table getTableById(final int id) {
    final String _sql = "SELECT * FROM tables WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "capacity");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfCurrentOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "currentOrderId");
      final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
      final Table _result;
      if (_cursor.moveToFirst()) {
        _result = new Table();
        _result.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _result.name = null;
        } else {
          _result.name = _cursor.getString(_cursorIndexOfName);
        }
        _result.capacity = _cursor.getInt(_cursorIndexOfCapacity);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _result.status = null;
        } else {
          _result.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfCurrentOrderId)) {
          _result.currentOrderId = null;
        } else {
          _result.currentOrderId = _cursor.getString(_cursorIndexOfCurrentOrderId);
        }
        _result.lastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
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
