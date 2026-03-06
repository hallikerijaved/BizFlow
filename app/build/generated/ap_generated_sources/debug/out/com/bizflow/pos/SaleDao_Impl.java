package com.bizflow.pos;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
public final class SaleDao_Impl implements SaleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Sale> __insertionAdapterOfSale;

  public SaleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSale = new EntityInsertionAdapter<Sale>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sales` (`id`,`date`,`totalAmount`,`items`,`invoiceNumber`,`paymentMethod`,`discountPercent`,`discountAmount`,`taxAmount`,`finalAmount`,`tableName`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Sale entity) {
        statement.bindLong(1, entity.id);
        if (entity.date == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.date);
        }
        statement.bindDouble(3, entity.totalAmount);
        if (entity.items == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.items);
        }
        if (entity.invoiceNumber == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.invoiceNumber);
        }
        if (entity.paymentMethod == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.paymentMethod);
        }
        statement.bindDouble(7, entity.discountPercent);
        statement.bindDouble(8, entity.discountAmount);
        statement.bindDouble(9, entity.taxAmount);
        statement.bindDouble(10, entity.finalAmount);
        if (entity.tableName == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.tableName);
        }
      }
    };
  }

  @Override
  public void insertSale(final Sale sale) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSale.insert(sale);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Sale> getAllSales() {
    final String _sql = "SELECT * FROM sales ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
      final int _cursorIndexOfItems = CursorUtil.getColumnIndexOrThrow(_cursor, "items");
      final int _cursorIndexOfInvoiceNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "invoiceNumber");
      final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
      final int _cursorIndexOfDiscountPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "discountPercent");
      final int _cursorIndexOfDiscountAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "discountAmount");
      final int _cursorIndexOfTaxAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "taxAmount");
      final int _cursorIndexOfFinalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "finalAmount");
      final int _cursorIndexOfTableName = CursorUtil.getColumnIndexOrThrow(_cursor, "tableName");
      final List<Sale> _result = new ArrayList<Sale>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Sale _item;
        _item = new Sale();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _item.date = null;
        } else {
          _item.date = _cursor.getString(_cursorIndexOfDate);
        }
        _item.totalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
        if (_cursor.isNull(_cursorIndexOfItems)) {
          _item.items = null;
        } else {
          _item.items = _cursor.getString(_cursorIndexOfItems);
        }
        if (_cursor.isNull(_cursorIndexOfInvoiceNumber)) {
          _item.invoiceNumber = null;
        } else {
          _item.invoiceNumber = _cursor.getString(_cursorIndexOfInvoiceNumber);
        }
        if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
          _item.paymentMethod = null;
        } else {
          _item.paymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
        }
        _item.discountPercent = _cursor.getDouble(_cursorIndexOfDiscountPercent);
        _item.discountAmount = _cursor.getDouble(_cursorIndexOfDiscountAmount);
        _item.taxAmount = _cursor.getDouble(_cursorIndexOfTaxAmount);
        _item.finalAmount = _cursor.getDouble(_cursorIndexOfFinalAmount);
        if (_cursor.isNull(_cursorIndexOfTableName)) {
          _item.tableName = null;
        } else {
          _item.tableName = _cursor.getString(_cursorIndexOfTableName);
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
  public double getDailySales(final String date) {
    final String _sql = "SELECT COALESCE(SUM(totalAmount), 0) FROM sales WHERE date LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final double _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getDouble(0);
      } else {
        _result = 0.0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Sale> getSalesByDate(final String date) {
    final String _sql = "SELECT * FROM sales WHERE date LIKE ? || '%' ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
      final int _cursorIndexOfItems = CursorUtil.getColumnIndexOrThrow(_cursor, "items");
      final int _cursorIndexOfInvoiceNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "invoiceNumber");
      final int _cursorIndexOfPaymentMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethod");
      final int _cursorIndexOfDiscountPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "discountPercent");
      final int _cursorIndexOfDiscountAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "discountAmount");
      final int _cursorIndexOfTaxAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "taxAmount");
      final int _cursorIndexOfFinalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "finalAmount");
      final int _cursorIndexOfTableName = CursorUtil.getColumnIndexOrThrow(_cursor, "tableName");
      final List<Sale> _result = new ArrayList<Sale>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Sale _item;
        _item = new Sale();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _item.date = null;
        } else {
          _item.date = _cursor.getString(_cursorIndexOfDate);
        }
        _item.totalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
        if (_cursor.isNull(_cursorIndexOfItems)) {
          _item.items = null;
        } else {
          _item.items = _cursor.getString(_cursorIndexOfItems);
        }
        if (_cursor.isNull(_cursorIndexOfInvoiceNumber)) {
          _item.invoiceNumber = null;
        } else {
          _item.invoiceNumber = _cursor.getString(_cursorIndexOfInvoiceNumber);
        }
        if (_cursor.isNull(_cursorIndexOfPaymentMethod)) {
          _item.paymentMethod = null;
        } else {
          _item.paymentMethod = _cursor.getString(_cursorIndexOfPaymentMethod);
        }
        _item.discountPercent = _cursor.getDouble(_cursorIndexOfDiscountPercent);
        _item.discountAmount = _cursor.getDouble(_cursorIndexOfDiscountAmount);
        _item.taxAmount = _cursor.getDouble(_cursorIndexOfTaxAmount);
        _item.finalAmount = _cursor.getDouble(_cursorIndexOfFinalAmount);
        if (_cursor.isNull(_cursorIndexOfTableName)) {
          _item.tableName = null;
        } else {
          _item.tableName = _cursor.getString(_cursorIndexOfTableName);
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
