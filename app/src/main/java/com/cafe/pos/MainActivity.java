package com.bizflow.pos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewTables;
    private TableAdapter tableAdapter;
    private TextView txtBusinessName;
    private AppDatabase database;
    private ExecutorService executor;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupDatabase();
        setupRecyclerView();
        loadTables();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_dashboard) {
            startActivity(new Intent(this, DashboardActivity.class));
            return true;
        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menu_sales_history) {
            startActivity(new Intent(this, SalesHistoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        recyclerViewTables = findViewById(R.id.recyclerViewTables);
        txtBusinessName = findViewById(R.id.txtBusinessName);
    }

    private void setupDatabase() {
        database = AppDatabase.getDatabase(this);
    }

    private void setupRecyclerView() {
        tableAdapter = new TableAdapter(new ArrayList<>(), this::onTableClick);
        
        boolean isTablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        int orientation = getResources().getConfiguration().orientation;
        
        int spanCount;
        if (isTablet) {
            spanCount = orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ? 4 : 3;
        } else {
            spanCount = orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        }
        
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        recyclerViewTables.setLayoutManager(gridLayoutManager);
        recyclerViewTables.setAdapter(tableAdapter);
    }

    private void onTableClick(Table table) {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("table_id", table.id);
        intent.putExtra("table_name", table.name);
        intent.putExtra("table_capacity", table.capacity);
        startActivity(intent);
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
                    Toast.makeText(this, "Error loading tables: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTables();
        updateBusinessName();
    }
    
    private void updateBusinessName() {
        android.content.SharedPreferences prefs = getSharedPreferences("business_settings", MODE_PRIVATE);
        String businessName = prefs.getString("business_name", "BizFlow");
        txtBusinessName.setText(businessName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
    
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupRecyclerView();
        loadTables();
    }
}