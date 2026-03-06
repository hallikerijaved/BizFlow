package com.bizflow.pos;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TableManagementActivity extends AppCompatActivity {
    private EditText etTableName, etTableCapacity;
    private Button btnAddTable;
    private RecyclerView recyclerViewTables;
    private TableAdapter tableAdapter;
    private AppDatabase database;
    private ExecutorService executor;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_management);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadTables();
    }

    private void initViews() {
        etTableName = findViewById(R.id.etTableName);
        etTableCapacity = findViewById(R.id.etTableCapacity);
        btnAddTable = findViewById(R.id.btnAddTable);
        recyclerViewTables = findViewById(R.id.recyclerViewTables);
    }

    private void setupDatabase() {
        database = AppDatabase.getDatabase(this);
    }

    private void setupRecyclerView() {
        tableAdapter = new TableAdapter(new ArrayList<>(), this::onTableClick);
        
        boolean isTablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        int spanCount = isTablet ? 4 : 2;
        
        recyclerViewTables.setLayoutManager(new GridLayoutManager(this, spanCount));
        recyclerViewTables.setAdapter(tableAdapter);
    }

    private void setupClickListeners() {
        btnAddTable.setOnClickListener(v -> addTable());
    }

    private void addTable() {
        String name = etTableName.getText().toString().trim();
        String capacityStr = etTableCapacity.getText().toString().trim();

        if (name.isEmpty() || capacityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int capacity = Integer.parseInt(capacityStr);
            executor.execute(() -> {
                try {
                    Table table = new Table(name, capacity);
                    database.tableDao().insertTable(table);
                    
                    mainHandler.post(() -> {
                        clearFields();
                        loadTables();
                        Toast.makeText(this, "Table added successfully", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    mainHandler.post(() -> 
                        Toast.makeText(this, "Error adding table", Toast.LENGTH_SHORT).show());
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid capacity", Toast.LENGTH_SHORT).show();
        }
    }

    private void onTableClick(Table table) {
        showTableOptions(table);
    }

    private void showTableOptions(Table table) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Table: " + table.name);
        builder.setItems(new String[]{"Edit Table", "Delete Table"}, (dialog, which) -> {
            if (which == 0) {
                editTable(table);
            } else {
                deleteTable(table);
            }
        });
        builder.show();
    }

    private void editTable(Table table) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Table");
        
        android.view.View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        android.widget.EditText etName = new android.widget.EditText(this);
        etName.setHint("Table Name");
        etName.setText(table.name);
        layout.addView(etName);
        
        android.widget.EditText etCapacity = new android.widget.EditText(this);
        etCapacity.setHint("Capacity");
        etCapacity.setText(String.valueOf(table.capacity));
        etCapacity.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etCapacity);
        
        builder.setView(layout);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String capacityStr = etCapacity.getText().toString().trim();
            
            if (!name.isEmpty() && !capacityStr.isEmpty()) {
                try {
                    int capacity = Integer.parseInt(capacityStr);
                    executor.execute(() -> {
                        table.name = name;
                        table.capacity = capacity;
                        database.tableDao().updateTable(table);
                        mainHandler.post(() -> {
                            loadTables();
                            Toast.makeText(this, "Table updated", Toast.LENGTH_SHORT).show();
                        });
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid capacity", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteTable(Table table) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Table");
        builder.setMessage("Are you sure you want to delete " + table.name + "?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            executor.execute(() -> {
                database.tableDao().deleteTable(table);
                mainHandler.post(() -> {
                    loadTables();
                    Toast.makeText(this, "Table deleted", Toast.LENGTH_SHORT).show();
                });
            });
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void clearFields() {
        etTableName.setText("");
        etTableCapacity.setText("");
    }

    private void loadTables() {
        executor.execute(() -> {
            try {
                List<Table> tables = database.tableDao().getAllTables();
                mainHandler.post(() -> {
                    if (tableAdapter != null) {
                        tableAdapter.updateTables(tables);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error loading tables", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}