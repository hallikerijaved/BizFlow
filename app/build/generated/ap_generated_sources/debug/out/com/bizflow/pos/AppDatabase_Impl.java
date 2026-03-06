package com.bizflow.pos;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile FoodDao _foodDao;

  private volatile SaleDao _saleDao;

  private volatile TableDao _tableDao;

  private volatile TableOrderDao _tableOrderDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `price` REAL NOT NULL, `category` TEXT, `available` INTEGER NOT NULL, `imagePath` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sales` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT, `totalAmount` REAL NOT NULL, `items` TEXT, `invoiceNumber` TEXT, `paymentMethod` TEXT, `discountPercent` REAL NOT NULL, `discountAmount` REAL NOT NULL, `taxAmount` REAL NOT NULL, `finalAmount` REAL NOT NULL, `tableName` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tables` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `capacity` INTEGER NOT NULL, `status` TEXT, `currentOrderId` TEXT, `lastUpdated` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `table_orders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tableId` INTEGER NOT NULL, `foodId` INTEGER NOT NULL, `foodName` TEXT, `foodPrice` REAL NOT NULL, `quantity` INTEGER NOT NULL, `totalPrice` REAL NOT NULL, `orderTime` INTEGER NOT NULL, `status` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4eaec49a51c76e3532e39988a1552eac')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `food_items`");
        db.execSQL("DROP TABLE IF EXISTS `sales`");
        db.execSQL("DROP TABLE IF EXISTS `tables`");
        db.execSQL("DROP TABLE IF EXISTS `table_orders`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFoodItems = new HashMap<String, TableInfo.Column>(6);
        _columnsFoodItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("price", new TableInfo.Column("price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("available", new TableInfo.Column("available", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("imagePath", new TableInfo.Column("imagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFoodItems = new TableInfo("food_items", _columnsFoodItems, _foreignKeysFoodItems, _indicesFoodItems);
        final TableInfo _existingFoodItems = TableInfo.read(db, "food_items");
        if (!_infoFoodItems.equals(_existingFoodItems)) {
          return new RoomOpenHelper.ValidationResult(false, "food_items(com.bizflow.pos.FoodItem).\n"
                  + " Expected:\n" + _infoFoodItems + "\n"
                  + " Found:\n" + _existingFoodItems);
        }
        final HashMap<String, TableInfo.Column> _columnsSales = new HashMap<String, TableInfo.Column>(11);
        _columnsSales.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("date", new TableInfo.Column("date", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("totalAmount", new TableInfo.Column("totalAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("items", new TableInfo.Column("items", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("invoiceNumber", new TableInfo.Column("invoiceNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("paymentMethod", new TableInfo.Column("paymentMethod", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("discountPercent", new TableInfo.Column("discountPercent", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("discountAmount", new TableInfo.Column("discountAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("taxAmount", new TableInfo.Column("taxAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("finalAmount", new TableInfo.Column("finalAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("tableName", new TableInfo.Column("tableName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSales = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSales = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSales = new TableInfo("sales", _columnsSales, _foreignKeysSales, _indicesSales);
        final TableInfo _existingSales = TableInfo.read(db, "sales");
        if (!_infoSales.equals(_existingSales)) {
          return new RoomOpenHelper.ValidationResult(false, "sales(com.bizflow.pos.Sale).\n"
                  + " Expected:\n" + _infoSales + "\n"
                  + " Found:\n" + _existingSales);
        }
        final HashMap<String, TableInfo.Column> _columnsTables = new HashMap<String, TableInfo.Column>(6);
        _columnsTables.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTables.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTables.put("capacity", new TableInfo.Column("capacity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTables.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTables.put("currentOrderId", new TableInfo.Column("currentOrderId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTables.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTables = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTables = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTables = new TableInfo("tables", _columnsTables, _foreignKeysTables, _indicesTables);
        final TableInfo _existingTables = TableInfo.read(db, "tables");
        if (!_infoTables.equals(_existingTables)) {
          return new RoomOpenHelper.ValidationResult(false, "tables(com.bizflow.pos.Table).\n"
                  + " Expected:\n" + _infoTables + "\n"
                  + " Found:\n" + _existingTables);
        }
        final HashMap<String, TableInfo.Column> _columnsTableOrders = new HashMap<String, TableInfo.Column>(9);
        _columnsTableOrders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("tableId", new TableInfo.Column("tableId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("foodId", new TableInfo.Column("foodId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("foodName", new TableInfo.Column("foodName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("foodPrice", new TableInfo.Column("foodPrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("totalPrice", new TableInfo.Column("totalPrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("orderTime", new TableInfo.Column("orderTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTableOrders.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTableOrders = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTableOrders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTableOrders = new TableInfo("table_orders", _columnsTableOrders, _foreignKeysTableOrders, _indicesTableOrders);
        final TableInfo _existingTableOrders = TableInfo.read(db, "table_orders");
        if (!_infoTableOrders.equals(_existingTableOrders)) {
          return new RoomOpenHelper.ValidationResult(false, "table_orders(com.bizflow.pos.TableOrder).\n"
                  + " Expected:\n" + _infoTableOrders + "\n"
                  + " Found:\n" + _existingTableOrders);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4eaec49a51c76e3532e39988a1552eac", "fdd8647c028181bb6730e0ef7a345842");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "food_items","sales","tables","table_orders");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `food_items`");
      _db.execSQL("DELETE FROM `sales`");
      _db.execSQL("DELETE FROM `tables`");
      _db.execSQL("DELETE FROM `table_orders`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FoodDao.class, FoodDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SaleDao.class, SaleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TableDao.class, TableDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TableOrderDao.class, TableOrderDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FoodDao foodDao() {
    if (_foodDao != null) {
      return _foodDao;
    } else {
      synchronized(this) {
        if(_foodDao == null) {
          _foodDao = new FoodDao_Impl(this);
        }
        return _foodDao;
      }
    }
  }

  @Override
  public SaleDao saleDao() {
    if (_saleDao != null) {
      return _saleDao;
    } else {
      synchronized(this) {
        if(_saleDao == null) {
          _saleDao = new SaleDao_Impl(this);
        }
        return _saleDao;
      }
    }
  }

  @Override
  public TableDao tableDao() {
    if (_tableDao != null) {
      return _tableDao;
    } else {
      synchronized(this) {
        if(_tableDao == null) {
          _tableDao = new TableDao_Impl(this);
        }
        return _tableDao;
      }
    }
  }

  @Override
  public TableOrderDao tableOrderDao() {
    if (_tableOrderDao != null) {
      return _tableOrderDao;
    } else {
      synchronized(this) {
        if(_tableOrderDao == null) {
          _tableOrderDao = new TableOrderDao_Impl(this);
        }
        return _tableOrderDao;
      }
    }
  }
}
